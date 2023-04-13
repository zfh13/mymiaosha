package com.miaoshaproject.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel implements Serializable {

    private Integer id;
    @NotNull(message = "用户名不能为空")
    private String name;
    @NotNull(message = "性别不能为空")
    private Byte gender;
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄必须大于0")
    @Max(value = 200, message = "年龄必须小于200")
    private Integer age;
    @NotNull(message = "手机号不能为空")
    private String telephone;

    private String registerMode;

    private String thirdPartyId;
    @NotNull(message = "密码不能为空")
    private String password;

}
