package com.pinball.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.pinball.mygame.MyGame.SCALE_FACTOR;

public class Entity {
    private Sprite sprite;
    private Body body;

    public enum RotationRadians {
        BOTTOM(0), RIGHT(1.57f), TOP(3.14f), LEFT(4.71f);

        private final float radians;
        RotationRadians(float radians) {
            this.radians = radians;
        }

        public float getRadians() {
            return this.radians;
        }
    }

    public final void makeBody(String name, float x, float y, World world, PhysicsShapeCache physicsBodies) {
        if (body != null) {
            world.destroyBody(body);
        }
        Body body = physicsBodies.createBody(name, world, SCALE_FACTOR, SCALE_FACTOR);
        body.setTransform(x, y, 0);
        this.body = body;
    }

    public final void makeBody(String name, float x, float y, float angle, World world, PhysicsShapeCache physicsBodies) {
        if (body != null) {
            world.destroyBody(body);
        }
        Body body = physicsBodies.createBody(name, world, SCALE_FACTOR, SCALE_FACTOR);
        body.setTransform(x, y, angle);
        this.body = body;
    }

    public final void makeSprite(String texture, int width, int height) {
        Texture spriteTexture = new Texture(texture);
        Sprite sprite = new Sprite(spriteTexture, 0, 0, width, height); // MIGHT be a performance problem
        sprite.setScale(SCALE_FACTOR);
        sprite.setOrigin(0, 0);
        this.sprite = sprite;
    }

    public void disableBody() {
        this.body.setActive(false);
    }

    public void enableBody() {
        this.body.setActive(true);
    }

    protected final Body getBody() {
        return body;
    }

    protected final Sprite getSprite() {
        return sprite;
    }

    protected final float getX() {
        return this.body.getPosition().x;
    }

    protected final float getY() {
        return this.body.getPosition().y;
    }

    protected final float getDegrees() {
        return (float) Math.toDegrees(this.body.getAngle());
    }

    public final void setId(String entityType, String differentiatingFactor) {
        EntityData entityData = new EntityData(entityType, differentiatingFactor);
        this.body.setUserData(entityData);
    }

    public final EntityData getId() {
        return (EntityData) this.body.getUserData();
    }

    public void drawEntity(SpriteBatch batch) {
        this.sprite.setPosition(getX(), getY());
        this.sprite.setRotation(getDegrees());
        this.sprite.setOrigin(0, 0);
        this.sprite.draw(batch);
    }
}
