package com.thinking.game.ballandwall;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class DisplayInformation {
    private static final String TAG = "DisplayInformation";
    
    public static float kScreenWidth;
    public static float kScreenHeight;
    
    public static float kWorldLeft;
    public static float kWorldRight;
    public static float kWorldTop;
    public static float kWorldBottom;
    public static float kWorldAtomLength;
    
    public static void init( Context context ) {
        DisplayMetrics dispMetrics = new DisplayMetrics();
        DisplayManager dm = (DisplayManager)context.getSystemService( Context.DISPLAY_SERVICE );
        Display disp = dm.getDisplay(0);        
        if ( disp != null ) {
            disp.getMetrics( dispMetrics );
            kScreenWidth = dispMetrics.widthPixels;
            kScreenHeight = dispMetrics.heightPixels;
        }
        else {
            kScreenWidth = 100.0f;
            kScreenHeight = 100.0f;
        }
        Log.v( TAG, String.format( "screen: [ %.2f, %.2f ] dpi: [ %.2f ]", 
            kScreenWidth, kScreenHeight, dispMetrics.density
        ));
        
        kWorldAtomLength = kScreenWidth * 0.02f;
        final float kWorldPadding = kWorldAtomLength*2;
        kWorldLeft = kWorldPadding;
        kWorldRight = kScreenWidth-kWorldPadding;
        kWorldTop = kWorldPadding;
        kWorldBottom = kScreenHeight;        
        
        Log.v( TAG, String.format( "world range: [ %.2f, %.2f, %.2f, %.2f ]", 
            kWorldLeft, kWorldTop, kWorldRight, kWorldBottom
        ));
    }
}
