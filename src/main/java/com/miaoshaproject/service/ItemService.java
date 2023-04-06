package com.miaoshaproject.service;

import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.service.model.ItemModel;

import java.util.List;

public interface ItemService {

    ItemModel createItem(ItemModel itemModel) throws BussinessException;

    List<ItemModel> listItem();

    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId,Integer amount);

    //商品下单后对应销量增加
    void increaseSales(Integer itemId,Integer amount) throws BussinessException;

    String initStockLog(Integer itemId, Integer amount);

    ItemModel getItemByIdInCache(Integer itemId);
}
