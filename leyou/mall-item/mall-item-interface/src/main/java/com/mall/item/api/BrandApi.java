package com.mall.item.api;

import com.mall.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: ddh
 * @data: 2020/3/30 0:36
 * @description
 */
@RequestMapping("brand")
public interface BrandApi {

    /**
     * 根据品牌 id 查询品牌
     *
     * @param id 品牌 id
     * @return R
     */
    @GetMapping("{id}")
    Brand queryBrandById(@PathVariable("id") Long id);
}
