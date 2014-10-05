package com.thinking.game.ballandwall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class DisplayUtils {
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
        final float kWorldPadding = DisplayUtils.kWorldAtomLength/10;
        kWorldLeft = kWorldPadding;
        kWorldRight = kScreenWidth-kWorldPadding;
        kWorldTop = kWorldPadding;
        kWorldBottom = kScreenHeight;        
        
        Log.v( TAG, String.format( "world range: [ %.2f, %.2f, %.2f, %.2f ]", 
            kWorldLeft, kWorldTop, kWorldRight, kWorldBottom
        ));
    }
    
    public static void drawPolygon(Canvas canvas, Paint paint, 
        float x, float y, float hx, float hy, float angle, int color
    ) {
        x = BodyFactory.worldToScreen(x);
        y = BodyFactory.worldToScreen(y);
        hx = BodyFactory.worldToScreen(hx);
        hy = BodyFactory.worldToScreen(hy);
        angle = BodyFactory.degreeToAngle(angle);
        
        paint.reset();
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.save();
        canvas.rotate(angle, x, y);
        canvas.drawRect(x-hx, y-hy, x+hx, y+hy, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(1.0f);
        canvas.drawRect(x-hx, y-hy, x+hx, y+hy, paint);
        canvas.restore();
    }
    
    public static void drawCircle(Canvas canvas, Paint paint, 
        float x, float y, float radius, float degree, int color
    ) {
        x = BodyFactory.worldToScreen(x);
        y = BodyFactory.worldToScreen(y);
        radius = BodyFactory.worldToScreen(radius);
        
        paint.reset();        
        paint.setColor(color);
        paint.setStyle(Style.FILL_AND_STROKE);  
        paint.setStrokeWidth(1.0f);
        canvas.drawCircle(x, y, radius, paint);
        paint.setColor( Color.BLACK );
        paint.setStyle(Style.STROKE);    
        paint.setStrokeWidth(1.0f);        
        float stopX = (float) ( x + radius * Math.cos( degree ) );
        float stopY = (float) ( y + radius * Math.sin( degree ) );    
        canvas.drawLine( x, y, stopX, stopY, paint );
    }
    
}
