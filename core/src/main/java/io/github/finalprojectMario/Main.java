package io.github.finalprojectMario;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.finalprojectMario.Screens.PlayScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    // Virtual width & height
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    // Pixels per meter (our scale)
    public static final float PPM = 100;

    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    // Holds all of our images/textures and draws everything
    public SpriteBatch batch;

    public static AssetManager manager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("assets/audio/music/mario_music.ogg", Music.class);
        manager.load("assets/audio/sounds/coin.wav", Sound.class);
        manager.load("assets/audio/sounds/bump.wav", Sound.class);
        manager.load("assets/audio/sounds/breakblock.wav", Sound.class);
        // Asynchronous loading, finish loading all of the assets for now
        manager.finishLoading();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
        manager.update();
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
    }
}
