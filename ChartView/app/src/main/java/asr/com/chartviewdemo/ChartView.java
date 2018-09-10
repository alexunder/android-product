package asr.com.chartviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static android.graphics.BlurMaskFilter.Blur.SOLID;
import static java.lang.Math.abs;

/**
 * Created by xuchenyu on 9/10/18.
 */

public class ChartView extends View {
    public static final String TAG = ChartView.class.getSimpleName();

    private static final int CHART_AREA_HOR_PADDING = 50;
    private static final int CHART_AREA_VER_PADDING = 50;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCoordinateAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mDataPaint.setColor(mDefaultLineColor[0]);
        mDataPaint.setStyle(Paint.Style.STROKE);
        mDataPaint.setStrokeWidth(4);

        //mCoordinateAxisPaint.setTextSize(Util.size2sp(defaultXySize, getContext()));
        mCoordinateAxisPaint.setColor(mDefaultTextColor);

        mXMaxValue = 59;
        mXMinValue = 0;
        mYMaxValue = 100;
        mYMinValue = 0;
        mXRealInterval = 1;
        mYRealInterval = 1;
        mXCoordinateInterval = 10;
        mYCoordinateInterval = 10;

        mMaxCountOfData = mXMaxValue - mXMinValue;
    }

    private Paint mCoordinateAxisPaint;
    private Paint mDataPaint;
    private int mXMaxValue;
    private int mXMinValue;
    private float mYMaxValue;
    private float mYMinValue;
    private float mXRealInterval;
    private float mYRealInterval;
    private int mXCoordinateInterval;
    private int mYCoordinateInterval;
    private int mMaxCountOfData;

    //We only support two lines with the same scale
    public class DataSource {
        public int mData[];
        String name;
        String unit;
    }
    private DataSource mSource1 = new DataSource();
    private DataSource mSource2 = new DataSource();

    private int[] mDefaultLineColor = {Color.RED, Color.GREEN, Color.BLUE};
    private int mDefaultTextColor = Color.GRAY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawCoordinateAxises(canvas);
        DrawDataLines(canvas);
    }

    private void DrawCoordinateAxises(Canvas canvas) {
        int left   = CHART_AREA_HOR_PADDING;
        int top    = CHART_AREA_VER_PADDING;
        int right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int bottom = getMeasuredHeight() - CHART_AREA_HOR_PADDING;

        int width  = right  - left;
        int height = bottom - top;

        //Draw X axis firstly
        int yOriginal = bottom;
        if (mYMinValue < 0.0) {
            yOriginal =(int)((mYMaxValue*height) / (mYMaxValue + abs(mYMinValue)));
        }
        canvas.drawLine(left, yOriginal, right, yOriginal, mCoordinateAxisPaint);

        //Draw Y axis secondly
        canvas.drawLine(left, yOriginal, left, top, mCoordinateAxisPaint);
    }

    private void DrawDataLines(Canvas canvas) {
        int left   = CHART_AREA_HOR_PADDING;
        int top    = CHART_AREA_VER_PADDING;
        int right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int bottom = getMeasuredHeight() - CHART_AREA_HOR_PADDING;

        int width  = right  - left;
        int height = bottom - top;

        mXRealInterval = width / mMaxCountOfData;

        if(mSource1.mData != null ) {
            mDataPaint.setColor(mDefaultLineColor[0]);
            float startx = left;
            float starty = top + height - (mSource1.mData[0] * height) / mYMaxValue;
            float endx = 0;
            float endy = 0;
            for (int i = 1; i < mMaxCountOfData; i++) {
                endx = left + i*mXRealInterval;
                endy = top + height - (mSource1.mData[i] * height) / mYMaxValue;
                canvas.drawLine(startx, starty, endx, endy, mDataPaint);
                startx = endx;
                starty = endy;
            }
        }

        if(mSource2.mData != null ) {
            mDataPaint.setColor(mDefaultLineColor[1]);
            float startx = left;
            float starty = top + height - (mSource2.mData[0] * height) / mYMaxValue;
            float endx = 0;
            float endy = 0;
            for (int i = 1; i < mMaxCountOfData; i++) {
                endx = left + i*mXRealInterval;
                endy = top + height - (mSource2.mData[i] * height) / mYMaxValue;
                canvas.drawLine(startx, starty, endx, endy, mDataPaint);
                startx = endx;
                starty = endy;
            }
        }
    }

    //Public interfaces
    void setXScale(int min, int max) {
        mXMaxValue = max;
        mXMinValue = min;
        mMaxCountOfData = mXMaxValue - mXMinValue;
    }

    void setYScale(float min, float max) {
        mYMinValue = min;
        mYMaxValue = max;
    }

    void setDataSource(CircularFifoQueue<Integer> data, String name, String unit, int index) {
        if (data.maxSize() != mMaxCountOfData) {
            Log.e(TAG, "The data size is not compatible with chart setting.");
            return;
        }

        if (index == 0) {
            if (mSource1.mData == null)
                mSource1.mData = new int [mMaxCountOfData];

            for (int i = 0; i < mMaxCountOfData; i++)
                mSource1.mData[i] = data.get(i);

            mSource1.name = name;
            mSource1.unit = unit;
        } else if (index == 1) {
            if (mSource2.mData == null)
                mSource2.mData = new int [mMaxCountOfData];

            for (int i = 0; i < mMaxCountOfData; i++)
                mSource2.mData[i] = data.get(i);

            mSource2.name = name;
            mSource2.unit = unit;
        }
    }
}
