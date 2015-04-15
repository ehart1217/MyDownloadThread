package com.yao.download.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.yao.download.R;
import com.yao.download.myview.CustomView;

public class MyViewActivity extends Activity {


	CustomView customView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		customView=(CustomView) findViewById(R.id.customView);
		customView.setData(getData());
	}

	public ArrayList<HashMap<String, String>> getData()
	{
		//9 2000
		//10 3000
		ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map1=new HashMap<String, String>();
		map1.put("time", "9");
		map1.put("price", "2000");
		list.add(map1);
		HashMap<String, String> map2=new HashMap<String, String>();
		map2.put("time", "10");
		map2.put("price", "3000");
		list.add(map2);
		HashMap<String, String> map3=new HashMap<String, String>();
		map3.put("time", "11");
		map3.put("price", "5000");
		list.add(map3);
		HashMap<String, String> map4=new HashMap<String, String>();
		map4.put("time", "12");
		map4.put("price", "8000");
		list.add(map4);
		HashMap<String, String> map5=new HashMap<String, String>();
		map5.put("time", "14");
		map5.put("price", "15000");
		list.add(map5);	
		
		HashMap<String, String> map6=new HashMap<String, String>();
		map6.put("time", "14");
		map6.put("price", "15000");
		list.add(map6);	
		HashMap<String, String> map7=new HashMap<String, String>();
		map7.put("time", "14");
		map7.put("price", "15000");
		list.add(map7);	
		HashMap<String, String> map8=new HashMap<String, String>();
		map8.put("time", "14");
		map8.put("price", "15000");
		list.add(map8);	
		HashMap<String, String> map9=new HashMap<String, String>();
		map9.put("time", "14");
		map9.put("price", "15000");
		list.add(map9);	
		
		
		return list;
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
