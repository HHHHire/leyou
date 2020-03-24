package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: ddh
 * @Date: 2019/10/4 20:46
 * @Description:
 */
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 插入到中间表
     * @param bid 品牌 id
     * @param cid 分类 id
     */
    @Insert("insert into tb_category_brand (category_id, brand_id) values(#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("bid") Long bid, @Param("cid") Long cid);
}
