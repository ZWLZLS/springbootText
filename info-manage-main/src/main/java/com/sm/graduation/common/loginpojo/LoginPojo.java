package com.sm.graduation.common.loginpojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//自动生成get,set方法
@NoArgsConstructor//自动生成无参构造器
@AllArgsConstructor//自动生成全参构造器
public class LoginPojo {
    private String username;
    private String password;
    private String captcha;
    private Integer power;
}
