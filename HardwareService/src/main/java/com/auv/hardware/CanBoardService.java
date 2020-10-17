package com.auv.hardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.auv.can.hardware.HardwareControlService;
import com.auv.can.hardware.model.ErrorRecoverModel;
import com.auv.can.hardware.model.HardwareErrorCode;
import com.auv.can.hardware.model.SerialPortCommand;
import com.auv.constant.Constants;
import com.auv.model.AUVBoardCellInit;
import com.auv.model.AUVCabinetCellStatus;
import com.auv.model.AUVErrorCode;
import com.auv.model.AUVErrorRecover;
import com.auv.utils.AUVLogUtil;
import com.auv.utils.Utils;
import com.tbtech.lynn.sell.bean.SmartCabinetCellBean;
import com.tbtech.lynn.sell.listener.OnFactoryMonitorListener;
import com.tbtech.lynn.sell.serial.SerialOperation;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiChuang
 * @version 4.0
 * @ClassName TBHardwareService
 * @Description CAN板控制类
 * @since 2020/1/16 17:43
 **/
public class CanBoardService extends HardwareService {

    private static final String TAG = "CanBoardService";

    private HardwareControlService service;
    @SuppressLint("StaticFieldLeak")
    private static CanBoardService instance;
    private Context context;
    private boolean isStart = false;

    public CanBoardService(Context context) {
        this.context = context;
        service = HardwareControlService.getInstance();
    }

    public static CanBoardService getInstance(Context context) {
        if (instance == null) {
            instance = new CanBoardService( context );
        }
        return instance;
    }

    @Override
    public void startCouplingTransaction(String serialPort, List<AUVBoardCellInit> boardCellInitList) {
        super.startCouplingTransaction( serialPort, boardCellInitList );
        try {
            AUVLogUtil.d( TAG, "startCouplingTransaction::isStart:" + isStart );
            if (!isStart) {
                service.start( context, serialPort, AUVBoardCellInit.castListCan( boardCellInitList ), true );
                isStart = true;
                this.saveLog( false );
                super.syncCellStatus( context );
            }
        } catch (Exception e) {
            AUVLogUtil.e( TAG, e );
        }
    }


    @Override
    public void saveLog(boolean open) {
        service.controlSerialLog( open );
    }

    @Override
    public Map<Integer, AUVCabinetCellStatus> getAllCellStatus() {
        Map map = this.service.getAllCellStatus();
        Map<Integer, AUVCabinetCellStatus> auvCabinetCellStatusMap = new ConcurrentHashMap<>();
        if (map != null && map.size() > 0) {
            for (Object key : map.keySet()) {
                Object smartCabinetCellBean = map.get( key );
                if (smartCabinetCellBean != null) {
                    auvCabinetCellStatusMap.put( (Integer) key, convertCellStatus( (SmartCabinetCellBean) smartCabinetCellBean ) );
                }
            }
        }
        return auvCabinetCellStatusMap;
    }

    @Override
    public AUVCabinetCellStatus getCellStatus(int cellNo) {
        return convertCellStatus( this.service.getCellStatus( cellNo ) );
    }

    @Override
    public boolean isStart() {
        return isStart;
    }

    @Override
    public void openDoor(int... cellNo) {
        service.openDoor( cellNo );
    }

    @Override
    public void openAllDoor() {
        service.openAllDoor();
    }

    @Override
    public void openLight(int cellNo) {
        service.controlLight( cellNo, true );
    }

    @Override
    public void openAllLight() {
        service.controlAllLight( true );
    }

    @Override
    public void closeLight(int cellNo) {
        service.controlLight( cellNo, false );
    }

    @Override
    public void closeAllLight() {
        AUVLogUtil.d( TAG, "closeAllLight::" );
        service.controlAllLight( false );
    }

    @Override
    public void openHeating(int cellNo) {
        AUVLogUtil.d( TAG, "openHeating::cellNo:" + cellNo );
        service.controlHeating( cellNo, true );
    }

    @Override
    public void openAllHeating() {
        service.controlAllHeating( true );
    }

    @Override
    public void closeHeating(int cellNo) {
        AUVLogUtil.d( TAG, "closeHeating::cellNo:" + cellNo );
        service.controlHeating( cellNo, false );
    }

    @Override
    public void closeAllHeating() {
        service.controlAllHeating( false );
    }

    @Override
    public void openIndicatorLight(int cellNo) {
        service.controlIndicatorLight( cellNo, true );
    }

    @Override
    public void openAllIndicatorLight() {
        service.controlAllIndicatorLight( true );
    }

    @Override
    public void closeIndicatorLight(int cellNo) {
        service.controlIndicatorLight( cellNo, false );
    }

    @Override
    public void closeAllIndicatorLight() {
        service.controlAllIndicatorLight( false );
    }

    @Override
    public void openDisinfect(int cellNo) {
        AUVLogUtil.d( TAG, "openDisinfect::cellNo:" + cellNo );
        service.controlDisinfectLight( cellNo, true );
    }

    @Override
    public void openAllDisinfect() {
        service.controlAllDisinfectLight( true );
    }

    @Override
    public void closeDisinfect(int cellNo) {
        AUVLogUtil.d( TAG, "closeDisinfect::cellNo:" + cellNo );
        service.controlDisinfectLight( cellNo, false );
    }

    @Override
    public void closeAllDisinfect() {
        service.controlAllDisinfectLight( false );
    }

    @Override
    public void showLog(LogListener logListener) {
        SerialOperation.getInstanceof().setOnFactoryListener( -94, new OnFactoryMonitorListener() {
            @Override
            public void onReceiveBuffer(byte[] bytes) {
                logListener.showLog( Utils.bytes2HexStr( bytes ) );
            }

            @Override
            public void onSendBuffer(byte[] bytes) {
                logListener.showLog( Utils.bytes2HexStr( bytes ) );
            }
        } );

    }

    @Override
    public void stop() {
        service.stop();
        isStart = false;
    }

    @Override
    public void setOnHardwareEventListener(ResultListener resultListener) {
        if (!isStart)
            return;
        service.setOnHardwareEventListener( new HardwareControlService.OnHardwareEventListener() {
            @Override
            public void feedbackDoorClosed(int cellNo) {
                resultListener.feedbackDoorClosedListener( cellNo );
            }

            @Override
            public void onException(HardwareErrorCode hardwareErrorCode) {
                resultListener.onException( new AUVErrorCode().convertCan( hardwareErrorCode ) );
            }

            @Override
            public void onExceptionRecover(ErrorRecoverModel errorRecoverModel) {
                resultListener.onExceptionRecover( new AUVErrorRecover().convertCan( errorRecoverModel ) );
            }

            @Override
            public void onCommandResult(int cellNo, boolean success, String messageId) {
                resultListener.onCommandResult( cellNo, success, messageId );
            }

            @Override
            public void cellStatusChangedListener(int cellNo, SerialPortCommand serialPortCommand, boolean isOpen) {
                resultListener.cellStatusChangedListener( cellNo, cast( serialPortCommand ), isOpen );
                syncResult( cellNo, serialPortCommand, isOpen );
            }

        } );
    }

    private void syncResult(int cellNo, @NotNull SerialPortCommand serialPortCommand, boolean isOpen) {
        String op = "";
        switch (serialPortCommand) {
            case COMMAND_CONTROL_LIGHT:
                op = Constants.LIGHT;
                break;
            case COMMAND_CONTROL_DISINFECT_LIGHT:
                op = Constants.DISINFECT;
                break;
            case COMMAND_CONTROL_HEATING:
                op = Constants.WARM;
                break;
            case COMMAND_CONTROL_DOOR:
                op = Constants.DOOR;
                break;
        }
        if (!TextUtils.isEmpty( op )) {
            syncResult( isOpen ? Constants.OPEN : Constants.CLOSE, op, cellNo, true );
            saveCellStatus( context, cellNo, cast( serialPortCommand ), isOpen );
        }
    }

    private com.auv.annotation.SerialPortCommand cast(SerialPortCommand serialPortCommand) {

        com.auv.annotation.SerialPortCommand command = null;

        switch (serialPortCommand) {
            case COMMAND_CONTROL_DOOR:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_DOOR;
                break;
            case COMMAND_CONTROL_HEATING:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_HEATING;
                break;
            case COMMAND_CONTROL_DISINFECT_LIGHT:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_DISINFECT_LIGHT;
                break;
            case COMMAND_CONTROL_LIGHT:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_LIGHT;
                break;
            case COMMAND_CONTROL_GOODS_EXITS:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_GOODS_EXITS;
                break;
            case COMMAND_CONTROL_INDICATOR_LIGHT:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_INDICATOR_LIGHT;
                break;
        }

        return command;
    }

    private AUVCabinetCellStatus convertCellStatus(SmartCabinetCellBean smartCabinetCellBean) {
        AUVCabinetCellStatus auvCabinetCellStatus = new AUVCabinetCellStatus();
//        auvCabinetCellStatus.setCellTemp(smartCabinetCellBean.getCellTemp());
        auvCabinetCellStatus.setCellLockOpen( smartCabinetCellBean.isCellLockOpen() );
//        auvCabinetCellStatus.setCellGoodsExist(smartCabinetCellBean.isCellGoodsExist());
        auvCabinetCellStatus.setCellCleanseOpen( smartCabinetCellBean.isCellCleanseOpen() );
        auvCabinetCellStatus.setCellHeatingOpen( smartCabinetCellBean.isCellHeatingOpen() );
        auvCabinetCellStatus.setCellLightOpen( smartCabinetCellBean.isCellLightOpen() );
        return auvCabinetCellStatus;
    }


}
