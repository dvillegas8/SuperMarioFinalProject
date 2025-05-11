package io.github.finalprojectMario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Screens.PlayScreen;

import static io.github.finalprojectMario.Main.ENEMY_BIT;

public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> marioJump;
    private float stateTimer;
    private boolean runningRight;

    public Mario(PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        // Running images is 1 2 and 3
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(getTexture(), i * 16, 11,16, 16));
        }
        marioRun = new Animation<TextureRegion> (0.1f, frames);
        frames.clear();

        for(int i = 4; i < 6; i++){
            frames.add(new TextureRegion(getTexture(), i * 16, 11, 16,16));
        }
        marioJump = new Animation<TextureRegion>(0.1f, frames);

        // Sample mario stand sprite from sprite sheet at cords 1,11 (sprite is 16X16 pixels)
        marioStand = new TextureRegion(getTexture(), 1,11,16,16);

        defineMario();
        // knows how large to render sprite on screen
        setBounds(0,0,16 / Main.PPM,16 / Main.PPM);
        setRegion(marioStand);
    }
    // Attach sprite to box2d body
    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }
        // Check if mario is running left and if the region isn't facing left
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        // check if mario is running right and region returns true (meaning he is facing left but running right)
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }
        // Does current state equal the previous state? If it does, ad dt to statetimer. Else, set equal to 0
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        // When mario is jumping (not when running of a cliff) display an arm up sprite
        if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
            return State.JUMPING;
        }
        // When mario is falling
        else if(b2body.getLinearVelocity().y < 0){
            return State.FALLING;
        }
        // When mario is moving left or right
        else if(b2body.getLinearVelocity().x != 0){
            return State.RUNNING;
        }
        else{
            return State.STANDING;
        }
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Main.PPM,32 / Main.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        // Created a category bit that is mario
        fdef.filter.categoryBits = Main.MARIO_BIT;
        // Set the category in what he can actually collide with
        fdef.filter.maskBits = Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);
        // Mario's head for when colliding with a block
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData("head");
        /*
        // EdgeShape fixture acts as Mario's feet and slides smoothly across the tiles, without bumping or changing animation.
        FixtureDef fdef2 = new FixtureDef();
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / Main.PPM, -6 / Main.PPM), new Vector2(2 / Main.PPM, -6 / Main.PPM));
        fdef2.shape = feet;
        b2body.createFixture(fdef2);
        Above code makes it so you can destroy bricks
        It is here just in case in the future
         */
    }

}
