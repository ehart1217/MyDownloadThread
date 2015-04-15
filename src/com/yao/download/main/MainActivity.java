
package com.yao.download.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.download.R;
import com.yao.download.bean.DeviceInfo_Bean;
import com.yao.download.http.HttpUtil;
import com.yao.download.myview.WaveView;
import com.yao.download.tools.FileDownloadThread;
import com.yao.download.tools.GetWifiInfo;
import com.yao.download.tools.Resolve_Data_From_Json;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    // ping网址 获取的结果
    private String ping_Str = "";
    // 显示wifi相关信息 Textview控件
    private TextView wifiInfo_TextView;
    // 显示手机本身相关信息 TextView控件
    private TextView phoneInfo_TextView;
    // 显示PING网址获取信息 TextView控件
    private TextView ping_TextView;
    // 显示 设备信息
    private TextView deviceInfo_TextView;
    /** 显示外网下载 速度跟内容 细则 */
    private TextView w_content_TextView;
    /** 显示内网下载 速度跟内容 细则 */
    private TextView n_content_TextView;

    // 显示外网进度 textview
    private TextView m_W_MessageView;
    // 显示外网下载进度的 bar
    private ProgressBar m_W_ProgressBar;
    // 下载按钮
    private Button downAppButton;

    // 显示内网进度textview
    private TextView m_N_TextView;
    // 显示内网下载进度 bar
    private ProgressBar m_N_ProgressBar;

    // 下载速度波动图
    private WaveView mWaveView;

    private Handler handler = new Handler();
    /** 下载速度 */
    private String Speed_Num = "";
    private GetWifiInfo wifiInfo = null;
    /** 获取网络字段（设备信息） */
    private String data_str = null;

    private List<Float> mWaveListOut;
    private List<Float> mWaveListIn;

    private DeviceInfo_Bean deviceInfo_Bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_activity);
        wifiInfo = new GetWifiInfo(this);
        // 初始化 按钮控件
        n_content_TextView = (TextView) findViewById(R.id.n_textView4);
        w_content_TextView = (TextView) findViewById(R.id.w_textView5);

        wifiInfo_TextView = (TextView) findViewById(R.id.textView1);
        phoneInfo_TextView = (TextView) findViewById(R.id.textView2);
        ping_TextView = (TextView) findViewById(R.id.ping_textView5);
        deviceInfo_TextView = (TextView) findViewById(R.id.info_textView5);
        downAppButton = (Button) findViewById(R.id.download_btn);
        // 内网
        m_N_TextView = (TextView) findViewById(R.id.download_n_message);
        m_N_ProgressBar = (ProgressBar) findViewById(R.id.download_n_progress);
        // 外网
        m_W_MessageView = (TextView) findViewById(R.id.download_w_message);
        m_W_ProgressBar = (ProgressBar) findViewById(R.id.download_w_progress);

        // 波动图
        mWaveView = (WaveView) findViewById(R.id.waveView);

        downAppButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击按钮清空波动图
                if (mWaveListIn != null) {
                    mWaveListIn.clear();
                }
                if (mWaveListOut != null) {
                    mWaveListOut.clear();
                }
                // TODO Auto-generated method stub 下载按钮事件处理
                // doDownload(getResources().getString(R.string.download_file_w_url),"weixin.apk");
                ping_TextView.setText("正在Ping www.baidu.com");
                new PingThread(3, "www.baidu.com").start();
                m_N_ProgressBar.setProgress(0);
                m_W_ProgressBar.setProgress(0);
                m_N_TextView.setText("请求数据...");
                m_W_MessageView.setText("等待下载中...");
                n_content_TextView.setText("数据请求...");
                w_content_TextView.setText("等待数据请求");
                doDownload(
                        getResources().getString(R.string.download_file_n_url),
                        "1003933.mp4", 0);
                downAppButton.setClickable(false);
            }
        });
        wifiInfo_TextView.setText(wifiInfo.getInfo());
        phoneInfo_TextView.setText(wifiInfo.getPhoneInfo());
        String getDevice_Info = "http://192.168.0.1/transfer/device-info";
        Map<String, String> map = new HashMap<String, String>();
        // 获取设备相关信息
        new GetDeviceInfo_Thread(getDevice_Info, map).start();

        // downAppButton.setVisibility(View.GONE);

    }

    /**
     * 获取设备相关信息
     * 
     * @author Administrator
     */
    private class GetDeviceInfo_Thread extends Thread {
        private String url_str;
        private Map<String, String> values_Map;

        public GetDeviceInfo_Thread(String url_str,
                Map<String, String> values_Map) {
            super();
            this.url_str = url_str;
            this.values_Map = values_Map;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Looper.prepare();
            data_str = null;
            try {
                System.out.println("------------->" + url_str);
                data_str = HttpUtil.postRequst(url_str, values_Map);
                if (!(data_str == null)) {
                    deviceInfo_Bean = Resolve_Data_From_Json
                            .Get_Service_Data_From_Json(data_str);
                } else {
                    System.out.println("获取设备信息出错");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            handler.post(new GetDeviceInfoRunnable());
            Looper.loop();

        }

    }

    /**
     * Ping 百度
     * 
     * @author Administrator
     */
    private class PingThread extends Thread {
        int pingNum;
        String m_strForNetAddress;

        public PingThread(int pingNum, String m_strForNetAddress) {
            super();
            this.pingNum = pingNum;
            this.m_strForNetAddress = m_strForNetAddress;
        }

        @Override
        public void run() {
            Looper.prepare();
            StringBuffer tv_PingInfo = new StringBuffer();
            Process p = null;
            int status = 0;
            try {
                p = Runtime.getRuntime().exec(
                        "/system/bin/ping -c " + pingNum + " "
                                + m_strForNetAddress);
                status = p.waitFor();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (status == 0) {
                tv_PingInfo.append("success" + "\r\n");
            } else {
                tv_PingInfo.append("failed" + "\r\n");
            }
            BufferedReader buf = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            String str = new String();
            // 读出所有信息并显示
            try {
                while ((str = buf.readLine()) != null) {
                    str = str + "\r\n";
                    tv_PingInfo.append(str);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                str = str + "\r\n";
                tv_PingInfo.append("Ping：" + m_strForNetAddress + "失败");
            }
            ping_Str = tv_PingInfo.toString();
            handler.post(new PingRunnable());
            Looper.loop();
        }

    }

    /**
     * Ping UI交互
     * 
     * @author Administrator
     */
    private class PingRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            downAppButton.setVisibility(View.VISIBLE);
            ping_TextView.setText("PING结果：\n" + ping_Str);
        }
    }

    /**
     * 获取设备信息 UI交互 线程
     * 
     * @author Administrator
     */
    private class GetDeviceInfoRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (deviceInfo_Bean == null) {
                deviceInfo_TextView.setText("获取设备信息失败");
            } else {
                deviceInfo_TextView.setText(deviceInfo_Bean.toString());
            }
        }
    }

    /**
     * 下载准备工作，获取SD卡路径，开启线程
     * 
     * @param downloadUrl 下载地址
     * @param filename 文件名
     */
    private void doDownload(String downloadUrl, String filename, int type_tag) {
        // 获取sd路径 getExternalStorageDirectory
        String path = Environment.getExternalStorageDirectory() + "/vison_yao/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        // 设置mProgressbar初始化
        if (type_tag == 0) {
            m_N_ProgressBar.setProgress(0);
        } else if (type_tag == 1) {
            m_W_ProgressBar.setProgress(0);
        }
        // 现在线程数
        int threadNum = 1;
        // 文件地址
        String filepath = path + filename;
        // 启动下载 线程
        downloadTask task = new downloadTask(downloadUrl, threadNum, filepath,
                type_tag);
        task.start();

    }

    /**
     * 下载 异步线程
     * 
     * @author Administrator
     */
    private class downloadTask extends Thread {
        private String downloadUrl;// 下载链接地址
        private int threadNum;// 开启线程数
        private String filePath;// 保存文件地址
        private int blockSize;// 每个线程现在容量

        private int n_w_tag;// 标记下载内网还是外网

        public downloadTask(String downloadUrl, int threadNum, String filePath,
                int type_tag) {
            super();
            this.downloadUrl = downloadUrl;
            this.threadNum = threadNum;
            this.filePath = filePath;
            this.n_w_tag = type_tag;
        }

        @Override
        public void run() {
            Looper.prepare();
            FileDownloadThread[] threads = new FileDownloadThread[threadNum];

            Message msg = new Message();
            try {
                URL url = new URL(downloadUrl);
                Log.d(TAG, "download file http path:" + downloadUrl);
                URLConnection connection = url.openConnection();

                connection.setAllowUserInteraction(true);
                connection.setConnectTimeout(20 * 1000);
                connection.setReadTimeout(20 * 1000);

                // 读取文件大小
                int fileSize = connection.getContentLength();
                if (fileSize < 0) {
                    Toast.makeText(getApplicationContext(), "读取文件失败",
                            Toast.LENGTH_SHORT).show();
                    System.out.println("读取文件失败");

                    msg.what = n_w_tag;
                    msg.arg1 = 0;
                    handler.post(new MyUiRunnable(msg));

                    return;
                }
                // 设置 mProgressBar 最大值
                if (n_w_tag == 0) {
                    m_N_ProgressBar.setMax(fileSize);
                } else if (n_w_tag == 1) {
                    m_W_ProgressBar.setMax(fileSize);
                }
                // 设计每条线程下载的数据长度
                blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
                        : fileSize / threadNum + 1;

                Log.d(TAG, "fileSize:" + fileSize + "  blockSize:");

                File file = new File(filePath);

                for (int i = 0; i < threads.length; i++) {
                    // 启动线程，分别下载每个线程需要下载的部分
                    threads[i] = new FileDownloadThread(file, url, i + 1,
                            blockSize);
                    threads[i].setName("Thread" + i);
                    threads[i].start();
                }

                boolean isFinished = false;
                int downloadAllSize = 0;

                Date beginTime = new Date();
                long diff = 0;
                String result = "";
                while (!isFinished) {
                    isFinished = true;
                    // 当前所有线程下载总量
                    downloadAllSize = 0;
                    for (int i = 0; i < threads.length; i++) {
                        downloadAllSize += threads[i].getDownLoadLength();
                        System.out.println("isCompleted:" + i + ":"
                                + threads[i].isCompleted());
                        if (!threads[i].isCompleted()) {
                            isFinished = false;
                        }
                    }
                    // 记录开始时间
                    Date endTime = new Date();
                    // 计算时间差
                    diff = endTime.getTime() - beginTime.getTime();
                    System.out.println("diff::" + diff);
                    System.out.println("downloadAllSize::" + downloadAllSize);
                    // 返回计算结果
                    if (diff != 0 && downloadAllSize != 0) {
                        result = String.valueOf((downloadAllSize / 1024)
                                / (diff / 1000));
                    }

                    System.out.println("线程是否运行：" + threads[0].isAlive());

                    if (!threads[0].isCompleted() && !threads[0].isAlive()) {
                        msg.what = n_w_tag;
                        msg.arg1 = 0;
                        handler.post(new MyUiRunnable(msg));
                        break;
                    }
                    // 通知 Handler去更新 UI

                    msg.getData().putInt("size", downloadAllSize);
                    msg.getData().putString("speed", result);
                    msg.what = n_w_tag;
                    msg.arg1 = 1;
                    handler.post(new MyUiRunnable(msg));
                    Thread.sleep(1000);// 休息1秒后再读取下载进度
                }

            } catch (MalformedURLException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Looper.loop();
        }

    }

    /**
     * 下载 进度数据交互
     * 
     * @author Administrator
     */
    private class MyUiRunnable implements Runnable {

        private Message message = null;

        public MyUiRunnable(Message message) {
            super();
            this.message = message;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int data_num = message.getData().getInt("size") / 1024;// KB
            if (message.what == 0) {
                if (message.arg1 == 1) {
                    m_N_ProgressBar.setProgress(message.getData()
                            .getInt("size"));
                    Speed_Num = message.getData().getString("speed");
                    float temp = (float) m_N_ProgressBar.getProgress()
                            / (float) m_N_ProgressBar.getMax();

                    int progress = (int) (temp * 100);
                    if (progress == 100) {
                        if (data_num > 0) {
                            m_N_TextView.setText("下载完成");
                            Toast.makeText(MainActivity.this, "下载完成",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            download_failed_Message(0);
                        }
                        doDownload(
                                getResources().getString(
                                        R.string.download_file_w_url),
                                "我来贷大学生.apk", 1);
                    } else {
                        m_N_TextView.setText("下载进度" + progress + "%" + "下载速度："
                                + Speed_Num + "KB/s");
                        // TODO
                        if (mWaveListIn == null) {
                            mWaveListIn = new ArrayList<Float>();
                        }

                        try {
                            Float f = new Float(Speed_Num);
                            mWaveListIn.add(f);
                            mWaveView.setIntranetData(mWaveListIn);
                            mWaveView.invalidate();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    // 实时显示 现在进度信息
                    n_content_TextView.setText("内网测试结果" + "\n\n" + "Wifi信号强度："
                            + wifiInfo.getWifistrength() + "\n\r" + "下载大小："
                            + data_num + "KB" + "\n\r" + "下载速度：" + Speed_Num
                            + "KB/s");

                } else {
                    download_failed_Message(0);
                    doDownload(
                            getResources().getString(
                                    R.string.download_file_w_url), "我来贷大学生.apk",
                            1);
                }
            } else if (message.what == 1) {
                if (message.arg1 == 1) {
                    m_W_ProgressBar.setProgress(message.getData()
                            .getInt("size"));
                    Speed_Num = message.getData().getString("speed");
                    float temp = (float) m_W_ProgressBar.getProgress()
                            / (float) m_W_ProgressBar.getMax();

                    int progress = (int) (temp * 100);
                    if (progress == 100) {
                        if (data_num > 0) {
                            downAppButton.setClickable(true);
                            m_W_MessageView.setText("下载完成");
                            Toast.makeText(MainActivity.this, "下载完成",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            download_failed_Message(1);
                            return;
                        }

                    } else {
                        m_W_MessageView.setText("下载进度" + progress + "%"
                                + "下载速度：" + Speed_Num + "KB/s");
                        // TODO
                        if (mWaveListOut == null) {
                            mWaveListOut = new ArrayList<Float>();
                        }
                        try {
                            Float f = new Float(Speed_Num);
                            mWaveListOut.add(f);
                            mWaveView.setOutnetData(mWaveListOut);
                            mWaveView.invalidate();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    w_content_TextView.setText("外网测试结果" + "\n\n" + "Wifi信号强度："
                            + wifiInfo.getWifistrength() + "\n\r" + "下载大小："
                            + data_num + "KB" + "\n\r" + "下载速度：" + Speed_Num
                            + "KB/s");
                } else {
                    download_failed_Message(1);
                }

            }
        }
    }

    /**
     * 下载失败显示
     * 
     * @param tag_num 0 内网，1 外网
     */
    private void download_failed_Message(int tag_num) {
        if (tag_num == 0) {
            m_N_TextView.setText("下载失败");
            n_content_TextView.setText("内网测试结果" + "\n\n" + "Wifi信号强度："
                    + wifiInfo.getWifistrength() + "\n\n" + "下载文件失败");
        } else if (tag_num == 1) {
            downAppButton.setClickable(true);
            m_W_MessageView.setText("下载失败");
            w_content_TextView.setText("外网测试结果" + "\n\n" + "Wifi信号强度："
                    + wifiInfo.getWifistrength() + "\n\n" + "下载文件失败");
        }

    }

}
