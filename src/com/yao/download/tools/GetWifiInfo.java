package com.yao.download.tools;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * 获取 Wifi 相关信息 mac,ip,status,wifi名,网络ID,连接速度,wifi强度 手机型号 版本号
 * 
 * @author Administrator
 * 
 */
public class GetWifiInfo {
	Context context;

	public GetWifiInfo(Context context) {
		this.context = context;
	}

	public String getInfo() {
		String result = "";
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String maxText = info.getMacAddress();
		String ipText = intToIp(info.getIpAddress());
		String status = "";
		// DhcpInfo dhcpInfo = wifi.getDhcpInfo();
		// String dns1 = intToIp(dhcpInfo.dns1);
		// String dns2 = intToIp(dhcpInfo.dns2);
		// // 网关
		// String way = intToIp(dhcpInfo.gateway);
		// // IP地址
		// String ipAddress = intToIp(dhcpInfo.ipAddress);
		// // 子网掩码
		// String netmask = intToIp(dhcpInfo.netmask);
		// // 服务端地址（其实就是路由器ip，和网关一样）
		// String serverAddress = intToIp(dhcpInfo.serverAddress);
		// //////////////////////////

		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			status = "已打开 ";
			String ssid = info.getSSID();
			int networkID = info.getNetworkId();
			int speed = info.getLinkSpeed();
			// 链接信号强度
			// int strength = info.getRssi();
			// WifiManager.calculateSignalLevel(info.getRssi(), 5);

			result = "mac：" + maxText + "\n\r" + "ip：" + ipText + "\n\r"
					+ "wifi状态 :" + status + "\n\r" + "wifi名 :" + ssid + "\n\r"
					+ "连接速度:" + speed + "Mbps";
			// + "\n\r" + "网络ID :" + networkID
			// + "信号强度:" + strength + "\n\r" + "dns1:" + dns1
			// + "\n\r" + "dns2:" + dns2 + "\n\r" + "网关：" + way + "\n\r"
			// + "ipAddress:" + ipAddress + "\n\r" + "子网掩码：" + netmask
			// + "\n\r" + "路由器ip：" + serverAddress;
		} else if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			result = " 当前正使用GPRS上网";
		}
		return result;
	}

	/**
	 * 获取wifi强度
	 * 
	 * @return
	 */
	public int getWifistrength() {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		int strength = 0;

		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			// 链接信号强度
			strength = info.getRssi();
			// WifiManager.calculateSignalLevel(info.getRssi(), 5);

		} else if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			strength = -111;
		}
		return strength;
	}

	/**
	 * 获取手机型号和版本号
	 * 
	 * @return
	 */
	public String getPhoneInfo() {
		String model_str = "";
		String phone_num = "暂不获取";
		String SDK_Version = "";
		String Firmware_Os_Version = "";
		// TelephonyManager phoneMgr = (TelephonyManager) context
		// .getSystemService(Context.TELEPHONY_SERVICE);
		model_str = Build.MODEL; // 手机型号
		// phone_num = phoneMgr.getLine1Number();// 本机电话号码
		SDK_Version = Build.VERSION.SDK_INT + "";// SDK版本号
		Firmware_Os_Version = Build.VERSION.RELEASE;// Firmware/OS 版本号

		return " 手机型号：" + model_str + "\n\r" + "SDK版本：" + SDK_Version + "\n\r"
				+ "手机号码：" + phone_num + "\n\r" + "系统版本：" + Firmware_Os_Version;
	}

	private String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}
}
