package com.xdxiaodao.code.sync.utils;


import com.xdxiaodao.code.sync.bean.ApiResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class HttpUtil {

  //设置连接参数
  private static RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(500)
      .setSocketTimeout(30000)
      .setConnectTimeout(5000)
      .build();

  /**
   * @return 响应体的内容
   */
  public static String doGet(String url) throws ClientProtocolException, IOException {

    // 创建http GET请求
    HttpGet httpGet = new HttpGet(url);
    httpGet.setConfig(config);//设置请求参数
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = getHttpClient();
    try {
      // 执行请求
      response = httpClient.execute(httpGet);
      // 判断返回状态是否为200
      if (response.getStatusLine().getStatusCode() == 200) {
        String content = EntityUtils.toString(response.getEntity(), "UTF-8");
//                System.out.println("内容长度："+content.length());
        return content;
      }
    } finally {
      if (response != null) {
        response.close();
      }
      if (httpClient != null) {
        httpClient.close();
      }
    }
    return null;
  }

  /**
   * 带有参数的get请求
   */
  public static String doGet(String url, Map<String, String> params)
      throws URISyntaxException, ClientProtocolException, IOException {
    URIBuilder uriBuilder = new URIBuilder(url);
    if (params != null) {
      for (String key : params.keySet()) {
        uriBuilder.setParameter(key, params.get(key));
      }
    }//http://xxx?ss=ss
    return doGet(uriBuilder.build().toString());
  }

  /**
   * 带有参数的post请求
   */
  public static String doPost(String url, Map<String, String> params)
      throws ClientProtocolException, IOException {

    // 创建http POST请求
    HttpPost httpPost = new HttpPost(url);
    httpPost.setConfig(config);
    if (params != null) {

      // 设置2个post参数，一个是scope、一个是q
      List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);

      for (String key : params.keySet()) {
        parameters.add(new BasicNameValuePair(key, params.get(key)));
      }
      // 构造一个form表单式的实体
      UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
      // 将请求实体设置到httpPost对象中
      httpPost.setEntity(formEntity);
    }

    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = getHttpClient();
    try {
      // 执行请求

      response = httpClient.execute(httpPost);
      // 判断返回状态是否为200
      if (response.getStatusLine().getStatusCode() == 200) {
        String content = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println(content);
        return content;
      }
      return "";
    } finally {
      if (response != null) {
        response.close();
      }
      if (httpClient != null) {
        httpClient.close();
      }
    }
  }

  public static String doPostJson(String url, String json)
      throws ClientProtocolException, IOException {
    // 创建http POST请求
    HttpPost httpPost = new HttpPost(url);
    httpPost.setConfig(config);
    if (StringUtils.isNotBlank(json)) {
      //标识出传递的参数是 application/json
      StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
      httpPost.setEntity(stringEntity);
    }

    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = getHttpClient();
    try {
      // 执行请求
      response = httpClient.execute(httpPost);
      // 判断返回状态是否为200
      if (response.getStatusLine().getStatusCode() == 200) {
        String content = EntityUtils.toString(response.getEntity(), "UTF-8");
        content = StringUtils.trimToEmpty(content);
        ApiResponse apiResponse = JsonUtil.jsonToObject(content, ApiResponse.class);
        if (null != apiResponse && apiResponse.getCode() == 0) {
          return content;
        }
        System.out.println(content);
        return "";
      }
      return "";
    } finally {
      if (response != null) {
        response.close();
      }
      //httpClient.close();
    }
  }

  /**
   * 没有参数的post请求
   */
  public static String doPost(String url) throws ClientProtocolException, IOException {
    return doPost(url, null);
  }

  private static CloseableHttpClient getHttpClient() {
    return HttpClientBuilder.create().setMaxConnTotal(200)
        .setMaxConnPerRoute(100)
        .build();
  }

  public static void main(String[] args) {
    String url = "http://10.143.45.148:8080/tool/discover?serviceId=portal-dsp";

    try {
      String result = HttpUtil.doGet(url);
      System.out.println(result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
