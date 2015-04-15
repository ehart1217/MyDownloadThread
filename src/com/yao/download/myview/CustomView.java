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
			// ������
			canvas.drawRect(rect, paint);
			// 40 �������һ��ʱ��Ŀ��,�ַ����Ŀ�ȸ��ֺţ������йأ�Ӧ����android�ṩ��apiȥ��һ���ַ����Ŀ��
			int gap = (viewWidth - 40) / (list.size() - 1);
			// ��ʱ��

			paint.setColor(0xFFFFFFFF);
			paint.setTextSize(20);

			// ʱ��ĸ߶�
			int timeHeight = 40;
			int preiceHeight = viewHeight - timeHeight;
			for (int i = 0; i < list.size(); i++) {
				// ȡ��ʱ��
				HashMap<String, String> map = list.get(i);
				String time = map.get("time");
				int timeLeft = i * gap;
				canvas.drawText(time, timeLeft, viewHeight, paint);

				// ���۸�
				String strPrice = map.get("price");
				int intPrice = Integer.parseInt(strPrice);
				int priceTop = (intPrice * preiceHeight / maxPrice);
				// ���ٵļ۸���ʾ�������棬Ӧ����ʾ��������
				priceTop = preiceHeight - priceTop;
				priceTop = priceTop + 20;// 20�Ǽ۸�ĸ߶�
				canvas.drawText(strPrice, timeLeft, priceTop, paint);

				if (i < list.size() - 1) {
					// ����
					int startX = timeLeft;
					int startY = priceTop;
					int endX = 0, endY = 0;
					// ��һ���۸������
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
