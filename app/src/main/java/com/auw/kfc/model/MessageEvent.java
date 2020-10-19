package com.auw.kfc.model;

public class MessageEvent {
    public static final String SYNC_CELL_SUCCESS = "syncCellSuccess";
    public static final String OPEN_CELL_SUCCESS = "openCellSuccess";
    public static final String OPEN_CELL_FAIL = "openCellFail";

    private String message;
    public String commandResult;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCommandResult() {
        return commandResult;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageEvent(String commandResult, String message) {
        this.commandResult = commandResult;
        this.message = message;
    }
}
