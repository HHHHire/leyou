package com.mall.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author: ddh
 * @data: 2020/4/2 15:28
 * @description
 */
@ConfigurationProperties(prefix = "mall.sms")
public class SmsProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    private String verifyCodeTemplate;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getVerifyCodeTemplate() {
        return verifyCodeTemplate;
    }

    public void setVerifyCodeTemplate(String verifyCodeTemplate) {
        this.verifyCodeTemplate = verifyCodeTemplate;
    }
}
