package com.mall.search.repository;

import com.mall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: ddh
 * @data: 2020/3/30 12:20
 * @description
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
