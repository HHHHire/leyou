package com.mall.goods.client;

import com.mall.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: ddh
 * @data: 2020/3/30 0:45
 * @description
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
