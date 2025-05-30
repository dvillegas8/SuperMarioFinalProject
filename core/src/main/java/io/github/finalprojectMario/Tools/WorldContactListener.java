package io.github.finalprojectMario.Tools;

import com.badlogic.gdx.physics.box2d.*;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Sprites.Enemies.Enemy;
import io.github.finalprojectMario.Sprites.InteractiveTileObject;
import io.github.finalprojectMario.Sprites.Items.Item;
import io.github.finalprojectMario.Sprites.Mario;

// Gets called when two fixtures in Box2d collide with each other
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // Collisions between two fixtures, Ex: Mario & Goomba
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Potential Ground_Bit collision or Mario_Bit collision
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Check collisions
        switch(cDef){
            case Main.MARIO_HEAD_BIT | Main.BRICK_BIT:
                // Case where mario hits a coin block with his head
            case Main.MARIO_HEAD_BIT | Main.COIN_BIT:
                // if fixA is mario's head, then fixb must be the coin
                if(fixA.getFilterData().categoryBits == Main.MARIO_HEAD_BIT){
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                }
                else{
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;
                // Case where enemy collides with mario
            case Main.ENEMY_HEAD_BIT | Main.MARIO_BIT:
                // Check which fix is an enemy
                if(fixA.getFilterData().categoryBits == Main.ENEMY_HEAD_BIT){
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                }
                else{
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                }
                break;
                // Case where an enemy hits an object, ex: pipe
            case Main.ENEMY_BIT | Main.OBJECT_BIT:
                // Check for Pipe collision, if so, change velocity in the other direction
                if(fixA.getFilterData().categoryBits == Main.ENEMY_BIT){
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                }
                else{
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
                // Case where mario collides with an enemy
            case Main.MARIO_BIT | Main.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == Main.MARIO_BIT){
                    ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                }
                else{
                    ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                }
                break;
                // Case where enemy collides with another enemy
            case Main.ENEMY_BIT | Main.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
                break;
                // Case where an item (ex: mushroom) collides with an object (ex: pipe)
            case Main.ITEM_BIT | Main.OBJECT_BIT:
                // Reverses the item/switches direction
                if(fixA.getFilterData().categoryBits == Main.ITEM_BIT){
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                }
                else{
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            // Checks if an item and mario collide
            case Main.ITEM_BIT | Main.MARIO_BIT:
                // If fixture A is the item, then Fix B is mario and use the item on B
                if(fixA.getFilterData().categoryBits == Main.ITEM_BIT){
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                }
                else{
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;
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
