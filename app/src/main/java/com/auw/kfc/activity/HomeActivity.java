package com.auw.kfc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.auv.standard.hardware.utils.LogUtils;
import com.auv.utils.AUVLogUtil;
import com.auv.utils.SharedPreferenceHelper;
import com.auw.kfc.R;
import com.auw.kfc.constant.Constant;
import com.auw.kfc.model.CellStateModel;
import com.auw.kfc.model.MessageEvent;
import com.auw.kfc.util.GreenDaoHelper;
import com.auw.kfc.util.OpenSerialPortService;
import com.auw.kfc.util.ScanGunKeyEventHolder;
import com.auw.kfc.util.ServiceUtils;
import com.auw.kfc.util.StringUtils;
import com.auw.kfc.util.UiUtils;
import com.auw.kfc.util.Utils;
import com.yum.cpos.grpclib.pickupservice.PickupGrpc;
import com.yum.cpos.grpclib.pickupservice.ReportFromCabinetERequest;
import com.yum.cpos.grpclib.pickupservice.ReportFromCabinetEResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();
    private ImageView scanImageView;
    private OpenSerialPortService openSerialPortService;
    private String receiverData = "";

    private ScanGunKeyEventHolder scanGunKeyEventHolder;

    private ManagedChannel channel;
    private PickupGrpc.PickupStub mPickupStub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        scanImageView = findViewById( R.id.scan_imageview );

        findViewById( R.id.system_view ).setOnLongClickListener( view -> {
            startActivity( new Intent( getApplicationContext(), SystemSetActivity.class ) );
            return false;

        } );

        initSerialService();
        initScanEvent();
        String grpcIp = SharedPreferenceHelper.getInstance( getApplicationContext() ).getString( Constant.GRPC_IP, "" );
        String grpcPort = SharedPreferenceHelper.getInstance( getApplicationContext() ).getString( Constant.GRPC_PORT, "" );
        if (!TextUtils.isEmpty( grpcIp ) && !TextUtils.isEmpty( grpcPort )) {
            AUVLogUtil.d( TAG, "initGrpc::IP:" + grpcIp + ",grpcPort:" + grpcPort );
            if (channel == null) {
                initGrpc( grpcIp, Integer.parseInt( grpcPort ) );
            }
        }
    }

    private boolean isSaveMeals = false;

    private void initGrpc(String ip, int port) {
        if (TextUtils.isEmpty( ip ) || port < 0) {
            return;
        }
        this.channel = ManagedChannelBuilder.forAddress( ip, port )
                .usePlaintext()
                .build();
        mPickupStub = PickupGrpc.newStub( channel );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );
        initSerialService();
        initScanEvent();
    }

    private void initSerialService() {
        if (openSerialPortService == null) {
            openSerialPortService = new OpenSerialPortService( getApplicationContext(), data -> {
                Log.d( TAG, "收到KFC数据：" + data );
                receiverData = data;
                JSONObject receiveData;
                try {
                    receiveData = JSONObject.parseObject( data );
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                for (String key : receiveData.keySet()) {
                    String res = receiveData.getString( Constant.SCAN_RESULT );
                    if (res.equals( Constant.HAVE_MEALS )) {
                        startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
                        break;
                    } else if (!res.equals( Constant.HAVE_MEALS )) {
                        if (Constant.SCAN_RESULT.equals( key )) {
                            Intent intent = new Intent( getApplicationContext(), ScanResultActivity.class );
                            intent.putExtra( Constant.SCAN_RESULT, res );
                            startActivity( intent );
                            break;
                        }
                    }

                }
            } );
        }
    }

    private void initScanEvent() {
        if (scanGunKeyEventHolder == null) {
            scanGunKeyEventHolder = new ScanGunKeyEventHolder();
            setScanGunKeyEventHolder();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return scanGunKeyEventHolder.analysisKeyEvent( event );
    }

    /**
     * 设置扫码事件监听
     */
    public void setScanGunKeyEventHolder() {
        //设置扫码枪扫描事件
        scanGunKeyEventHolder.setOnBarCodeCatchListener( barcode -> {
            if (TextUtils.isEmpty( barcode )) {
                return;
            }
            Log.i( TAG, "barcode:" + barcode );
            handQRCode( barcode );
        } );
    }

    private void handQRCode(String barcode) {
        String[] scanResult = barcode.split( "-" );
        if (scanResult != null && scanResult.length > 0 && Utils.isKFCQRCode( scanResult )) {
            isSaveMeals = false;
            //处理扫码取餐
            //门店编号
            String shop = scanResult[0];
            //订单日期
            String orderDate = scanResult[1];
            //订单序号
            String orderNum = scanResult[2];
            takeMeals( orderNum );

        } else {
            isSaveMeals = true;
            //处理扫码存餐
            if (mPickupStub != null) {
                ReportFromCabinetERequest reportFromCabinetERequest = ReportFromCabinetERequest.newBuilder().setQrCode( barcode ).setEventType( Constant.EVENT_TYPE_CREATE ).build();
                mPickupStub.reportFromCabinetE( reportFromCabinetERequest, new StreamObserver<ReportFromCabinetEResponse>() {
                    @Override
                    public void onNext(ReportFromCabinetEResponse value) {
                        String code = value.getStatus().getCode();
                        AUVLogUtil.d( TAG, "onNext::code:" + code );
                        saveMeals( code );
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onCompleted() {

                    }
                } );
            } else {
                saveMeals( barcode );
            }
        }
    }

    private void takeMeals(String orderNum) {
        List<CellStateModel> listByOrder = GreenDaoHelper.getInstance( getApplicationContext() ).getListByCode( orderNum );
        if (listByOrder != null && listByOrder.size() > 0) {
            if (hardwareControlService != null) {
                count = listByOrder.size();
                for (CellStateModel cellStateModel : listByOrder) {
                    AUVLogUtil.d( TAG, "takeMeals::openDoor:" + cellStateModel.getCellNo() );
                    hardwareControlService.openDoor( cellStateModel.getCellNo() );
                }
            }
        } else {
            showToast( "暂无订单", false );
        }
    }


    protected int openDoorCount = 0;
    protected int count = 0;
    protected StringBuffer cellNoSb = new StringBuffer();

    @Subscribe
    public void openCellResult(MessageEvent messageEvent) {
        if (!ServiceUtils.isForeground( this )) {
            return;
        }
        String result = messageEvent.getCommandResult();
        String cellNo = messageEvent.getMessage();
        if (StringUtils.isNotEmpty( result )) {
            AUVLogUtil.d( TAG, "openCellSuccess::result:" + result );
            if (isSaveMeals) {//存餐
                if (StringUtils.isNotEmpty( result ) && result.equals( MessageEvent.OPEN_CELL_SUCCESS )) {
                    showToast( "存餐成功", false );
                    ReportFromCabinetERequest reportFromCabinetERequest = ReportFromCabinetERequest.newBuilder().setCellNo( cellNo ).setEventType( Constant.EVENT_TYPE_CLOSED ).build();
                    mPickupStub.reportFromCabinetE( reportFromCabinetERequest, new StreamObserver<ReportFromCabinetEResponse>() {
                        @Override
                        public void onNext(ReportFromCabinetEResponse value) {
                            String code = value.getTransactionId();
                            AUVLogUtil.d( TAG, "onNext::" );
                            if (TextUtils.isEmpty( code )) {
                                return;
                            }
                            AUVLogUtil.d( TAG, "onNext::code:" + code );
                            saveMeals( code );
                        }

                        @Override
                        public void onError(Throwable t) {

                        }

                        @Override
                        public void onCompleted() {

                        }
                    } );
                } else {
                    runOnUiThread( () -> {
                        String temp = getString( R.string.open_cell_fail );
                        String openCellFail = String.format( temp, cellNo );
                        CellStateModel cellStateModel = GreenDaoHelper.getInstance( getApplicationContext() ).getByCellNo( Integer.parseInt( cellNo ) );
                        if (cellStateModel != null) {
                            cellStateModel.setCode( "" );
                            cellStateModel.setIsUse( 0 );
                            GreenDaoHelper.getInstance( getApplicationContext() ).updateCellState( cellStateModel );
                        }
                        showToast( openCellFail, false );
                    } );

                }
            } else {
                AUVLogUtil.d( TAG, "openCellSuccess::openDoorCount:" + openDoorCount + ",count:" + count );
                if (StringUtils.isNotEmpty( cellNo )) {
                    CellStateModel cellStateModel = GreenDaoHelper.getInstance( getApplicationContext() ).getByCellNo( Integer.parseInt( cellNo ) );
                    if (cellStateModel != null) {
                        String cellAlias = cellStateModel.getCellNo() + "";
                        cellNoSb.append( cellAlias ).append( "," );
                        cellStateModel.setCode( "" );
                        cellStateModel.setIsUse( 0 );
                        GreenDaoHelper.getInstance( getApplicationContext() ).updateCellState( cellStateModel );
                    }
                }
                count++;
                if (count == openDoorCount) {
                    Intent intent = new Intent( getApplicationContext(), ScanResultActivity.class );
                    intent.putExtra( Constant.SCAN_RESULT, cellNoSb.toString().substring( 0, cellNoSb.length() - 1 ) );
                    startActivity( intent );
                }
            }
        }


    }


    private void saveMeals(String code) {
        List<CellStateModel> listByOrder = GreenDaoHelper.getInstance( getApplicationContext() ).getListByCode( code );
        if (listByOrder != null && listByOrder.size() > 0) {
            showToast( "订单已存在", false );
        } else {
            if (hardwareControlService != null) {
                List<CellStateModel> cellStateModelList = GreenDaoHelper.getInstance( getApplicationContext() ).getNoUseCell();
                if (ServiceUtils.isNotEmpty( cellStateModelList )) {
                    int currentCellNo = ServiceUtils.getRandomCell( cellStateModelList.size() );
                    CellStateModel cellStateModel;
                    if (currentCellNo < 0 || currentCellNo >= cellStateModelList.size()) {
                        cellStateModel = cellStateModelList.get( 0 );
                    } else {
                        cellStateModel = cellStateModelList.get( currentCellNo );
                    }
                    cellStateModel.setCode( code );
                    cellStateModel.setIsUse( 1 );
                    AUVLogUtil.d( TAG, "saveMeals::openDoor:" + cellStateModel.getCellNo() );
                    hardwareControlService.openDoor( cellStateModel.getCellNo() );
                } else {
                    showToast( "暂无可用格子", false );
                }
            }

        }
    }


    private Handler receiverHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage( msg );
            if (TextUtils.isEmpty( receiverData )) {
                startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
            } else {
                startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
            }
            receiverHandler.removeMessages( 0 );
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan_layout || view.getId() == R.id.scan_imageview) {
            receiverHandler.sendEmptyMessageAtTime( 0, 500 );
            if (openSerialPortService != null) {
                String command = setSendData( Constant.QUERY_KFC, Constant.QUERY_ORDER );
                openSerialPortService.onSend( command );
            } else {
                startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
            }
        }
    }
}
