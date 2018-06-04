package com.erlangshen.sdk;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by wsl on 1/26 0026.
 */
public class Http {

    private String host;
    private String api;
    private Object params;
    private Map<String, Object> headers;
    private String ak;
    private String sk;

    public Http(String host, String api, Map<String, Object> headers, Object params, String ak, String sk) throws Exception {
        this.host = host;
        this.api = api;
        this.headers = headers;
        this.params = params;
        this.ak = ak;
        this.sk = sk;
    }

    public String post() throws Exception {
        return this.sendHttp("POST");
    }

    public String get() throws Exception {
        return this.sendHttp("GET");
    }

    public String put() throws Exception {
        return this.sendHttp("PUT");
    }

    public String delete() throws Exception {
        return this.sendHttp("DELETE");
    }

    private String sendHttp(String method) throws Exception {
        StringBuilder url = new StringBuilder(this.host).append(this.api);
        StringBuilder paramStr = new StringBuilder();
        if(null != this.params) {
            if("GET".equals(method)) {
                url.append("?");

                Map<String , Object> pars = (Map<String , Object>)this.params;
                for(String key : pars.keySet()) {
                    if(!url.toString().endsWith("?")) {
                        url.append("&");
                        paramStr.append("&");
                    }

                    url.append(key).append("=").append(pars.get(key));
                    paramStr.append(key).append("=").append(pars.get(key));
                }
            } else {
                paramStr.append(JSONObject.toJSONString(this.params));
            }
        }


        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url.toString());
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod(method);

            conn.setRequestProperty("Content-Type", "application/json");

            // 用 “请求方式:api_url?参数@body” 的格式hmacsha1加密
            String dataAes = SecretUtil.hmacsha1(this.sk, method + ":" + this.api + "@" + SecretUtil.md5(paramStr.toString()));
            String signature = SecretUtil.base64Encrypt(this.ak + "&" + dataAes);
            conn.setRequestProperty("signature", signature);

            if(null != this.headers) {
                for(String key : this.headers.keySet()) {
                    if("ak".equals(key)) {
                        continue;
                    }
                    conn.setRequestProperty(key, this.headers.get(key).toString());
                }
            }

            if("POST".equals(method) || "PUT".equals(method)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }

            if(!"GET".equals(method)) {
                out = new PrintWriter(conn.getOutputStream());

                out.print(paramStr);
                out.flush();
            }

            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            if(e.getMessage().indexOf("401") != -1) {
                throw new Exception("AK/SK错误");
            }
            e.printStackTrace();
        } finally {
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return result;
    }

}
