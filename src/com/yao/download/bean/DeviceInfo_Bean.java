package com.yao.download.bean;

public class DeviceInfo_Bean {

	private String termSn = "";// "9303SL201405070002",
	private String signal = "";// "9",
	private String portalVersion = "";// "PortalVer|Version=1.5.1.1_1.2;Time=201502021000;",
	private String deviceVersion = "";// "Device_SN = 9303SL201405070002",
	private String isLogin = "";// "1",
	private String DNSServer = "";// "210.21.4.130"

	public String getTermSn() {
		return termSn;
	}

	public void setTermSn(String termSn) {
		this.termSn = termSn;
	}

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

	public String getPortalVersion() {
		return portalVersion;
	}

	public void setPortalVersion(String portalVersion) {
		this.portalVersion = portalVersion;
	}

	public String getDeviceVersion() {
		return deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(String isLogin) {
			this.isLogin=isLogin;
	}

	public String getDNSServer() {
		return DNSServer;
	}

	public void setDNSServer(String dNSServer) {
		DNSServer = dNSServer;
	}

	@Override
	public String toString() {
		if(isLogin.equals("1")){
			isLogin="δ����";
		}else if(isLogin.equals("0")){
			isLogin="�ѷ���";
		}
		return "�豸��Ϣ: \n �豸���:" + termSn + "\n 3G�ź�:" + signal + "\n "
				+ portalVersion + "\n �̼��汾��:" + deviceVersion + "\n �Ƿ���У�"
				+ isLogin + "\n DNS������:" + DNSServer.toString();
	}

}
