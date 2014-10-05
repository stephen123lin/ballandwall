package com.thinking.game.ballandwall;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

public class GameWorld {
    private static final String TAG = "GameWorld";

    private Object mLock = new Object();
    private BodyFactory mFactory;
    private GameSurfaceView mSurfaceView;
    private AtomicBoolean mIsActive;
    
    public GameWorld( Context context ) {
        mFactory = new BodyFactory();
        mSurfaceView = new GameSurfaceView( context );
        mIsActive = new AtomicBoolean( false );
    }
    public void start() {
        initWall();
        mIsActive.set( true );
        new Thread() {
            public void run() {
                while ( mIsActive.get() ) {
                    // TODO:
                    synchronized ( mLock ) {
                        mSurfaceView.repaint( mFactory.getBodyList() );
                        mFactory.step();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep( 10 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }                    
                }
                // end of loop
                Log.v( TAG, "Game world is NOT active anymore" );
                mSurfaceView = null;
                mFactory.release();
                mFactory = null;
            };
        }.start();
    }
    public void stop() {
        mIsActive.set( false );
    }
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }
    
    public void testBall( float x, float y ) {               
        synchronized ( mLock ) {
            float ballRadius = DisplayUtils.kWorldAtomLength * 2.0f;
            float velocityLength = ballRadius;
            double angle = 360.0 * Math.random();
            float velocityHx = (float) ( velocityLength * Math.cos( angle ) );
            float velocityHy = (float) ( velocityLength * Math.sin( angle ) );
            
            Body body = mFactory.addBall( x, y, ballRadius, 0.0f );
            if ( body != null ) {
                Vec2 velocity = new Vec2( velocityHx, velocityHy );
                body.setLinearVelocity( velocity );
                BodyUserData userData = new BodyUserData();
                userData.mVelocityLength = velocityLength;
                body.setUserData( userData );
            }
        }        
    }
    private void initWall() {
        final float wallWidth = DisplayUtils.kWorldAtomLength/10;  
        
        // bottom
        float hx = DisplayUtils.kScreenWidth/2;
        float hy = wallWidth;
        float x = hx;
        float y = DisplayUtils.kWorldBottom+hy;
        Log.v( TAG, String.format( "initWall( %.2f, %.2f ) with ( %.2f, %.2f )",
            x, y, hx, hy 
        ));
        mFactory.addWall( x, y, hx, hy, 0.0f );
        
        // top
        y = DisplayUtils.kWorldTop-hy;
        Log.v( TAG, String.format( "initWall( %.2f, %.2f ) with ( %.2f, %.2f )",
            x, y, hx, hy 
        ));
        mFactory.addWall( x, y, hx, hy, 0.0f );
        
        // left
        hx = wallWidth;
        hy = DisplayUtils.kScreenHeight/2;
        x = hx;
        y = hy;
        Log.v( TAG, String.format( "initWall( %.2f, %.2f ) with ( %.2f, %.2f )",
            x, y, hx, hy 
        ));
        mFactory.addWall( x, y, hx, hy, 0.0f );
        
        // right
        x = DisplayUtils.kWorldRight+hx;
        Log.v( TAG, String.format( "initWall( %.2f, %.2f ) with ( %.2f, %.2f )",
            x, y, hx, hy 
        ));
        mFactory.addWall( x, y, hx, hy, 0.0f );
    }
}
