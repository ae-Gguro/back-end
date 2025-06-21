package com.example.gguro.apiPayload.exception.handler;


import com.example.gguro.apiPayload.code.BaseErrorCode;
import com.example.gguro.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
