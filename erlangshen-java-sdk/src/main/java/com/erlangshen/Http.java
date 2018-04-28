package com.erlangshen;

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

    private String api;
    private Map<String, Object> params;
    private Map<String, Object> headers;
    private String sk;

    public Http(String host, String api, Map<String, Object> headers, Map<String, Object> params, String sk) {
        this.api = host + api;
        this.headers = headers;
        this.params = params;
        this.sk = sk;
    }

    public String post() {
        String url = this.api;
        return this.sendHttp(url, "POST");
    }

    public String get() {
        StringBuffer url = new StringBuffer(this.api);

        if(this.params.size() != 0) {
            url.append("?");
        }

        for(String key : this.params.keySet()) {
            if(!url.toString().endsWith("?")) {
                url.append("&");
            }

            url.append(key).append("=").append(this.params.get(key));
        }

        return this.sendHttp(url.toString(), "GET");
    }

    public String put() {
        String url = this.api;

        return this.sendHttp(url, "PUT");
    }

    public String delete() {
        String url = this.api;

        return this.sendHttp(url, "DELETE");
    }

    private String sendHttp(String url, String method) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            for(String key : this.headers.keySet()) {
                conn.setRequestProperty(key, this.headers.get(key).toString());
            }

            if("POST".equals(method) || "PUT".equals(method)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }

            StringBuffer paramJson = new StringBuffer();
            if(!"GET".equals(method) && this.params.size() != 0) {
                out = new PrintWriter(conn.getOutputStream());

                paramJson.append("{");
                for(String key : this.params.keySet()) {
                    if(paramJson.toString().endsWith("}")) {
                        paramJson.append(",");
                    }

                    paramJson.append("\"").append(key).append("\":")
                            .append("\"").append(this.params.get(key)).append("\"");
                }
                paramJson.append("}");

                out.print(paramJson.toString());
                out.flush();
            }

            // 用 “请求方式:api_url?参数@body” 的格式hmacsha1加密
            SecretUtil.hmacsha1(this.sk, method + ":" + url + "@" + SecretUtil.md5(paramJson.toString()));
            conn.setRequestProperty("Content-Type", "application/json");

            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
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
