package io.github.some_example_name;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.HashMap;



//import javax.swing.Renderer;


/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    Music music;

    TiledMap map;
    TiledMap Map2;

    OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;

    int loops = 0;

    SpriteBatch spriteBatch;
    Viewport viewport;

    int width;
    int height;

    boolean isTouched = false;
    Vector2 touchPosition = new Vector2();

    Pingu pingu1;
    Pingu pingu2;
    Pingu pingu3;


    Texture texture2;
    Sprite sprite;

    int waterTile1X;
    int waterTile1Y;

    int waterTile2X;
    int waterTile2Y;


    HashMap<Integer, ArrayList<Integer>> hashMap;


    @Override
    public void show() {
        // Prepare your screen here.

        // Local player will always be pingu1
        // We may have different coordinates and different textures
        pingu1 = new Pingu(1, 2, "pingured.png");
//        Gdx.app.log("Show", pingu1.x + " " + pingu1.y);
        pingu2 = new Pingu(3,2, "pinguyellow.png");
        pingu3 = new Pingu(3,0,"pingupurple.png");

        texture2 = new Texture("water.png");
        sprite = new Sprite(texture2);

        //random location but not 1,1
        do {
            waterTile1X = (int)(Math.random()*3);
            waterTile1Y = (int)(Math.random()*3);
        } while (waterTile1X == 1 && waterTile1Y == 1);

      //water tile 2 logic random location not near first water

        float distanceFromTile1 = 0;
        do{
            waterTile2X = (int)(Math.random()*3);
            waterTile2Y = (int)(Math.random()*3);
            distanceFromTile1 = ((waterTile2X - waterTile1X) * (waterTile2X - waterTile1X)) +
                ((waterTile2Y - waterTile1Y) * (waterTile2Y - waterTile1Y));

        } while(distanceFromTile1 <= 2);

        //random pingu location not on the water or on each other

        boolean conditionToPlaceCharacter = false;
        do {
            pingu1.x = 1+(int)(Math.random()*3);
            pingu1.y = (int)(Math.random()*3);
            conditionToPlaceCharacter =
                (pingu1.x-1 != waterTile1X || pingu1.y != waterTile1Y) &&
                    (pingu1.x-1 != waterTile2X || pingu1.y != waterTile2Y);

        } while (!conditionToPlaceCharacter);
        // Not on waterTile1 and not on waterTile2

        do {
            pingu2.x = 1+(int)(Math.random()*3);
            pingu2.y = (int)(Math.random()*3);
            conditionToPlaceCharacter =
                (pingu2.x-1 != waterTile1X || pingu2.y != waterTile1Y) &&
                    (pingu2.x-1 != waterTile2X || pingu2.y != waterTile2Y) &&
                    (pingu2.x != pingu1.x || pingu2.y != pingu1.y);

        } while (!conditionToPlaceCharacter); // Not on waterTile1 and not on waterTile2 and not on pingu1


        do {
            pingu3.x = (int)(Math.random()*3) + 1;
            pingu3.y = (int)(Math.random()*3);
            conditionToPlaceCharacter =
                (pingu3.x - 1 != waterTile1X || pingu3.y != waterTile1Y) &&
                    (pingu3.x - 1 != waterTile2X || pingu3.y != waterTile2Y) &&
                    (pingu3.x != pingu1.x || pingu3.y != pingu1.y) &&
                    (pingu3.x != pingu2.x || pingu3.y != pingu2.y);
        } while (!conditionToPlaceCharacter); // Not on waterTile1 and not on waterTile2 and not on pingu1 and not on pingu2




        // Set music

        music = Gdx.audio.newMusic(Gdx.files.internal("pingumusic.mp3"));
        music.setLooping(true);
        music.setVolume(200f);
        music.play();

        //set map ice and red
        spriteBatch = new SpriteBatch();
//        viewport = new StretchViewport(3,3);
        viewport = new FitViewport(3,3);
        // Try things:
        // 1. Extend the world dimensions to add a bottom and top
        // 2.

        map = new TmxMapLoader().load("map.tmx");
        Map2 = new TmxMapLoader().load("MapRed.tmx");


        renderer = new OrthogonalTiledMapRenderer(map, 1f / 32f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 3, 3);
        renderer.setView(camera);

        hashMap = new HashMap<>();
        ArrayList<Integer> x1y2al = new ArrayList<>();
        x1y2al.add(22);
        x1y2al.add(21);
        x1y2al.add(11);
        hashMap.put(12, x1y2al);
        ArrayList<Integer> x2y1al = new ArrayList<>();
        x2y1al.add(12);
        x2y1al.add(22);
        x2y1al.add(32);
        x2y1al.add(11);
        x2y1al.add(31);
        x2y1al.add(10);
        x2y1al.add(20);
        x2y1al.add(30);
        hashMap.put(21, x2y1al);
        ArrayList<Integer> x2y2al=new ArrayList<>();
        x2y2al.add(12);
        x2y2al.add(11);
        x2y2al.add(21);
        x2y2al.add(31);
        x2y2al.add(32);
        hashMap.put(22, x2y2al);
        ArrayList<Integer> x3y2al=new ArrayList<>();
        x3y2al.add(22);
        x3y2al.add(21);
        x3y2al.add(31);
        hashMap.put(32, x3y2al);
        ArrayList<Integer> x1y1al=new ArrayList<>();
        x1y1al.add(12);
        x1y1al.add(22);
        x1y1al.add(21);
        x1y1al.add(20);
        x1y1al.add(10);
        hashMap.put(11, x1y1al);
        ArrayList<Integer> x3y1al=new ArrayList<>();
        x3y1al.add(32);
        x3y1al.add(22);
        x3y1al.add(21);
        x3y1al.add(20);
        x3y1al.add(30);
        hashMap.put(31, x3y1al);
        ArrayList<Integer> x1y0al=new ArrayList<>();
        x1y0al.add(11);
        x1y0al.add(21);
        x1y0al.add(20);
        hashMap.put(10, x1y0al);
        ArrayList<Integer> x2y0al=new ArrayList<>();
        x2y0al.add(10);
        x2y0al.add(11);
        x2y0al.add(21);
        x2y0al.add(31);
        x2y0al.add(30);
        hashMap.put(20, x2y0al);
        ArrayList<Integer> x3y0al=new ArrayList<>();
        x3y0al.add(20);
        x3y0al.add(21);
        x3y0al.add(31);
        hashMap.put(30, x3y0al);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        input();
        logic();
        draw();
    }

    private void draw(){
        ScreenUtils.clear(new Color(Color.BLACK),true);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        renderer.render();
        TiledMapTileLayer layer = (TiledMapTileLayer) Map2.getLayers().get(0);


        // can we get a tile from the map? <- write the documentation
        // can we draw one individual cell? <- get the TextureRegion, then paint?
        // Based on pingu's position, get the arraylist
        // for all the elements in arraylist, paint the matching cell
        int pingu_X = pingu1.x;
        int pingu_Y = pingu1.y;
        spriteBatch.begin();
//        Gdx.app.log("draw", pingu1.x * 10 + pingu1.y + "");
        for (int coordinates: hashMap.get(pingu_X * 10 + pingu_Y)) {
            int x = (coordinates/10) - 1;
            int y = coordinates%10;
//            Gdx.app.log("Coordinates" + loops, pingu2.x + " " + pingu2.y + ", " + x + " " + y);
            if (pingu2.x == x + 1 && pingu2.y == y) {
               //Gdx.app.log("Coordinates", pingu2.x + " " + pingu2.y + ", " + x + " " + y);
                continue;
            }
            if (pingu3.x == x + 1 && pingu3.y == y) {
                //Gdx.app.log("Coordinates", pingu2.x + " " + pingu2.y + ", " + x + " " + y);
                continue;
            }
            TiledMapTileLayer.Cell cell = layer.getCell(x,y);
            TextureRegion textureRegion = cell.getTile().getTextureRegion();
            spriteBatch.draw(textureRegion, x, y, 1, 1);
        }


        spriteBatch.draw(sprite.getTexture(), waterTile1X, waterTile1Y,1,1);
        spriteBatch.draw(sprite.getTexture(), waterTile2X, waterTile2Y,1,1);
        //above for water tile

        spriteBatch.draw(pingu1.sprite,pingu1.x,pingu1.y,0,0,8,8,0.1f,0.1f,90,true);
        spriteBatch.draw(pingu2.sprite,pingu2.x,pingu2.y,0,0,8,8,0.1f,0.1f,90,true);
        spriteBatch.draw(pingu3.sprite,pingu3.x,pingu3.y,0,0,8,8,0.1f,0.1f,90,true);
        spriteBatch.end();
//        loops++;
    }

    private void logic()
    {
        int pingu_X = pingu1.x;
        int pingu_Y = pingu1.y;
        //Vector2 pingu_pos = new Vector2(pingu_X, pingu_Y);


        if (isTouched)
        {
            // Can I move there?
            // Add
            if (hashMap.get(10 * pingu_X + pingu_Y).contains(touchPosition.x * 10 + touchPosition.y))
            {
                if (touchPosition.x == pingu2.x && touchPosition.y == pingu2.y) {
                    return;
                }
                if (touchPosition.x == pingu3.x && touchPosition.y == pingu3.y) {
                    return;
                }
                // TODO add pingu3
                pingu1.x = (touchPosition.x);
                pingu1.y = (touchPosition.y);
            }

            // get the arraylist that matches the position
            // if tile is within arraylist, then paint it red
            // else paint it blue

        }
    }

    private void input() {
        if (Gdx.input.isTouched()) {
            Gdx.app.log("input", Gdx.input.getX() + " " + Gdx.input.getY());
            isTouched = true;
            touchPosition = convertPixelsToTileCoordinates(Gdx.input.getX(), Gdx.input.getY());
            Gdx.app.log("input", touchPosition.x + " " + touchPosition.y);
        } else {
            isTouched = false;
        }
    }

    static class Vector2 {
        //Vector2(int x, int y) {this.x = x; this.y = y;}
        public int x;
        public int y;

        public Vector2() {

        }
    }

    // Assume x and y are valid
    // input: x and y are pixel coordinates
    private Vector2 convertPixelsToTileCoordinates(float x, float y) {
        Vector2 vector2 = new Vector2();
        vector2.x = (int) (x/((float) width /3)+1);
        vector2.y = (int) (3-(y-((float) height- (float)width) /2)/360);
        return vector2;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);//centers the camera
        // Resize your screen here. The parameters represent the new window size.
        this.width = width;
        this.height = height;
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}
