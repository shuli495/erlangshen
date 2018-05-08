package com.website.Executor;

import com.alibaba.fastjson.JSONObject;
import com.fastjavaframework.Setting;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.VerifyUtils;
import com.website.model.vo.ClientMailVO;
import com.website.model.vo.ClientPhoneVO;
import com.website.model.vo.ClientSecurityVO;
import com.website.model.vo.TokenVO;
import com.website.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异地登录告警
 */
public class LoginPlaceReport extends AbstractQpsControl {

    private String userId;
    private String clientId;
    private String beforeIp;
    private String loginIp;

    @Autowired
    private UserService userService;
    @Autowired
    private ClientSecurityService clientSecurityService;
    @Autowired
    private ClientPhoneService clientPhoneService;
    @Autowired
    private ClientMailService clientMailService;

    public LoginPlaceReport(String userId, String clientId, String beforeIp, String loginIp) {
        super("bdmap");

        this.userId = userId;
        this.clientId = clientId;
        this.beforeIp = beforeIp;
        this.loginIp = loginIp;

        super.exe();
    }

    @Override
    public void run() {
        ClientSecurityVO clientSecurityVO = clientSecurityService.baseFind(this.clientId);

        try {
            // 根据qps间隔调用
            String qps = Setting.getProperty("bdmap.qps");
            int qpsNum = 0;
            if(VerifyUtils.isNotEmpty(qps)) {
                qpsNum = Integer.valueOf(qps);
            }
            while (qpsNum > 0 && super.getQpsSwitch().compareAndSet(true, true)) {
                Thread.sleep(1000/qpsNum/2);
            }

            // 调用接口开关，开启，防止多线程超出qps
            super.getQpsSwitch().set(true);

            // 登录ip对应的地址
            JSONObject oldIp = null;
            JSONObject newIp = null;
            try {
                oldIp = LoginPlaceReport.getAddress(this.beforeIp);
            } catch (Exception e) {
                super.getIndex().decrementAndGet();
                throw new ThrowException(e.getMessage());
            }
            try {
                newIp = LoginPlaceReport.getAddress(this.loginIp);
            } catch (Exception e) {
                super.getIndex().decrementAndGet();
                throw new ThrowException(e.getMessage());
            }

            // 调用完成，关闭开关
            super.getQpsSwitch().set(false);

            // 与上次登录地址不同
            if(!oldIp.getJSONObject("content").getJSONObject("address_detail").getString("city")
                    .equals(newIp.getJSONObject("content").getJSONObject("address_detail").getString("city"))) {
                // 全部通知
                if(clientSecurityVO.getCheckPlacePriority() == 0) {
                    // 短信通知
                    this.sendByPhone(clientSecurityVO.getCheckPlacePhoneTypeId());
                    // 邮件通知
                    this.sendByMail(clientSecurityVO.getCheckPlaceMailTypeId());

                } else if(clientSecurityVO.getCheckPlacePriority() == 1) {
                    // 手机优先
                    try {
                        this.sendByPhone(clientSecurityVO.getCheckPlacePhoneTypeId());
                    } catch (Exception e) {
                        this.sendByMail(clientSecurityVO.getCheckPlaceMailTypeId());
                    }
                } else {
                    // 邮箱优先
                    try {
                        this.sendByMail(clientSecurityVO.getCheckPlaceMailTypeId());
                    } catch (Exception e) {
                        this.sendByPhone(clientSecurityVO.getCheckPlacePhoneTypeId());
                    }
                }
            }
        } catch (Exception e){}

    }

    /**
     * 短信通知
     * @param clientPhoneId
     */
    private void sendByPhone(Integer clientPhoneId) {
        if(VerifyUtils.isNotEmpty(clientPhoneId)) {
            ClientPhoneVO clientPhoneVO = clientPhoneService.baseFind(clientPhoneId);
            if(null != clientPhoneVO) {
                userService.sendPhone(null, clientPhoneVO.getType(), userId, null, null);
            }
        }
    }

    /**
     * 邮件通知
     * @param clientMailId
     */
    private void sendByMail(Integer clientMailId) {
        if(VerifyUtils.isNotEmpty(clientMailId)) {
            ClientMailVO clientMailVO = clientMailService.baseFind(clientMailId);
            if(null != clientMailVO) {
                userService.sendMail(null, clientMailVO.getType(), userId, null, null, null);
            }
        }
    }

    /**
     * 根据ip获取地址
     * @param ip
     * @return
     * @throws Exception
     */
    public static JSONObject getAddress(String ip) throws Exception {
        try {
            String url = String.format(Setting.getProperty("bdmap.api.ip"), ip, String.format(Setting.getProperty("bdmap.ak")));
            URL realUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),"utf-8"));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            JSONObject ipObj = JSONObject.parseObject(result);
            if(ipObj.getInteger("status") == 0) {
                return ipObj;
            } else {
                throw new ThrowException(result);
            }
        } catch (Exception e) {
            throw new ThrowException("查询ip地址异常："+ e.getMessage(), "821001");
        }
    }

}
