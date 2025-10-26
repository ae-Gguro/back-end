package com.example.gguro.apiPayload.exception.handler;


import com.example.gguro.apiPayload.code.BaseErrorCode;
import com.example.gguro.apiPayload.exception.GeneralException;

public class ProfileHandler extends GeneralException {
    public ProfileHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
