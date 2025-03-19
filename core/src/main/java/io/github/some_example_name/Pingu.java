package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Pingu {
    public Pingu(int X, int Y, String texture) {
        this(X, Y, texture, false);
    }
    public Pingu(int X, int Y, String texture, boolean isImpostor){
        this.x = X;
        this.y = Y;
        this.texture = new Texture(texture);
        sprite = new Sprite(this.texture);
        this.isImpostor = isImpostor;
    }
    public int x;
    public int y;
    public Sprite sprite;
    public Texture texture;
    public boolean isImpostor;
}
