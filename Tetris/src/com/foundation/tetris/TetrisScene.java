/**
 * 
 */
package com.foundation.tetris;

import android.util.Log;

/**
 * @author alexunder
 *
 */
public class TetrisScene {
	private static final String TAG = "TetrisScene";
	
	private TetrisContext  mcontext_freeze;
	private TetrisContext  mcontext_activity;
	private TetrisBlock    mblock;

	private int m_i_current_block_index;
	private int m_i_next_block_index;

	private GameOverListener mListener;
	
	public interface GameOverListener{
		public void GameOver();
	}

	public TetrisScene() {
		mcontext_freeze   = null;
		mcontext_activity = null;
		mblock = null;
		
		m_i_current_block_index = 0;
		m_i_next_block_index    = 0;
	}
	
	public boolean CreateScene(int iwidth, int ilength) {
		mcontext_activity = TetrisContext.CreateSceneContext( 2, iwidth, ilength );

		if(mcontext_activity == null) {
			return false;
		}

		mcontext_freeze = TetrisContext.CreateSceneContext( 2, iwidth, ilength );
		
		if(mcontext_activity == null) {
			return false;
		}

		mblock = new TetrisBlock();

		return true;
	}
	
	public void StartGame()
	{
		mcontext_freeze.ClearSceneContext();
		mcontext_activity.ClearSceneContext();
		//BlockFactory();
		m_i_current_block_index = MathUtil.generateRandomNumber( 0, 4);

		mblock.initBlock(mcontext_freeze.getSceneData(), 
							mcontext_freeze.getXSize(),
							mcontext_freeze.getYSize(),
							m_i_current_block_index);
		m_i_next_block_index = MathUtil.generateRandomNumber( 0, 4);
		mblock.draw(mcontext_activity);
	}
		
	public void EndGame() {
      //maybe invoke a callback function
		Log.v(TAG, "EndGame");
		mListener.GameOver();
	}
	
	public void setGameOverListener(GameOverListener listener) {
		mListener = listener;
	}
	
	public void user_right() {
		mblock.move_right();
		DrawBlock();
	}
		
	public void user_left() {
		mblock.move_left();
		DrawBlock();
	}
		
	public void user_fall() {
		while (!mblock.isBlockDown()) {
			mblock.fall_slow();
			DrawBlock();
		}

		CheckGameStatus();
	}
		
	public void user_down() {
		mblock.fall_slow();
		DrawBlock();

		if ( mblock.isBlockDown() ) {
			CheckGameStatus();
		}
	}

	public void user_rotate() {
		mblock.rotate();
		DrawBlock();
	}
	
	public boolean GetNextBlockData( byte[] data, int isize ) {
		if(data == null || isize != TetrisBlock.BLOCK_DATA_SIZE) {
			return false;
		}

		for(int i = 0; i < TetrisBlock.BLOCK_DATA_SIZE; i++) {
			data[i] = 0;
		}

		switch ( m_i_next_block_index ) {
		case TetrisBlock.BLOCK_CUBIC: {
				data[5]  = 1;
				data[6]  = 1;
				data[9]  = 1;
				data[10] = 1;
			 }
			 break;
		case TetrisBlock.BLOCK_BAR: {
				 data[1]  = 2;
				 data[5]  = 2;
				 data[9]  = 2;
				 data[13] = 2;
			 }
			 break;
		case TetrisBlock.BLOCK_LHOOK: {
				 data[1]  = 3;
				 data[2]  = 3;
				 data[6]  = 3;
				 data[10] = 3;
			 }
			 break;
		case TetrisBlock.BLOCK_RHOOK: {
				data[1]  = 4;
				data[2]  = 4;
				data[5]  = 4;
				data[9]  = 4;
			}
			break;
		case TetrisBlock.BLOCK_MIDDLE: {
				 data[1]  = 5;
				 data[6]  = 5;
				 data[5]  = 5;
				 data[9]  = 5;
			 }
			 break;
		}

		return true;
	}
	
	int isOneGridNeedShow( int index ) {
		return mcontext_activity.getLinearPoint(index);
	}

	/*Private method*/
	private void DrawBlock() {
		//Sync_Scene_Data(mcontext_activity, mcontext_freeze);
		//static ----> active
		mcontext_activity.SyncFromOtherSceneData(mcontext_freeze);
		mblock.draw(mcontext_activity);
	}
	
	private void CheckGameStatus() {
		int i;
		int j;
		int flag = 0;

		int x;
		int y;
		//firstly,check that is need to clear blocks
		for(i = 0; i < mcontext_activity.getYSize(); i++) {
			for(j = 0; j < mcontext_activity.getXSize(); j++) {
				if(mcontext_activity.getPixel(j, i) == 0) {                   
					flag=0;
					break;
				}
				else {
					flag=1;
				}
			}
			
			//when the ith floor can be clear
			if(flag == 1) {
				for(j = 0; j < mcontext_activity.getXSize(); j++) {
					mcontext_activity.setPixel((byte)0, j, i);
				}

				for(x = i-1; x >= 0; x--)
					for(y = 0; y < mcontext_activity.getXSize(); y++) {
					    if(mcontext_activity.getPixel(y, x) != 0) {
							//m_pcontext_activity->pSceneData[y + (x+1)*m_pcontext_activity->b_x_size] = m_pcontext_activity->pSceneData[y + x*m_pcontext_activity->b_x_size];
							byte value = mcontext_activity.getPixel(y, x);
							mcontext_activity.setPixel(value, y,  x + 1);
							mcontext_activity.setPixel((byte)0, y, x);
							//m_pcontext_activity->pSceneData[y + x*m_pcontext_activity->b_x_size] = 0;
						}
					}
			}
		}

		for(i = 0; i < mcontext_activity.getXSize(); i++) {
			if ( mcontext_activity.getPixel(i, 0)!= 0 ) {
				EndGame();
				return;
			}
		}
		
		//Sync_Scene_Data(mcontext_freeze, mcontext_activity);
		//Active ----> static
		mcontext_freeze.SyncFromOtherSceneData(mcontext_activity);
		BlockFactory();
	}
	
	private void BlockFactory() {
		m_i_current_block_index = m_i_next_block_index;
		m_i_next_block_index = MathUtil.generateRandomNumber( 0, 4);

		mblock.initBlock(mcontext_freeze.getSceneData(), 
							mcontext_freeze.getXSize(),
							mcontext_freeze.getYSize(),
							m_i_current_block_index);
		
		mblock.draw(mcontext_activity);
	}
}
