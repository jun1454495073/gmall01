package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
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

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrAttrgroupRelationService relationService;
    @Autowired
    private AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrGroupByCid(QueryCondition condition, Long cid) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id",cid)
        );
        return new PageVo(page);
    }

    @Override
    public AttrGroupVO queryAttrByGid(Long gid) {
        //先通过gid获取到attrid
        AttrGroupVO attrGroupVO = new AttrGroupVO();
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(attrGroupEntity,attrGroupVO);

        List<AttrAttrgroupRelationEntity> relationEntities = relationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        if (CollectionUtils.isEmpty(relationEntities)){
            return attrGroupVO;
        }
        attrGroupVO.setRelations(relationEntities);

        List<Long> collect = relationEntities.stream().map(relationEntity -> {
            return relationEntity.getAttrId();
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)){
            return attrGroupVO;
        }
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(collect);
        attrGroupVO.setAttrEntities(attrEntities);

        return attrGroupVO;
    }

    @Override
    public List<AttrGroupVO> queryAllGroupByCid(Long cid) {
        //通过cid求出gid
        List<AttrGroupEntity> list = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        List<AttrGroupVO> collect = list.stream().map(attrGroupEntity -> {
            AttrGroupVO attrGroupVO = new AttrGroupVO();
            AttrGroupVO attrGroupVO1 = this.queryAttrByGid(attrGroupEntity.getAttrGroupId());
            BeanUtils.copyProperties(attrGroupVO1, attrGroupVO);
            return attrGroupVO;
        }).collect(Collectors.toList());

        return collect;
    }

}