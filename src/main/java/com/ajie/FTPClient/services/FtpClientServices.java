package com.ajie.FTPClient.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @ClassName FtpClientServices
 * @Description
 * @Author septzhang
 * @Date 2022/2/13 23:17
 * @Version 1.0
 **/
public class FtpClientServices {
    /**
     * 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
     */
    CloseableHttpClient httpClient = HttpClients.createDefault();
    /**
     * 参数
     */
    StringBuilder params = new StringBuilder();

    /**
     * 响应模型
     */
    CloseableHttpResponse response = null;
    /**
     * 配置信息
     */
    RequestConfig requestConfig = RequestConfig.custom()
            /**
             * 设置连接超时时间(单位毫秒)
             */
            .setConnectTimeout(5000)
            /**
             * 设置请求超时时间(单位毫秒)
             */
            .setConnectionRequestTimeout(5000)
            /**
             * socket读写超时时间(单位毫秒)
             */
            .setSocketTimeout(5000)
            /**
             * 设置是否允许重定向(默认为true)
             */
            .setRedirectsEnabled(true).build();


    /**
     * 生成HttpGet
     *
     * @param fileUuid
     * @return
     */
    private HttpGet doGet(String type, String key, String fileUuid) {
        try {
            /**
             * 字符数据最好encoding以下;
             */
            params.append(key + "=").append(URLEncoder.encode(fileUuid, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return new HttpGet("http://localhost:8080/" + type + "?" + params);
    }

    /**
     * 上传文件方法
     *
     * @param filePath 本地文件路径
     * @param filePath
     */
    public boolean uploadFiles(String filePath) {
        /**
         * 创建Post请求
         */
        HttpPost httpPost = new HttpPost("http://localhost:8080/upload");
        try {
            File file = new File(filePath);

            /**
             * 创建基于文件的输入流
             */
            FileInputStream fis = new FileInputStream(file);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            /**
             * 解决返回中文乱码问题
             */
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            /**
             * 文件流
             */
            builder.addBinaryBody("file", fis, ContentType.MULTIPART_FORM_DATA, file.getName());
            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);

            /**
             * 将上面的配置信息 运用到这个Get请求里
             */
            httpPost.setConfig(requestConfig);
            /**
             * 执行(发送)Get请求
             */
            response = httpClient.execute(httpPost);
            /**
             * 每次使用完流，都应该进行关闭
             */
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            /**
             * 从响应模型中获取响应实体
             */
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                /**
                 * 返回状态码为200，上传成功
                 */
                String str = EntityUtils.toString(responseEntity);
                JSONObject jsonObject = JSONObject.parseObject(str);
                if ("200".equals(jsonObject.getString("code"))) {
                    return true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                /**
                 * 释放资源
                 */
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 查询response信息
     *
     * @param fileUuid 指定文件的UUID
     */
    private JSONObject getResponseInfo(String fileUuid) {
        /**
         * 创建Get请求
         */
        HttpGet httpGet = doGet("getInfo", "fileUUID", fileUuid);
        /**
         * 将上面的配置信息 运用到这个Get请求里
         */
        httpGet.setConfig(requestConfig);
        try {
            /**
             * 执行(发送)Get请求
             */
            response = httpClient.execute(httpGet);
            /**
             * 从响应模型中获取响应实体
             */
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                return entityString2JSONObject(EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                /**
                 * 释放资源
                 */
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 下载文件方法
     * @param fileUuid 指定文件的UUID
     * @param filePath  要存储文件的路径
     * @param fileName  文件的名字
     * @return
     */
    public Boolean downloadFiles(String fileUuid, String filePath,String fileName) {
        /**
         * 创建Get请求
         */
        HttpGet httpGet = doGet("download", "fileUUID", fileUuid);
        /**
         * 将上面的配置信息 运用到这个Get请求里
         */
        httpGet.setConfig(requestConfig);
        try {
            /**
             * 执行(发送)Get请求
             */
            response = httpClient.execute(httpGet);
            /**
             * 从响应模型中获取响应实体
             */
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                byte[] all = EntityUtils.toByteArray(responseEntity);
                /**
                 * 创建基于文件的输入流
                 */
                InputStream is = responseEntity.getContent();
                /**
                 * 创建File对象
                 */
                File file = new File(filePath + "/" + fileName);
                /**
                 * 文件路径不存在，则创建
                 */
                if (!file.exists() || !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    FileOutputStream fout = new FileOutputStream(file);
                    fout.write(all);
                    fout.flush();
                    fout.close();
                    return true;
                } finally {
                    // 关闭低层流。
                    is.close();
                }

            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                /**
                 * 释放资源
                 */
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取JSONObject对象
     *
     * @param entityString 返回的要转化成Json格式 的 String
     * @return JSONObject JSONObject对象
     */
    private JSONObject entityString2JSONObject(String entityString) {
        JSONObject resObject = JSONObject.parseObject(entityString);
        JSONObject fileObject = JSONObject.parseObject(resObject.getString("file"));
        if (null != fileObject) {
            return JSONObject.parseObject(resObject.getString("file"));
        }
        return null;
    }

    /**
     * 获取file信息
     *
     * @param fileUuid 指定文件的UUID
     * @return JSONObject JSONObject对象
     */
    public String getFileInfo(String fileUuid) {
        return getResponseInfo(fileUuid).toString();
    }


}
