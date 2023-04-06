package com.miaoshaproject.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miaoshaproject.dao.StockLogMapper;
import com.miaoshaproject.dataobject.StockLog;
import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.model.ItemModel;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqProducer {

    private DefaultMQProducer producer;

    private TransactionMQProducer transactionMQProducer;

    @Value("${mq.nameserver.addr}")
    private String nameAddr;

    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockLogMapper stockLogMapper;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();

        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                Integer itemId = (Integer)((Map)o).get("itemId");
                Integer promoId = (Integer) ((Map)o).get("promoId");
                Integer userId = (Integer) ((Map)o).get("userId");
                Integer amount = (Integer) ((Map)o).get("amount");
                String stockLogId = (String) ((Map)o).get("stockLogId");
                try {
                    orderService.createOrder(userId, itemId, promoId, amount);
                } catch (BussinessException e) {
                    throw new RuntimeException(e);
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String jsonString  = new String(messageExt.getBody());
                Map<String,Object> map = JSONObject.parseObject(jsonString,Map.class);
                Integer itemId = (Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");
                String stockLogId = (String) map.get("stockLogId");
                StockLog stockLogDO = stockLogMapper.selectByPrimaryKey(stockLogId);
                if(stockLogDO == null){
                    return LocalTransactionState.UNKNOW;
                }
                if(stockLogDO.getStatus().intValue() == 2){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else if(stockLogDO.getStatus().intValue() == 1){
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;

            }
        });


    }

    public boolean transactionAsyncReduceStock(Integer userId,Integer itemId,Integer promoId,Integer amount,String stockLogId) {
        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId",itemId);
        bodyMap.put("amount",amount);

        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("itemId",itemId);
        argsMap.put("amount",amount);
        argsMap.put("userId",userId);
        argsMap.put("promoId",promoId);
        argsMap.put("stockLogId",stockLogId);
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + itemId);
        itemModel.setStock(itemModel.getStock() - amount);
        itemModel.setSales(itemModel.getSales() + amount);
        redisTemplate.opsForValue().set("item_" + itemId, itemModel);

        Message message = new Message(topicName,"increase", JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        TransactionSendResult sendResult = null;
        try{
            sendResult = transactionMQProducer.sendMessageInTransaction(message,argsMap);
        } catch (MQClientException e) {
            throw new RuntimeException(e);

        }
        if(sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
            return true;
        } else if (sendResult.getLocalTransactionState() == LocalTransactionState.UNKNOW){
            return false;
        } else {
            return false;
        }
    }












    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId",itemId);
        bodyMap.put("amount",amount);
        Message message = new Message(topicName,"increase", JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        try{
            producer.send(message);
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
