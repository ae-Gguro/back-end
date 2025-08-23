package com.example.gguro.apiPayload.exception.handler;


import com.example.gguro.apiPayload.code.BaseErrorCode;
import com.example.gguro.apiPayload.exception.GeneralException;

public class NotificationSettingHandler extends GeneralException {
    public NotificationSettingHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
