package com.website.common;

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
public class LoginPlaceReport {

    private static Thread thread;   // 0点后将列表中的线程添加值线程池
    private static ExecutorService executorService; // 线程池
    private static List<LoginPlaceReportThread> list;    //线程列表，保存当天超出最大qps后的线程

    private static AtomicInteger index;     // 当前使用量，用于计算最大调用数
    private static AtomicBoolean qpsSwitch; // 线程开关，用于控制qps

    public LoginPlaceReport(TokenVO token, String userId, String clientId, String beforeIp, String loginIp) {
        if(null == list) {
            list = new LinkedList<>();
        }
        if(null == executorService) {
            executorService = Executors.newCachedThreadPool();
        }
        if(null == this.qpsSwitch) {
            this.qpsSwitch = new AtomicBoolean(false);
        }
        if(null == this.index) {
            this.index = new AtomicInteger(0);
        }

        //
        LoginPlaceReportThread checkLoginPlace = new LoginPlaceReportThread(token, userId, clientId, beforeIp, loginIp);

        // 最大调用数
        String qpsMaxStr = Setting.getProperty("bdmap.qps.max");
        double qpsMaxNum = 0;
        if(VerifyUtils.isNotEmpty(qpsMaxStr)) {
            // 0.05%做缓存，防止24点边界数据溢出
            qpsMaxNum = Integer.valueOf(qpsMaxStr) * 0.95;
        }

        // 未超过接口每日最大调用限制，直接放入线程池
        if(qpsMaxNum < 2 || index.get() <= qpsMaxNum) {
            LoginPlaceReport.index.addAndGet(2);
            executorService.execute(checkLoginPlace);
        } else {
            // 超出最大限制，存入列表
            list.add(checkLoginPlace);

            //24:05将队列中的线程放入线程池
            if(null == thread || !thread.isAlive()) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR, 24);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 5);
                cal.set(Calendar.MILLISECOND, 0);

                thread = new AddToThread(cal.getTimeInMillis() - System.currentTimeMillis());
            }
        }
    }

    /**
     * 24点后将列表中的线程添加值线程池
     */
    class AddToThread extends Thread {

        private long sleepTime; // 休眠时间，当前时间到24点的毫秒数；到达接口最大限制，等待24点后执行

        public AddToThread(long sleepTime) {
            this.sleepTime = sleepTime;
        }

        public void run() {
            if(sleepTime > 0) {
                try {
                    Thread.sleep(this.sleepTime);
                } catch (InterruptedException e) {
                    throw new ThrowException("线程休眠异常："+ e.getMessage());
                }
            }

            // 重置索引
            LoginPlaceReport.index.set(0);

            // 从头部将线程放入线程池
            if(!LoginPlaceReport.list.isEmpty()) {
                LoginPlaceReportThread thread = LoginPlaceReport.list.remove(0);
                if(null != thread) {
                    LoginPlaceReport.index.addAndGet(2);
                    LoginPlaceReport.executorService.execute(thread);
                }
            }
        }
    }

    /**
     * 异地登录告警线程
     */
    class LoginPlaceReportThread extends Thread {

        @Autowired
        private UserService userService;
        @Autowired
        private TokenService tokenService;
        @Autowired
        private ClientSecurityService clientSecurityService;
        @Autowired
        private ClientPhoneService clientPhoneService;
        @Autowired
        private ClientMailService clientMailService;

        private TokenVO token;
        private String userId;
        private String clientId;
        private String beforeIp;
        private String loginIp;

        public LoginPlaceReportThread(TokenVO token, String userId, String clientId, String beforeIp, String loginIp) {
            this.userId = userId;
            this.clientId = clientId;
            this.beforeIp = beforeIp;
            this.loginIp = loginIp;
            this.token = token;
        }

        public void run() {
            ClientSecurityVO clientSecurityVO = clientSecurityService.baseFind(this.clientId);

            try {
                // 根据qps间隔调用
                String qps = Setting.getProperty("bdmap.qps");
                int qpsNum = 0;
                if(VerifyUtils.isNotEmpty(qps)) {
                    qpsNum = Integer.valueOf(qps);
                }
                while (qpsNum > 0 && LoginPlaceReport.qpsSwitch.compareAndSet(true, true)) {
                    Thread.sleep(1000/qpsNum/2);
                }

                // 调用接口开关，开启，防止多线程超出qps
                LoginPlaceReport.qpsSwitch.set(true);

                // 登录ip对应的地址
                JSONObject oldIp = null;
                JSONObject newIp = null;
                try {
                    oldIp = LoginPlaceReport.getAddress(this.beforeIp);
                } catch (Exception e) {
                    LoginPlaceReport.index.decrementAndGet();
                    throw new ThrowException(e.getMessage());
                }
                try {
                    newIp = LoginPlaceReport.getAddress(this.loginIp);
                } catch (Exception e) {
                    LoginPlaceReport.index.decrementAndGet();
                    throw new ThrowException(e.getMessage());
                }

                // 调用完成，关闭开关
                LoginPlaceReport.qpsSwitch.set(false);

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
                    userService.sendPhone(token, clientPhoneVO.getType(), userId, null, null);
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
                    userService.sendMail(token, clientMailVO.getType(), userId, null, null, null);
                }
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
            throw new ThrowException("查询ip地址异常："+ e.getMessage());
        }
    }

}
