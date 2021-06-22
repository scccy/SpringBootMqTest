package com.test.mqtest.webhook;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class webhook {

    @Autowired
     webhookDataTransform webhookDataTransform;

    @PostMapping("/webhook/")
    public JSONObject webhookGet(@RequestBody JSONObject param){
        return webhookDataTransform.getJson(param);
    }
}
