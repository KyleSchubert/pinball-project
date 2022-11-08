package com.pinball.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class MyGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	Texture testImage;
	private Rectangle testRectangle;
	World world;
    ExtendViewport viewport;
    Sprite pinball;
    static final float SCALE_FACTOR = 0.05f;
    // START SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    float accumulator = 0;
    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
    Box2DDebugRenderer debugRenderer;
    PhysicsShapeCache physicsBodies;
    Body pinballPhysicsSpot;
    Body pinballBoard;
    Body pinballBoardPaddleArea1;
    Body pinballBoardPaddleArea2;

	@Override
	public void create () { // this is where assets are usually loaded, apparently
        Box2D.init();
        world = new World(new Vector2(0, -50), true);

        physicsBodies = new PhysicsShapeCache("physics.xml");

		batch = new SpriteBatch();
		testImage = new Texture("pepe.png");

		camera = new OrthographicCamera();
        viewport = new ExtendViewport(800*SCALE_FACTOR, 900*SCALE_FACTOR, camera);

		testRectangle = new Rectangle();
		testRectangle.x = 300*SCALE_FACTOR;
		testRectangle.y = 500*SCALE_FACTOR;
		testRectangle.width = 30*SCALE_FACTOR;
		testRectangle.height = 30*SCALE_FACTOR;

        Texture pinballTexture = new Texture("pinball v2.png"); // so I don't need to .dispose() this one?
        pinball = new Sprite(pinballTexture, 0, 0, 32, 32);
        pinball.setScale(SCALE_FACTOR);
        pinball.setOrigin(0, 0);

        pinballPhysicsSpot = createBody("pinball v2", 5, 40, 0);

        debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);

        stepWorld();
        testRectangle.x += 1*SCALE_FACTOR;
        testRectangle.y -= 1*SCALE_FACTOR;

		batch.begin();
		batch.draw(testImage, testRectangle.x, testRectangle.y);
        Vector2 pinballPosition = pinballPhysicsSpot.getPosition();
        float pinballDegrees = (float) Math.toDegrees(pinballPhysicsSpot.getAngle()); // doesn't do anything to circles?
        drawSprite(pinball, pinballPosition.x, pinballPosition.y, pinballDegrees);
		batch.end();

        debugRenderer.render(world, camera.combined);
	}

    private void drawSprite(Sprite sprite, float x, float y, float degrees) {
        sprite.setPosition(x, y);
        sprite.setRotation(degrees);
        sprite.setOrigin(0, 0);
        sprite.draw(batch);
    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
        createPinballBoard();
    }

    private Body createBody(String name, float x, float y, float angle) {
        Body body = physicsBodies.createBody(name, world, SCALE_FACTOR, SCALE_FACTOR);
        body.setTransform(x, y, angle);
        return body;
    }

    private void createPinballBoard() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        if (pinballBoard != null) {
            world.destroyBody(pinballBoard);
        }
        pinballBoard = physicsBodies.createBody("pinball board", world, bodyDef, SCALE_FACTOR, SCALE_FACTOR);
        pinballBoard.setTransform(0, 0, 0);

        if (pinballBoardPaddleArea1 != null) {
            world.destroyBody(pinballBoardPaddleArea1);
        }
        pinballBoardPaddleArea1 = physicsBodies.createBody("pinball board paddle area", world, bodyDef, SCALE_FACTOR, SCALE_FACTOR);
        pinballBoardPaddleArea1.setTransform(0, 0, 0);

        if (pinballBoardPaddleArea2 != null) {
            world.destroyBody(pinballBoardPaddleArea2);
        }
        pinballBoardPaddleArea2 = physicsBodies.createBody("pinball board paddle area2", world, bodyDef, SCALE_FACTOR, SCALE_FACTOR);
        pinballBoardPaddleArea2.setTransform(0, 0, 0);
    }

	@Override
	public void dispose () {
		batch.dispose();
		testImage.dispose();
        world.dispose();
        debugRenderer.dispose();
	}
}
