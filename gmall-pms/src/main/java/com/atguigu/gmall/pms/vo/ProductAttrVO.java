package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class ProductAttrVO extends ProductAttrValueEntity {
    public void setValueSelected(List<String> valueSelected){
        if(!CollectionUtils.isEmpty(valueSelected)){
            String join = StringUtils.join(valueSelected, ",");
            this.setAttrValue(join);
        }
    }
}
