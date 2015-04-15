package com.yao.download.myview;



import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View {
	int viewWidth, viewHeight;

	ArrayList<HashMap<String, String>> list;
	int maxPrice;

	public void setData(ArrayList<HashMap<String, String>> list) {
		if (list != null) {
			this.list = list;
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String strPrice = map.get("price");
				int intPrice = Integer.parseInt(strPrice);
				if (intPrice > maxPrice) {
					maxPrice = intPrice;
				}
			}
		} else {
			this.list = new ArrayList<HashMap<String, String>>();
		}
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			Rect rect = new Rect(0, 0, viewWidth, viewHeight);
			Paint paint = new Paint();
			paint.setColor(0xFF000000);
			// 画背景
			canvas.drawRect(rect, paint);
			// 40 代表最后一个时间的宽度,字符串的宽度跟字号，字体有关，应该用android提供的api去得一个字符串的宽度
			int gap = (viewWidth - 40) / (list.size() - 1);
			// 画时间

			paint.setColor(0xFFFFFFFF);
			paint.setTextSize(20);

			// 时间的高度
			int timeHeight = 40;
			int preiceHeight = viewHeight - timeHeight;
			for (int i = 0; i < list.size(); i++) {
				// 取到时间
				HashMap<String, String> map = list.get(i);
				String time = map.get("time");
				int timeLeft = i * gap;
				canvas.drawText(time, timeLeft, viewHeight, paint);

				// 画价格
				String strPrice = map.get("price");
				int intPrice = Integer.parseInt(strPrice);
				int priceTop = (intPrice * preiceHeight / maxPrice);
				// 最少的价格显示在最上面，应该显示在最下面
				priceTop = preiceHeight - priceTop;
				priceTop = priceTop + 20;// 20是价格的高度
				canvas.drawText(strPrice, timeLeft, priceTop, paint);

				if (i < list.size() - 1) {
					// 画线
					int startX = timeLeft;
					int startY = priceTop;
					int endX = 0, endY = 0;
					// 下一个价格的坐标
					endX = (i + 1) * gap;
					String strNextPrice = list.get(i + 1).get("price");
					int intNextPrice = Integer.parseInt(strNextPrice);
					endY = (intNextPrice * preiceHeight) / maxPrice;
					endY = preiceHeight - endY;
					endY = endY + 20;
					canvas.drawLine(startX, startY, endX, endY, paint);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		viewHeight = h;
		viewWidth = w;
		super.onSizeChanged(w, h, oldw, oldh);
	}

}
