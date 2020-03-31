package com.mall.item.service;

import com.mall.item.mapper.CategoryMapper;
import com.mall.item.pojo.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: ddh
 * @date: 2019/10/4  19:07
 * @description:
 */
@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 根据父节点的 id 查询子节点
     *
     * @param pid 父节点 id
     * @return List<Category>
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }

    /**
     * 通过分类 id 查询
     *
     * @param ids 分类 id
     * @return List<String>
     */
    public List<String> queryNameByIds(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }

}

