package com.thinking.game.ballandwall;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.thinking.utils.Utils;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( TAG, "onCreate" );
        super.onCreate(savedInstanceState);        
        Utils.toggleImmersiveMode( this );
        DisplayInformation.init( this );
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ( hasFocus ) {
            
        }
    } 
    
    @Override
    protected void onDestroy() {
        Log.d( TAG, "onDestroy" );
        super.onDestroy();
    }
    
}
