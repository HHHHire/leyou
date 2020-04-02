package com.mall.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.mall.sms.config.SmsProperties;
import com.mall.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author: ddh
 * @data: 2020/4/2 16:25
 * @description
 */
@Component
public class SmsListener {
    private final SmsUtils smsUtils;
    private final SmsProperties properties;

    public SmsListener(SmsUtils smsUtils, SmsProperties properties) {
        this.smsUtils = smsUtils;
        this.properties = properties;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.SMS.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MALL.SMS.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"verifycode.sms"}
    ))
    public void sendMss(Map<String, String> msg) throws ClientException {
        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(code)) {
            smsUtils.sendSms(phone, code, properties.getSignName(), properties.getVerifyCodeTemplate());
        }
    }
}
