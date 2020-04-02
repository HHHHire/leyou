package com.mall.item.controller;

import com.mall.item.pojo.SpecGroup;
import com.mall.item.pojo.SpecParam;
import com.mall.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: ddh
 * @data: 2020/3/26 13:42
 * @description
 */
@Controller
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类 id 查询参数组
     *
     * @param cid 分类 id
     * @return ResponseEntity
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据条件查询规格参数
     *
     * @param gid       分组 id
     * @param cid       分类 id
     * @param generic   是否通用参数
     * @param searching 是否用于搜索
     * @return ResponseEntity<List < SpecParam>>
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    ) {
        List<SpecParam> params = specificationService.queryParam(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 查询参数组和值(规格参数)
     *
     * @param cid 分类 id
     * @return R
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long cid) {
        List<SpecGroup> specGroups = specificationService.queryGroupsWithParam(cid);
        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);
    }
}
