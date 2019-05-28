package com.erlangshen.common;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.VerifyUtils;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * http工具类
 * @author https://github.com/shuli495/erlangshen
 */
public class HttpHelper {

    /**
     * get请求
     * @param url
     * @return
     */
    public static String get(String url) {
        if(VerifyUtils.isEmpty(url)) {
            return "";
        }

        try {
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

            return result;
        } catch (Exception e) {
            throw new ThrowException(e.getMessage());
        }
    }

    /**
     * 读取request body
     * @param request
     * @return
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
