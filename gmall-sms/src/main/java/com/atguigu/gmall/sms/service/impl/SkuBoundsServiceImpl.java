package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.sms.vo.SmsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionDao reductionDao;
    @Autowired
    private SkuLadderDao ladderDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void insertMoney(SmsVO smsVO) {
        //主要目的是将smsVO中的数据存入到数据库的三个表中：sms_sku_bounds、sms_sku_full_reduction、sms_sku_ladder
        //sms_sku_bounds
        SkuBoundsEntity bounds = new SkuBoundsEntity();
        BeanUtils.copyProperties(smsVO,bounds);
        List<String> work = smsVO.getWork();
        bounds.setWork(new Integer(work.get(0))+new Integer(work.get(1))*2+new Integer(work.get(2))*4+new Integer(work.get(3))*8);
        save(bounds);

        //sms_sku_full_reduction
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(smsVO,reductionEntity);
        reductionEntity.setAddOther(smsVO.getFullAddOther());
        reductionDao.insert(reductionEntity);

        //sms_sku_ladder
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(smsVO,ladderEntity);
        ladderEntity.setAddOther(smsVO.getLadderAddOther());
        //price是需要计算的
        ladderDao.insert(ladderEntity);
    }

}