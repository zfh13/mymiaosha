package com.miaoshaproject.response;

import lombok.Data;

@Data
public class CommonReturnType {

    private String status;

    private Object data;

    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status) {
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }





}
