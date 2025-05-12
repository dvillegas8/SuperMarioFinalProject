package io.github.finalprojectMario.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Scenes.Hud;
import io.github.finalprojectMario.Screens.PlayScreen;
import io.github.finalprojectMario.Sprites.Items.ItemDef;
import io.github.finalprojectMario.Sprites.Items.Mushroom;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Main.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
        System.out.println("coin");
        if(getCell().getTile().getId() == BLANK_COIN){
            Main.manager.get("assets/audio/sounds/bump.wav", Sound.class).play();
        }
        else{
            if(object.getProperties().containsKey("mushroom")){
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x,body.getPosition().y + 16 / Main.PPM),
                    Mushroom.class));
                Main.manager.get("assets/audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else{
                Main.manager.get("assets/audio/sounds/coin.wav", Sound.class).play();
            }
        }
        // Change texture
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);
    }
}
