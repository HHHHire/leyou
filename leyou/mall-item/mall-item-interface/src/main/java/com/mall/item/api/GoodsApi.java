package com.mall.item.api;

import com.mall.common.pojo.PageResult;
import com.mall.item.pojo.Sku;
import com.mall.item.pojo.SpuDetail;
import com.mall.item.vo.SpuVO;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: ddh
 * @data: 2020/3/26 15:48
 * @description
 */
public interface GoodsApi {

    /**
     * 根据 spuId 查询 spuDetail
     * @param spuId spuId
     * @return R
     */
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据 spuId 查询 sku 集合
     * @param spuId spuId
     * @return R
     */
    @GetMapping("sku/list")
    List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据条件分页查询 spu
     *
     * @param key      关键字
     * @param saleable 是否上下架
     * @param page     页数
     * @param rows     行数
     * @return ResponseEntity<PageResult < SpuVO>>
     */
    @GetMapping("spu/page")
    PageResult<SpuVO> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) String saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );
}
