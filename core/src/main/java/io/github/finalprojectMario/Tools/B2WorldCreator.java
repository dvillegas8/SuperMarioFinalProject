package io.github.finalprojectMario.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.finalprojectMario.Main;
import io.github.finalprojectMario.Screens.PlayScreen;
import io.github.finalprojectMario.Sprites.Brick;
import io.github.finalprojectMario.Sprites.Coin;
import io.github.finalprojectMario.Sprites.Enemies.Enemy;
import io.github.finalprojectMario.Sprites.Enemies.Goomba;
import io.github.finalprojectMario.Sprites.Enemies.Turtle;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;
    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Create bodies and fixtures in each object/tile on the ground
        for(MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + rect.getHeight() / 2) / Main.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Main.PPM, rect.getHeight() / 2 / Main.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);

        }
        // Create bodies and fixtures in each object/tile on the pipe
        for(MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + rect.getHeight() / 2) / Main.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Main.PPM, rect.getHeight() / 2 / Main.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = Main.OBJECT_BIT;
            body.createFixture(fdef);
        }
        // Create bodies and fixtures in each object/tile on the bricks
        for(MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){


            new Brick(screen, object);
        }
        // Create bodies and fixtures in each object/tile on the coin
        for(MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){

            new Coin(screen, object);
        }
        // Create all goombas
        goombas = new Array<Goomba>();
        for(MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / Main.PPM, rect.getY() / Main.PPM));
        }
        turtles = new Array<Turtle>();
        for(MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / Main.PPM, rect.getY() / Main.PPM));
        }
    }

    public Array<Goomba> getGoombas(){
        return goombas;
    }
    public Array<Turtle> getTurtles(){
        return turtles;
    }
    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }

}
