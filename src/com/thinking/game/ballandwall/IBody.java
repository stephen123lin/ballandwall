package com.thinking.game.ballandwall;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public interface IBody {    
    public interface OnContactListener {
        void onContact( IBody self, IBody other );
    }
    
    public void goToSleep();
    public boolean isAwake();
    public void performRelease();
    public void setColor( int color );
    public void setDarkColor( int color );
    public float getX();
    public float getY();
    public float getAngle();
    public boolean isSensor();
    public int getCategory();
    public void setLinearVelocity( float cx, float cy );
    public void applyForce( float cx, float cy );
    public void keepVelocity();
    public void move( float cx, float cy );
    public void setOnContactListener( OnContactListener listener );
    public void performContact( IBody self, IBody other );
    public void drawSelf( Canvas canvas, Paint paint );
}
