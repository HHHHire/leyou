package com.mall.goods.listener;

import com.mall.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author: ddh
 * @data: 2020/4/1 22:25
 * @description
 */
@Component
public class GoodsListener {

    private final GoodsHtmlService goodsHtmlService;

    public GoodsListener(GoodsHtmlService goodsHtmlService) {
        this.goodsHtmlService = goodsHtmlService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.ITEM.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MALL.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) {
        if (id == null) {
            return;
        }
        goodsHtmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.ITEM.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MALL.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        goodsHtmlService.deleteHtml(id);
    }
}
