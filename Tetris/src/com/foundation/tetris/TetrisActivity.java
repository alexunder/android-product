package com.foundation.tetris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class TetrisActivity extends Activity {
	
	private static final String TAG = "TetrisActivity";
	
	private Button mbuttonStart;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // remove title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        mbuttonStart = (Button)findViewById(R.id.start_button);
        
        mbuttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Log.v(TAG, "Button onClick");
            	
            	Intent i = new Intent();
				i.setClass(v.getContext(), GameActivity.class);
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			startActivity(i);
				finish();
            }
        });
    }
}