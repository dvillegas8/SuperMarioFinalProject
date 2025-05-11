package io.github.finalprojectMario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Screens.PlayScreen;

public class Goomba extends Enemy{
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16,16));
        }
        walkAnimation = new Animation<TextureRegion>(0.4f, frames);
        stateTime = 0;
        // Knows how big our sprite is
        setBounds(getX(), getY(), 16 / Main.PPM, 16 / Main.PPM);
    }

    public void update(float dt){
        stateTime +=dt;
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(walkAnimation.getKeyFrame(stateTime, true));
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        // Created a category bit that is mario
        fdef.filter.categoryBits = Main.ENEMY_BIT;
        // Set the category in what he can actually collide with
        fdef.filter.maskBits = Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT | Main.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }
}
