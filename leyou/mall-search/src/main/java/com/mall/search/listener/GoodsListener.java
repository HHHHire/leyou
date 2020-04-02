package com.mall.search.listener;

import com.mall.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: ddh
 * @data: 2020/4/1 22:46
 * @description rabbitmq 消费者监听
 */
@Component
public class GoodsListener {

    private final SearchService searchService;

    public GoodsListener(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 监听创建或者更新索引
     *
     * @param id spuId
     * @throws IOException e
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.SEARCH.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MALL.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) throws IOException {
        if (id == null) {
            return;
        }
        searchService.save(id);
    }

    /**
     * 监听删除索引
     *
     * @param id spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.SEARCH.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MALL.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        searchService.delete(id);
    }
}
