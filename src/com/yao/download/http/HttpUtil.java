package com.yao.download.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {

	// 创建httpclient对象
	public static HttpClient httpClient = new DefaultHttpClient();

	/**
	 * @param url
	 *            发送请求的URL
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String getRequest(String url) throws Exception {
		// 创建httpget对象
		HttpGet get = new HttpGet(url);
		// 发送GET请求
		HttpResponse httpResponse = httpClient.execute(get);

		// 如果服务器成功返回响应
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// 获取服务器响应字符串
			String result = EntityUtils.toString(httpResponse.getEntity());

			Log.e("Service_Data_Bean_get", result);
			return result;
		}
		return null;
	}

	public static String postRequst(String url, Map<String, String> rawParams) {
		try {
			// 创建httpPost对象
			HttpPost post = new HttpPost(url);

			// 如果传递的参数个数比较多，可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
				System.out.println("输出参数值：" + rawParams.get(key) + "输出key值："
						+ key);
			}

			// 设置请求参数
			post.setEntity(new UrlEncodedFormEntity(params, "GBK"));
			post.addHeader("Content-Type", "application/json");
			
            // 请求超时
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
            // 读取超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
			// 发送post请求
			HttpResponse httpResponse = httpClient.execute(post);
			// 如果服务器成功地返回响应
			if (httpResponse.getStatusLine().getStatusCode() == 200) { // 获取服务器响应字符串
				String result = EntityUtils.toString(httpResponse.getEntity());
				System.out.println("HttpUtil中获取到的字符串为：" + result);
				post.abort();
				return result;
			} else {
				System.out.println("HttpUtil中获取到的字符串为：为空");
			}
			return null;
			// "ERROR";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取指定路径，的数据。
	 * 
	 */
	public static byte[] getImage(String urlpath) throws Exception {
		// urlpath=URLEncoder.encode(urlpath,"UTF-8");
		System.out.println("HttpUtil转码之前：" + urlpath);
		urlpath = urlpath.substring(0, urlpath.lastIndexOf("/") + 1)
				+ URLEncoder.encode(
						urlpath.substring(urlpath.lastIndexOf("/"),
								urlpath.length()), "UTF-8");
		System.out.println("HttpUtil转码之后：" + urlpath);
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		// 别超过10秒。
		if (conn.getResponseCode() == 200) {

			InputStream inputStream = conn.getInputStream();
			return readStream(inputStream);
		}
		return null;
	}

	/**
	 * 读取数据 输入流
	 * 
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		inStream.close();

		return outstream.toByteArray();
	}

}
