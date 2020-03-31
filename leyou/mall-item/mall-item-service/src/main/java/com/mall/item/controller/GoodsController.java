package com.mall.item.controller;

import com.mall.common.pojo.PageResult;
import com.mall.item.pojo.Sku;
import com.mall.item.pojo.SpuDetail;
import com.mall.item.service.GoodsService;
import com.mall.item.vo.SpuVO;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: ddh
 * @data: 2020/3/26 15:48
 * @description
 */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

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
    public ResponseEntity<PageResult<SpuVO>> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) String saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ) {
        PageResult<SpuVO> result = goodsService.querySpuByPage(key, saleable, page, rows);
        if (result == null || CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     * @param spuVO 参数实体类
     * @return R
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuVO spuVO) {
        goodsService.saveGoods(spuVO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品
     * @param spuVO 商品实体
     * @return R
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuVO spuVO) {
        goodsService.updateGoods(spuVO);
        return ResponseEntity.status(HttpStatus.CONTINUE).build();
    }

    /**
     * 根据 spuId 查询 spuDetail
     * @param spuId spuId
     * @return R
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = goodsService.querySpuDetailBySpuId(spuId);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据 spuId 查询 sku 集合
     * @param spuId spuId
     * @return R
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long spuId) {
        List<Sku> skus = goodsService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }
}
