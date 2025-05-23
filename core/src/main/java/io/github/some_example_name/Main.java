package io.github.some_example_name;

import com.badlogic.gdx.Game;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {

        setScreen(new FirstScreen(this));
    }

    public void switchToGameOverScreen(boolean youWon)
    {
        setScreen(new GameOverScreen(youWon));
    }
}
