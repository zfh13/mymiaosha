package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.OrderDOMapper;
import com.miaoshaproject.dao.SequenceDOMapper;
import com.miaoshaproject.dao.StockLogMapper;
import com.miaoshaproject.dataobject.OrderDO;
import com.miaoshaproject.dataobject.SequenceDO;
import com.miaoshaproject.dataobject.StockLog;
import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.dataobject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private StockLogMapper stockLogMapper;

    @Override
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount,String stockLogId) throws BussinessException {
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null) {
            throw new BussinessException((EmBusinessError.PARAMETER_VALIDATION_ERROR),"商品不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if(userId == null) {
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户不存在");
        }
        if(amount <= 0 || amount > 99){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        if(promoId != null) {
            // 1. 校验对应活动是否存在对应商品
            if(promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            } else if(itemModel.getPromoModel().getStatus().intValue() != 2){
                // 2。校验活动是否在进行中
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动未开始");
            }
        }
        //2.落单减库存，
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result) {
            throw new BussinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);

        orderDOMapper.insertSelective(orderDO);
        //商品销量增加
        itemService.increaseSales(itemId,amount);

        StockLog stockLog = stockLogMapper.selectByPrimaryKey(stockLogId);
        if(stockLog == null) {
            throw new BussinessException(EmBusinessError.UNKNOW_ERROR);
        }
        stockLog.setStatus(2);
        stockLogMapper.updateByPrimaryKeySelective(stockLog);

        //返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo() {

        StringBuilder sb = new StringBuilder();
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = localDateTime.format(DateTimeFormatter.ISO_DATE).replace("-","");
        sb.append(date);
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6 - sequenceStr.length(); i++) {
            sb.append(0);
        }
        sb.append(sequenceStr);

        //3.最后两位分库分表位
        sb.append("00");
        return sb.toString();


    }

    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        return orderDO;
    }
}
