package com.yao.download.tools;

import android.util.Log;

import com.google.myjson.Gson;
import com.yao.download.bean.DeviceInfo_Bean;

public class Resolve_Data_From_Json {

	/**
	 * 解析 服务器返回的 json数据
	 * 
	 * @param data_str
	 * @return 设备相关信息
	 */
	public static DeviceInfo_Bean Get_Service_Data_From_Json(String data_str) {
		DeviceInfo_Bean deviceInfo_Bean = new DeviceInfo_Bean();
		try {
			Gson gson = new Gson();
			deviceInfo_Bean = gson.fromJson(data_str, DeviceInfo_Bean.class);
			Log.e("DeviceInfo_Bean", deviceInfo_Bean.toString());
			return deviceInfo_Bean;
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
			return null;
		}
	}

}
