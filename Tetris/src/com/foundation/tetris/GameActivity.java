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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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
	private Button mPauseBtn;
	
	private boolean mIsPause = false;;
	
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
	        
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	    		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	        
	    //mView = new GameView(getBaseContext());
	    setContentView(R.layout.game);
		
	    mView = (GameView)findViewById(R.id.gameview);
	    mPauseBtn = (Button)findViewById(R.id.pause);
	    
	    mPauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if(mIsPause) {
            		gameResume();
            	} else {
            		gamePause();
            	}
            }
        });
	    
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
     
     private void LaunchTimer() {
    	 mTimer = new Timer();
    	 
    	 mtask = new TimerTask(){  
    	    	 	public void run() {  
    	    	 		mView.userDown();
    	    	 		mHandler.sendEmptyMessage(MSG_INVALIDATE_VIEW);
    	    	 	}  
    			};  
    	 
    	 mTimer.schedule(mtask, 100, 1000);
     }
     
     public void GameOver() {
    	 mTimer.cancel();
    	 mHandler.sendEmptyMessage(MSG_GAME_OVER);
 	 }
     
     public void GameStart() {
    	 LaunchTimer();
 		 mView.startGame(this);
     }
     
     public void gamePause() {
    	 mTimer.cancel();
    	 mView.SetPauseState(true);
    	 mPauseBtn.setText("Resume");
    	 mIsPause = true;
     }
     
     public void gameResume() {
    	 LaunchTimer();
    	 
    	 mView.SetPauseState(false);
    	 mPauseBtn.setText("Pause");
    	 mIsPause = false;
     }
     
}
