package com.example.gguro.apiPayload.exception.handler;


import com.example.gguro.apiPayload.code.BaseErrorCode;
import com.example.gguro.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {
    public UserHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
