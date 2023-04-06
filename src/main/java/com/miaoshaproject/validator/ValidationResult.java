package com.miaoshaproject.validator;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.Map;



public class ValidationResult {

    @Setter
    private boolean hasError;

    @Getter
    @Setter
    private Map<String,String> errorMsgMap = new HashMap<>();


    public boolean hasErrors() {
        return hasError;
    }

    public String getErrMsg() {
        return StringUtils.join(errorMsgMap.values().toArray(), ',');
    }







}
