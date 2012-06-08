/**
 * 
 */
package com.foundation.tetris;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author alexunder
 *
 */
public class GameActivity extends Activity {

	private static final String TAG = "GameActivity";
	
	private static final int MSG_INVALIDATE_VIEW = 0;
	
	private GameView mView;
	private Timer mTimer;
	
	Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case MSG_INVALIDATE_VIEW: {   
                	  mView.invalidate();
                  }
                  break;   
             }   
             super.handleMessage(msg);   
        }   
    };  
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	        
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	    		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	        
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    mTimer = new Timer();
	    mView = new GameView(getBaseContext());
	    setContentView(mView);
	        
	    TimerTask task = new TimerTask(){  
	    	public void run() {  
	    		Log.v(TAG, "TimerTask");
				mView.userDown();
				mHandler.sendEmptyMessage(MSG_INVALIDATE_VIEW);
			}  
		};  
			
		mTimer.schedule(task, 100, 1000);
		mView.startGame();
	 }
    
     @Override
	 public void onDestroy() {
    	 super.onDestroy();
    	 mTimer.cancel();
     }
}
