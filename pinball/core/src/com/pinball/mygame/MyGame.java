package com.pinball.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class MyGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	World world;
    ExtendViewport viewport;
    Sprite pinball;
    static final float SCALE_FACTOR = 0.05f;
    Box2DDebugRenderer debugRenderer;
    PhysicsShapeCache physicsBodies;
    Body pinballPhysicsSpot;
    Body pinballBoard;
    Body pinballBoardPaddleArea1;
    Body pinballBoardPaddleArea2;
    Sprite pinballBoardSprite;
    Sprite pinballBoardPaddleArea1Sprite;
    Sprite pinballBoardPaddleArea2Sprite;

	@Override
	public void create () { // this is where assets are usually loaded, apparently
        Box2D.init();
        world = new World(new Vector2(0, -50), true);
        physicsBodies = new PhysicsShapeCache("physics.xml");
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
        viewport = new ExtendViewport(800*SCALE_FACTOR, 900*SCALE_FACTOR, camera);
        debugRenderer = new Box2DDebugRenderer();

        pinball = prepareGenericSprite("pinball v2.png", 32, 32);
        pinballPhysicsSpot = createBody("pinball v2", 5, 40, 0);

        pinballBoardSprite = prepareGenericSprite("pinball board.png", 800, 900);
        pinballBoardPaddleArea1Sprite = prepareGenericSprite("pinball board paddle area.png", 800, 900);
        pinballBoardPaddleArea2Sprite = prepareGenericSprite("pinball board paddle area2.png", 800, 900);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.36f, 0.4f, 0.45f, 1);
        stepWorld();
		batch.begin();

        Vector2 pinballPosition = pinballPhysicsSpot.getPosition();
        float pinballDegrees = (float) Math.toDegrees(pinballPhysicsSpot.getAngle()); // doesn't do anything to circles?
        drawSprite(pinball, pinballPosition.x, pinballPosition.y, pinballDegrees);
        drawSprite(pinballBoardSprite, 0, 0, 0);
        drawSprite(pinballBoardPaddleArea1Sprite, 0, 0, 0);
        drawSprite(pinballBoardPaddleArea2Sprite, 0, 0, 0);

		batch.end();

        // DEBUG WIREFRAME:
        //debugRenderer.render(world, camera.combined);
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
        pinballBoard = prepareWorldPart(pinballBoard, "pinball board");
        pinballBoardPaddleArea1 = prepareWorldPart(pinballBoardPaddleArea1, "pinball board paddle area");
        pinballBoardPaddleArea2 = prepareWorldPart(pinballBoardPaddleArea2, "pinball board paddle area2");
    }

    private Body prepareWorldPart(Body body, String physicsBodyName) {
        if (body != null) {
            world.destroyBody(body);
        }
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = physicsBodies.createBody(
                physicsBodyName,
                world, bodyDef, SCALE_FACTOR, SCALE_FACTOR
        );
        body.setTransform(0, 0, 0);
        return body;
    }

    private Sprite prepareGenericSprite(String texture, int width, int height) {
        Texture spriteTexture = new Texture(texture);
        Sprite sprite = new Sprite(spriteTexture, 0, 0, width, height); // MIGHT be a performance problem
        sprite.setScale(SCALE_FACTOR);
        sprite.setOrigin(0, 0);
        return sprite;
    }

	@Override
	public void dispose () {
		batch.dispose();
        world.dispose();
        debugRenderer.dispose();
	}

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
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics but more was used
}
