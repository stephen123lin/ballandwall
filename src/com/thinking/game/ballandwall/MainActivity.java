package com.thinking.game.ballandwall;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.thinking.utils.Utils;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private GameWorld mWorld;
    private float mX;
    private float mY;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( TAG, "onCreate" );
        super.onCreate(savedInstanceState);        
        Utils.toggleImmersiveMode( this );
        DisplayUtils.init( this );        
        mWorld = new GameWorld( this );        
        setContentView( mWorld.getSurfaceView() );
        mWorld.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ( hasFocus ) {
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int count = event.getPointerCount();
        int action = event.getAction();
        int mask = event.getActionMasked();
        switch ( action & mask ) {
        case MotionEvent.ACTION_POINTER_DOWN:
            if ( 2 == count ) {
                float x1 = event.getX(1);
                float y1 = event.getY(1);
                x = ( x + x1 ) / 2;
                y = ( y + y1 ) / 2;
                Log.v( TAG, String.format( "touch: %.2f %.2f", x, y ) );
                mWorld.testBall( x, y );
            }
            break;
        case MotionEvent.ACTION_DOWN:            
            break;
        case MotionEvent.ACTION_UP:
            mX = -1.0f;
            mY = -1.0f;
            break;
        case MotionEvent.ACTION_MOVE:
            if ( 1 == count ) {                
                if ( mX < 0.0f && mY < 0.0f ) {
                    mX = x;
                    mY = y;
                }
                else if ( Math.abs( x-mX ) > 100.0f ) {
                    Log.e( TAG, "unexpected move: too far" );                    
                }
                else {                    
//                    mGameRunnable.touchMove( x-mX, 0 );
                    // TODO:
                    // move the stick
                    mX = x;
                    mY = y;
                }
                
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        Log.d( TAG, "onDestroy" );
        super.onDestroy();
        mWorld.stop();
        mWorld = null;
    }

}
