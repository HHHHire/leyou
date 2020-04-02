package com.mall.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @author: ddh
 * @data: 2020/3/31 23:00
 * @description
 */
@Service
public class GoodsHtmlService {
    private final GoodsService goodsService;
    private final TemplateEngine templateEngine;

    public GoodsHtmlService(GoodsService goodsService, TemplateEngine templateEngine) {
        this.goodsService = goodsService;
        this.templateEngine = templateEngine;
    }

    /**
     * 创建静态页面
     *
     * @param spuId spuId
     */
    public void createHtml(Long spuId) {
        // 初始化运行上下文
        Context context = new Context();
        // 设置数据模型
        context.setVariables(goodsService.loadData(spuId));
        PrintWriter printWriter = null;
        // 把静态文件生成到服务器本地(nginx的html目录下)
        File file = new File("F:\\Software\\nginx-1.16.1\\nginx-1.16.1\\html\\item\\" + spuId + ".html");
        try {
            printWriter = new PrintWriter(file);
            templateEngine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 删除静态页面
     *
     * @param id spuId
     */
    public void deleteHtml(Long id) {
    }
}

