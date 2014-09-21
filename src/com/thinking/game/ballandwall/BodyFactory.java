package com.thinking.game.ballandwall;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import android.util.Log;

public class BodyFactory {
    private static final String TAG = "BodyFactory";
    
    public static final float DEGTORAD = 0.0174532925199432957f;
    public static final float RADTODEG = 57.295779513082320876f;
    public static final float RATE = 10.0f;
    public static final float TIME_STEP = 2.0f/60.0f;
    public static final int VEL_ITER = 6;
    public static final int POS_ITER = 6;
    
    private World mWorld;
    private List< Contact > mVecContact;
    private Object mLock = new Object();
    
    public BodyFactory() {
        Log.d( TAG, "BodyFactory.init" );
        
        mWorld = new World( new Vec2( 0.0f, 0.0f ) );
        mWorld.setAllowSleep( true );
        
        mWorld.setContactListener( new ContactListener() {
            @Override
            public void preSolve(Contact arg0, Manifold arg1) {
            }
            @Override
            public void postSolve(Contact arg0, ContactImpulse arg1) {
            }
            @Override
            public void endContact(Contact contact) {
                
                Body bodyA = contact.getFixtureA().getBody();                
                Body bodyB = contact.getFixtureB().getBody();
                if ( bodyA != null && bodyB != null ) {
                    if ( bodyA.m_fixtureList.isSensor() || bodyB.m_fixtureList.isSensor() ) {
                        return ;
                    }
                    // TODO:
                    // push to contact vector
                    List< Contact > vecContact = getContact();
                    if ( null == vecContact ) {
                        Log.e( TAG, "Failed to add contact: null vecVector" );
                        return ;
                    }
                    vecContact.add( contact );
                }
            }
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();                
                Body bodyB = contact.getFixtureB().getBody();
                if ( bodyA != null && bodyB != null ) {
                    if ( bodyA.m_fixtureList.isSensor() || bodyB.m_fixtureList.isSensor() ) {
                        // TODO:
                        // push to contact vector
                        List< Contact > vecContact = getContact();
                        if ( null == vecContact ) {
                            Log.e( TAG, "Failed to add contact: null vecVector" );
                            return ;
                        }
                        vecContact.add( contact );
                    }                    
                }
            } // end of public void beginContact(Contact contact) {
        }); // end of mWorld.setContactListener(
        
        mVecContact = new ArrayList< Contact >();
    }    
    public void release() {
        Log.d( TAG, "release" );
        synchronized ( mLock ) {
            if ( mWorld != null ) {
                mWorld.setContactListener( null );
                mWorld = null;
            }
            if ( mVecContact != null ) {
                mVecContact.clear();
                mVecContact = null;
            }
        }
    }
    
    // public static method    
    public static float worldToScreen( float val ) {
        return val * RATE;
    }
    public static float screenToWorld( float val ) {
        return val / RATE;
    }
    public static float angleToDegree( float angle ) {
        return angle * DEGTORAD;
    }
    public static float degreeToAngle( float degree ) {
        return degree * RADTODEG;
    }
    
    // TODO:
    // public method
    public void setGravity( float x, float y ) {
        World world = getWorld();
        if ( null == world ) {
            Log.e( TAG, "Failed to world.setGravity: null world" );
            return ;
        }
        world.setGravity( new Vec2( x, y ) );
    }
    
    private static Shape createCircleShape( float radius ) {
        CircleShape shape = new CircleShape();
        shape.m_radius = radius;        
        return shape;
    }
    private static Shape createPolygonShape( float halfWidth, float halfHeight ) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfWidth, halfHeight);  
        return shape;
    }    
    private static Body createFixturedBody( Body body, Shape shape, float density, float friction, float restitution ) {
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;
        if ( null == body ) {
            Log.e( TAG, "Failed to attachFixture: null body" );
            return null;
        }
        body.createFixture(fd);
        return body;
    }
    private static Body createFixturedBody( Body body, Shape shape, float density, float friction, float restitution, Filter filter ) {        
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.filter = filter;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;
        if ( null == body ) {
            Log.e( TAG, "Failed to attachFixture: null body" );
            return null;
        }
        body.createFixture(fd);
        return body;
    }
    private static Body createFixturedBody( Body body, Shape shape, float density, float friction, float restitution, Filter filter, boolean isSensor ) {        
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = isSensor;
        fd.filter = filter;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;
        if ( null == body ) {
            Log.e( TAG, "Failed to attachFixture: null body" );
            return null;
        }
        body.createFixture(fd);
        return body;
    }
    private static Body createBody( World world, float x, float y, BodyType type ) {
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = type;
        if ( null == world ) {
            Log.e( TAG, "Failed to createBody: null world" );
            return null;
        }
        return world.createBody(bd);
    }
    
    public Body createBall( float x, float y, float radius ) {
        Log.d( TAG, "createBall" );
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        radius = BodyFactory.screenToWorld( radius );
        
        Log.v( TAG, String.format( "createBall( %.2f, %.2f, %.2f )", 
            x, y, radius
        ));
        
        return createFixturedBody( 
            createBody( getWorld(), x, y, BodyType.DYNAMIC ), 
            createCircleShape( radius ), 
            1.0f, 0.0f, 1.0f 
        );
    }
    public Body createBrick( float x, float y, float halfWidth, float halfHeight ) {
        Log.d( TAG, "createBrick" );
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        halfWidth = BodyFactory.screenToWorld( halfWidth );
        halfHeight = BodyFactory.screenToWorld( halfHeight );  
        
        return createFixturedBody( 
            createBody( getWorld(), x, y, BodyType.STATIC ), 
            createPolygonShape( halfWidth, halfHeight ), 
            0.0f, 0.0f, 1.0f 
        );
    }

    public Body createBounder(float x, float y, float halfWidth,
            float halfHeight) {
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        halfWidth = BodyFactory.screenToWorld( halfWidth );
        halfHeight = BodyFactory.screenToWorld( halfHeight );
        
        return createFixturedBody( 
            createBody( getWorld(), x, y, BodyType.STATIC ), 
            createPolygonShape( halfWidth, halfHeight ), 
            0.0f, 0.0f, 1.0f 
        );
    }
    
    public Body createIron(float x, float y, float halfWidth,
            float halfHeight) {
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        halfWidth = BodyFactory.screenToWorld( halfWidth );
        halfHeight = BodyFactory.screenToWorld( halfHeight );
        
        return createFixturedBody( 
            createBody( getWorld(), x, y, BodyType.STATIC ), 
            createPolygonShape( halfWidth, halfHeight ), 
            0.0f, 0.0f, 1.0f 
        );
    }

    public Body createStick(float x, float y, float halfWidth,
            float halfHeight) {
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        halfWidth = BodyFactory.screenToWorld( halfWidth );
        halfHeight = BodyFactory.screenToWorld( halfHeight );
        
        return createFixturedBody( 
            createBody( getWorld(), x, y, BodyType.STATIC ), 
            createPolygonShape( halfWidth, halfHeight ), 
            0.0f, 0.0f, 1.0f 
        );
    }    

    public Body createBonus( float x, float y, float halfWidth, float halfHeight ) {
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        halfWidth = BodyFactory.screenToWorld( halfWidth );
        halfHeight = BodyFactory.screenToWorld( halfHeight );  
        
        return createFixturedBody( 
            createBody( getWorld(), x, y, BodyType.STATIC ), 
            createPolygonShape( halfWidth, halfHeight ), 
            0.0f, 0.0f, 1.0f 
        );

    }

    public void step() {
        stepTheWorld();
        handleContact();
    }

    public void remove( Body body ) {
        World world = getWorld();
        if ( null == world ) {
            Log.e( TAG, "Failed to world.destroyBody: null world" );
            return ;
        }
        world.destroyBody( body );
        body.setUserData( null );
    }

    // TODO:
    private World getWorld() {
        synchronized ( mLock ) {
            return mWorld;
        }
    }
    private List< Contact > getContact() {
        synchronized ( mLock ) {
            return mVecContact;
        } 
    }
    private void stepTheWorld(){
        World world = getWorld();
        if ( null == world ) {
            Log.e( TAG, "Failed to world.step: null world" );
            return ;
        }
        world.step( TIME_STEP, VEL_ITER, POS_ITER );
    }
    private void handleContact() {
        List< Contact > vecContact = getContact();
        if ( null == vecContact ) {
            Log.e( TAG, "Failed to handleContact: null vecContact" );
            return ;
        }
        for ( Contact contact: vecContact ) {
            handleBody( contact.getFixtureA().getBody(), contact.getFixtureB().getBody() );
        }
        mVecContact.clear();
    }    
    private void handleBody( Body self, Body other ) {
        Log.v( TAG, "handleBody" );
    }
}
