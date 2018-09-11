package asr.com.chartviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static java.lang.Math.abs;

/**
 * Created by Alexlander on 9/10/18.
 */

public class ChartView extends View {
    public static final String TAG = ChartView.class.getSimpleName();

    private static final int CHART_AREA_HOR_PADDING = 50;
    private static final int CHART_AREA_VER_PADDING = 50;
    private static final int CHART_AREA_VER_TOP_PADDING = 100;

    private static final int TEXT_TITLE_SIZE = 50;
    private static final int TEXT_LABEL_SIZE = 25;

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
        mCoordinateAxisPaint.setColor(mDefaultTextColor);

        mFillTitle = new Paint();
        mFillTitle.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mXMaxValue = 60;
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
    private Paint mFillTitle;
    private Paint mTextPaint;

    private int mXMaxValue;
    private int mXMinValue;
    private float mYMaxValue;
    private float mYMinValue;
    private float mYInternalMaxValue;
    private float mYInternalMinValue;
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
        int top    = CHART_AREA_VER_TOP_PADDING;
        int right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int bottom = getMeasuredHeight() - CHART_AREA_HOR_PADDING;

        int width  = right  - left;
        int height = bottom - top;

        //Draw X axis firstly
        //int yOriginal = bottom;
        //int xOriginal = left;

        canvas.drawLine(left, bottom, right, bottom, mCoordinateAxisPaint);
        //Draw X axis label
        mTextPaint.setTextSize(TEXT_LABEL_SIZE);
        canvas.drawText(mXMinValue + "", left,
                bottom + TEXT_LABEL_SIZE, mTextPaint);
        canvas.drawText(mXMaxValue + "", left + width,
                bottom + TEXT_LABEL_SIZE, mTextPaint);

        //Draw Y axis secondly
        canvas.drawLine(left, bottom, left, top, mCoordinateAxisPaint);

        Log.d(TAG, "mYMinValue=" + mYMinValue );
        Log.d(TAG, "mYMaxValue=" + mYMaxValue );
        mTextPaint.setTextSize(TEXT_LABEL_SIZE);
        canvas.drawText((int)mYMinValue + "", left - CHART_AREA_HOR_PADDING/2,
                bottom, mTextPaint);
        canvas.drawText((int)mYMaxValue + "", left - CHART_AREA_HOR_PADDING/2,
                bottom - height, mTextPaint);
    }

    private void DrawDataLines(Canvas canvas) {
        int left   = CHART_AREA_HOR_PADDING;
        int top    = CHART_AREA_VER_TOP_PADDING;
        int right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int bottom = getMeasuredHeight() - CHART_AREA_VER_PADDING;

        int width  = right  - left;
        int height = bottom - top;

        mXRealInterval = width / mMaxCountOfData;

        if(mSource1.mData != null ) {
            //Draw the title area
            mFillTitle.setColor(mDefaultLineColor[0]);
            canvas.drawRect(left, 0, left + width / 2,
                    top, mFillTitle);

            mTextPaint.setTextSize(TEXT_TITLE_SIZE);
            canvas.drawText(mSource1.name, left + width / 4, TEXT_TITLE_SIZE, mTextPaint);

            mDataPaint.setColor(mDefaultLineColor[0]);
            float startx = left;
            float starty = top + height - ((mSource1.mData[0]- mYMinValue) * height) / mYInternalMaxValue;
            float endx = 0;
            float endy = 0;
            for (int i = 1; i < mMaxCountOfData; i++) {
                endx = left + i*mXRealInterval;
                endy = top + height - ((mSource1.mData[i] - mYMinValue) * height) / mYInternalMaxValue;
                canvas.drawLine(startx, starty, endx, endy, mDataPaint);
                startx = endx;
                starty = endy;
            }
        }

        if(mSource2.mData != null ) {
            //Draw the title area
            mFillTitle.setColor(mDefaultLineColor[1]);
            canvas.drawRect(right - width / 2, 0, right,
                    top, mFillTitle);

            mTextPaint.setTextSize(TEXT_TITLE_SIZE);
            canvas.drawText(mSource2.name, right - width / 4, TEXT_TITLE_SIZE, mTextPaint);

            mDataPaint.setColor(mDefaultLineColor[1]);
            float startx = left;
            float starty = top + height - ((mSource2.mData[0] - mYMinValue)* height) / mYInternalMaxValue;
            float endx = 0;
            float endy = 0;
            for (int i = 1; i < mMaxCountOfData; i++) {
                endx = left + i*mXRealInterval;
                endy = top + height - ((mSource2.mData[i] - mYMinValue) * height) / mYInternalMaxValue;
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
        mYInternalMaxValue = mYMaxValue - mYMinValue;
        mYInternalMinValue = 0;
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

        invalidate();
    }
}
