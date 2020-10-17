package com.auv.model;

import com.auv.standard.hardware.model.HardwareErrorCode;

/**
 * @author LiChuang
 * @version 1.0
 * @Description
 * @since 2020/2/22 15:40
 **/
public class AUVErrorCode {

    private int errorCode;
    private String errorMessage;
    private int cellNo;
    private int boardNo;

    public AUVErrorCode convertStandard(HardwareErrorCode errorCode) {
        this.setBoardNo( errorCode.getBoardNo() );
        this.setCellNo( errorCode.getCellNo() );
        this.setErrorCode( errorCode.getErrorCode() );
        this.setErrorMessage( errorCode.getErrorMessage() );
        return this;

    }

    public AUVErrorCode convertCan(com.auv.can.hardware.model.HardwareErrorCode errorCode) {
        this.setBoardNo( errorCode.getBoardNo() );
        this.setCellNo( errorCode.getCellNo() );
        this.setErrorCode( errorCode.getErrorCode() );
        this.setErrorMessage( errorCode.getErrorMessage() );
        return this;

    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getCellNo() {
        return cellNo;
    }

    public void setCellNo(int cellNo) {
        this.cellNo = cellNo;
    }

    public int getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(int boardNo) {
        this.boardNo = boardNo;
    }
}
