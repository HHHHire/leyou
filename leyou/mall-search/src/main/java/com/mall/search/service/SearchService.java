package com.mall.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.pojo.PageResult;
import com.mall.item.pojo.*;
import com.mall.search.client.BrandClient;
import com.mall.search.client.CategoryClient;
import com.mall.search.client.GoodsClient;
import com.mall.search.client.SpecificationClient;
import com.mall.search.pojo.Goods;
import com.mall.search.pojo.SearchRequest;
import com.mall.search.pojo.SearchResult;
import com.mall.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: ddh
 * @data: 2020/3/30 9:27
 * @description
 */
@Service
public class SearchService {
    private final CategoryClient categoryClient;
    private final GoodsClient goodsClient;
    private final SpecificationClient specificationClient;
    private final BrandClient brandClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GoodsRepository goodsRepository;

    public SearchService(CategoryClient categoryClient, GoodsClient goodsClient, SpecificationClient specificationClient, BrandClient brandClient, GoodsRepository goodsRepository) {
        this.categoryClient = categoryClient;
        this.goodsClient = goodsClient;
        this.specificationClient = specificationClient;
        this.brandClient = brandClient;
        this.goodsRepository = goodsRepository;
    }

    /**
     * 查询
     *
     * @param request 查询参数
     * @return SearchResult
     */
    public SearchResult search(SearchRequest request) {
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }
        // 自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加查询条件
//        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        // 添加分页，分页页码从0开始
        queryBuilder.withPageable(PageRequest.of(request.getPage() - 1, request.getSize()));
        // 添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        // 添加分类和品牌的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 执行查询，获取结果集
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        // 获取品牌和分类聚合结果集并解析
        List<Map<String, Object>> categories = getCategoryAggResult(search.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(search.getAggregation(brandAggName));

        List<Map<String, Object>> specs = null;
        // 判断是否是一个分类，只有一个分类时才做规格参数聚合
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            // 对规格参数进行聚合
            specs = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }

        return new SearchResult(search.getTotalElements(), search.getTotalPages(), search.getContent(), categories, brands, specs);
    }

    /**
     * 构建布尔查询
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 给布尔查询添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // 添加过滤条件
        // 获取用户选择的过滤信息
        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else {
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件聚合规格参数
     *
     * @param id         分类 id
     * @param basicQuery 基本查询条件
     * @return List<Map < String, Object>>
     */
    private List<Map<String, Object>> getParamAggResult(Long id, QueryBuilder basicQuery) {
        // 自定义查询对象构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本查询条件
        queryBuilder.withQuery(basicQuery);
        // 查询要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParam(null, id, null, true);
        // 添加规格参数的聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        // 添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 执行聚合查询
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        List<Map<String, Object>> spesc = new ArrayList<>();
        // 解析聚合结果集，key-聚合名称(规格参数名) value-聚合对象
        Map<String, Aggregation> aggregationMap = search.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            // 初始化一个map {k:规格参数名 options: 聚合的规格参数值}
            Map<String, Object> map = new HashMap<>();
            map.put("k", entry.getKey());
            // 初始化一个 options 的集合，收集桶的值
            List<String> options = new ArrayList<>();
            // 获取聚合
            StringTerms value = (StringTerms) entry.getValue();
            // 获取桶的集合
            value.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options", options);
            spesc.add(map);
        }
        return spesc;
    }

    /**
     * 解析品牌聚合的数据
     *
     * @param aggregation 聚合结果
     * @return List<Brand>
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;

        // 获取聚合中的桶
        return terms.getBuckets().stream().map(bucket -> {
            return brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    /**
     * 解析分类聚合的数据
     *
     * @param aggregation 聚合结果
     * @return List<Map < String, Object>>
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;

        // 获取桶的集合，转化成List<Map<String,Object>>
        return terms.getBuckets().stream().map(bucket -> {
            // 初始化一个map
            HashMap<String, Object> map = new HashMap<>();
            // 获取桶中的分类id
            Long id = bucket.getKeyAsNumber().longValue();
            // 根据分类id查询分类名称
            List<String> names = categoryClient.queryNamesByIds(Collections.singletonList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        // 查询商品分类名称
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 根据品牌id查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        // 根据spuId查询所有的sku
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        // 初始化一个价格集合，收集所有sku的价格
        List<Long> prices = new ArrayList<>();
        // 收集sku的必要字段信息
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            // 获取sku中的图片，数据库中的图片可能是多张，以，分割，取第一张图片
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuMapList.add(map);
        });
        // 根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = specificationClient.queryParam(null, spu.getCid3(), null, true);
        // 根据spuId查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        // 把通用的规格参数值，进行反序列化
        Map<String, Object> genericSpecMap = objectMapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 把特殊的规格参数值，进行反序列化
        Map<String, List<Object>> specialSpecMap = objectMapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });
        HashMap<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            // 判断参数规格的类型，是否是通用的规格参数
            if (param.getGeneric()) {
                // 如果是通用类型，从genericSpecMap中获取规格参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                // 判断是否是数值类型，如果是，则返回一个区间
                if (param.getNumeric()) {
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(), value);
            } else {
                // 如果是特殊规格参数，从specialSpecMap中获取规格参数值
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(), value);
            }
        });

        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + StringUtils.join(names, " ") + " " + brand.getName());
        goods.setPrice(prices);
        goods.setSkus(objectMapper.writeValueAsString(skuMapList));
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


}
