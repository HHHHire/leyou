package com.mall.item.api;

import com.mall.item.pojo.SpecGroup;
import com.mall.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("spec")
public interface SpecificationApi {

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
    List<SpecParam> queryParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    );

    /**
     * 查询参数组和值(规格参数)
     *
     * @param cid 分类 id
     * @return R
     */
    @GetMapping("group/param/{cid}")
    List<SpecGroup> queryGroupsWithParam(@PathVariable("cid") Long cid);
}
