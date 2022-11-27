package com.pinball.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.pinball.mygame.MyGame.SCALE_FACTOR;


public class Bumper extends Entity {
    private static final int FRAME_COLS = 4, FRAME_ROWS = 1;
    private static final float FRAME_DURATION = 0.210f;
    private final Animation<TextureRegion> animation;
    private float frameTime = 0;

    public Bumper(World world, PhysicsShapeCache physicsBodies, float x, float y, String id) {
        this.makeBody("bumper", x, y, world, physicsBodies);
        Texture spriteSheet = new Texture(Gdx.files.internal("bumperSpritesheet.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAME_COLS, spriteSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] animationFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                animationFrames[index++] = tmp[i][j];
            }
        }
        animation = new Animation<>(FRAME_DURATION, animationFrames);
        this.setId("bumper", id);
    }

    public final void drawAnimation(SpriteBatch batch, float timeElapsed) {
        this.frameTime += timeElapsed;
        if (this.frameTime > FRAME_COLS * FRAME_DURATION) {
            this.frameTime -= FRAME_COLS * FRAME_DURATION;
        }
        TextureRegion currentFrame = animation.getKeyFrame(this.frameTime, true);
        batch.draw(currentFrame, getX(), getY(), 0, 0,
                33, 34, SCALE_FACTOR, SCALE_FACTOR, 0);
    }
}
