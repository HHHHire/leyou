package com.mall.user.service;

import com.mall.common.utils.NumberUtils;
import com.mall.user.mapper.UserMapper;
import com.mall.user.pojo.User;
import com.mall.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: ddh
 * @data: 2020/4/2 1:55
 * @description
 */
@Service
public class UserService {
    private static final String KEY_PREFIX = "user:verify:";
    private final UserMapper userMapper;
    private final AmqpTemplate amqpTemplate;
    private final StringRedisTemplate redisTemplate;

    public UserService(UserMapper userMapper, AmqpTemplate amqpTemplate, StringRedisTemplate redisTemplate) {
        this.userMapper = userMapper;
        this.amqpTemplate = amqpTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 校验数据是否可以用
     *
     * @param data 数据
     * @param type 数据类型(1:用户名，2:手机号)
     * @return Boolean
     */
    public Boolean checkUser(String data, Integer type) {
        User record = new User();

        if (type == 1) {
            record.setUsername(data);
        } else if (type == 2) {
            record.setPhone(data);
        } else {
            return null;
        }

        return userMapper.selectCount(record) == 0;
    }

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     */
    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }

        // 生成验证码
        String code = NumberUtils.generateCode(6);

        // 发送消息到rabbitmq
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("verifycode.sms", msg);

        // 将验证码存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    /**
     * 注册用户
     *
     * @param user user
     * @param code 短信验证码
     */
    public void register(User user, String code) {
        // 查询redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        // 校验验证码
        if (!StringUtils.equals(redisCode, code)) {
            return;
        }

        // 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        // 加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        // 新增用户
        user.setId(null);
        user.setCreated(new Date());
        userMapper.insertSelective(user);

        // 删除redis中的验证码
        redisTemplate.delete(KEY_PREFIX + user.getPhone());
    }

    /**
     * 查询用户
     *
     * @param username 用户名
     * @param password 密码
     * @return User
     */
    public User queryUser(String username, String password) {
        // 通过用户名查询用户
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        if (user == null) {
            return null;
        }

        // 若不为空，对用户输入的密码进行加盐加密
        password = CodecUtils.md5Hex(password, user.getSalt());

        // 和数据库中的密码比较
        if (StringUtils.equals(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
