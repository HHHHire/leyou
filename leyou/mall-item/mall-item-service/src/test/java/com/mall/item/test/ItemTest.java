package com.mall.item.test;

import com.mall.item.mapper.SpecParamMapper;
import com.mall.item.pojo.SpecParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: ddh
 * @data: 2020/3/30 17:27
 * @description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemTest {
    @Autowired
    private SpecParamMapper paramMapper;

    @Test
    public void test() {
        SpecParam specParam = paramMapper.selectByPrimaryKey(1L);
        System.out.println(specParam);
    }
}
