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

	// ����httpclient����
	public static HttpClient httpClient = new DefaultHttpClient();

	/**
	 * @param url
	 *            ���������URL
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public static String getRequest(String url) throws Exception {
		// ����httpget����
		HttpGet get = new HttpGet(url);
		// ����GET����
		HttpResponse httpResponse = httpClient.execute(get);

		// ����������ɹ�������Ӧ
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// ��ȡ��������Ӧ�ַ���
			String result = EntityUtils.toString(httpResponse.getEntity());

			Log.e("Service_Data_Bean_get", result);
			return result;
		}
		return null;
	}

	public static String postRequst(String url, Map<String, String> rawParams) {
		try {
			// ����httpPost����
			HttpPost post = new HttpPost(url);

			// ������ݵĲ��������Ƚ϶࣬���ԶԴ��ݵĲ������з�װ
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// ��װ�������
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
				System.out.println("�������ֵ��" + rawParams.get(key) + "���keyֵ��"
						+ key);
			}

			// �����������
			post.setEntity(new UrlEncodedFormEntity(params, "GBK"));
			post.addHeader("Content-Type", "application/json");
			
            // ����ʱ
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
            // ��ȡ��ʱ
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
			// ����post����
			HttpResponse httpResponse = httpClient.execute(post);
			// ����������ɹ��ط�����Ӧ
			if (httpResponse.getStatusLine().getStatusCode() == 200) { // ��ȡ��������Ӧ�ַ���
				String result = EntityUtils.toString(httpResponse.getEntity());
				System.out.println("HttpUtil�л�ȡ�����ַ���Ϊ��" + result);
				post.abort();
				return result;
			} else {
				System.out.println("HttpUtil�л�ȡ�����ַ���Ϊ��Ϊ��");
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
	 * ��ȡָ��·���������ݡ�
	 * 
	 */
	public static byte[] getImage(String urlpath) throws Exception {
		// urlpath=URLEncoder.encode(urlpath,"UTF-8");
		System.out.println("HttpUtilת��֮ǰ��" + urlpath);
		urlpath = urlpath.substring(0, urlpath.lastIndexOf("/") + 1)
				+ URLEncoder.encode(
						urlpath.substring(urlpath.lastIndexOf("/"),
								urlpath.length()), "UTF-8");
		System.out.println("HttpUtilת��֮��" + urlpath);
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		// �𳬹�10�롣
		if (conn.getResponseCode() == 200) {

			InputStream inputStream = conn.getInputStream();
			return readStream(inputStream);
		}
		return null;
	}

	/**
	 * ��ȡ���� ������
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
