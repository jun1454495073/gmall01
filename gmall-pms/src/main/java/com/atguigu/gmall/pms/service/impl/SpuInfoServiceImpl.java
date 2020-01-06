package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsFeign;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.ProductAttrVO;
import com.atguigu.gmall.pms.vo.SkusVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.sms.vo.SmsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private ProductAttrValueService attrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService imagesService;
    @Autowired
    private SkuSaleAttrValueService valueService;
    @Autowired
    private GmallSmsFeign smsFeign;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAllByCatId(QueryCondition condition, Long catId) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (catId!=0){
            wrapper.eq("catalog_id",catId);
        }
        String key = condition.getKey();
        if (StringUtils.isNotBlank(key)){
            wrapper.and(wrappe->wrappe.eq("id",key).or(wrapp->wrapp.like("spu_name",key)));
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper
        );
        return new PageVo(page);
    }

    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {
        //主要需要进行三部分的添加数据，一共是九张表
        //pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();

        //pms_spu_info_desc
        List<String> spuImages = spuInfoVO.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)){
            SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
            descEntity.setSpuId(spuId);
            descEntity.setDecript(StringUtils.join(spuImages,","));
            spuInfoDescDao.insert(descEntity);
        }

        //pms_product_attr_value，这个好像是可以直接批量插入
        List<ProductAttrVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> valueEntityList = baseAttrs.stream().map(AttrValueVO -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                BeanUtils.copyProperties(AttrValueVO, productAttrValueEntity);
                productAttrValueEntity.setSpuId(spuId);
                productAttrValueEntity.setAttrSort(0);
                productAttrValueEntity.setQuickShow(0);
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            attrValueService.saveBatch(valueEntityList);

        }

        //第二个加入有关sku的信息、这里都不可以批量插入
        //pms_sku_info、pms_sku_sale_attr_value、pms_sku_images
        List<SkusVO> skus = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skus)){
            return;
        }

        skus.forEach(sku -> {
            //pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            //默认图片是一个数组需要转换为字符串给对象赋值
            List<String> images = sku.getImages();
            if (!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg()==null?images.get(0):skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();

            //pms_sku_images
            if (!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> imagesEntities = images.stream().map(image -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setImgSort(0);
                    imagesEntity.setImgUrl(image);
                    imagesEntity.setDefaultImg(StringUtils.equals(image,skuInfoEntity.getSkuDefaultImg())?1:0);
                    return imagesEntity;
                }).collect(Collectors.toList());
                imagesService.saveBatch(imagesEntities);
            }

            //pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> saleAttrs = sku.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(skuSaleAttrValueEntity -> {
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    skuSaleAttrValueEntity.setAttrSort(0);
                });
                valueService.saveBatch(saleAttrs);
            }

            //第三个整理优惠的各种信息，需要去sms调用openfeign
            SmsVO smsVO = new SmsVO();
            BeanUtils.copyProperties(sku,smsVO);
            smsVO.setSkuId(skuId);
            smsFeign.insertMoney(smsVO);
        });


    }


}