package com.auv.hardware;

import android.content.Context;

import com.auv.constant.Constants;
import com.auv.model.AUVBoardCellInit;
import com.auv.model.AUVCabinetCellStatus;
import com.auv.model.AUVErrorCode;
import com.auv.model.AUVErrorRecover;
import com.auv.standard.hardware.HardwareControlService;
import com.auv.standard.hardware.model.ErrorRecoverModel;
import com.auv.standard.hardware.model.HardwareErrorCode;
import com.auv.standard.hardware.model.SerialPortCommand;
import com.auv.utils.AliYunIotUtils;
import com.auv.utils.SharedPreferenceHelper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 4.0
 * @Description 标准板控制类
 * @since 2020/1/16 11:16
 **/
public class StandardBoardService extends HardwareService {

    private HardwareControlService service;

    private static StandardBoardService instance;

    private Context context;

    private boolean isStartStandardBoard = false;

    private StandardBoardService(Context context) {
        this.context = context;
        service = HardwareControlService.getInstance();
    }

    @Override
    public boolean isStart() {
        return isStartStandardBoard;
    }

    @Override
    public void openDoor(int... cellNo) {
        for (int cellNum : cellNo)
            service.openDoor( cellNum );
    }

    @Override
    public void openAllDoor() {
        service.openAllDoor();
    }

    @Override
    public void openLight(int cellNo) {
        syncResult( AliYunIotUtils.OPEN, AliYunIotUtils.LIGHT, cellNo, true );
        service.controlLight( cellNo, true );
    }

    @Override
    public void openAllLight() {
        service.controlAllLight( true );
    }

    @Override
    public void closeLight(int cellNo) {
        syncResult( AliYunIotUtils.CLOSE, AliYunIotUtils.LIGHT, cellNo, true );
        service.controlLight( cellNo, false );
    }

    @Override
    public void closeAllLight() {
        service.controlAllLight( false );
    }

    @Override
    public void openHeating(int cellNo) {
        syncResult( AliYunIotUtils.OPEN, AliYunIotUtils.WARM, cellNo, true );
        service.controlHeating( cellNo, true );
    }

    @Override
    public void openAllHeating() {
        service.controlAllHeating( true );
    }

    @Override
    public void closeHeating(int cellNo) {
        syncResult( AliYunIotUtils.CLOSE, AliYunIotUtils.WARM, cellNo, true );
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
        syncResult( AliYunIotUtils.OPEN, AliYunIotUtils.DISINFECT, cellNo, true );
        service.controlDisinfectLight( cellNo, true );
    }

    @Override
    public void openAllDisinfect() {
        service.controlAllDisinfectLight( true );
    }

    @Override
    public void closeDisinfect(int cellNo) {
        syncResult( AliYunIotUtils.CLOSE, AliYunIotUtils.DISINFECT, cellNo, true );
        service.controlDisinfectLight( cellNo, false );
    }

    @Override
    public void closeAllDisinfect() {
        service.controlAllDisinfectLight( false );
    }

    public static StandardBoardService getInstance(Context context) {
        if (instance == null) {
            instance = new StandardBoardService( context );
        }
        return instance;
    }

    @Override
    public void showLog(LogListener logListener) {

    }

    /**
     * 启动集联控制服务，目前使用这个硬件通讯协议
     *
     * @param serialPort        串口编号
     * @param boardCellInitList 控制板信息
     */
    public void startCouplingTransaction(String serialPort, List<AUVBoardCellInit> boardCellInitList) {
        super.startCouplingTransaction( serialPort, boardCellInitList );
        service.start( serialPort, AUVBoardCellInit.castListStandard( boardCellInitList ) );
        isStartStandardBoard = true;
    }

    @Override
    public void saveLog(boolean open) {
    }


    @Override
    public Map<Integer, AUVCabinetCellStatus> getAllCellStatus() {
        Map<Integer, AUVCabinetCellStatus> map = new ConcurrentHashMap<>();

        AUVCabinetCellStatus status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( false );
        status.setCellHeatingOpen( false );
        status.setCellCleanseOpen( true );

        map.put( 1, status );

        status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( false );
        status.setCellHeatingOpen( false );
        status.setCellCleanseOpen( true );

        map.put( 2, status );
        status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( false );
        status.setCellHeatingOpen( false );
        status.setCellCleanseOpen( true );

        map.put( 3, status );
        status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( false );
        status.setCellHeatingOpen( false );
        status.setCellCleanseOpen( true );

        map.put( 4, status );
        status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( false );
        status.setCellHeatingOpen( false );
        status.setCellCleanseOpen( true );

        map.put( 5, status );
        status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( false );
        status.setCellHeatingOpen( false );
        status.setCellCleanseOpen( true );

        map.put( 6, status );

        return map;
    }

    @Override
    public AUVCabinetCellStatus getCellStatus(int cellNo) {

        AUVCabinetCellStatus status = new AUVCabinetCellStatus();
        status.setCellLockOpen( true );
        status.setCellLightOpen( true );
        status.setCellHeatingOpen( true );
        status.setCellCleanseOpen( true );

        return status;
    }

    @Override
    public void stop() {
        service.stop();
        isStartStandardBoard = false;
    }


    private void syncResult(int cellNo, @NotNull SerialPortCommand serialPortCommand, boolean success) {
        String op = "", cellNoStr;
        boolean isOpen = false;
        try {
            cellNoStr = String.valueOf( cellNo );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        AUVCabinetCellStatus cellStatus = (AUVCabinetCellStatus) SharedPreferenceHelper.getInstance( context ).getCellStatus( cellNoStr, AUVCabinetCellStatus.class );
        if (cellStatus == null) {
            cellStatus = new AUVCabinetCellStatus();
        }
        switch (serialPortCommand) {
            case COMMAND_CONTROL_LIGHT:
                op = Constants.LIGHT;
                if (success) {
                    isOpen = !cellStatus.isCellLightOpen();
                    cellStatus.setCellLightOpen( isOpen );
                }
                break;
            case COMMAND_CONTROL_DISINFECT_LIGHT:
                if (success) {
                    isOpen = !cellStatus.isCellLightOpen();
                    cellStatus.setCellLightOpen( !cellStatus.isCellLightOpen() );
                }
                op = Constants.DISINFECT;
                break;
            case COMMAND_CONTROL_HEATING:
                if (success) {
                    isOpen = !cellStatus.isCellLightOpen();
                    cellStatus.setCellLightOpen( !cellStatus.isCellLightOpen() );
                }
                op = Constants.WARM;
                break;
            case COMMAND_CONTROL_DOOR:
                if (success) {
                    isOpen = !cellStatus.isCellLightOpen();
                    cellStatus.setCellLightOpen( !cellStatus.isCellLightOpen() );
                }
                op = Constants.DOOR;
                break;
        }
        syncResult( isOpen ? Constants.OPEN : Constants.CLOSE, op, cellNo, success );
        saveCellStatus( context, cellNo, cast( serialPortCommand ), isOpen );
    }


    @Contract(pure = true)
    private com.auv.annotation.SerialPortCommand cast(@NotNull SerialPortCommand serialPortCommand) {
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
            case COMMAND_CONTROL_INDICATOR_LIGHT:
                command = com.auv.annotation.SerialPortCommand.COMMAND_CONTROL_INDICATOR_LIGHT;
                break;
        }

        return command;
    }


    /*
            监听执行结果
         */
    @Override
    public void setOnHardwareEventListener(final ResultListener resultListener) {
        service.setOnHardwareEventListener( new HardwareControlService.OnHardwareEventListener() {
            @Override
            public void feedbackDoorClosed(int cellNo) {
                syncResult( AliYunIotUtils.CLOSE, AliYunIotUtils.DOOR, cellNo, true );
                resultListener.feedbackDoorClosedListener( cellNo );
            }

            @Override
            public void onException(HardwareErrorCode hardwareErrorCode) {
                resultListener.onException( new AUVErrorCode().convertStandard( hardwareErrorCode ) );
            }

            @Override
            public void onCommandResult(int cellNo, boolean success, SerialPortCommand serialPortCommand, String messageId) {
                resultListener.onCommandResult( cellNo, success, messageId );
                syncResult( cellNo, serialPortCommand, success );
            }


            @Override
            public void onExceptionRecover(ErrorRecoverModel errorRecoverModel) {
                resultListener.onExceptionRecover( new AUVErrorRecover().convertStandard( errorRecoverModel ) );
            }
        } );
    }

}
