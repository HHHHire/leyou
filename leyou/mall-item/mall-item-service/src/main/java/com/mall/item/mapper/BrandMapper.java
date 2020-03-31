package com.mall.item.mapper;

import com.mall.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: ddh
 * @Date: 2019/10/4 20:46
 * @Description:
 */
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 插入到中间表
     *
     * @param bid 品牌 id
     * @param cid 分类 id
     */
    @Insert("insert into tb_category_brand (category_id, brand_id) values(#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("bid") Long bid, @Param("cid") Long cid);

    /**
     * 根据分类 id 查询品牌列表
     *
     * @param cid 分类 id
     * @return List<Brand>
     */
    @Select("select * from tb_brand b inner join tb_category_brand c on b.id = c.brand_id where c.category_id = #{cid}")
    List<Brand> selectBrandsByCid(@Param("cid") Long cid);
}
