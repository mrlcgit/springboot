package com.ml.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

/**
 * 模拟一个Http请求
 */
public class UtilHttp {

	private static final int connectTimeout = 1000 * 60; // 连接超时时间60s
	private static final int socketTimeout = 1000 * 60; // 读取数据超时时间60s

	/**
	 * 向指定URL发送GET方法的请求
	 * <p>
	 * <p>
	 * static int timeout = 10 * 1000;// 以秒为单位 static String host =
	 * "117.177.243.6";//117.177.243.7 static int port = 80;
	 */
	public static String get(String apiurl, String host, String port) {
		int ports = Integer.valueOf(port);
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(apiurl);

			// 创建代理服务器
			InetSocketAddress addr = new InetSocketAddress(host, ports);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理

			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection(proxy);

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;");
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(socketTimeout);

			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送 GET请求
	 *
	 * @param strUrl        发送请求的 URL
	 * @param requestParams 请求参数
	 * @return 远程资源的响应结果
	 */
	public static String sendGet(String strUrl, String requestParams) {
		String responseParams = "";
		BufferedReader bufferedReader = null;
		try {
			String strRequestUrl = strUrl + "?" + requestParams;
			URL url = new URL(strRequestUrl);
			URLConnection urlConnection = url.openConnection(); // 打开与 URL 之间的连接

			// 设置通用的请求属性
			urlConnection.setRequestProperty("accept", "*/*");
			urlConnection.setRequestProperty("connection", "Keep-Alive");
			urlConnection.setRequestProperty("user-agent",
					"Mozilla/4.0  (compatible;  MSIE  6.0;  Windows  NT  5.1;SV1)");

			urlConnection.connect(); // 建立连接

			// 使用BufferedReader输入流来读取URL的响应
			bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String strLine;
			while ((strLine = bufferedReader.readLine()) != null) {
				responseParams += strLine;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return responseParams;
	}

	/**
	 * HttpClientPost 方式，向指定 URL 发送 POST请求
	 * 
	 * @param strUrl        发送请求的 URL
	 * @param requestParams 请求参数
	 * @param cookies       格式为
	 *                      "JSESSIONID=4A3998E6FCA477D878BFF99C26FB1608;user=aa;id=123"
	 * @return 远程资源的响应结果
	 */
	public static String doPost(String strUrl, List<BasicNameValuePair> requestParams, String cookies) {

		String responseParams = "";
		StringBuffer stringBuffer = new StringBuffer();
		long startTime = 0, endTime = 0;

		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout).build(); // 设置请求和传输超时时间

		HttpPost httpPost = new HttpPost(strUrl);

		if (StringUtils.hasText(cookies)) {
			httpPost.addHeader("Cookie", cookies);
		}

		httpPost.setConfig(requestConfig);
		HttpEntity httpEntity;

		try {
			if (requestParams != null) {
				// 设置相关参数
				httpEntity = new UrlEncodedFormEntity(requestParams, "UTF-8");
				httpPost.setEntity(httpEntity);

			}
			startTime = System.nanoTime();
			CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
			int code = closeableHttpResponse.getStatusLine().getStatusCode();

			if (code == 200 || code == 500) {
				try {
					httpEntity = closeableHttpResponse.getEntity();
					if (httpEntity != null) {
						long length = httpEntity.getContentLength();
						// 当返回值长度较小的时候，使用工具类读取
						if (length != -1 && length < 2048) {
							stringBuffer.append(EntityUtils.toString(httpEntity));
						} else { // 否则使用IO流来读取
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(httpEntity.getContent(), "UTF-8"));
							String line;
							while ((line = bufferedReader.readLine()) != null) {
								stringBuffer.append(line);
							}
							bufferedReader.close();
							responseParams = stringBuffer.toString();
						}
						endTime = System.nanoTime();
					}
				} catch (Exception e) {
					endTime = System.nanoTime();

				} finally {
					closeableHttpResponse.close();
				}
			} else {
				endTime = System.nanoTime();
				httpPost.abort();
			}
		} catch (IOException e) {
			endTime = System.nanoTime();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
			}
		}
		return responseParams;
	}

}
