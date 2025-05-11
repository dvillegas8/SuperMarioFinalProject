package io.github.finalprojectMario.Tools;

import com.badlogic.gdx.physics.box2d.*;
import io.github.finalprojectMario.Sprites.InteractiveTileObject;

// Gets called when two fixtures in Box2d collide with each other
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // We have two fixtures but don't know which one of them are so we check if one of them is the head
        if(fixA.getUserData() == "head" || fixB.getUserData() == "head"){
            // Get the head by testing if each fixture has the head user data
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            // Other than the head
            Fixture object = head == fixA ? fixB : fixA;
            // Returns true if the objects get user data is an interactivetileobject class
            if(object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())){
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
