package io.github.some_example_name;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    final Drop game;
    public MainMenuScreen(final Drop game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "Penguin!!",1,1.5f);
        game.font.draw(game.batch, "Tap to begin", 1,1);
        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game) {
                @Override
                public void show() {

                }

                @Override
                public void render(float delta) {

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
            });
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width,height, true);

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
