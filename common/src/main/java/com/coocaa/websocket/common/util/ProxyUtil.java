package com.coocaa.websocket.common.util;

import com.google.common.net.HttpHeaders;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 反向代理功能 待完善 cookie未解决
 */
public class ProxyUtil {

    public static Request create(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String targetUrl) throws IOException {
        String requestQueryString = httpServletRequest.getQueryString();
        targetUrl = StringUtils.isBlank(requestQueryString) ? targetUrl : targetUrl + "?" + requestQueryString;
        Request httpRequest = null;

        if (httpServletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null
                && httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE) != null
                || httpServletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {

            final String originalBody = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            final RequestBody requestBody = RequestBody.create(MediaType.parse(httpServletRequest.getContentType()), originalBody);

            //获取头部
            Enumeration<String> headerNameEnum = httpServletRequest.getHeaderNames();
            Map headerMap = new HashMap<>();
            while (headerNameEnum.hasMoreElements()) {
                String headerLine = headerNameEnum.nextElement();
                headerMap.put(headerNameEnum, httpServletRequest.getHeader(headerLine));
            }

            httpRequest = new Request.Builder().method(httpServletRequest.getMethod(), requestBody)
                    .url(targetUrl)
                    .headers(Headers.of(headerMap))
                    .build();
        } else {
            httpRequest = new Request.Builder().url(targetUrl)
                    //.headers(Headers.of(HttpHeader.HttpHandle()))
                    .build();
        }
        //发起请求
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Response response = okHttpClient.newCall(httpRequest).execute();
        byte[] responseByte = response.body().bytes();

        //返回报文
        httpServletResponse.addHeader("Content-Type", response.header("Content-Type"));
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getOutputStream().write(responseByte);
        return httpRequest;
    }
}
