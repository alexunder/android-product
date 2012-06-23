/**
 * 
 */
package com.foundation.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author alexunder
 *
 */
public class GameView extends View implements OnTouchListener, OnGestureListener {

	private static final String TAG = "GameView";
	
	private static final int PLAY_AREA_LEFT_PADDING = 0;
	private static final int PLAY_AREA_UP_PADDING   = 10;
	private static final int PLAY_AREA_WIDTH_BLOCK  = 10;
	private static final int PLAY_AREA_HEIGHT_BLOCK = 20;
	
	private static final int BLOCK_CUBE_SIZE = 23;
	
	private static final int FLING_MIN_VELOCITY = 200;
	private static final int FLING_MIN_DISTANCE_X = 30;
	private static final int FLING_MIN_DISTANCE_Y = 50;
	
	private Paint fillpaint;
	private Paint strokepaint;
	
	private TetrisScene mScene;
	private Bitmap[] mBlockBitmap = new Bitmap[5]; 
	
	private boolean mIsInitialize;
	
	private GestureDetector mGestureDetector;
	
	private boolean mIsPause;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.v(TAG, "GameView");
		
		fillpaint = new Paint();
		strokepaint = new Paint();
		strokepaint.setAntiAlias(true);
		strokepaint.setStrokeWidth(2);
		strokepaint.setStrokeCap(Paint.Cap.BUTT);
		strokepaint.setStrokeJoin(Paint.Join.ROUND);
		strokepaint.setStyle(Paint.Style.STROKE);
		fillpaint.setStyle(Paint.Style.FILL);
		mScene = new TetrisScene();
		mIsInitialize = false;
		mIsPause = false;
		
		mGestureDetector = new GestureDetector(context, this);  
		mGestureDetector.setIsLongpressEnabled(true);  
	}
	
	@Override
	protected void onAttachedToWindow () {
		super.onFinishInflate();
		
		mBlockBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.block_red);
		mBlockBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.block_yellow);
		mBlockBitmap[2] = BitmapFactory.decodeResource(getResources(), R.drawable.block_blue);
		mBlockBitmap[3] = BitmapFactory.decodeResource(getResources(), R.drawable.block_green);
		mBlockBitmap[4] = BitmapFactory.decodeResource(getResources(), R.drawable.block_purple);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//Draw the back first
		drawSquarePlayerZoneBackground(canvas);
		
		if (!mIsInitialize) {
			return;
		}
		
		int idrawindex = 0;

		for ( int i = 0; i < PLAY_AREA_HEIGHT_BLOCK; i++ )
		for ( int j = 0; j < PLAY_AREA_WIDTH_BLOCK; j++ ) {
			idrawindex = mScene.isOneGridNeedShow(j + PLAY_AREA_WIDTH_BLOCK*i);

			if( idrawindex > 0 ) {
				int xCoordinate = PLAY_AREA_LEFT_PADDING + BLOCK_CUBE_SIZE*j;
				int yCoordinate = PLAY_AREA_UP_PADDING + BLOCK_CUBE_SIZE*i;
				canvas.drawBitmap(mBlockBitmap[idrawindex - 1], xCoordinate, yCoordinate, null); 
			}
		}	
		
		drawNextBlock(canvas);
	}
	
	 @Override
	 public boolean onTouchEvent(MotionEvent me){
		 Log.v(TAG, "onTouchEvent");
	     if (mGestureDetector.onTouchEvent(me))
	    	 return true;
	     else
	         return super.onTouchEvent(me); // or false (it's what you whant).
	    }
	
	@Override
    public boolean onTouch(View v, MotionEvent event) {
		Log.v(TAG, "onTouch");
		return false;
	}
	
	@Override
    public boolean onSingleTapUp(MotionEvent e) {
          Log.v(TAG, "onSingleTapUp");
          if(mIsPause) {
        	  return true;
          }
          
          mScene.user_rotate();
          invalidate();
          return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    	Log.v(TAG, "onShowPress");
    	//mScene.user_rotate();
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
    	Log.v(TAG, "onScroll");
    	return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    	Log.v(TAG, "onLongPress");
    }

    @Override
    public boolean onDown(MotionEvent e) {
    	Log.v(TAG, "onDown");
    	
    	if(mIsPause) {
    		return false;
        }
    	
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	 Log.v(TAG, "onFling");
    	 Log.v(TAG, "velocityX=" + velocityX);
    	 Log.v(TAG, "velocityY=" + velocityY);
    	 Log.v(TAG, "distanceX="  + Math.abs(e1.getX() - e2.getX()));
    	 Log.v(TAG, "distanceY="  + Math.abs(e1.getY() - e2.getY()));
    	 
    	 //left or right
    	 if (Math.abs(velocityX) > FLING_MIN_VELOCITY) {
    		 if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE_X ) {
    			 mScene.user_left();
    			 invalidate();
    		 } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE_X) {
    			 mScene.user_right();
    			 invalidate();
    		 }
    	 } else if(Math.abs(velocityY) > FLING_MIN_VELOCITY) {
    		 if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE_Y) {
    			 mScene.user_fall();
    			 invalidate();
    		 }
    	 }
    	 return false;
    }
	/**
	 * Draws on screen the playable area
	 * 
	 * @param canvas
	 * @param board
	 */
	private void drawSquarePlayerZoneBackground(Canvas canvas) {
		
		int left   = PLAY_AREA_LEFT_PADDING; 
		int top    = PLAY_AREA_UP_PADDING;
		int right  = left + PLAY_AREA_WIDTH_BLOCK*BLOCK_CUBE_SIZE;
		int bottom = top + PLAY_AREA_HEIGHT_BLOCK*BLOCK_CUBE_SIZE;
		int bcolor = 0;
		
		bcolor = getResources().getColor(R.color.BG);
		
		fillpaint.setColor(bcolor);
		strokepaint.setColor(getResources().getColor(R.color.PLAYZONESTROKE));
		strokepaint.setStrokeWidth(1);
		
		canvas.drawRect(left, top, right, bottom, fillpaint);
		canvas.drawRect(left, top, right, bottom, strokepaint);
	}
	
	private void drawNextBlock(Canvas canvas) {
		int left = PLAY_AREA_LEFT_PADDING + PLAY_AREA_WIDTH_BLOCK*BLOCK_CUBE_SIZE;
		//int right = left + BLOCK_CUBE_SIZE*4;
		int top = PLAY_AREA_UP_PADDING + 25;
		//int bottom = top + BLOCK_CUBE_SIZE*4;
		
		byte [] data = new byte[16];
		if(mScene.GetNextBlockData(data, 16)) {
			for( int i = 0; i < 4; i++)
				for( int j = 0; j < 4; j++)
				{
					if(data[j + i*4] > 0) {
						int xCoordinate = left + BLOCK_CUBE_SIZE*j;
						int yCoordinate = top + BLOCK_CUBE_SIZE*i;
						canvas.drawBitmap(mBlockBitmap[data[j+i*4] - 1], xCoordinate, yCoordinate, null);	
					} 
				}
		}
	}
	
	public void startGame(TetrisScene.GameOverListener listener) {
		Log.v(TAG, "startGame");
		mScene.CreateScene(PLAY_AREA_WIDTH_BLOCK, PLAY_AREA_HEIGHT_BLOCK);
		mScene.StartGame();
		mScene.setGameOverListener(listener);
		mIsInitialize = true;
	}
	
	public void userDown() {
		mScene.user_down();
	}
	
	public void SetPauseState(boolean state) {
		mIsPause = state;
	}
}
