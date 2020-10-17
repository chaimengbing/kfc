package com.auv.model;


import com.auv.standard.hardware.model.BoardInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiChuang
 * @version 1.0
 * @Description
 * @since 2020/2/5 11:54
 **/
public class AUVBoardCellInit {
    private int boardNo;
    private int cellCount;


    public static List<BoardInfo> castListStandard(List<AUVBoardCellInit> list) {
        List<BoardInfo> originalList = new ArrayList<>();
        BoardInfo boardInfo;
        if (list != null) {
            for (AUVBoardCellInit auvList :
                    list) {
                boardInfo = new BoardInfo();
                boardInfo.setBoardNo( auvList.getBoardNo() );
                boardInfo.setCellCount( auvList.getCellCount() );
                originalList.add( boardInfo );
            }
        }
        return originalList;
    }

    public static List<com.auv.can.hardware.model.BoardInfo> castListCan(List<AUVBoardCellInit> list) {
        List<com.auv.can.hardware.model.BoardInfo> originalList = new ArrayList<>();
        com.auv.can.hardware.model.BoardInfo boardInfo;
        if (list != null) {
            for (AUVBoardCellInit auvList :
                    list) {
                boardInfo = new com.auv.can.hardware.model.BoardInfo();
                boardInfo.setBoardNo( auvList.getBoardNo() );
                boardInfo.setCellCount( auvList.getCellCount() );
                originalList.add( boardInfo );
            }
        }
        return originalList;
    }

    public int getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(int boardNo) {
        this.boardNo = boardNo;
    }

    public int getCellCount() {
        return cellCount;
    }

    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }
}
