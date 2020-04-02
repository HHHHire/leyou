package com.mall.goods.service;

import com.mall.goods.client.BrandClient;
import com.mall.goods.client.CategoryClient;
import com.mall.goods.client.GoodsClient;
import com.mall.goods.client.SpecificationClient;
import com.mall.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: ddh
 * @data: 2020/3/31 20:54
 * @description
 */
@Service
public class GoodsService {
    private final BrandClient brandClient;
    private final CategoryClient categoryClient;
    private final SpecificationClient specificationClient;
    private final GoodsClient goodsClient;

    public GoodsService(BrandClient brandClient, CategoryClient categoryClient, SpecificationClient specificationClient, GoodsClient goodsClient) {
        this.brandClient = brandClient;
        this.categoryClient = categoryClient;
        this.specificationClient = specificationClient;
        this.goodsClient = goodsClient;
    }

    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> model = new HashMap<>(7);

        // 根据spuId查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        // 根据spuId查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuId);

        // 查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.queryNamesByIds(cids);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }

        // 查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        // 查询skus
        List<Sku> skus = goodsClient.querySkusBySpuId(spuId);

        // 查询规格参数组
        List<SpecGroup> specGroups = specificationClient.queryGroupsWithParam(spu.getCid3());

        // 查询特殊的规格参数
        List<SpecParam> params = specificationClient.queryParam(null, spu.getCid3(), false, null);
        // 初始化特殊规格参数的map
        Map<Long, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });


        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("categories", categories);
        model.put("brand", brand);
        model.put("skus", skus);
        model.put("groups", specGroups);
        model.put("paramMap", paramMap);
        return model;
    }
}
