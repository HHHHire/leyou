package com.mall.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: ddh
 * @date: 2019/10/4  19:15
 * @description:
 */
@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据分类 id 查询分类名称
     *
     * @param ids 分类 id
     * @return R
     */
    @GetMapping
    List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}
