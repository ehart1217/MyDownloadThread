package com.yao.download.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileDownload_Web {

	private Context context;

	public FileDownload_Web(Context context) {
		super();
		this.context = context;
	}

	// 获取文件流
	private InputStream getInputStream(String urlStr) {
		InputStream is = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// conn.setRequestMethod("POST");
			conn.setConnectTimeout(5000);
			conn.connect();
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	// 获取网页文本内容
	public String getWebText(String urlStr) {
		InputStream is = getInputStream(urlStr);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = "";
		try {
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("TAG", "流文件读写错误");
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	// 下载文件
	private void downloader(InputStream is, String path, String filename) {
		String filepath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			filepath = Environment.getExternalStorageDirectory() + path + "/";
		} else {
			Log.e("WEB_ERROR", "SDCard异常，请检查SDCard是否安装正确！");
			Toast.makeText(context.getApplicationContext(),
					"SDCard异常，请检查SDCard是否安装正确！", Toast.LENGTH_LONG).show();
		}
		if (!filepathExist(filepath)) {
			createFilepath(filepath);
		}
		if (!fileExist(filepath + filename)) {
			createFile(is, filepath, filename);
		}
	}

	// 判断文件路径是否存在
	private boolean filepathExist(String filepath) {
		File file = new File(filepath);
		return file.exists();
	}

	// 创建文件路径
	private void createFilepath(String filepath) {
		File file = new File(filepath);
		file.mkdirs();
	}

	// 判断文件是否存在
	private boolean fileExist(String filename) {
		return filepathExist(filename);
	}

	// 创建文件
	private void createFile(InputStream is, String filepath, String filename) {
		File file = new File(filepath + "/" + filename);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 下载文件到SDCard
	public void loadToSdcard(String url, String filepath, String filename) {
		InputStream is = getInputStream(url);
		downloader(is, filepath, filename);
	}

	// 下载文件到应用所在的本地目录
	private void loadToLocation(String urlStr, String filename) {
		InputStream is = getInputStream(urlStr);
		OutputStream os = null;
		try {
			os = context.getApplicationContext().openFileOutput(filename,
					Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
