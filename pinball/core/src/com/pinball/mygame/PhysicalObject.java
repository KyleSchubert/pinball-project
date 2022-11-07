package com.pinball.mygame;

import com.badlogic.gdx.graphics.Texture;

public class PhysicalObject {
    protected float x;
    protected float y;
    protected Texture texture;

    PhysicalObject(float x, float y, String textureFile) {
        this.x = x;
        this.y = y;
        this.texture = new Texture(textureFile);
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}
