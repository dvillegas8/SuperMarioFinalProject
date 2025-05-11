package io.github.finalprojectMario.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Scenes.Hud;
import io.github.finalprojectMario.Screens.PlayScreen;

public class Brick extends InteractiveTileObject{
    public Brick(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(Main.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        System.out.println("brick");
        setCategoryFilter(Main.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);
        Main.manager.get("assets/audio/sounds/breakblock.wav", Sound.class).play();
    }
}
