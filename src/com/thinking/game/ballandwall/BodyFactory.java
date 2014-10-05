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
    
    public static final int CATEGORY_ALL = 0xffffffff;
    public static final int CATEGORY_BALL = 0x00000001;
    public static final int CATEGORY_WALL = 0x00000002;
    
    private Object mLock = new Object();
    private World mWorld;
    private List< Contact > mVecContact;
    private List< Body > mVecBody;
    
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
                    List< Contact > vecContact = getContactList();
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
                        List< Contact > vecContact = getContactList();
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
        mVecBody = new ArrayList<Body>();
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
            if ( mVecBody != null ) {
                mVecBody.clear();
                mVecBody = null;
            }
        }
    }
    
    // TODO:
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
    
    public static Shape createCircleShape( float radius ) {
        CircleShape shape = new CircleShape();
        shape.m_radius = radius;        
        return shape;
    }
    
    public static Shape createPolygonShape( float halfWidth, float halfHeight ) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfWidth, halfHeight);
        return shape;
    }    
    
    public static Body createFixturedBody( Body body, Shape shape, int categoryBits, int maskBits, boolean isSensor ) {        
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = isSensor;
        fd.filter.categoryBits = categoryBits;
        fd.filter.maskBits = maskBits;
        fd.density = 1.0f;
        fd.friction = 0.0f;
        fd.restitution = 1.0f;
        if ( null == body ) {
            Log.e( TAG, "Failed to attachFixture: null body" );
            return null;
        }
        body.createFixture(fd);
        return body;
    }
    
    public static Body createBody( World world, float x, float y, float angle, BodyType type ) {
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.angle = angle;
        bd.type = type;
        if ( null == world ) {
            Log.e( TAG, "Failed to createBody: null world" );
            return null;
        }
        return world.createBody(bd);
    }
    
    // TODO:
    // public method
    public List< Body > getBodyList() {
        synchronized ( mLock ) {
            return mVecBody;
        }
    }
    private void addBody( Body body ) {
        synchronized ( mLock ) {
            if ( mVecBody != null && body != null ) {
                mVecBody.add( body );
            }
        }
    }
    
    public Body addBall( float x, float y, float radius, float angle ) {
        Log.d( TAG, "addBall" );
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        radius = BodyFactory.screenToWorld( radius );
        angle = BodyFactory.angleToDegree( angle );
        
        Log.v( TAG, String.format( "addBall( %.2f, %.2f, %.2f )", 
            x, y, radius
        ));
        
        Body body = BodyFactory.createFixturedBody( 
            BodyFactory.createBody( getWorld(), x, y, angle, BodyType.DYNAMIC ), 
            BodyFactory.createCircleShape( radius ), 
            CATEGORY_BALL, CATEGORY_ALL, false 
        );
        addBody( body );
        return body;
    }
    
    public Body addWall( float x, float y, float halfWidth, float halfHeight, float angle ) {
        Log.d( TAG, "addWall" );
        x = BodyFactory.screenToWorld( x );
        y = BodyFactory.screenToWorld( y );
        halfWidth = BodyFactory.screenToWorld( halfWidth );
        halfHeight = BodyFactory.screenToWorld( halfHeight );
        angle = BodyFactory.angleToDegree( angle );
        
        Log.v( TAG, String.format( "addWall( %.2f, %.2f, %.2f, %.2f )", 
            x, y, halfWidth, halfHeight
        ));
        
        Body body = BodyFactory.createFixturedBody( 
            BodyFactory.createBody( getWorld(), x, y, angle, BodyType.STATIC ), 
            BodyFactory.createPolygonShape( halfWidth, halfHeight ),
            CATEGORY_WALL, CATEGORY_ALL, false 
        );
        addBody( body );
        return body;
    }
    
    public void setGravity( float x, float y ) {
        World world = getWorld();
        if ( null == world ) {
            Log.e( TAG, "Failed to world.setGravity: null world" );
            return ;
        }
        world.setGravity( new Vec2( x, y ) );
    }
    
    public void step() {
        synchronized ( mLock ) {
            stepTheWorld_l();
        }        
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
    // private method
    private World getWorld() {
        synchronized ( mLock ) {
            return mWorld;
        }
    }
    
    private List< Contact > getContactList() {
        synchronized ( mLock ) {
            return mVecContact;
        } 
    }
    private void stepTheWorld_l(){
        if ( mWorld != null ) {
            mWorld.step( TIME_STEP, VEL_ITER, POS_ITER );
        }
    }
    private void handleContact() {
        List< Contact > vecContact = getContactList();
        if ( null == vecContact ) {
            Log.e( TAG, "Failed to handleContact: null vecContact" );
            return ;
        }
        for ( Contact contact: vecContact ) {
            handleBodyContact( contact.getFixtureA().getBody(), contact.getFixtureB().getBody() );
            handleBodyContact( contact.getFixtureB().getBody(), contact.getFixtureA().getBody() );
        }
        vecContact.clear();
    }    
    private void handleBodyContact( Body self, Body other ) {
//        Log.d( TAG, "handleBodyContact" );
        int categoryBits = self.getFixtureList().getFilterData().categoryBits;
        if ( ( categoryBits & CATEGORY_BALL ) != 0 ) {
            BodyUserData userData = (BodyUserData) self.getUserData();
            float velocityLength = userData.mVelocityLength;
            keepVelocity( self, velocityLength );
        }
        else if ( ( categoryBits & CATEGORY_WALL ) != 0 ) { 
            
        }
    }
    
    private static void keepVelocity( Body body, float velocityLength ) {        
        float x = body.getLinearVelocity().x;
        float y = body.getLinearVelocity().y;
        float scale = velocityLength/body.getLinearVelocity().length();
        body.setLinearVelocity( new Vec2( x*scale, y*scale ) );        
    }
}
