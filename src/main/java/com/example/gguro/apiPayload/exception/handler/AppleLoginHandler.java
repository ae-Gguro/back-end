package com.example.gguro.apiPayload.exception.handler;


import com.example.gguro.apiPayload.code.BaseErrorCode;
import com.example.gguro.apiPayload.exception.GeneralException;

public class AppleLoginHandler extends GeneralException {
    public AppleLoginHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
