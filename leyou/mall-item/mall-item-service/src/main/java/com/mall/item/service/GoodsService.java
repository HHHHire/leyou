package com.mall.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.pojo.PageResult;
import com.mall.item.mapper.*;
import com.mall.item.pojo.*;
import com.mall.item.vo.SpuVO;
import org.apache.commons.lang.StringUtils;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ddh
 * @data: 2020/3/26 15:47
 * @description
 */
@Service
public class GoodsService {
    private final SpuMapper spuMapper;

    private final SpuDetailMapper spuDetailMapper;

    private final BrandMapper brandMapper;

    private final SkuMapper skuMapper;

    private final StockMapper stockMapper;

    private final CategoryService categoryService;

    public GoodsService(SpuMapper spuMapper, SpuDetailMapper spuDetailMapper, BrandMapper brandMapper, CategoryService categoryService, SkuMapper skuMapper, StockMapper stockMapper) {
        this.spuMapper = spuMapper;
        this.spuDetailMapper = spuDetailMapper;
        this.brandMapper = brandMapper;
        this.categoryService = categoryService;
        this.skuMapper = skuMapper;
        this.stockMapper = stockMapper;
    }


    public PageResult<SpuVO> querySpuByPage(String key, String saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 添加查询条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        // 添加上下架过滤条件
        if (StringUtils.isNotBlank(saleable)) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 添加分页
        PageHelper.startPage(page, rows);

        // 执行查询，获取spu集合
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        // spu转换成spuVO集合
        List<SpuVO> spuVOS = spus.stream().map(spu -> {
            SpuVO spuVO = new SpuVO();
            BeanUtils.copyProperties(spu, spuVO);

            // 查询分类名称
            List<String> names = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuVO.setCname(StringUtils.join(names, "-"));

            // 查询品牌
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuVO.setBname(brand.getName());

            return spuVO;
        }).collect(Collectors.toList());

        // 返回PageResult<spuVO>
        return new PageResult<>(spuPageInfo.getTotal(), spuVOS);
    }

    /**
     * 新增商品
     *
     * @param spuVO 商品实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(SpuVO spuVO) {
        // 先新增 spu
        spuVO.setId(null);
        spuVO.setSaleable(true);
        spuVO.setValid(true);
        spuVO.setCreateTime(new Date());
        spuVO.setLastUpdateTime(spuVO.getCreateTime());
        spuMapper.insertSelective(spuVO);

        // 再去新增 spuDetail
        SpuDetail spuDetail = spuVO.getSpuDetail();
        spuDetail.setSpuId(spuVO.getId());
        spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuVO);
    }

    private void saveSkuAndStock(SpuVO spuVO) {
        spuVO.getSkus().forEach(sku -> {
            // 新增 sku
            sku.setId(null);
            sku.setSpuId(spuVO.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            // 新增 stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);

        });
    }

    /**
     * 根据 spuId 查询 spuDetail
     *
     * @param spuId spuId
     * @return SpuDetail
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据 spuId 查询 sku 集合
     *
     * @param spuId spuId
     * @return R
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新商品
     * @param spuVO 商品实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuVO spuVO) {
        // 查询 spuId 查询要删除的 sku
        Sku record = new Sku();
        record.setSpuId(spuVO.getId());
        List<Sku> skus = skuMapper.select(record);

        // 删除 stock
        skus.forEach(sku -> {
            stockMapper.deleteByPrimaryKey(sku.getId());
        });

        // 删除 sku
        skuMapper.delete(record);

        // 新增 sku stock
        saveSkuAndStock(spuVO);

        // 更新 spu spuDetail
        spuVO.setCreateTime(null);
        spuVO.setLastUpdateTime(new Date());
        spuVO.setSaleable(null);
        spuVO.setValid(null);
        spuMapper.updateByPrimaryKeySelective(spuVO);
        spuDetailMapper.updateByPrimaryKeySelective(spuVO.getSpuDetail());
    }
}
