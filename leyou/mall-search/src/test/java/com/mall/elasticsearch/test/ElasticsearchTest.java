package com.mall.elasticsearch.test;

import com.mall.common.pojo.PageResult;
import com.mall.item.vo.SpuVO;
import com.mall.search.client.GoodsClient;
import com.mall.search.pojo.Goods;
import com.mall.search.repository.GoodsRepository;
import com.mall.search.service.SearchService;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ddh
 * @data: 2020/3/30 12:20
 * @description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void test() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;
        do {
            // 分页查询spu，获取分页结果集
            PageResult<SpuVO> result = goodsClient.querySpuByPage(null, null, page, rows);
            // 获取当前页的数据
            List<SpuVO> items = result.getItems();
            // 处理List<SpuVO> ==> List<Goods>
            List<Goods> goodList = items.stream().map(spuVO -> {
                try {
                    return searchService.buildGoods(spuVO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            //执行新增数据的方法
            goodsRepository.saveAll(goodList);
            rows=items.size();
            page++;
        } while (rows == 100);
    }
}
