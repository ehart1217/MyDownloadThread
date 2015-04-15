package com.yao.download.tools;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * ��ȡ Wifi �����Ϣ mac,ip,status,wifi��,����ID,�����ٶ�,wifiǿ�� �ֻ��ͺ� �汾��
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
		// // ����
		// String way = intToIp(dhcpInfo.gateway);
		// // IP��ַ
		// String ipAddress = intToIp(dhcpInfo.ipAddress);
		// // ��������
		// String netmask = intToIp(dhcpInfo.netmask);
		// // ����˵�ַ����ʵ����·����ip��������һ����
		// String serverAddress = intToIp(dhcpInfo.serverAddress);
		// //////////////////////////

		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			status = "�Ѵ� ";
			String ssid = info.getSSID();
			int networkID = info.getNetworkId();
			int speed = info.getLinkSpeed();
			// �����ź�ǿ��
			// int strength = info.getRssi();
			// WifiManager.calculateSignalLevel(info.getRssi(), 5);

			result = "mac��" + maxText + "\n\r" + "ip��" + ipText + "\n\r"
					+ "wifi״̬ :" + status + "\n\r" + "wifi�� :" + ssid + "\n\r"
					+ "�����ٶ�:" + speed + "Mbps";
			// + "\n\r" + "����ID :" + networkID
			// + "�ź�ǿ��:" + strength + "\n\r" + "dns1:" + dns1
			// + "\n\r" + "dns2:" + dns2 + "\n\r" + "���أ�" + way + "\n\r"
			// + "ipAddress:" + ipAddress + "\n\r" + "�������룺" + netmask
			// + "\n\r" + "·����ip��" + serverAddress;
		} else if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			result = " ��ǰ��ʹ��GPRS����";
		}
		return result;
	}

	/**
	 * ��ȡwifiǿ��
	 * 
	 * @return
	 */
	public int getWifistrength() {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		int strength = 0;

		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			// �����ź�ǿ��
			strength = info.getRssi();
			// WifiManager.calculateSignalLevel(info.getRssi(), 5);

		} else if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			strength = -111;
		}
		return strength;
	}

	/**
	 * ��ȡ�ֻ��ͺźͰ汾��
	 * 
	 * @return
	 */
	public String getPhoneInfo() {
		String model_str = "";
		String phone_num = "�ݲ���ȡ";
		String SDK_Version = "";
		String Firmware_Os_Version = "";
		// TelephonyManager phoneMgr = (TelephonyManager) context
		// .getSystemService(Context.TELEPHONY_SERVICE);
		model_str = Build.MODEL; // �ֻ��ͺ�
		// phone_num = phoneMgr.getLine1Number();// �����绰����
		SDK_Version = Build.VERSION.SDK_INT + "";// SDK�汾��
		Firmware_Os_Version = Build.VERSION.RELEASE;// Firmware/OS �汾��

		return " �ֻ��ͺţ�" + model_str + "\n\r" + "SDK�汾��" + SDK_Version + "\n\r"
				+ "�ֻ����룺" + phone_num + "\n\r" + "ϵͳ�汾��" + Firmware_Os_Version;
	}

	private String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}
}
