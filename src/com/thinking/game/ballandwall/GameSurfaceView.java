package com.thinking.game.ballandwall;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Body;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "GameSurfaceView";    
    
    private Object mLock = new Object();
    private Paint mPaint;
    private List< Body > mVecBody;
    private int mBkColor;        
    
    public GameSurfaceView( Context context ) {
        super(context);
        
        mBkColor = Color.BLACK;
        mPaint = new Paint();
        mPaint.setAntiAlias( true );    
        mVecBody = new ArrayList<Body>();
        getHolder().addCallback( this );
    }
    public void release() {
        synchronized ( mLock ) {
            getHolder().removeCallback( this );
            mVecBody = null;
            mPaint = null;
        }        
    }
    
    // call back
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d( TAG, "surfaceCreated" );
        repaint();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d( TAG, "surfaceChanged" );
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d( TAG, "surfaceDestroyed" );
    }
    // TODO:
    // draw all body in the world 
    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d( TAG, "onDraw" );        
        if ( null == canvas ) {
            // This case will not happen actually
            Log.e( TAG, "Fail to onDraw: null canvas" );
            return ;
        }
        Paint paint = getPaint();
        List< Body > vecBody = getBodyList();
        if ( null == paint || null == vecBody ) {
            Log.e( TAG, "Failed to onDraw: null paint or body list" );
            return ;
        }
        // background color
        canvas.drawColor( mBkColor );
        // draw the all
        // TODO:
        for ( Body body: vecBody ) {
            drawBody( canvas, paint, body );
        }
    }
    
    // TODO:
    // public method
    public void repaint( List< Body > vecBody ) {
        setBodyList( vecBody );
        repaint();
    }
    
    public void repaint() {
//        Log.d( TAG, "repaint" );
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if ( null == canvas ) {
            Log.e( TAG, "Fail to repaint: null canvas" ); 
            return ;            
        }
        synchronized ( holder ) {
            onDraw( canvas );            
        }
        holder.unlockCanvasAndPost( canvas );
    }
    
    // TODO:
    // private method
    private Paint getPaint() {
        synchronized ( mLock ) {
            return mPaint;            
        }
    }
    private void setBodyList( List< Body > vecBody ) {
        synchronized ( mLock ) {
            mVecBody = vecBody;            
        }
    }
    private List< Body > getBodyList() {
        synchronized ( mLock ) {
            return mVecBody;
        }
    }

    private static void drawBody( Canvas canvas, Paint paint, Body body ) {
        if ( null == canvas || null == paint || null == body ) {
            Log.e( TAG, "Failed to drawBody: null object" );
            return ;
        }
        ShapeType shapeType = body.getFixtureList().getShape().getType();
        if ( ShapeType.CIRCLE == shapeType ) {
            DisplayUtils.drawCircle( canvas, paint, 
                body.getPosition().x, body.getPosition().y, 
                body.getFixtureList().getShape().getRadius(),
                body.getAngle(), 
                Color.YELLOW
            );
        }
        else if ( ShapeType.POLYGON == shapeType ) {
            PolygonShape shape = (PolygonShape) body.getFixtureList().getShape();
            float x = body.getPosition().x;
            float y = body.getPosition().y;
            float left = shape.getVertex(0).x;
            float top = shape.getVertex(0).y;
            float right = shape.getVertex(2).x;
            float bottom = shape.getVertex(2).y;
            DisplayUtils.drawPolygon( canvas, paint, 
                x, y, right, bottom, 
                body.getAngle(), 
                Color.GREEN
            );
            
        }
        else {
            Log.e( TAG, "Failed to onDraw due to unknown shape type: " + shapeType );
        }
    }   
    
}
