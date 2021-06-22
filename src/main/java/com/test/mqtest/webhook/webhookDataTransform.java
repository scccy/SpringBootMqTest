package com.test.mqtest.webhook;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shici.mqsdk.ProxyHost;
import com.shici.mqsdk.model.Httpheader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Component
public class webhookDataTransform {
    private static Logger logger = LoggerFactory.getLogger(webhookDataTransform.class);

    @Autowired
    ProxyHost proxyHost;

    public JSONObject getJson(JSONObject param) {
        JSONObject resultJson = new JSONObject();
        String from = param.getString("from");
        try {
            if ("warning".equals(from)) {

                String waringMeg = param.getJSONObject("data").get("abstractDesc").toString();
                JSONArray receiverList = param.getJSONArray("receiverList");

                for (int i = 0; i < receiverList.size(); i++) {
                    String phone = receiverList.getJSONObject(i).get("phone").toString();
                    JSONObject sendJSON = new JSONObject();
                    sendJSON.put("phone", phone);
                    sendJSON.put("meg", waringMeg);
                    Object result = sendMessage(sendJSON);
                    if (result.equals("success")){
                        System.out.println("成功");
                    }else{
                        resultJson.put("code",500);
                        resultJson.put("msg",result);
                        return resultJson;
                    }
                }

            }else{
                resultJson.put("code",500);
                resultJson.put("msg","不是warning预警");
                return resultJson;
            }
        } catch (Exception e) {
            resultJson.put("code",500);
            resultJson.put("msg",e);
            return resultJson;
        }
        resultJson.put("code",200);
        resultJson.put("msg","消息发送成功");
        return resultJson;
    }


//    客户银行发送短信方法
    public Object sendMessage(JSONObject jsonParam) {

        String phone = jsonParam.getString("phone");
        String meg = jsonParam.getString("meg");
        String source = "0026";
        String sourceName = "企业网银系统";
        String bsnMsgId = source + getBsnMsgId();
        System.out.println(bsnMsgId);

        try {

            proxyHost.setServers("110.110.011.11:119");
            Httpheader head = new Httpheader();

            // 自定义head
            head.setTopic("TOPIC_MSGC_TRANS");
            head.setKeys(bsnMsgId);


            // body里req head
            JSONObject httpBody = new JSONObject();
            JSONObject reqHead = new JSONObject();
            reqHead.put("source", source);
            reqHead.put("sourceName", sourceName);
            reqHead.put("transCode", "QYWY0004");
            reqHead.put("bsnType", "YZ01");
            reqHead.put("version", "1.0");
            reqHead.put("msgCount", 1);
            httpBody.put("reqHead", reqHead);


            JSONArray reqBody = new JSONArray();
            JSONObject jsonEle1 = new JSONObject();

            jsonEle1.put("bsnMsgId", bsnMsgId);
            jsonEle1.put("openChannel", "10000");
            jsonEle1.put("mobile", phone);
            jsonEle1.put("sendType", "0");
            jsonEle1.put("smsContent", meg);
            reqBody.add(jsonEle1);

            httpBody.put("reqBody", reqBody);
            System.out.printf("开始发送");
            System.out.println(httpBody);
            for (int i = 0; i < 1; i++) {
////                内部
            String resultJson = proxyHost.nsproxySend(httpBody, head);
            System.out.println("result is " + resultJson);
            return "success";
            }
        } catch (Exception e) {
            logger.info("send error: " + e.toString());
            e.printStackTrace();
            return e;
        }
        return "success";
    }

//  获取bsnMsgId yyyyMMdd+八位随机数
    public static String getBsnMsgId() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Random random = new Random();
        String dateTime = df.format(new Date());
        String randomStr = String.valueOf(random.nextLong()).substring(1, 8 + 1);
        return dateTime + randomStr;
    }
}




