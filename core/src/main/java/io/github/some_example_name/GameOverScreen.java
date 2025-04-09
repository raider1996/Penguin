package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {

    boolean hasWon;

    public GameOverScreen(boolean youWon) {

    }

    @Override
    public void show() {
        // Instantiate the visuals
    }

    @Override
    public void render(float delta) {
        // Display "game over"
        ScreenUtils.clear(new Color(Color.BLACK),true);
        Gdx.app.log("GameOverScreen", "hasWon " + hasWon);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
