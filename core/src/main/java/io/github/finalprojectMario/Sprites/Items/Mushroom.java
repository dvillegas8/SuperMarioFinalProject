package io.github.finalprojectMario.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Screens.PlayScreen;
import io.github.finalprojectMario.Sprites.Mario;

public class Mushroom extends Item{
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"),0,0,16,16);
        velocity = new Vector2(0.7f,0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        // Defines what is the fixture
        fdef.filter.categoryBits = Main.ITEM_BIT;
        // What this fixture can collide with
        fdef.filter.maskBits = Main.MARIO_BIT | Main.OBJECT_BIT | Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

    }

    @Override
    // once mario grabs the mushroom
    public void use(Mario mario) {
        destroy();
        mario.grow();
    }
    @Override
    public void update(float dt){
        super.update(dt);
        // Center texture over the body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
