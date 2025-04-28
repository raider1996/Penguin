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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;




//imposter does not go in water
// show move players

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    Music music;

    TiledMap map;
    TiledMap Map2;

    OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;

    //boolean isSelected = false;

    SpriteBatch spriteBatch;
    Viewport viewport;

    int width;
    int height;

    boolean isTouched = false;
    Vector2 touchPosition = new Vector2();
    boolean isMovementActive = true;

    Texture texture2;
    Sprite sprite;

    Texture textureT;
    Sprite spriteT;
    Texture textureB;
    Sprite spriteB;
    Texture textureRing;
    Sprite spriteRing;

    int waterTile1X;
    int waterTile1Y;

    int waterTile2X;
    int waterTile2Y;

    int counter;
    float timeSinceLastMovement = 0;

    Stage stage;
    ProgressBar progressBar;
    Button button;

    Pingu[]penguin;

    HashMap<Integer, ArrayList<Integer>> hashMap;

    Main game;

    boolean gameStarted = false;

    public FirstScreen(Main main) {
        game = main;
    }


    @Override
    public void show()
    {   //server

        final String SERVER_IP = "152.105.66.107";  // Server IP
        final int SERVER_PORT = 4300;

        new Thread(()-> {
            try (Socket clientSocket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {


                System.out.println("Connected to server!");
                //Gdx.app.log("draw", "pingu position invalid " + pingu_X + " " + pingu_Y);
                Gdx.app.log("ServerResponse", "Received data: ");

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Received raw message: [" + line + "]");

                    if (line.startsWith("role ")) {
                        String[] parts = line.split(" ");
                        if (parts.length >= 3) {
                            try {
                                int playerId = Integer.parseInt(parts[1]);
                                int isImposter = Integer.parseInt(parts[2]);

                                System.out.println("You are Player " + playerId);
                                System.out.println(isImposter == 1 ? "You are the Imposter!" : "You are a Crewmate.");

                            } catch (NumberFormatException e) {
                                System.err.println("Invalid number format in role message: " + line);
                            }
                        } else {
                            System.err.println("Invalid role message format: " + line);
                        }
                    } else if (line.startsWith("start")) {
                        System.out.println("Game has started!");
                        gameStarted = true; // Call your render function
                    } else {
                        System.out.println("Unhandled message: " + line);
                    }
                }

            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            }
        }).start();





        // Prepare your screen here.

        stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage);
        Texture BackgroundTexture = new Texture(Gdx.files.internal("progressbar.png"));
        Texture KnobTexture =  new Texture(Gdx.files.internal("progressbarknob.png"));

        // Set music

        music = Gdx.audio.newMusic(Gdx.files.internal("pingumusic.mp3"));
        music.setLooping(true);
        music.setVolume(900f);
        music.play();

        ProgressBar.ProgressBarStyle Style = new ProgressBar.ProgressBarStyle();
        Style.background = new TextureRegionDrawable(BackgroundTexture);
        Style.knob = new TextureRegionDrawable(KnobTexture);

        Style.knobBefore = new TextureRegionDrawable(KnobTexture);

        progressBar= new ProgressBar(1,20,1,false,Style);

        progressBar.setValue(0);

        Table table = new Table();
//        table.setFillParent(true);
        table.add(progressBar).width(652).height(134);
        table.setPosition(550.0F, 2150.0F);
        stage.addActor(table);

        Button.ButtonStyle bstyle = new Button.ButtonStyle();
        Texture ButtonTexture = new Texture(Gdx.files.internal("button.png"));
        Texture ButtonPressTexture = new Texture(Gdx.files.internal("buttonpressed.png"));
        // Create texture for buttonpressed
        // get a drawable for both
        // >> use it for up and down
        bstyle.up = new TextureRegionDrawable(ButtonTexture);
        bstyle.down = new TextureRegionDrawable(ButtonPressTexture);
        // new button

        button = new Button(bstyle);
        button.setPosition(365f, 10f);
        button.setSize(350,440);
        stage.addActor(button);

        button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //
            }
        });

        // add button to stage

        // Local player will always be pingu1, penguin[0]
        // We may have different coordinates and different textures
        Pingu pingu1 = new Pingu(1, 3,1, "pingured.png");
//        Gdx.app.log("Show", pingu1.x + " " + pingu1.y);
        Pingu pingu2 = new Pingu(3,3,2, "pinguyellow.png");
        Pingu pingu3 = new Pingu(3,1,3,"pingupurple.png");

        //pingu array
        penguin = new Pingu[]{pingu1, pingu2, pingu3};
//        penguin[(int)(Math.random()*3)].isImpostor = true;

        texture2 = new Texture("water.png");
        sprite = new Sprite(texture2);

        textureT = new Texture("Top.png");
        spriteT = new Sprite(textureT);
        textureB = new Texture("Bottom.png");
        spriteB = new Sprite(textureB);
        textureRing = new Texture("circle.png");
        spriteRing = new Sprite(textureRing);

        //random location but not 1,1
        do
        {
            waterTile1X = (int)(Math.random()*3);
            waterTile1Y = 1+(int)(Math.random()*3);
        } while (waterTile1X == 1 && waterTile1Y == 1);

      //water tile 2 logic random location not near first water

        float distanceFromTile1;
        do
        {
            waterTile2X = (int)(Math.random()*3);
            waterTile2Y = 1+(int)(Math.random()*3);
            distanceFromTile1 = ((waterTile2X - waterTile1X) * (waterTile2X - waterTile1X)) +
                ((waterTile2Y - waterTile1Y) * (waterTile2Y - waterTile1Y));

        } while(distanceFromTile1 <= 2);

        //random pingu location not on the water or on each other

        boolean conditionToPlaceCharacter;
        do
        {
            pingu1.x = 1+(int)(Math.random()*3);
            pingu1.y = 1+(int)(Math.random()*3);
            conditionToPlaceCharacter =
                (pingu1.x-1 != waterTile1X || pingu1.y != waterTile1Y) &&
                    (pingu1.x-1 != waterTile2X || pingu1.y != waterTile2Y);
            Gdx.app.log("Pingu1", pingu1.x + " " + pingu1.y);
        } while (!conditionToPlaceCharacter);
        // Not on waterTile1 and not on waterTile2

        do
        {
            pingu2.x = 1+(int)(Math.random()*3);
            pingu2.y = 1+(int)(Math.random()*3);
            conditionToPlaceCharacter =
                (pingu2.x-1 != waterTile1X || pingu2.y != waterTile1Y) &&
                    (pingu2.x-1 != waterTile2X || pingu2.y != waterTile2Y) &&
                    (pingu2.x != pingu1.x || pingu2.y != pingu1.y);

        } while (!conditionToPlaceCharacter); // Not on waterTile1 and not on waterTile2 and not on pingu1


        do
        {
            pingu3.x = 1+(int)(Math.random()*3);
            pingu3.y = 1+(int)(Math.random()*3);
            conditionToPlaceCharacter =
                (pingu3.x - 1 != waterTile1X || pingu3.y != waterTile1Y) &&
                    (pingu3.x - 1 != waterTile2X || pingu3.y != waterTile2Y) &&
                    (pingu3.x != pingu1.x || pingu3.y != pingu1.y) &&
                    (pingu3.x != pingu2.x || pingu3.y != pingu2.y);
        } while (!conditionToPlaceCharacter); // Not on waterTile1 and not on waterTile2 and not on pingu1 and not on pingu2






        //set map ice and red
        spriteBatch = new SpriteBatch();
//        viewport = new StretchViewport(3,3);
        viewport = new FitViewport(3,5);
        // Try things:
        // 1. Extend the world dimensions to add a bottom and top row
        // 2. Change the way the coordinates work in the map
        // 3. Change the hashmap
        // 4. Change the movement
        // 5. Change the placement of the pingu

        map = new TmxMapLoader().load("map.tmx");
        Map2 = new TmxMapLoader().load("MapRed.tmx");


        renderer = new OrthogonalTiledMapRenderer(map, 1f / 32f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 3, 3);
        renderer.setView(camera);

        hashMap = new HashMap<>();
        ArrayList<Integer> x1y3al = new ArrayList<>();
        x1y3al.add(23);
        x1y3al.add(22);
        x1y3al.add(12);
        hashMap.put(13, x1y3al);
        ArrayList<Integer> x2y2al = new ArrayList<>();
        x2y2al.add(13);
        x2y2al.add(23);
        x2y2al.add(33);
        x2y2al.add(12);
        x2y2al.add(32);
        x2y2al.add(11);
        x2y2al.add(21);
        x2y2al.add(31);
        hashMap.put(22, x2y2al);
        ArrayList<Integer> x2y3al=new ArrayList<>();
        x2y3al.add(13);
        x2y3al.add(12);
        x2y3al.add(22);
        x2y3al.add(32);
        x2y3al.add(33);
        hashMap.put(23, x2y3al);
        ArrayList<Integer> x3y3al=new ArrayList<>();
        x3y3al.add(23);
        x3y3al.add(22);
        x3y3al.add(32);
        hashMap.put(33, x3y3al);
        ArrayList<Integer> x1y2al=new ArrayList<>();
        x1y2al.add(13);
        x1y2al.add(23);
        x1y2al.add(22);
        x1y2al.add(21);
        x1y2al.add(11);
        hashMap.put(12, x1y2al);
        ArrayList<Integer> x3y2al=new ArrayList<>();
        x3y2al.add(33);
        x3y2al.add(23);
        x3y2al.add(22);
        x3y2al.add(21);
        x3y2al.add(31);
        hashMap.put(32, x3y2al);
        ArrayList<Integer> x1y1al=new ArrayList<>();
        x1y1al.add(12);
        x1y1al.add(22);
        x1y1al.add(21);
        hashMap.put(11, x1y1al);
        ArrayList<Integer> x2y1al=new ArrayList<>();
        x2y1al.add(11);
        x2y1al.add(12);
        x2y1al.add(22);
        x2y1al.add(32);
        x2y1al.add(31);
        hashMap.put(21, x2y1al);
        ArrayList<Integer> x3y1al=new ArrayList<>();
        x3y1al.add(21);
        x3y1al.add(22);
        x3y1al.add(32);
        hashMap.put(31, x3y1al);
    }

    @Override
    public void render(float delta) {
        if (gameStarted) {
            // Handle game start
            System.out.println("Handling game start inside render loop!");

            // Call your start logic
            startGame();

            // Reset the flag if you want it to trigger only once
            gameStarted = false;
        }

        // Regular rendering
        input();
        logic();
        draw();
    }

    private void startGame() {
        System.out.println("Game setup done!");
    }


    private void draw()
    {
        ScreenUtils.clear(new Color(Color.BLACK),true);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        // Drawing the blue map
        renderer.render();
        TiledMapTileLayer layer = (TiledMapTileLayer) Map2.getLayers().get(0);


        spriteBatch.begin();
        // can we get a tile from the map? <- write the documentation
        // can we draw one individual cell? <- get the TextureRegion, then paint?
        // Based on pingu's position, get the arraylist
        // for all the elements in arraylist, paint the matching cell
        // Drawing the red tiles
        if (isMovementActive)
        {
            int pingu_X = penguin[0].x;
            int pingu_Y = penguin[0].y;
//        Gdx.app.log("draw", pingu1.x * 10 + pingu1.y + "");
            // TODO: Check the coordinates are valid
            if (!hashMap.containsKey(pingu_X * 10 + pingu_Y))
            {
                Gdx.app.log("draw", "pingu position invalid " + pingu_X + " " + pingu_Y);
            }
            for (int coordinates : hashMap.get(pingu_X * 10 + pingu_Y))
            {
                int x = (coordinates / 10) - 1;
                int y = coordinates % 10;
                if (penguin[1].x == x + 1 && penguin[1].y == y)
                {
                    continue;
                }
                if (penguin[2].x == x + 1 && penguin[2].y == y)
                {
                    continue;
                }
                TiledMapTileLayer.Cell cell = layer.getCell(x, y - 1); // Removed 1
                TextureRegion textureRegion = cell.getTile().getTextureRegion();
                spriteBatch.draw(textureRegion, x, y, 1, 1);
            }
        }
        spriteBatch.draw(sprite.getTexture(), waterTile1X, waterTile1Y,1,1);
        spriteBatch.draw(sprite.getTexture(), waterTile2X, waterTile2Y,1,1);
        //above for water tile

        spriteBatch.draw(textureT,0,4, 3, 1);
        spriteBatch.draw(textureB,0,0, 3, 1);
        spriteBatch.draw(penguin[0].sprite,penguin[0].x,penguin[0].y,0,0,8,8,0.1f,0.1f,90,true);
        spriteBatch.draw(penguin[1].sprite,penguin[1].x,penguin[1].y,0,0,8,8,0.1f,0.1f,90,true);
        spriteBatch.draw(penguin[2].sprite,penguin[2].x,penguin[2].y,0,0,8,8,0.1f,0.1f,90,true);

        for (int i = 0; i < 3; i++) {
            if (penguin[i].isSelected)
            {
                // draw ring texture
                spriteBatch.draw(textureRing,penguin[i].x-1,penguin[i].y,1,1);
            }
        }


        spriteBatch.end();


        progressBar.setValue(counter);
        //counter is 20 not 2
        if(counter == 20)
        {
          music.pause();
          isMovementActive = false;
        }

        stage.act(Gdx.graphics.getDeltaTime());
//        progressBar.setValue(100f);
        stage.draw();
    }

    private void logic()
    {
        int pingu_X = penguin[0].x;
        int pingu_Y = penguin[0].y;

        // Keep track of deltatime since lastmove
        timeSinceLastMovement += Gdx.graphics.getDeltaTime();
//


        if (isTouched && isMovementActive)
        {
            // Can I move there?
            // Add
            if (hashMap.get(10 * pingu_X + pingu_Y).contains(touchPosition.x * 10 + touchPosition.y + 1))
            {
                if (touchPosition.x == penguin[1].x && touchPosition.y == penguin[1].y - 1)
                {
                    Gdx.app.log("logic", "on top of pingu 2");
                    return;
                }
                if (touchPosition.x == penguin[2].x && touchPosition.y == penguin[2].y - 1)
                {
                    Gdx.app.log("logic", "on top of pingu 3");
                    return;
                }
                penguin[0].x = (touchPosition.x);
                penguin[0].y = (touchPosition.y)+1;

                //add to progress bar is touched under 2 seconds
                //reset the timer
                if (timeSinceLastMovement < 2.0 && counter < 20)
                {
                    counter++;
                }
                timeSinceLastMovement = 0;
            }

            // get the arraylist that matches the position
            // if tile is within arraylist, then paint it red
            // else paint it blue

        }
        if (!isMovementActive && isTouched && !penguin[0].isImpostor)
        {
            // Unselect all the pingus
            if (!(touchPosition.x == 2 && touchPosition.y == 0)) {
                for (int i = 0; i < 3; i++) {
                    penguin[i].isSelected = false;
                }
            }
            // We want to know if pingu2 or pingu3 were selected
            if (penguin[1].x == (touchPosition.x) && penguin[1].y == (touchPosition.y)+1)
            {
                penguin[1].isSelected = true;
            }
            if (penguin[2].x == (touchPosition.x) && penguin[2].y == (touchPosition.y)+1)
            {
                penguin[2].isSelected =true;
            }

            // If the touch position is that of the button
            if (touchPosition.x == 2 && touchPosition.y == 0) {
                //Gdx.app.log("BUTTON", "PRESSED!");
                int selectedPingu = -1;
                for (int i = 0; i < 3; i++) {
                    if (penguin[i].isSelected) {
                        selectedPingu = i;
//                        break;
                    }
                }
                if (selectedPingu != -1) {
                    game.switchToGameOverScreen(penguin[selectedPingu].isImpostor);
                }
            }

            // youWon = selectedPingu.isimpostor
            // switch to game over with the boolean
//            if ()

        }


    }

    private void input()
    {
        if (Gdx.input.isTouched())
        {
            Gdx.app.log("input", Gdx.input.getX() + " " + Gdx.input.getY());
            isTouched = true;
            touchPosition = convertPixelsToTileCoordinates(Gdx.input.getX(), Gdx.input.getY());
            Gdx.app.log("input", touchPosition.x + " " + touchPosition.y);
        } else
        {
            isTouched = false;
        }
    }

    /*
    1,2 2,2 3,2
    1,1 2,1 3,1
    1,0 2,0 3,0
     */


    static class Vector2
    {
        //Vector2(int x, int y) {this.x = x; this.y = y;}
        public int x;
        public int y;

        public Vector2()
        {

        }
    }

    // Assume x and y are valid
    // input: x and y are pixel coordinates
    private Vector2 convertPixelsToTileCoordinates(float x, float y)
    {
        Vector2 vector2 = new Vector2();
        vector2.x = (int) (x/((float) width /3)+1);
        vector2.y = (int) (3-(y-((float) height- (float)width) /2)/360);
        return vector2;
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width,height,true);//centers the camera
        // Resize your screen here. The parameters represent the new window size.
        this.width = width;
        this.height = height;
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void pause()
    {
        // Invoked when your application is paused.
    }

    @Override
    public void resume()
    {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide()
    {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose()
    {
        // Destroy screen's assets here.
        stage.dispose();
    }
}
