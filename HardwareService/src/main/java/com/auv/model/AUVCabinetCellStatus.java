package com.auv.model;


public class AUVCabinetCellStatus {
    private int cellTemp;
    private boolean cellLockOpen;
    private boolean cellLightOpen;
    private boolean cellCleanseOpen;
    private boolean cellHeatingOpen;
    private boolean cellGoodsExist;

    public AUVCabinetCellStatus() {
    }

    public int getCellTemp() {
        return this.cellTemp;
    }

    public void setCellTemp(int var1) {
        this.cellTemp = var1;
    }

    public boolean isCellLockOpen() {
        return this.cellLockOpen;
    }

    public void setCellLockOpen(boolean var1) {
        this.cellLockOpen = var1;
    }

    public boolean isCellLightOpen() {
        return this.cellLightOpen;
    }

    public void setCellLightOpen(boolean var1) {
        this.cellLightOpen = var1;
    }

    public boolean isCellCleanseOpen() {
        return this.cellCleanseOpen;
    }

    public void setCellCleanseOpen(boolean var1) {
        this.cellCleanseOpen = var1;
    }

    public boolean isCellHeatingOpen() {
        return this.cellHeatingOpen;
    }

    public void setCellHeatingOpen(boolean var1) {
        this.cellHeatingOpen = var1;
    }

    public boolean isCellGoodsExist() {
        return this.cellGoodsExist;
    }

    public void setCellGoodsExist(boolean var1) {
        this.cellGoodsExist = var1;
    }

    @Override
    public String toString() {
        return "AUVCabinetCellStatus [cellTemp=" + this.cellTemp + ", cellLockOpen=" + this.cellLockOpen + ", cellLightOpen=" + this.cellLightOpen + ", cellCleanseOpen=" + this.cellCleanseOpen + ", cellHeatingOpen=" + this.cellHeatingOpen + ", cellGoodsExist=" + this.cellGoodsExist + "]";
    }
}