package com.mall.search.client;

import com.mall.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: ddh
 * @data: 2020/3/30 0:32
 * @description
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
