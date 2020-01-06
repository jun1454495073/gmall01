package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrByCid(QueryCondition condition, Long cid,Integer type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id",cid);
        if (type!=null){
            wrapper.eq("attr_type",type);
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(condition),
                wrapper
        );
        return new PageVo(page);
    }

    @Override
    public void saveAttrVO(AttrVO attrVO) {
        //将属性信息加入数据库中，不仅仅需要加入到attr表中，还需要加入到中间表中
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO,attrEntity);
        this.save(attrEntity);
        //然后在中间表中添加数据
        Long attrId = attrEntity.getAttrId();
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attrId);
        relationEntity.setAttrSort(0);
        relationEntity.setAttrGroupId(attrVO.getAttrGroupId());
        attrAttrgroupRelationDao.insert(relationEntity);

    }

}