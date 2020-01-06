package com.atguigu.gmall.pms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.sms.api.GmallSmsApi;
import com.atguigu.sms.vo.SmsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("sms-service")
public interface GmallSmsFeign extends GmallSmsApi{

}
