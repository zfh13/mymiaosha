package com.miaoshaproject.service;

import com.miaoshaproject.service.model.PromoModel;

public interface PromoService {

    PromoModel getPromoById(Integer itemId);

    String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId);

    void publicPromo(Integer promoId);
}
