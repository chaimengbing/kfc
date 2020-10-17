package com.auv.model;


import com.auv.standard.hardware.model.ErrorRecoverModel;

/**
 * @author LiChuang
 * @version 1.0
 * @since 2020/2/25 17:16
 **/
public class AUVErrorRecover {

    private int errorCode;
    private String errorMessage;
    private int cellNo;
    private int boardNo;


    public AUVErrorRecover() {
    }

    public AUVErrorRecover convertStandard(ErrorRecoverModel errorRecoverModel) {
        this.setBoardNo( errorRecoverModel.getBoardNo() );
        this.setCellNo( errorRecoverModel.getCellNo() );
        this.setErrorCode( errorRecoverModel.getErrorCode() );
        this.setErrorMessage( errorRecoverModel.getErrorMessage() );
        return this;

    }

    public AUVErrorRecover convertCan(com.auv.can.hardware.model.ErrorRecoverModel errorCode) {
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
