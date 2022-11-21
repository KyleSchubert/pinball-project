package com.pinball.mygame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity {
    private Sprite sprite;
    private Body body;

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

    public float getX() {
        return this.body.getPosition().x;
    }

    public float getY() {
        return this.body.getPosition().y;
    }

    public float getDegrees() {
        return (float) Math.toDegrees(this.body.getAngle());
    }
}
