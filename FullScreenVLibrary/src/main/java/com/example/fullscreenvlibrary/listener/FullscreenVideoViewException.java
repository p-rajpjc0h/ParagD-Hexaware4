package com.example.fullscreenvlibrary.listener;

public class FullscreenVideoViewException {

    public int code;
    public String message;

    public FullscreenVideoViewException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public FullscreenVideoViewException(String message) {
        this.code = -1;
        this.message = message;
    }
}
