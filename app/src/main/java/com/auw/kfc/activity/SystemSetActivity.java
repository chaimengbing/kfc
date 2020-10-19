package com.auw.kfc.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.auv.standard.hardware.utils.SerialPortUtils;
import com.auv.utils.SharedPreferenceHelper;
import com.auw.kfc.R;
import com.auw.kfc.constant.Constant;

import java.util.ArrayList;
import java.util.List;

public class SystemSetActivity extends BaseActivity implements View.OnClickListener {


    private Spinner spinnerSystem;
    //串口
    private String selectItem;
    private Button btnBack;
    private EditText editShop;
    private EditText editShopNum;
    private TextView editDeviceName;
    private EditText editDeviceId;
    private EditText editAppId;
    private Button btnSave;
    private List<String> serialList;

    private EditText ipAdressEdit;
    private EditText portEdit;

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_system_layout );
        sharedPreferenceHelper = SharedPreferenceHelper.getInstance( getApplicationContext() );
        initView();
        initSpinner();
        showSetData();
    }


    private void initView() {
        btnBack = findViewById( R.id.system_btn_back );
        btnBack.setOnClickListener( this );
        editShop = findViewById( R.id.system_edit_shop );
        editShopNum = findViewById( R.id.shop_num_edit );
        ipAdressEdit = findViewById( R.id.ip_address_edit );
        portEdit = findViewById( R.id.port_edit );
        editDeviceName = findViewById( R.id.system_edit_devicename );

        spinnerSystem = findViewById( R.id.system_spinner );
        editDeviceId = findViewById( R.id.system_edit_device_id );
        editAppId = findViewById( R.id.system_edit_app_id );
        btnSave = findViewById( R.id.device_btn_save );
        btnSave.setOnClickListener( this );
    }

    //获取数据
    public void showSetData() {
        String appId = sharedPreferenceHelper.getString( Constant.KEY_APP_ID, "" );
        editAppId.setText( appId );
        String shopName = sharedPreferenceHelper.getString( Constant.KEY_SHOP_NAME, "" );
        editShop.setText( shopName );
        String shopNum = sharedPreferenceHelper.getString( Constant.KEY_SHOP_NUM, "" );
        editShopNum.setText( shopNum );
        String deviceName = sharedPreferenceHelper.getString( Constant.KEY_DEVICE_NAME, Constant.DEFAULT_DEVICE_NAME );
        editDeviceName.setText( deviceName );

        //串口
        int pos = 0;
        String spValue = sharedPreferenceHelper.getString( Constant.KEY_SERIAL_PORT, Constant.DEFAULT_SERIAL_PORT );

        for (int i = 0; i < serialList.size(); i++) {
            String listValue = serialList.get( i );
            if (listValue.equals( spValue )) {
                pos = i;
            }
        }
        spinnerSystem.setSelection( pos, true );

        String grpcIp = sharedPreferenceHelper.getString( Constant.GRPC_IP, "" );
        String grpcPort = sharedPreferenceHelper.getString( Constant.GRPC_PORT, "" );
        if (!TextUtils.isEmpty( grpcIp )) {
            ipAdressEdit.setText( grpcIp );
        }
        if (!TextUtils.isEmpty( grpcPort )) {
            portEdit.setText( grpcPort );
        }

    }

    //spinner的下拉列表
    private void initSpinner() {
        //创建下拉框的数据源
        //获取设备支持的串口
        serialList = getAllDevices();
        //创建适配器(下拉框的数据源是来自适配器)
        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, serialList );
        //为适配器添加样式
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerSystem.setAdapter( adapter );
        spinnerSystem.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取Spinner控件的适配器
                selectItem = adapter.getItem( position );
            }

            //没有选中时的处理
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast( "请选择串口类型", false );
            }
        } );
    }

    public static List<String> getAllDevices() {
        String[] allSerialPorts = SerialPortUtils.getAllDevices();
        List<String> serialPortList = new ArrayList();
        String[] var3 = allSerialPorts;
        int var4 = allSerialPorts.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String serialPort = var3[var5];
            String DEV = "/dev/";
            String serial_pix = "/dev/ttyS";
            if (!serialPort.contains( DEV )) {
                serialPort = DEV + serialPort;
            }

            if (serialPort.startsWith( serial_pix )) {
                serialPort = serialPort.substring( 0, serial_pix.length() + 1 );
                serialPortList.add( serialPort );
            }
        }

        return serialPortList;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.device_btn_save:
                saveData();
                break;
            default:
                break;
        }
    }

    //保存数据
    public void saveData() {
        String spinnerValue = spinnerSystem.getSelectedItem().toString();
        sharedPreferenceHelper.saveString( Constant.KEY_APP_ID, editAppId.getText().toString() );
        sharedPreferenceHelper.saveString( Constant.KEY_SERIAL_PORT, spinnerValue );
        sharedPreferenceHelper.saveString( Constant.KEY_DEVICE_NAME, editDeviceName.getText().toString() );
        sharedPreferenceHelper.saveString( Constant.KEY_SHOP_NUM, editShopNum.getText().toString() );
        sharedPreferenceHelper.saveString( Constant.KEY_SHOP_NAME, editShop.getText().toString() );

        sharedPreferenceHelper.saveString( Constant.GRPC_IP, ipAdressEdit.getText().toString() );
        sharedPreferenceHelper.saveString( Constant.GRPC_PORT, portEdit.getText().toString() );
        showToast( "保存成功", false );
    }
}
