package com.example.batch.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpClient {

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public String httpRequest(String url, String params, String method, String host, String auth, String contentType) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Host", host);
        conn.setRequestProperty("Authorization", auth);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("x-encrypt", "128");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);//设置连接主机超时（单位：毫秒）
        conn.setReadTimeout(15000);//设置从主机读取数据超时（单位：毫秒）
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params);
        wr.flush();
        wr.close();

//        int responseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) { response.append(inputLine); }

        in.close();
        return  response.toString();
    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public String httpsRequest(String url, String params, String method, String host, String auth, String contentType) throws Exception {

        URL obj = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

        conn.setRequestMethod(method);
        conn.setRequestProperty("Host", host);
        conn.setRequestProperty("Authorization", auth);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("x-encrypt", "128");
        conn.setDoOutput(true);
        conn.setConnectTimeout(2000);//设置连接主机超时（单位：毫秒）
        conn.setReadTimeout(100000);//设置从主机读取数据超时（单位：毫秒）
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) { response.append(inputLine); }

        in.close();
        return  response.toString();
    }

}