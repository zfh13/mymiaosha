package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVo;
import com.miaoshaproject.dataobject.error.BussinessException;
import com.miaoshaproject.dataobject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/user")
@CrossOrigin
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;


    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,@RequestParam(name = "password") String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(StringUtils.isBlank(telephone) || StringUtils.isBlank(password)) {
            return null;
        }
        UserModel userModel = userService.validateLogin(telephone,this.enCodeByMD5(password));
        UserVo userVo = convertToUserVo(userModel);
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-","");
        redisTemplate.opsForValue().set(uuid,userModel);
        redisTemplate.expire(uuid,1, TimeUnit.HOURS);
        //this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        //this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        System.out.println(this.httpServletRequest.getSession().getAttribute("IS_LOGIN"));

        return CommonReturnType.create(null);
    }





    @RequestMapping(value = "/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BussinessException {
        UserModel userModel = userService.getUserById(id);

        if(userModel == null) {
            throw new BussinessException(EmBusinessError.UNKNOW_ERROR);
        }

        UserVo userVo = convertToUserVo(userModel);
        return CommonReturnType.create(userVo);

    }

    @RequestMapping(value = "/getotp", method = RequestMethod.POST  ,consumes = CONTENT_TYPE_FORMED )
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telephone") String telephone) {
        Random random = new Random();
        int randomInt = random.nextInt(9999);
        int ran = randomInt + 10000;
        String otp = String.valueOf(ran);
        httpServletRequest.getSession().setAttribute(telephone,otp);

        System.out.println("telephone = " + telephone + "&optCode=" + otp);
        return CommonReturnType.create(null);

    }

    @RequestMapping(value="/register",method = RequestMethod.POST ,consumes = CONTENT_TYPE_FORMED)
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name="password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String targetotp = (String)httpServletRequest.getSession().getAttribute(telephone);
        if(!StringUtils.equals(targetotp,otpCode)) {
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        UserModel userModel = new UserModel();
        userModel.setPassword(enCodeByMD5(password));
        userModel.setName(name);
        userModel.setRegisterMode("Byphone");
        userModel.setAge(age);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    public String enCodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder encoder = Base64.getEncoder();
            // 加密字符串
            String newStr = encoder.encodeToString(md5.digest(str.getBytes("utf-8")));
            return newStr;
    }




    public UserVo convertToUserVo(UserModel userModel) {
        if(userModel == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userModel,userVo);
        return userVo;
    }






}
