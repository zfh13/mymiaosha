package com.miaoshaproject.service.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderModel {

    //要用string
    private String id;

    private Integer userId;

    private Integer itemId;

    private BigDecimal itemPrice;

    private Integer amount;

    private BigDecimal orderPrice;

    //若非空，表示以秒杀方式下单
    private Integer promoId;



}
