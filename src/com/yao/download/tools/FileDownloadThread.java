package com.yao.download.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;


/**
 * �ļ�������
 * @author Administrator
 *
 */
public class FileDownloadThread extends Thread {
	
	private static final String TAG=FileDownloadThread.class.getName();
	
	/** ��ǰ�����Ƿ� ��� */
	private boolean isCompleted=false ;
	/**  ��ǰ�����ļ�����*/
	private int downloadLength=0;
	/** �ļ�����·��*/
	private  File file;
	/** �ļ�����·��*/
	private URL downloadUrl;
	/** ��ǰ�����߳� ID*/
	private int threadId;
	/**��ǰ�߳����ؽ���*/
	private int blocksize;
	
	private boolean isRunning=true;
	
	/**
	 * 
	 * @param file �ļ������ַ
	 * @param downloadUrl �ļ����ص�ַ
	 * @param threadId �߳�ID
	 * @param blocksize �������ݳ���
	 */
	public FileDownloadThread(File file, URL downloadUrl, int threadId,
			int blocksize) {
		super();
		this.file = file;
		this.downloadUrl = downloadUrl;
		this.threadId = threadId;
		this.blocksize = blocksize;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		BufferedInputStream bis=null;
		RandomAccessFile raf=null;
		try {
			URLConnection conn=downloadUrl.openConnection();
			conn.setAllowUserInteraction(true);
			conn.setConnectTimeout(20 * 1000);
			conn.setReadTimeout(20*1000);
			
			int startPos=blocksize*(threadId-1);//��ʼλ��
			int endPos=blocksize*threadId-1;//����λ��
			
			//���õ�ǰ���ص���㣬���յ�
			conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
			
			 System.out.println(Thread.currentThread().getName() + "  bytes="  
	                    + startPos + "-" + endPos);  
			 
			 byte[] buffer=new byte[1024*10];
			 
			 bis=new BufferedInputStream(conn.getInputStream());
			 raf=new RandomAccessFile(file, "rwd");
			 raf.seek(startPos);
			 int len;
			 while((len=bis.read(buffer,0,1024))!=-1){
				 raf.write(buffer,0,len);
				 downloadLength+=len;
			 }
			 isCompleted=true;
			 Log.d(TAG, "��ǰ�߳�"+threadId+"�����꣬�ܶ���СΪ:"  
	                    + downloadLength); 
			 
			
		} catch (Exception e) {
			// TODO: handle exception
			isCompleted=false;
			e.printStackTrace();
		}finally{
			
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(raf!=null){
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	

	/**
	 * �߳��ļ��Ƿ��������
	 * @return
	 */
	public boolean isCompleted(){
		return isCompleted;
	}
	/**
	 * �߳������ļ�����
	 * @return
	 */
	public int getDownLoadLength(){
		return downloadLength;
	}
	
	
	
	
	

}
