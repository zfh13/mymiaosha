package com.miaoshaproject.service;

import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {

     UserModel getUserById(Integer id);

     void register(UserModel userModel) throws BussinessException;

     UserModel validateLogin(String telphone,String encrptPassword) throws BussinessException;


    UserModel getUserByIdInCache(Integer userId);
}
