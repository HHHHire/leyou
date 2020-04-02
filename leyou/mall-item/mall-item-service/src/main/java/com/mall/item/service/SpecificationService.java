package com.mall.item.service;

import com.mall.item.mapper.SpecGroupMapper;
import com.mall.item.mapper.SpecParamMapper;
import com.mall.item.pojo.SpecGroup;
import com.mall.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: ddh
 * @data: 2020/3/26 13:41
 * @description
 */
@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    /**
     * 根据分类 id 查询参数组
     *
     * @param cid 分类 id
     * @return List<SpecGroup>
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return groupMapper.select(specGroup);
    }

    /**
     * 根据条件查询规格参数
     *
     * @param gid       分组 id
     * @param cid       分类 id
     * @param generic   是否通用参数
     * @param searching 是否用于搜索
     * @return List <SpecParam>
     */
    public List<SpecParam> queryParam(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return paramMapper.select(specParam);
    }

    /**
     * 查询参数组和值(规格参数)
     *
     * @param cid 分类 id
     * @return List<SpecGroup>
     */
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> specGroups = queryGroupsByCid(cid);
        specGroups.forEach(specGroup -> {
            List<SpecParam> params = queryParam(specGroup.getId(), null, null, null);
            specGroup.setParams(params);
        });
        return specGroups;
    }
}
