package com.mall.goods.client;

import com.mall.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: ddh
 * @data: 2020/3/30 0:46
 * @description
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
