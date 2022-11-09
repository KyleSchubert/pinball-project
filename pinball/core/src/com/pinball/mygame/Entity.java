package com.pinball.mygame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
    Sprite sprite;
    Body body;

    public void setBody(Body body) {
        this.body = body;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Body getBody() {
        return body;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
