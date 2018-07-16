package org.janusgraph.core.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by liutingna on 2018/7/16.
 *
 * @author liutingna
 */
public class HttpUtil {
    public static String handleHttpMethod(String url, String requestMethod, NameValuePair[] nameValuePairs) {
        String encoding = "utf-8";
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String body = null;
        try {
            HttpRequestBase httpRequestBase = null;
            if (StringUtils.isEmpty(requestMethod) || StringUtils.equalsIgnoreCase(requestMethod, "get")) {
                StringBuffer buffer = new StringBuffer();
                if(nameValuePairs!=null) {
                    for (NameValuePair nameValuePair : nameValuePairs) {
                        buffer.append(nameValuePair.getName());
                        buffer.append("=");
                        buffer.append(nameValuePair.getValue());
                        buffer.append("&");
                    }
                }
                String paramStr = buffer.length()>0?buffer.toString().substring(0, buffer.length() - 1):buffer.toString();
                String postUrl = "";
                if (StringUtils.endsWith(url, "/")) {
                    postUrl = url.substring(0, url.length() - 1) + "?" + paramStr;
                } else {
                    postUrl = url + "?" + paramStr;
                }
                HttpGet httpGet = new HttpGet(postUrl);
                httpRequestBase = httpGet;
            } else if (StringUtils.equalsIgnoreCase(requestMethod, "post")) {
                //创建post方式请求对象
                HttpPost httpPost = new HttpPost(url);
//                BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                Map<String, Object> postParam = new HashMap<>();
                for (NameValuePair nameValuePair : nameValuePairs) {
                    postParam.put(nameValuePair.getName(), nameValuePair.getValue());
                }
                String postJsonStr = JSON.toJSONString(postParam);
                StringEntity se = new StringEntity( postJsonStr,"UTF-8");
                httpPost.setEntity(se);
                //设置header信息
                //指定报文头【Content-type】、【User-Agent】
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                httpRequestBase = httpPost;
            }
            //执行请求操作，并拿到结果（异步请求）
            final HttpRequestBase requestBase = httpRequestBase;
            Callable<CloseableHttpResponse> callable = () -> {
                CloseableHttpResponse httpResponse = closeableHttpClient.execute(requestBase);
                return httpResponse;
            };
            FutureTask<CloseableHttpResponse> futureTask = new FutureTask(callable);
            futureTask.run();
            response = futureTask.get(5000, TimeUnit.MILLISECONDS);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            System.err.println("Http服务调用失败，原因：{}"+e);
        } finally {
            //释放链接
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    System.err.println("关闭HttpClient错误，原因：{}"+ e);
                }
            }
        }
        return body;
    }
}
