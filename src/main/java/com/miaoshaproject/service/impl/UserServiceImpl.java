package com.miaoshaproject.service.impl;

import com.miaoshaproject.controller.viewobject.UserVo;
import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.dataobject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDo = userDOMapper.selectByPrimaryKey(id);
        if(userDo == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDo.getId());

        return convertFromData(userDo,userPasswordDO);

    }


    @Transactional
    @Override
    public void register(UserModel userModel) throws BussinessException {

        UserDO userDo = convertFromData(userModel);
        try{
            userDOMapper.insertSelective(userDo);
        } catch (DuplicateKeyException e) {
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已被注册！");
        }
        userModel.setId(userDo.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);



    }

    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BussinessException {
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        if(userDO == null) {
            throw new BussinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromData(userDO,userPasswordDO);
        //拿到用户信息内加密的密码是否和传输的是否相匹配
        if(!StringUtils.equals(encrptPassword,userModel.getPassword())){
            throw new BussinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;

    }

    @Override
    public UserModel getUserByIdInCache(Integer userId) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_validate"+userId);
        if(userModel == null) {
            userModel = this.getUserById(userId);
            redisTemplate.opsForValue().set("user_validate"+userId,userId);
            redisTemplate.expire("user_validate"+userId,10, TimeUnit.MINUTES);
        }
        return userModel;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setPassword(userModel.getPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    public UserModel convertFromData(UserDO userDO, UserPasswordDO userPasswordDO) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        userModel.setPassword(userPasswordDO.getPassword());
        return userModel;

    }

    private UserDO convertFromData(UserModel userModel) {
        if(userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }
}
