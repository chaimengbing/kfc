package com.auv.hardware;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auv.annotation.SerialPortCommand;
import com.auv.constant.Constants;
import com.auv.model.AUVBoardCellInit;
import com.auv.model.AUVCabinetCellStatus;
import com.auv.model.AUVErrorCode;
import com.auv.model.AUVErrorRecover;
import com.auv.standard.hardware.model.BoardCellNo;
import com.auv.standard.hardware.utils.SerialPortUtils;
import com.auv.utils.AUVLogUtil;
import com.auv.utils.AliYunIotUtils;
import com.auv.utils.LinkkitUtils;
import com.auv.utils.SharedPreferenceHelper;
import com.auv.utils.ThreadPoolUtil;
import com.auv.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author LiChuang
 * @version 1.0
 * @ClassName HardwareService
 * @Description 控制硬件插件类
 * @since 2020/1/16 10:53
 **/
public abstract class HardwareService {

    private static final String TAG = HardwareService.class.getName();
    private static List<AUVBoardCellInit> boardCellInitList;

    /**
     * 启动集联控制服务
     */

    public void startCouplingTransaction(String serialPort, List<AUVBoardCellInit> boardCellInitList) {
        HardwareService.boardCellInitList = boardCellInitList;
    }


    public static List<String> getSupportSerialPort() {
        String[] allSerialPorts = SerialPortUtils.getAllDevices();
        List<String> serialPortList = new ArrayList<>();
        for (String serialPort : allSerialPorts) {
            serialPortList.add( serialPort.substring( 0, serialPort.indexOf( "(" ) ).trim() );
        }
        return serialPortList;
    }

    /**
     * 设置日志保存到本地
     *
     * @param open
     */
    public abstract void saveLog(boolean open);

    /*
       查询所有格口状态
    */
    public abstract Map<Integer, AUVCabinetCellStatus> getAllCellStatus();


    /*
        查询所有格口状态
     */
    public abstract AUVCabinetCellStatus getCellStatus(int cellNo);

    /**
     * 关闭串口
     */
    public abstract void stop();

    public abstract boolean isStart();


    public List<AUVBoardCellInit> getBoardCellInitList() {
        return boardCellInitList;
    }


    public abstract void openDoor(int... cellNo);

    public abstract void openAllDoor();

    public abstract void openLight(int cellNo);

    public abstract void openAllLight();

    public abstract void closeLight(int cellNo);

    public abstract void closeAllLight();

    public abstract void openHeating(int cellNo);

    public abstract void openAllHeating();

    public abstract void closeHeating(int cellNo);

    public abstract void closeAllHeating();

    public abstract void openIndicatorLight(int cellNo);

    public abstract void openAllIndicatorLight();

    public abstract void closeIndicatorLight(int cellNo);

    public abstract void closeAllIndicatorLight();

    public abstract void openDisinfect(int cellNo);

    public abstract void openAllDisinfect();

    public abstract void closeDisinfect(int cellNo);

    public abstract void closeAllDisinfect();

    /**
     * 将副柜的格子号转换为真实的控制板地址
     */
    BoardCellNo convertCellNoToBoardCellNo(int cellNo) {
        final BoardCellNo boardCellNo = new BoardCellNo();
        --cellNo;
        int index = 0;
        for (final AUVBoardCellInit boardCellInit : HardwareService.boardCellInitList) {
            final int oldIndex = index;
            index += boardCellInit.getCellCount();
            if (cellNo < index) {
                boardCellNo.setBoardNo( boardCellInit.getBoardNo() );
                /*
                此算法为将2号，3号柜子的格子初始化为从1开始计算
                 */
                boardCellNo.setCellNo( cellNo - oldIndex + 1 );
                break;
            }
        }
        return boardCellNo;
    }

    /**
     * 将真实的控制板地址转换为格子号
     */
    BoardCellNo convertBoardCellNoToCellNo(int boardNo, int cellNo) {
        BoardCellNo boardCellNo = new BoardCellNo();
        int index = 0;
        for (int i = 0; i < HardwareService.boardCellInitList.size(); i++) {
            AUVBoardCellInit boardInfo = HardwareService.boardCellInitList.get( i );
            if (boardNo > boardInfo.getBoardNo()) {
                index += boardInfo.getCellCount();
            } else if (boardNo == boardInfo.getBoardNo()) {
                boardCellNo.setBoardNo( boardNo );
                boardCellNo.setCellNo( index + cellNo );
                break;
            }
        }

        return boardCellNo;
    }

    /**
     * 监听硬件执行结果
     */
    public interface ResultListener {
        //监听关门
        void feedbackDoorClosedListener(int cellNo);

        //错误结果监听回调
        void onException(AUVErrorCode errorCode);

        //监听执行结果
        void onCommandResult(int cellNo, boolean success, String messageId);

        void onExceptionRecover(AUVErrorRecover errorRecover);

        //状态变更监听
        void cellStatusChangedListener(int cellNo, SerialPortCommand serialPortCommand, boolean isOpen);
    }

    /**
     * 监听日志
     */
    public interface LogListener {
        void showLog(String msg);
    }


    protected void syncResult(int command, String property, int cellNo, boolean success) {
        JSONObject ansyReturn = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject operation = new JSONObject();
        JSONObject unit = new JSONObject();
        JSONArray operations = new JSONArray();
        unit.put( "cellNo", cellNo );
        operation.put( "command", command );
        operation.put( "property", property );
        if (success) {
            operation.put( "msg", "success" );
        } else {
            operation.put( "msg", "fail" );
        }
        operations.add( operation );
        unit.put( "operations", operations );
        jsonArray.add( unit );
        /*
         * 获取云端发送的指令集合
         */
        ThreadPoolUtil.executorService.execute( () -> {
            if (success) {
                ansyReturn.put( "success", jsonArray );
            } else {
                ansyReturn.put( "fail", jsonArray );
            }

            //异步返回的信息
            ansyReturn.put( "timestamp", Utils.timestamp() );
            LinkkitUtils.getInstance().publishByName( Constants.ANSY, ansyReturn.toJSONString() );
            AUVLogUtil.d( TAG, "ansyResult::ansyReturn:" + ansyReturn.toJSONString() );
        } );
    }


    /**
     * 同步格子状态
     */

    void syncCellStatus(Context context) {
        List<AUVBoardCellInit> boardCellInits = InitHardwareService.hardwareService.getBoardCellInitList();
        int cellTotal = 0;
        if (boardCellInits != null) {
            Log.d( TAG, "syncCellStatus::boardCellInits = " + boardCellInits.toString() );
            for (AUVBoardCellInit cellInit : boardCellInits) {
                cellTotal += cellInit.getCellCount();
                AUVLogUtil.d( TAG, "syncCellStatus::cellTotal = " + cellTotal );
            }
            if (cellTotal > 0) {
                for (int cellNo = 1; cellNo <= cellTotal; cellNo++) {
                    AUVCabinetCellStatus cellStatus = (AUVCabinetCellStatus) SharedPreferenceHelper.getInstance( context ).getCellStatus( String.valueOf( cellNo ), AUVCabinetCellStatus.class );
                    if (null != cellStatus) {
                        AUVLogUtil.d( TAG, "syncCellStatus::cellNo = " + cellNo + " , " + cellStatus.toString() );
                        if (cellStatus.isCellLightOpen()) {
                            InitHardwareService.hardwareService.openLight( cellNo );
                        }
                        if (cellStatus.isCellCleanseOpen()) {
                            InitHardwareService.hardwareService.openDisinfect( cellNo );
                        }
                        if (cellStatus.isCellHeatingOpen()) {
                            InitHardwareService.hardwareService.openHeating( cellNo );
                        }
                        SharedPreferenceHelper.getInstance( context ).putObject( String.valueOf( cellNo ), cellStatus, AUVCabinetCellStatus.class );
                    }
                }
                for (int cellNo = 1; cellNo <= cellTotal; cellNo++) {
                    AUVCabinetCellStatus cellStatus = (AUVCabinetCellStatus) SharedPreferenceHelper.getInstance( context ).getCellStatus( String.valueOf( cellNo ), AUVCabinetCellStatus.class );
                    if (null != cellStatus) {
                        AUVLogUtil.d( TAG, "syncCellStatus::cellNo = " + cellNo + " , " + cellStatus.toString() );
                        if (cellStatus.isCellLockOpen()) {
                            AUVCabinetCellStatus status = InitHardwareService.hardwareService.getCellStatus( cellNo );
                            if (null != status && AliYunIotUtils.getInstance().isConnected()) {
                                if (!status.isCellLockOpen()) {
                                    saveCellStatus( context, cellNo, SerialPortCommand.COMMAND_CONTROL_DOOR, false );
                                    syncResult( Constants.CLOSE, Constants.DOOR, cellNo, !status.isCellLockOpen() );
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 同步格子状态
     */

    public void sendCellStatus(Context context) {
        List<AUVBoardCellInit> boardCellInits = InitHardwareService.hardwareService.getBoardCellInitList();
        int cellTotal = 0;
        if (boardCellInits != null) {
            for (AUVBoardCellInit cellInit : boardCellInits) {
                cellTotal += cellInit.getCellCount();
            }
            AUVLogUtil.d( TAG, "sendCellStatus::cellTotal:" + cellTotal );
            if (cellTotal > 0) {
                for (int cellNo = 1; cellNo <= cellTotal; cellNo++) {
                    AUVCabinetCellStatus cellStatus = (AUVCabinetCellStatus) SharedPreferenceHelper.getInstance( context ).getCellStatus( String.valueOf( cellNo ), AUVCabinetCellStatus.class );
                    if (null != cellStatus) {
                        AUVLogUtil.d( TAG, "sendCellStatus::cellNo:" + cellNo + " , " + cellStatus.toString() );
                        syncResult( cellStatus.isCellLightOpen() ? Constants.OPEN : Constants.CLOSE, Constants.LIGHT, cellNo, true );
                        syncResult( cellStatus.isCellCleanseOpen() ? Constants.OPEN : Constants.CLOSE, Constants.DISINFECT, cellNo, true );
                        syncResult( cellStatus.isCellHeatingOpen() ? Constants.OPEN : Constants.CLOSE, Constants.WARM, cellNo, true );
                        syncResult( cellStatus.isCellLockOpen() ? Constants.OPEN : Constants.CLOSE, Constants.DOOR, cellNo, true );
                    }
                }
            }
        }
    }


    /**
     * 保存格子状态
     */
    synchronized void saveCellStatus(Context context, int cellNo, SerialPortCommand serialPortCommand, boolean isOpen) {
        String cellNoStr;
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
                cellStatus.setCellLightOpen( isOpen );
                break;
            case COMMAND_CONTROL_DISINFECT_LIGHT:
                cellStatus.setCellCleanseOpen( isOpen );
                break;
            case COMMAND_CONTROL_HEATING:
                cellStatus.setCellHeatingOpen( isOpen );
                break;
            case COMMAND_CONTROL_DOOR:
                cellStatus.setCellLockOpen( isOpen );
                break;
            default:
                break;
        }
        SharedPreferenceHelper.getInstance( context ).putObject( cellNoStr, cellStatus, AUVCabinetCellStatus.class );

    }

    /**
     * 硬件结果监听
     */
    public abstract void setOnHardwareEventListener(ResultListener resultListener);

    /**
     * 获取发送日志
     */
    public abstract void showLog(LogListener logListener);

}
