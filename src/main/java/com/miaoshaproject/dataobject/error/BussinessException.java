package com.miaoshaproject.dataobject.error;

public class BussinessException extends Exception implements CommonError{

    private CommonError commonError;

    //直接接收EmBusinessError的传参用于构造业务异常
    public BussinessException(CommonError commonError){
        super();
        this.commonError = commonError;
    }

    //接收自定义errMsg的方式构造业务异常（通过覆盖原本errMsg）
    public BussinessException(CommonError commonError,String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }


    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String str) {
        this.commonError.setErrMsg(str);
        return this;
    }
}
