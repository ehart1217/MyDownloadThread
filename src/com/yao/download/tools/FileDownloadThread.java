package com.yao.download.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;


/**
 * 文件下载类
 * @author Administrator
 *
 */
public class FileDownloadThread extends Thread {
	
	private static final String TAG=FileDownloadThread.class.getName();
	
	/** 当前下载是否 完成 */
	private boolean isCompleted=false ;
	/**  当前下载文件长度*/
	private int downloadLength=0;
	/** 文件保存路径*/
	private  File file;
	/** 文件下载路径*/
	private URL downloadUrl;
	/** 当前下载线程 ID*/
	private int threadId;
	/**当前线程下载进度*/
	private int blocksize;
	
	private boolean isRunning=true;
	
	/**
	 * 
	 * @param file 文件保存地址
	 * @param downloadUrl 文件下载地址
	 * @param threadId 线程ID
	 * @param blocksize 下载数据长度
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
			
			int startPos=blocksize*(threadId-1);//开始位置
			int endPos=blocksize*threadId-1;//结束位置
			
			//设置当前下载的起点，跟终点
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
			 Log.d(TAG, "当前线程"+threadId+"下载完，总动大小为:"  
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
	 * 线程文件是否下载完毕
	 * @return
	 */
	public boolean isCompleted(){
		return isCompleted;
	}
	/**
	 * 线程下载文件长度
	 * @return
	 */
	public int getDownLoadLength(){
		return downloadLength;
	}
	
	
	
	
	

}
