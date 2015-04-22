package com.zwb.ui.refreshlistview.exception;

/**
 * Created by pc on 2015/4/22.
 */
public class BaseListViewException extends Exception {
    private String message;

    public BaseListViewException(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
