package com.mall.goods.controller;

import com.mall.goods.service.GoodsHtmlService;
import com.mall.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author: ddh
 * @data: 2020/3/31 19:48
 * @description
 */
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model) {
        Map<String, Object> map = goodsService.loadData(id);
        model.addAllAttributes(map);
        goodsHtmlService.createHtml(id);
        return "item";
    }
}
