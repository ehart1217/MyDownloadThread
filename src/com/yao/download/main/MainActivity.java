
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
    // ping��ַ ��ȡ�Ľ��
    private String ping_Str = "";
    // ��ʾwifi�����Ϣ Textview�ؼ�
    private TextView wifiInfo_TextView;
    // ��ʾ�ֻ����������Ϣ TextView�ؼ�
    private TextView phoneInfo_TextView;
    // ��ʾPING��ַ��ȡ��Ϣ TextView�ؼ�
    private TextView ping_TextView;
    // ��ʾ �豸��Ϣ
    private TextView deviceInfo_TextView;
    /** ��ʾ�������� �ٶȸ����� ϸ�� */
    private TextView w_content_TextView;
    /** ��ʾ�������� �ٶȸ����� ϸ�� */
    private TextView n_content_TextView;

    // ��ʾ�������� textview
    private TextView m_W_MessageView;
    // ��ʾ�������ؽ��ȵ� bar
    private ProgressBar m_W_ProgressBar;
    // ���ذ�ť
    private Button downAppButton;

    // ��ʾ��������textview
    private TextView m_N_TextView;
    // ��ʾ�������ؽ��� bar
    private ProgressBar m_N_ProgressBar;

    // �����ٶȲ���ͼ
    private WaveView mWaveView;

    private Handler handler = new Handler();
    /** �����ٶ� */
    private String Speed_Num = "";
    private GetWifiInfo wifiInfo = null;
    /** ��ȡ�����ֶΣ��豸��Ϣ�� */
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
        // ��ʼ�� ��ť�ؼ�
        n_content_TextView = (TextView) findViewById(R.id.n_textView4);
        w_content_TextView = (TextView) findViewById(R.id.w_textView5);

        wifiInfo_TextView = (TextView) findViewById(R.id.textView1);
        phoneInfo_TextView = (TextView) findViewById(R.id.textView2);
        ping_TextView = (TextView) findViewById(R.id.ping_textView5);
        deviceInfo_TextView = (TextView) findViewById(R.id.info_textView5);
        downAppButton = (Button) findViewById(R.id.download_btn);
        // ����
        m_N_TextView = (TextView) findViewById(R.id.download_n_message);
        m_N_ProgressBar = (ProgressBar) findViewById(R.id.download_n_progress);
        // ����
        m_W_MessageView = (TextView) findViewById(R.id.download_w_message);
        m_W_ProgressBar = (ProgressBar) findViewById(R.id.download_w_progress);

        // ����ͼ
        mWaveView = (WaveView) findViewById(R.id.waveView);

        downAppButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // �����ť��ղ���ͼ
                if (mWaveListIn != null) {
                    mWaveListIn.clear();
                }
                if (mWaveListOut != null) {
                    mWaveListOut.clear();
                }
                // TODO Auto-generated method stub ���ذ�ť�¼�����
                // doDownload(getResources().getString(R.string.download_file_w_url),"weixin.apk");
                ping_TextView.setText("����Ping www.baidu.com");
                new PingThread(3, "www.baidu.com").start();
                m_N_ProgressBar.setProgress(0);
                m_W_ProgressBar.setProgress(0);
                m_N_TextView.setText("��������...");
                m_W_MessageView.setText("�ȴ�������...");
                n_content_TextView.setText("��������...");
                w_content_TextView.setText("�ȴ���������");
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
        // ��ȡ�豸�����Ϣ
        new GetDeviceInfo_Thread(getDevice_Info, map).start();

        // downAppButton.setVisibility(View.GONE);

    }

    /**
     * ��ȡ�豸�����Ϣ
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
                    System.out.println("��ȡ�豸��Ϣ����");
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
     * Ping �ٶ�
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
            // ����������Ϣ����ʾ
            try {
                while ((str = buf.readLine()) != null) {
                    str = str + "\r\n";
                    tv_PingInfo.append(str);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                str = str + "\r\n";
                tv_PingInfo.append("Ping��" + m_strForNetAddress + "ʧ��");
            }
            ping_Str = tv_PingInfo.toString();
            handler.post(new PingRunnable());
            Looper.loop();
        }

    }

    /**
     * Ping UI����
     * 
     * @author Administrator
     */
    private class PingRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            downAppButton.setVisibility(View.VISIBLE);
            ping_TextView.setText("PING�����\n" + ping_Str);
        }
    }

    /**
     * ��ȡ�豸��Ϣ UI���� �߳�
     * 
     * @author Administrator
     */
    private class GetDeviceInfoRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (deviceInfo_Bean == null) {
                deviceInfo_TextView.setText("��ȡ�豸��Ϣʧ��");
            } else {
                deviceInfo_TextView.setText(deviceInfo_Bean.toString());
            }
        }
    }

    /**
     * ����׼����������ȡSD��·���������߳�
     * 
     * @param downloadUrl ���ص�ַ
     * @param filename �ļ���
     */
    private void doDownload(String downloadUrl, String filename, int type_tag) {
        // ��ȡsd·�� getExternalStorageDirectory
        String path = Environment.getExternalStorageDirectory() + "/vison_yao/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        // ����mProgressbar��ʼ��
        if (type_tag == 0) {
            m_N_ProgressBar.setProgress(0);
        } else if (type_tag == 1) {
            m_W_ProgressBar.setProgress(0);
        }
        // �����߳���
        int threadNum = 1;
        // �ļ���ַ
        String filepath = path + filename;
        // �������� �߳�
        downloadTask task = new downloadTask(downloadUrl, threadNum, filepath,
                type_tag);
        task.start();

    }

    /**
     * ���� �첽�߳�
     * 
     * @author Administrator
     */
    private class downloadTask extends Thread {
        private String downloadUrl;// �������ӵ�ַ
        private int threadNum;// �����߳���
        private String filePath;// �����ļ���ַ
        private int blockSize;// ÿ���߳���������

        private int n_w_tag;// �������������������

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

                // ��ȡ�ļ���С
                int fileSize = connection.getContentLength();
                if (fileSize < 0) {
                    Toast.makeText(getApplicationContext(), "��ȡ�ļ�ʧ��",
                            Toast.LENGTH_SHORT).show();
                    System.out.println("��ȡ�ļ�ʧ��");

                    msg.what = n_w_tag;
                    msg.arg1 = 0;
                    handler.post(new MyUiRunnable(msg));

                    return;
                }
                // ���� mProgressBar ���ֵ
                if (n_w_tag == 0) {
                    m_N_ProgressBar.setMax(fileSize);
                } else if (n_w_tag == 1) {
                    m_W_ProgressBar.setMax(fileSize);
                }
                // ���ÿ���߳����ص����ݳ���
                blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
                        : fileSize / threadNum + 1;

                Log.d(TAG, "fileSize:" + fileSize + "  blockSize:");

                File file = new File(filePath);

                for (int i = 0; i < threads.length; i++) {
                    // �����̣߳��ֱ�����ÿ���߳���Ҫ���صĲ���
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
                    // ��ǰ�����߳���������
                    downloadAllSize = 0;
                    for (int i = 0; i < threads.length; i++) {
                        downloadAllSize += threads[i].getDownLoadLength();
                        System.out.println("isCompleted:" + i + ":"
                                + threads[i].isCompleted());
                        if (!threads[i].isCompleted()) {
                            isFinished = false;
                        }
                    }
                    // ��¼��ʼʱ��
                    Date endTime = new Date();
                    // ����ʱ���
                    diff = endTime.getTime() - beginTime.getTime();
                    System.out.println("diff::" + diff);
                    System.out.println("downloadAllSize::" + downloadAllSize);
                    // ���ؼ�����
                    if (diff != 0 && downloadAllSize != 0) {
                        result = String.valueOf((downloadAllSize / 1024)
                                / (diff / 1000));
                    }

                    System.out.println("�߳��Ƿ����У�" + threads[0].isAlive());

                    if (!threads[0].isCompleted() && !threads[0].isAlive()) {
                        msg.what = n_w_tag;
                        msg.arg1 = 0;
                        handler.post(new MyUiRunnable(msg));
                        break;
                    }
                    // ֪ͨ Handlerȥ���� UI

                    msg.getData().putInt("size", downloadAllSize);
                    msg.getData().putString("speed", result);
                    msg.what = n_w_tag;
                    msg.arg1 = 1;
                    handler.post(new MyUiRunnable(msg));
                    Thread.sleep(1000);// ��Ϣ1����ٶ�ȡ���ؽ���
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
     * ���� �������ݽ���
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
                            m_N_TextView.setText("�������");
                            Toast.makeText(MainActivity.this, "�������",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            download_failed_Message(0);
                        }
                        doDownload(
                                getResources().getString(
                                        R.string.download_file_w_url),
                                "��������ѧ��.apk", 1);
                    } else {
                        m_N_TextView.setText("���ؽ���" + progress + "%" + "�����ٶȣ�"
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
                    // ʵʱ��ʾ ���ڽ�����Ϣ
                    n_content_TextView.setText("�������Խ��" + "\n\n" + "Wifi�ź�ǿ�ȣ�"
                            + wifiInfo.getWifistrength() + "\n\r" + "���ش�С��"
                            + data_num + "KB" + "\n\r" + "�����ٶȣ�" + Speed_Num
                            + "KB/s");

                } else {
                    download_failed_Message(0);
                    doDownload(
                            getResources().getString(
                                    R.string.download_file_w_url), "��������ѧ��.apk",
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
                            m_W_MessageView.setText("�������");
                            Toast.makeText(MainActivity.this, "�������",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            download_failed_Message(1);
                            return;
                        }

                    } else {
                        m_W_MessageView.setText("���ؽ���" + progress + "%"
                                + "�����ٶȣ�" + Speed_Num + "KB/s");
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
                    w_content_TextView.setText("�������Խ��" + "\n\n" + "Wifi�ź�ǿ�ȣ�"
                            + wifiInfo.getWifistrength() + "\n\r" + "���ش�С��"
                            + data_num + "KB" + "\n\r" + "�����ٶȣ�" + Speed_Num
                            + "KB/s");
                } else {
                    download_failed_Message(1);
                }

            }
        }
    }

    /**
     * ����ʧ����ʾ
     * 
     * @param tag_num 0 ������1 ����
     */
    private void download_failed_Message(int tag_num) {
        if (tag_num == 0) {
            m_N_TextView.setText("����ʧ��");
            n_content_TextView.setText("�������Խ��" + "\n\n" + "Wifi�ź�ǿ�ȣ�"
                    + wifiInfo.getWifistrength() + "\n\n" + "�����ļ�ʧ��");
        } else if (tag_num == 1) {
            downAppButton.setClickable(true);
            m_W_MessageView.setText("����ʧ��");
            w_content_TextView.setText("�������Խ��" + "\n\n" + "Wifi�ź�ǿ�ȣ�"
                    + wifiInfo.getWifistrength() + "\n\n" + "�����ļ�ʧ��");
        }

    }

}
