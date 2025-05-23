package io.github.finalprojectMario.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Screens.PlayScreen;
import io.github.finalprojectMario.Sprites.Enemies.Enemy;
import io.github.finalprojectMario.Sprites.Enemies.Turtle;

import static io.github.finalprojectMario.Main.ENEMY_BIT;

public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    // Animations
    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;
    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineBigMario;
    private boolean marioIsDead;

    public Mario(PlayScreen screen){
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        // Get running images from sprite sheet
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0,16, 16));
        }
        marioRun = new Animation<TextureRegion> (0.1f, frames);
        frames.clear();
        // Get big mario running animations from sprite sheet
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0,16, 32));
        }
        bigMarioRun = new Animation<TextureRegion> (0.1f, frames);
        frames.clear();

        // Get animation frames for growing mario
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0,16,32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        // Get texture for mario jumping
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80,0,16,16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80,0,16,32);

        // Sample mario stand sprite from sprite sheet at cords 1,11 (sprite is 16X16 pixels)
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0,0,16,16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0,0,16,32);

        // Sample the mario dead texture
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16,16);

        defineMario();
        // knows how large to render sprite on screen
        setBounds(0,0,16 / Main.PPM,16 / Main.PPM);
        setRegion(marioStand);
    }
    // Attach sprite to box2d body
    public void update(float dt){
        // Corrects the position of mario depending if he is big or not
        if(marioIsBig){
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / Main.PPM);
        }
        else{
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        // Update the sprite with mario's correct frame depending on what action he is doing
        setRegion(getFrame(dt));
        // Check if mario big, if so, draw him
        if(timeToDefineBigMario){
            defineBigMario();
        }
        if(timeToRedefineBigMario){
            redefineMario();
        }
        //Mario falls/dies if he touches the void
        if (b2body.getPosition().y<0 && !marioIsDead){
            //Stop his velocity and move him back up
            b2body.setLinearVelocity(0,0);
            b2body.getPosition().y=0;
            //Kill him :)
            hit(null);
        }
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        // Finds which state mario is in and returns the corresponding texture
        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
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
    // Returns the state mario is in
    public State getState(){
        if(marioIsDead){
            return State.DEAD;
        }
        else if(runGrowAnimation){
            return State.GROWING;
        }
        // When mario is jumping (not when running of a cliff) display an arm up sprite
        else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
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

    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(),getY(),getWidth(),getHeight() * 2);
        Main.manager.get("assets/audio/sounds/powerup.wav", Sound.class).play();
    }
    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10 / Main.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        // Created a category bit that is mario
        fdef.filter.categoryBits = Main.MARIO_BIT;
        // Set the category in what he can actually collide with
        fdef.filter.maskBits = Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT | Main.ENEMY_HEAD_BIT | Main.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0,-14 / Main.PPM));
        b2body.createFixture(fdef).setUserData(this);

        // Mario's head for when colliding with a block
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));
        fdef.filter.categoryBits = Main.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;

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
        fdef.filter.maskBits = Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT | Main.ENEMY_HEAD_BIT | Main.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        // Mario's head for when colliding with a block
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));
        fdef.filter.categoryBits = Main.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
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
    public void hit(Enemy enemy){
        // Makes mario be able to kick a turtle in the shell state
        if(enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }
        else {
            // Returns mario to the small version
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineBigMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                Main.manager.get("assets/audio/sounds/powerdown.wav", Sound.class).play();
            }
            else {
                // Mario die's case
                Main.manager.get("assets/audio/music/mario_music.ogg", Music.class).stop();
                Main.manager.get("assets/audio/sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = Main.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                // Makes mario jump a little after he gets hit and dies
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }
    public void jump(){
        // Need this condition so that mario cant fly
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }
    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        // Destroy big mario
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        // Created a category bit that is mario
        fdef.filter.categoryBits = Main.MARIO_BIT;
        // Set the category in what he can actually collide with
        fdef.filter.maskBits = Main.GROUND_BIT | Main.COIN_BIT | Main.BRICK_BIT | Main.ENEMY_BIT | Main.OBJECT_BIT | Main.ENEMY_HEAD_BIT | Main.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        // Mario's head for when colliding with a block
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));
        fdef.filter.categoryBits = Main.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineBigMario = false;
        Main.manager.get("assets/audio/sounds/powerdown.wav", Sound.class).play();
    }
    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isDead(){
        return marioIsDead;
    }
    public float getStateTimer(){
        return stateTimer;
    }

}
