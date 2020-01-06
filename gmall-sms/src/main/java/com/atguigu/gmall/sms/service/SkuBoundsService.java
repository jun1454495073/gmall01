package com.atguigu.gmall.sms.service;

import com.atguigu.sms.vo.SmsVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品sku积分设置
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-05 21:51:25
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void insertMoney(SmsVO smsVO);
}

