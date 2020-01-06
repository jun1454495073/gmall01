package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkusVO extends SkuInfoEntity {
    //sms中SuBoundsEntity有关于成长积分的事情
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<String> work;

    //sms中SkuFullReductionEntity有关于满多少减多少事情
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;//原来是addOther

    //sms中SkuLadderEntity有关于满多少件打多少折的事情
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;//原来是addOther
    //    private BigDecimal price;由服务器计算得到

    private List<String> images;
    private List<SkuSaleAttrValueEntity> saleAttrs;

}
