package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.ItemVo;
import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.CacheService;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/item")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController extends BaseController{


    @Autowired
    ItemService itemService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl,
                                       @RequestParam(name = "description") String description) throws BussinessException {
        // 封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        itemModel.setDescription(description);
//
        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVo itemVO = this.convertVOFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    //商品详情页浏览
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){

        ItemModel itemModel = null;
        itemModel = (ItemModel) cacheService.getFromCommonCache("item_" + id);
        if(itemModel == null) {
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);
            if(itemModel == null) {
                itemModel = itemService.getItemById(id);
                redisTemplate.opsForValue().set("item_" + id, itemModel);
                redisTemplate.expire("item_" + id, 10, TimeUnit.MINUTES);
            }

        }
        cacheService.setCommonCache("item_"+id, itemModel);



        ItemVo itemVo = this.convertVOFromModel(itemModel);

        return CommonReturnType.create(itemVo);
    }

    private ItemVo convertVOFromModel(ItemModel itemModel) {
        if(itemModel == null) {
            return null;
        }
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);
        return itemVo;

    }



}
