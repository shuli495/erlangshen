package com.website.common;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;

/**
 * 短信发送
 * @author https://github.com/shuli495/erlangshen
 */
public class PhoneSender {
    /**
     * 发送短信
     * @param ak    阿里云ak
     * @param sk    阿里云sk
     * @param phoneNum  手机号码
     * @param signName  阿里云短信服务签名
     * @param tmplateCode   阿里云短信服务模板
     * @param params    发送参数
     */
    public void send(String ak, String sk, String phoneNum, String signName, String tmplateCode, String params) {
        try {
            final String product = "Dysmsapi";
            final String domain = "dysmsapi.aliyuncs.com";
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ak, sk);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            request.setPhoneNumbers(phoneNum);
            request.setSignName(signName);
            request.setTemplateCode(tmplateCode);
            request.setTemplateParam(params);

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if(null == sendSmsResponse.getCode() || !sendSmsResponse.getCode().equals("OK")) {
                throw new ThrowException(sendSmsResponse.getCode()+"("+sendSmsResponse.getMessage()+")", "911001");
            }
        } catch (Exception e) {
            if(e.getMessage().indexOf("isv.BUSINESS_LIMIT_CONTROL") != -1) {
                String level = "";
                String maxNum = "";
                try {
                    String[] text = e.getMessage().split("触发")[1].split("级");
                    level = text[0];
                    maxNum = text[1].split(":")[1].split("\\)")[0];
                } catch (Exception e1) {
                    throw new ThrowPrompt("短信发送失败：超出流量控制，请稍后再试！", "911002");
                }
                throw new ThrowPrompt("每个手机号码每" + level + "最多发送" + maxNum + "条短信");
            }
            throw new ThrowException("短信发送失败：" + e.getMessage(), "911003");
        }
    }
}
