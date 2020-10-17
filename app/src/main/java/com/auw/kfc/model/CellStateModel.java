package com.auw.kfc.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CellStateModel {

    @Id(autoincrement = true)
    private Long id;
    //柜子号 （唯一）
    @NotNull
    private int cellNo;
    //别名
    private String cellAlias;
    //人名
    private String userName;
    //卡ID
    private String userCardId;
    //用户ID
    private String userId;
    //takeByUser
    private String user;
    //板子号
    private int boardId;
    //是否为预订单
    private int preorder;
    //订单简化序号
    private String orderSimpleNum;
    //取餐码
    private String code;
    //订单序号
    private String orderNumber;
    //是否为已占用状态     0关 1开
    private int isUse;
    //是否为大格子     0 小格 1 大格
    private int isBig;
    //是否为开灯状态      0关 1开
    private int lightOpen;
    //是否为加热状态    0关 1开
    private int heatingOpen;
    //是否为消毒状态   0关 1开
    private int disinfectOpen;
    //是否为加锁状态    0关 1开
    private int lockOpen;

//    "cellId": 1, // 格子号
//            "orderId": "", // 订单编号
//            "isWarm": 0, // 当前加热状态:1-加热状态/0-未加热状态
//            "isLight": 0, // 当前开灯状态:1-开灯状态/0-未开灯状态
//            "isDisinfect": 0, // 当前消毒状态: 1-开消毒状态/0-未消毒状态
//            "lockOpen": 0, // 锁定状态:1-已锁/0-未锁定
//            "code": null, // 取餐码
//            "cellAlias": "1", // 格子别名
//            "isBig": 0 // 是否为大格:1-大格/0-小格

    @Transient
    private boolean isSelected;
    @Transient
    private int cellId;
    @Transient
    private String orderId;
    @Transient
    private int isWarm;
    @Transient
    private int isLight;
    @Transient
    private int isDisinfect;


    @Generated(hash = 767874591)
    public CellStateModel(Long id, int cellNo, String cellAlias, String userName,
                          String userCardId, String userId, String user, int boardId,
                          int preorder, String orderSimpleNum, String code, String orderNumber,
                          int isUse, int isBig, int lightOpen, int heatingOpen, int disinfectOpen,
                          int lockOpen) {
        this.id = id;
        this.cellNo = cellNo;
        this.cellAlias = cellAlias;
        this.userName = userName;
        this.userCardId = userCardId;
        this.userId = userId;
        this.user = user;
        this.boardId = boardId;
        this.preorder = preorder;
        this.orderSimpleNum = orderSimpleNum;
        this.code = code;
        this.orderNumber = orderNumber;
        this.isUse = isUse;
        this.isBig = isBig;
        this.lightOpen = lightOpen;
        this.heatingOpen = heatingOpen;
        this.disinfectOpen = disinfectOpen;
        this.lockOpen = lockOpen;
    }

    @Generated(hash = 2025424886)
    public CellStateModel() {
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(String userCardId) {
        this.userCardId = userCardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCellNo() {
        return cellNo;
    }

    public void setCellNo(int cellNo) {
        this.cellNo = cellNo;
    }

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public int getIsBig() {
        return isBig;
    }

    public void setIsBig(int isBig) {
        this.isBig = isBig;
    }

    public int getLightOpen() {
        return lightOpen;
    }

    public void setLightOpen(int lightOpen) {
        this.lightOpen = lightOpen;
    }

    public int getHeatingOpen() {
        return heatingOpen;
    }

    public void setHeatingOpen(int heatingOpen) {
        this.heatingOpen = heatingOpen;
    }

    public int getDisinfectOpen() {
        return disinfectOpen;
    }

    public void setDisinfectOpen(int disinfectOpen) {
        this.disinfectOpen = disinfectOpen;
    }

    public int getLockOpen() {
        return lockOpen;
    }

    public void setLockOpen(int lockOpen) {
        this.lockOpen = lockOpen;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public int getPreorder() {
        return preorder;
    }

    public void setPreorder(int preorder) {
        this.preorder = preorder;
    }


    public String getCellAlias() {
        return cellAlias;
    }

    public void setCellAlias(String cellAlias) {
        this.cellAlias = cellAlias;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderSimpleNum() {
        return orderSimpleNum;
    }

    public void setOrderSimpleNum(String orderSimpleNum) {
        this.orderSimpleNum = orderSimpleNum;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
        this.cellNo = cellId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
        this.orderNumber = orderId;
    }

    public int getIsWarm() {
        return isWarm;
    }

    public void setIsWarm(int isWarm) {
        this.isWarm = isWarm;
        this.heatingOpen = isWarm;
    }

    public int getIsLight() {
        return isLight;
    }

    public void setIsLight(int isLight) {
        this.isLight = isLight;
        this.lightOpen = isLight;
    }

    public int getIsDisinfect() {
        return isDisinfect;
    }

    public void setIsDisinfect(int isDisinfect) {
        this.isDisinfect = isDisinfect;
        this.disinfectOpen = isDisinfect;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
