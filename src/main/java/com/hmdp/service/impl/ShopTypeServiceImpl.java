package com.hmdp.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONString;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ShopType> queryTypeList() {
        // 1.查询redis中是否存在
        List<String> shopTypeJson = stringRedisTemplate.opsForList().range("cache:shoptype:", 0, -1);
        // 2.如果存在直接调用redis中的数据
        if (!shopTypeJson.isEmpty()) {
            List<ShopType> typeList = new ArrayList<>();
            for (String shopType : shopTypeJson) {
                typeList = JSONUtil.toList(shopType, ShopType.class);
            }
            log.info("ShopType存在Redis直接调用:{}", typeList);
            return typeList;
        }
        // 3.如果不存在则查询数据库
        List<ShopType> typeList =
                query().orderByAsc("sort").list();
        // 4.将查询到的数据存入redis中
        stringRedisTemplate.opsForList().rightPush("cache:shoptype:", JSONUtil.toJsonStr(typeList));        // 5.返回数据
        log.info("ShopType不存在查询数据库:{}", typeList);
        return typeList;
    }
}
