package com.mall.user.controller;

import com.mall.user.pojo.User;
import com.mall.user.service.UserService;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author: ddh
 * @data: 2020/4/2 1:54
 * @description
 */
@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 校验数据是否可以用
     *
     * @param data 数据
     * @param type 数据类型(1:用户名，2:手机号)
     * @return Boolean
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bool = userService.checkUser(data, type);
        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return R
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 注册用户
     *
     * @param user user 实体
     * @param code 验证码
     * @return R
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询用户
     *
     * @param username 用户名
     * @param password 密码
     * @return R
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }
}
