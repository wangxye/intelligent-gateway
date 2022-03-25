package com.wxy.gateway.exception;

import com.wxy.gateway.constants.IntegateExceptionEnum;
import lombok.Data;

@Data
public class IntegateException extends RuntimeException{

    private Integer code;
    private String errMsg;

    public IntegateException(IntegateExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
        this.errMsg = exceptionEnum.getMsg();
    }

    public IntegateException(String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
        this.code = 5000;
    }

    public IntegateException(Integer code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }

}
