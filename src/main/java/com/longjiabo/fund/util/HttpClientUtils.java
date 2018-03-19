package com.longjiabo.fund.util;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;


public class HttpClientUtils {
    private static final Logger log = LoggerFactory
            .getLogger(HttpClientUtils.class);

    public static InputStream get(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        if (BaseUtils.needProxy()) {
            HttpHost proxy = new HttpHost("cn-proxy.jp.oracle.com", 80, "http");
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            get.setConfig(config);
        }
        get.setHeader("Pragma", "no-cache");
        get.setHeader("Cache-Control", "no-cache");
        get.setHeader("Content-type", "text/html; charset=utf-8");
        get.setHeader("Accept", "text/html");
        get.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
        try {
            CloseableHttpResponse st = client.execute(get);
            if (st.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return st.getEntity().getContent();
            }
        } catch (IOException e) {
            log.error("", e);
        }
        return null;

    }

    public static String uncompress(byte[] str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
        return out.toString();
    }
}
