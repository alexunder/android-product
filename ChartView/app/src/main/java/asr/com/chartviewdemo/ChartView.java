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
    private static final int CHART_AREA_VER_BOTTOM_PADDING = 100;

    private static final int TEXT_TITLE_SIZE = 30;
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
    private DataSource mSource3 = new DataSource();

    private int[] mDefaultLineColor = {Color.RED, Color.GREEN, Color.BLUE};
    private int mDefaultTextColor = Color.GRAY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawCoordinateAxises(canvas);
        DrawDataLines(canvas);
        DrawDataInfo(canvas);
    }

    private void DrawCoordinateAxises(Canvas canvas) {
        int left   = CHART_AREA_HOR_PADDING;
        int top    = CHART_AREA_VER_PADDING;
        int right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int bottom = getMeasuredHeight() - CHART_AREA_VER_PADDING - CHART_AREA_VER_BOTTOM_PADDING;

        int width  = right  - left;
        int height = bottom - top;

        //Draw X axis firstly
        //int yOriginal = bottom;
        //int xOriginal = left;

        canvas.drawLine(left, bottom, right, bottom, mCoordinateAxisPaint);
        //Draw X axis label
        mTextPaint.setTextSize(TEXT_LABEL_SIZE);

        int super_interval_x = 10;
        int super_count_x = (mXMaxValue - mXMinValue) / super_interval_x;
        int draw_interval_x = width / super_count_x;
        for (int i= 0; i <= super_count_x; i++) {
            int px  = left + i * draw_interval_x;
            int py = bottom;
            canvas.drawLine(px, py, px, py - 20, mCoordinateAxisPaint);
            canvas.drawText(mXMinValue + i * super_interval_x + "",
                    px, bottom + TEXT_LABEL_SIZE, mTextPaint);
        }

        //Draw Y axis secondly
        canvas.drawLine(left, bottom, left, top, mCoordinateAxisPaint);

        int super_interval_y = 20;
        int super_count_y = (int)(mYInternalMaxValue - mYInternalMinValue) / super_interval_y;
        int draw_interval_y = height / super_count_y;

        for (int i = 0; i <= super_count_y; i++) {
            int px = left;
            int py = bottom - i * draw_interval_y;
            canvas.drawLine(px, py, px + 20, py, mCoordinateAxisPaint);
            canvas.drawText((int)mYMinValue + i*super_interval_y +"",
                    px - CHART_AREA_HOR_PADDING/2, py, mTextPaint);
        }
    }

    private void DrawDataLines(Canvas canvas) {
        int left   = CHART_AREA_HOR_PADDING;
        int top    = CHART_AREA_VER_PADDING;
        int right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int bottom = getMeasuredHeight() - CHART_AREA_VER_PADDING - CHART_AREA_VER_BOTTOM_PADDING;

        int width  = right  - left;
        int height = bottom - top;

        mXRealInterval = width / mMaxCountOfData;

        if(mSource1.mData != null ) {
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

        if(mSource3.mData != null ) {
            mDataPaint.setColor(mDefaultLineColor[2]);
            float startx = left;
            float starty = top + height - ((mSource3.mData[0] - mYMinValue)* height) / mYInternalMaxValue;
            float endx = 0;
            float endy = 0;
            for (int i = 1; i < mMaxCountOfData; i++) {
                endx = left + i*mXRealInterval;
                endy = top + height - ((mSource3.mData[i] - mYMinValue) * height) / mYInternalMaxValue;
                canvas.drawLine(startx, starty, endx, endy, mDataPaint);
                startx = endx;
                starty = endy;
            }
        }
    }

    private void DrawDataInfo(Canvas canvas) {
        int info_area_left   = CHART_AREA_HOR_PADDING;
        int info_area_top    = getMeasuredHeight() - CHART_AREA_VER_BOTTOM_PADDING;
        int info_area_right  = getMeasuredWidth()  - CHART_AREA_HOR_PADDING;
        int info_area_bottom = getMeasuredHeight();

        int block_length = getMeasuredWidth() / 2;
        int block_height = 20;
        int current_top = info_area_top;

        if (mSource1.mData != null) {
            mFillTitle.setColor(mDefaultLineColor[0]);
            canvas.drawRect(info_area_left, current_top,
                    info_area_left + block_length,
                    current_top + block_height, mFillTitle);

            mTextPaint.setTextSize(TEXT_TITLE_SIZE);
            canvas.drawText(mSource1.name, info_area_left + block_length + 70,
                    current_top + TEXT_TITLE_SIZE/2, mTextPaint);

            current_top = current_top + 33;
        }

        if (mSource2.mData != null) {
            mFillTitle.setColor(mDefaultLineColor[1]);
            canvas.drawRect(info_area_left, current_top,
                    info_area_left + block_length,
                    current_top + block_height, mFillTitle);

            mTextPaint.setTextSize(TEXT_TITLE_SIZE);
            canvas.drawText(mSource2.name, info_area_left + block_length + 70,
                    current_top + TEXT_TITLE_SIZE/2, mTextPaint);

            current_top = current_top + 33;
        }

        if (mSource3.mData != null) {
            mFillTitle.setColor(mDefaultLineColor[2]);
            canvas.drawRect(info_area_left, current_top,
                    info_area_left + block_length,
                    current_top + block_height, mFillTitle);

            mTextPaint.setTextSize(TEXT_TITLE_SIZE);
            canvas.drawText(mSource3.name, info_area_left + block_length + 70,
                    current_top + TEXT_TITLE_SIZE/2, mTextPaint);

            current_top = current_top + 33;
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
        } else if (index == 2) {
            if (mSource3.mData == null)
                mSource3.mData = new int [mMaxCountOfData];

            for (int i = 0; i < mMaxCountOfData; i++)
                mSource3.mData[i] = data.get(i);

            mSource3.name = name;
            mSource3.unit = unit;
        }

        invalidate();
    }
}
