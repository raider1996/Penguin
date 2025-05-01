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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    Music music;

    TiledMap map;
    TiledMap Map2;

    OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;

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

    Stage stage;
    ProgressBar progressBar;
    Button button;

    Pingu[] penguin;

    HashMap<Integer, ArrayList<Integer>> hashMap;

    Main game;

    boolean gameStarted = false;

    PrintWriter out;

    public FirstScreen(Main main) {
        game = main;
    }


    private void runThread() {
        //server

        final String SERVER_IP = "152.105.66.105";  // Server IP
        final int SERVER_PORT = 4302;

        try (Socket clientSocket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            out = new PrintWriter(clientSocket.getOutputStream(),true);

            System.out.println("Connected to server!");
            Gdx.app.log("ServerResponse", "Received data: ");

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Received raw message: [" + line + "]");

                if (line.startsWith("role ")) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 5) {
                        try {
                            int playerId = Integer.parseInt(parts[1]);
                            if (playerId >= 0 && playerId < penguin.length) {
                                penguin[playerId].isImpostor = Integer.parseInt(parts[2]) == 1;
                                penguin[playerId].x = Integer.parseInt(parts[3]);
                                penguin[playerId].y = Integer.parseInt(parts[4]) + 1;

                                System.out.println("You are Player " + playerId);
                                System.out.println(penguin[playerId].isImpostor ? "You are the Imposter!" : "You are a Crewmate.");
                                System.out.println("Spawn Position:(" + penguin[playerId].x + "," + penguin[playerId].y + ")");
                            } else {
                                System.err.println("Invalid player ID received: " + playerId);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format in role message: " + line);
                        }

                    } else {
                        System.err.println("Invalid role message format: " + line);
                    }
                } else if (line.startsWith("map")) {
                    String[] parts = line.split(" ");
                    if (parts.length == 5) {
                        try {
                            waterTile1X = Integer.parseInt(parts[1]) - 1;
                            waterTile1Y = Integer.parseInt(parts[2]) + 1;
                            waterTile2X = Integer.parseInt(parts[3]) - 1;
                            waterTile2Y = Integer.parseInt(parts[4]) + 1;

                            System.out.println("Received water tiles:");
                            System.out.println("Tile 1: (" + waterTile1X + "," + waterTile1Y + ")");
                            System.out.println("Tile 2: (" + waterTile2X + "," + waterTile2Y + ")");

                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format in map message:" + line);
                        }
                    } else {
                        System.err.println("Invalid map message format: " + line);

                    }
                } else if (line.startsWith("start")) {
                    System.out.println("Game has started!");
                    gameStarted = true;
                    //sendMoveCommand(out,2,3);// Call your render function
                } else if (line.startsWith("move")) {
                    String[] parts = line.split(" ");
                    if (parts.length == 4) {
                        try {

                            int Playerid = Integer.parseInt(parts[1]);
                            int x = Integer.parseInt(parts[2]);
                            int y = Integer.parseInt(parts[3]);

                            //update the player position based on move command
                            if (Playerid >= 0 && Playerid < penguin.length) {
                                penguin[Playerid].x = x;
                                penguin[Playerid].y = y;
                                System.out.println("Player " + Playerid + "moved to: (" + x + ", " + y + ")");
                            }

                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format in move message:" + line);
                        }
                    } else {
                        System.out.println("Unhandled message: " + line);
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //method to send player movement to the server
    private void sendPlayerMoveToServer(int playerId, int x, int y) {
        //construct the message to send
        String moveMessage = "move " + playerId + " " + x + " " + y + "\n";

        //send the message to the server
        out.println(moveMessage);
        System.out.println("Sent move command to server: " + moveMessage);
    }

    @Override
    public void show()
    {// Prepare your screen here.

        stage = new Stage(new ScreenViewport());
        Texture BackgroundTexture = new Texture(Gdx.files.internal("progressbar.png"));
        Texture KnobTexture =  new Texture(Gdx.files.internal("progressbarknob.png"));

        // Set music

        music = Gdx.audio.newMusic(Gdx.files.internal("pingumusic.mp3"));
        music.setLooping(true);
        music.setVolume(0.9f);
        music.play();

        ProgressBar.ProgressBarStyle Style = new ProgressBar.ProgressBarStyle();
        Style.background = new TextureRegionDrawable(BackgroundTexture);
        Style.knob = new TextureRegionDrawable(KnobTexture);

        Style.knobBefore = new TextureRegionDrawable(KnobTexture);

        progressBar= new ProgressBar(1,20,1,false,Style);

        progressBar.setValue(0);

        Table table = new Table();
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

        // add button to stage

        // Local player will always be pingu1, penguin[0]
        // We may have different coordinates and different textures
        Pingu pingu1 = new Pingu(1, 3, "pingured.png");
        Pingu pingu2 = new Pingu(3,3, "pinguyellow.png");
        Pingu pingu3 = new Pingu(3,1,"pingupurple.png");

        //pingu array
        penguin = new Pingu[]{pingu1, pingu2, pingu3};

        texture2 = new Texture("water.png");
        sprite = new Sprite(texture2);

        textureT = new Texture("Top.png");
        spriteT = new Sprite(textureT);
        textureB = new Texture("Bottom.png");
        spriteB = new Sprite(textureB);
        textureRing = new Texture("circle.png");
        spriteRing = new Sprite(textureRing);



        //set map ice and red
        spriteBatch = new SpriteBatch();
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

        new Thread(this::runThread).start();
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
        if (isMovementActive && hashMap.containsKey(penguin[0].x * 10 + penguin[0].y)) {
            for (int coord : hashMap.get(penguin[0].x * 10 + penguin[0].y)) {
                int x = (coord / 10) - 1;
                int y = coord % 10;
                if ((penguin[1].x == x + 1 && penguin[1].y == y) || (penguin[2].x == x + 1 && penguin[2].y == y))
                    continue;
                TiledMapTileLayer.Cell cell = layer.getCell(x, y - 1);
                if (cell != null) {
                    TextureRegion region = cell.getTile().getTextureRegion();
                    spriteBatch.draw(region, x, y, 1, 1);
                }
            }
        }
        spriteBatch.draw(textureT,0,4, 3, 1);
        spriteBatch.draw(textureB,0,0, 3, 1);
        spriteBatch.draw(sprite.getTexture(), waterTile1X, waterTile1Y,1,1);
        spriteBatch.draw(sprite.getTexture(), waterTile2X, waterTile2Y,1,1);
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
        stage.draw();
    }



    private void logic()
    {
        int pingu_X = penguin[0].x;
        int pingu_Y = penguin[0].y;

        // Keep track of deltatime since lastmove
        float timeSinceLastMovement = Gdx.graphics.getDeltaTime();


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

                ////send the new position to c++ server
                sendPlayerMoveToServer (penguin[0].playerId ,penguin[0].x,penguin[0].y);

                //add to progress bar is touched under 2 seconds
                //reset the timer
                if (timeSinceLastMovement < 2.0 && counter < 20)
                {
                    counter++;
                }
            }

            // get the arraylist that matches the position
            // if tile is within arraylist, then paint it red
            // else paint it blue

       // }
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
                int selectedPingu = -1;
                for (int i = 0; i < 3; i++) {
                    if (penguin[i].isSelected) {
                        selectedPingu = i;
                    }
                }
                if (selectedPingu != -1) {
                    game.switchToGameOverScreen(penguin[selectedPingu].isImpostor);
                }
            }

            // youWon = selectedPingu.isimpostor
            // switch to game over with the boolean

        }


    }
}



    private void input() {

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
