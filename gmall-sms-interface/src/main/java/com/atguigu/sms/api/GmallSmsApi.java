package com.atguigu.sms.api;


import com.atguigu.core.bean.Resp;
import com.atguigu.sms.vo.SmsVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GmallSmsApi {

    @PostMapping("sms/skubounds/saveSms")
    public Resp<Object> insertMoney(@RequestBody SmsVO smsVO);

}
