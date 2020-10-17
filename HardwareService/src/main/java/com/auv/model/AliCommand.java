package com.auv.model;

import java.util.List;

public class AliCommand {

    private int cellNo;
    private List<AliOp> opList;


    public int getCellNo() {
        return cellNo;
    }

    public void setCellNo(int cellNo) {
        this.cellNo = cellNo;
    }

    public List<AliOp> getOpList() {
        return opList;
    }

    public void setOpList(List<AliOp> opList) {
        this.opList = opList;
    }
}
