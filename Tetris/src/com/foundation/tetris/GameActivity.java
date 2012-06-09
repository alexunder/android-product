/**
 * 
 */
package com.foundation.tetris;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
public class GameActivity extends Activity 
						    implements TetrisScene.GameOverListener{

	private static final String TAG = "GameActivity";
	
	private static final int MSG_INVALIDATE_VIEW = 0;
	private static final int MSG_GAME_OVER = 1;
	
	private final int DIALOG_GAMEOVER_ID = 0; 
	
	private GameView mView;
	private Timer mTimer;
	
	Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case MSG_INVALIDATE_VIEW: {   
                	  mView.invalidate();
                  }
                  break;
                  case MSG_GAME_OVER: {
                	  showDialog(DIALOG_GAMEOVER_ID);
                  }
                  break;
             }   
             super.handleMessage(msg);   
        }   
    };  
    
    TimerTask mtask;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	        
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	    		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	        
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    mView = new GameView(getBaseContext());
	    setContentView(mView);
		
	    GameStart();
	 }
    
     @Override
	 public void onDestroy() {
    	 super.onDestroy();
    	 mTimer.cancel();
     }
     
     protected Dialog onCreateDialog (int id) {
    	 Dialog dialog;
    	 switch (id) {
    	 	case DIALOG_GAMEOVER_ID: {
    	 		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	 		builder.setMessage("Game Over!")
    	 		       .setCancelable(false)
    	 		       .setPositiveButton("Once again", new DialogInterface.OnClickListener() {
    	 		           public void onClick(DialogInterface dialog, int id) {
    	 		        	  GameStart();
    	 		           }
    	 		       })
    	 		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	 		           public void onClick(DialogInterface dialog, int id) {
    	 		        	    Intent i = new Intent();
    	 						i.setClass(GameActivity.this, TetrisActivity.class);
    	 		    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	 		    			startActivity(i);
    	 						finish();
    	 		           }
    	 		       });
    	 		dialog =  builder.create();
    	 	}
    	 	break;
    	 	default:{
    	 		dialog = null;
    	 	}
    	 }
    	 return dialog;
     }
     
     public void GameOver() {
    	 mTimer.cancel();
    	 mHandler.sendEmptyMessage(MSG_GAME_OVER);
 	 }
     
     public void GameStart() {
    	 mTimer = new Timer();
    	 
    	 mtask = new TimerTask(){  
    	    	 	public void run() {  
    	    	 		Log.v(TAG, "TimerTask");
    	    	 		mView.userDown();
    	    	 		mHandler.sendEmptyMessage(MSG_INVALIDATE_VIEW);
    	    	 	}  
    			};  
    	 
    	 mTimer.schedule(mtask, 100, 1000);
 		 mView.startGame(this);
     }
}
