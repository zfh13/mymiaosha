package com.miaoshaproject.controller;

import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.dataobject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;

    @Autowired
    private HttpServletRequest httpServletRequest;


    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "promoToken", required = false) String promoToken) throws BussinessException {

        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)) {
            return new BussinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单"));
        }
        UserModel userModel = (UserModel)redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BussinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }
        if (promoId != null) {
            String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + userModel.getId() + "_itemid_" + itemId);
            if (inRedisPromoToken == null)
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀令牌校验失败");
            if (!StringUtils.equals(promoToken, inRedisPromoToken))
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀令牌校验失败");
        }
        return CommonReturnType.create(null);
    }






}
