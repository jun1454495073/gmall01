package com.atguigu.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SmsVO {
    private Long skuId;
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
}
