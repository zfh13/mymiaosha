package com.miaoshaproject.service;

import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {

    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount,String stockLogId) throws BussinessException;

}
