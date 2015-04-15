
package com.yao.download.myview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.common.collect.Lists;
import com.yao.download.R;

public class WaveView extends View {

    private final static String TAG = "WaveView";
    int viewWidth, viewHeight;
    int bgWidth, bgHeight;

    private final static int COMMON_PADDING = 35; // 坐标轴离View边缘的边距
    private final static int COMMON_TEXT_PADDING = 10; // 文字离View的边缘的边距

    private final static int MAX_GAP = 100;// 初始的X轴元素之间的距离,最大距离
    private final static int MIN_GAP = 60;// 最小距离

    private final static int BOTTOM_PADDING = COMMON_PADDING;
    private final static int RIGHT_PADDING = COMMON_PADDING;

    List<Float> mIntranetList;
    List<Float> mOutNetList;

    Float maxPrice = Float.valueOf(0f);

    /**
     * 设置内网下载速度数据集合
     * 
     * @param list
     */
    public void setIntranetData(List<Float> list) {
        if (list != null) {
            this.mIntranetList = list;
            for (int i = 0; i < list.size(); i++) {
                Float intPrice = list.get(i);
                // 设置最大的数字
                if (intPrice > maxPrice) {
                    maxPrice = intPrice;
                }
            }
        } else {
            this.mIntranetList = new ArrayList<Float>();
        }
    }

    /**
     * 设置外网下载速度数据集合
     * 
     * @param list
     */
    public void setOutnetData(List<Float> list) {
        if (list != null) {
            this.mOutNetList = list;
            for (int i = 0; i < list.size(); i++) {
                Float intPrice = list.get(i);
                // 设置最大的数字
                if (intPrice > maxPrice) {
                    maxPrice = intPrice;
                }
            }
        } else {
            this.mOutNetList = new ArrayList<Float>();
        }
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            Rect rect = new Rect(0, 0, bgWidth, bgHeight);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.pale_turquoise));
            paint.setStrokeWidth(2.5f);
            // 画背景
            canvas.drawRect(rect, paint);
            // 40 代表最后一个时间的宽度,字符串的宽度跟字号，字体有关，应该用android提供的api去得一个字符串的宽度
            int gap = (viewWidth - 40 - 4 * COMMON_PADDING) / (getMaxListSize() - 1);
            if (gap > MAX_GAP) {
                gap = MAX_GAP;
            }
            // if (gap < MIN_GAP) {
            // gap = MIN_GAP;
            // }
            // 画时间

            // 能够容纳点的数量
            int holdNum = (viewWidth - 2 * COMMON_PADDING) / MIN_GAP;

            paint.setColor(getResources().getColor(R.color.medium_orchid));
            paint.setTextSize(20);

            // 画坐标系的框架
            drawAL(0, viewHeight - COMMON_PADDING, viewWidth, viewHeight - COMMON_PADDING, canvas,
                    paint);// X轴
            canvas.drawText("时刻", viewWidth - COMMON_TEXT_PADDING - COMMON_PADDING, viewHeight
                    - COMMON_TEXT_PADDING, paint);

            drawAL(COMMON_PADDING, viewHeight, COMMON_PADDING, 0, canvas, paint);// Y轴
            canvas.drawText("下载速度（KB/S）", COMMON_TEXT_PADDING,
                    COMMON_PADDING * 0.5f + COMMON_TEXT_PADDING, paint);

            int maxSize = getMaxListSize();
            List<Integer> toDisplayTimeList = getDisplayTime(holdNum, maxSize);
            for (int i = 0; i < maxSize; i++) {
                if (toDisplayTimeList.contains(i)) {
                    int timeLeft = (i) * gap;
                    canvas.drawText(i + "", timeLeft + COMMON_PADDING,
                            viewHeight - COMMON_TEXT_PADDING, paint);
                }
            }

            if (mIntranetList != null) {
                paint.setColor(getResources().getColor(R.color.royal_blue));
                drawWave(mIntranetList, gap, canvas, paint, holdNum);
                canvas.drawText("内网", viewWidth - COMMON_PADDING * 2,
                        COMMON_PADDING * 0.5f + COMMON_TEXT_PADDING, paint);
                canvas.drawLine(viewWidth - COMMON_PADDING * 3, COMMON_PADDING * 0.5f, viewWidth
                        - COMMON_PADDING * 2, COMMON_PADDING * 0.5f, paint);

            }
            if (mOutNetList != null) {
                paint.setColor(getResources().getColor(R.color.light_coral));
                drawWave(mOutNetList, gap, canvas, paint, holdNum);
                canvas.drawText("外网", viewWidth - COMMON_PADDING * 2,
                        COMMON_PADDING * 1.5f + COMMON_TEXT_PADDING, paint);
                canvas.drawLine(viewWidth - COMMON_PADDING * 3, COMMON_PADDING * 1.5f, viewWidth
                        - COMMON_PADDING * 2, COMMON_PADDING * 1.5f, paint);
            }

        } catch (Exception e) {
            Log.i(TAG, "error:" + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bgWidth = w;
        bgHeight = h;
        viewHeight = h;
        viewWidth = w;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 画箭头
     * 
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    public void drawAL(int sx, int sy, int ex, int ey, Canvas canvas, Paint paint)
    {
        double H = 12; // 箭头高度
        double L = 3.5; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        canvas.drawLine(sx, sy, ex, ey, paint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, paint);

    }

    // 计算
    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    private void drawWave(List<Float> list, int gap, Canvas canvas, Paint paint, int holdNum) {
        // 高度
        int timeHeight = 40;
        int preiceHeight = viewHeight - timeHeight - 2 * COMMON_PADDING;

        // int startIndex = list.size() - holdNum;// 第一个X坐标
        Log.i(TAG, "list:" + list);
        List<Integer> toDisplaySpeedList = getDisplaySpeed(list, holdNum);
        Log.i(TAG, "toDisplaySpeedList:" + toDisplaySpeedList);
        for (int i = 0; i < list.size(); i++) {
            // // 取到时间
            int timeLeft = (i) * gap;
            // canvas.drawText(i + "", timeLeft + COMMON_PADDING,
            // viewHeight - COMMON_TEXT_PADDING, paint);

            // 画价格
            int intPrice = list.get(i).intValue();
            Float priceTop = (intPrice * preiceHeight / maxPrice);
            // 最少的价格显示在最上面，应该显示在最下面
            priceTop = preiceHeight - priceTop;
            priceTop = priceTop + 20;// 20是价格的高度
            // 绘制下载速度的时候拥挤就只显示峰值或者谷值
            if (getMaxListSize() < holdNum || toDisplaySpeedList.contains(i)) {
                canvas.drawText(intPrice + "", timeLeft + COMMON_PADDING,
                        priceTop + COMMON_PADDING, paint);
            }

            if (i < list.size() - 1) {
                // 画线
                Float startX = (float) timeLeft;
                Float startY = priceTop;
                Float endX = 0f, endY = 0f;
                // 下一个价格的坐标
                endX = (float) ((i + 1) * gap);
                Float intNextPrice = list.get(i + 1);
                endY = (intNextPrice * preiceHeight) / maxPrice;
                endY = preiceHeight - endY;
                endY = endY + 20;
                canvas.drawLine(startX + COMMON_PADDING, startY + COMMON_PADDING, endX
                        + COMMON_PADDING, endY + COMMON_PADDING, paint);
            }

        }
    }

    private int getMaxListSize() {
        int inSize = 0;
        int outSize = 0;
        if (mIntranetList != null) {
            inSize = mIntranetList.size();
        }
        if (mOutNetList != null) {
            outSize = mOutNetList.size();
        }
        return inSize > outSize ? inSize : outSize;
    }

    /**
     * 下载速度显示抽稀， 得到需要显示的下载速度的序列号集合（各个上下顶点）
     * 
     * @param list 传入的完整集合
     * @return
     */
    private List<Integer> getDisplaySpeed(List<Float> list, int holdNum) {
        List<Integer> resultList = Lists.newArrayList();
        if (list == null) {
            return resultList;
        }
        if (list.size() < 3) {
            for (int i = 0; i < list.size(); i++) {
                resultList.add(i);
            }
        } else {
            resultList.add(0);
            for (int i = 1; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    float previous = list.get(i - 1);
                    float current = list.get(i);
                    float next = list.get(i + 1);
                    int space = getMaxListSize() / holdNum + 1;

                    if ((current > previous && current > next) ||
                            (current < previous && current < next)) {
                        float rate = current
                                / (float) list.get(resultList.get(resultList.size() - 1));
                        Log.i(TAG, "rate:" + rate);
                        if (i - resultList.get(resultList.size() - 1) >= space
                                || (rate > 1.1 || rate < 0.9)) {
                            resultList.add(i);
                        }
                    } else if (i - resultList.get(resultList.size() - 1) >= space) {
                        resultList.add(i);
                    }
                }

            }
            resultList.add(list.size() - 1);
        }
        return resultList;
    }

    // 时间轴的抽稀
    private List<Integer> getDisplayTime(int holdNum, int size) {
        List<Integer> indexList = Lists.newArrayList();
        int space = size / holdNum + 1;
        for (int i = 0; i < size; i++) {
            if (i % space == 0) {
                indexList.add(i);
            }
        }
        return indexList;
    }
    // private List<Integer> vacuate(int holdNum, List<Integer> indexList) {
    // if (indexList.size() < holdNum) {
    // return indexList;
    // } else if (indexList.size() > 1) {
    // indexList.get(1) - indexList.get(0) + 1
    // }
    // }
}
