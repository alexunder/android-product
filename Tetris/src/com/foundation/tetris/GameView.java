/**
 * 
 */
package com.foundation.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * @author alexunder
 *
 */
public class GameView extends View {

	private static final String TAG = "GameView";
	
	private static final int PLAY_AREA_LEFT_PADDING = 0;
	private static final int PLAY_AREA_UP_PADDING   = 10;
	private static final int PLAY_AREA_WIDTH_BLOCK  = 10;
	private static final int PLAY_AREA_HEIGHT_BLOCK = 20;
	
	private static final int BLOCK_CUBE_SIZE = 23;
	
	private Paint fillpaint;
	private Paint strokepaint;
	
	private TetrisScene mScene;
	private Bitmap[] mBlockBitmap = new Bitmap[5]; 
	
	private boolean mIsInitialize;

	public GameView(Context context) {
		super(context);
		
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
}
