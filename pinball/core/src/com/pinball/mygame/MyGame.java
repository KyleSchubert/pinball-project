package com.pinball.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class MyGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	World world;
    ExtendViewport viewport;
    static final float SCALE_FACTOR = 0.05f;
    Box2DDebugRenderer debugRenderer;
    PhysicsShapeCache physicsBodies;
    Entity pinball;
    Entity pinballBoard;
    Entity paddleArea1;
    Entity paddleArea2;
    Entity pusher;
    DistanceJointDef jointDef;
    static final float ORIGINAL_PUSHER_JOINT_LENGTH = 1 / (8 * SCALE_FACTOR);

	@Override
	public void create () { // this is where assets are usually loaded, apparently
        Box2D.init();
        world = new World(new Vector2(0, -50), true);
        physicsBodies = new PhysicsShapeCache("physics.xml");
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
        viewport = new ExtendViewport(800*SCALE_FACTOR, 900*SCALE_FACTOR, camera);
        debugRenderer = new Box2DDebugRenderer();
        pinball = new Entity();
        pinballBoard = new Entity();
        paddleArea1 = new Entity();
        paddleArea2 = new Entity();
        pusher = new Entity();

        pinball.setSprite(prepareGenericSprite("pinball v3.png", 32, 32));
        pinball.setBody(createBody("pinball v2", 37.8f, 8, 0));

        // color palette -->  https://coolors.co/7fe57f-000000-5c6672-b8c5d6-f4faff
        // added noise of   17, 12, 26
        pusher.setSprite(prepareGenericSprite("pusher.png", 43, 44));
        pusher.setBody(createBody("pusher", 37.8f, 7, 0));
        pinballBoard.setSprite(prepareGenericSprite("pinball board v2.png", 800, 900));
        paddleArea1.setSprite(prepareGenericSprite("pinball board paddle area v2.png", 800, 900));
        paddleArea2.setSprite(prepareGenericSprite("pinball board paddle area2 v2.png", 800, 900));
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.36f, 0.4f, 0.45f, 1);
        stepWorld();
		batch.begin();

        Vector2 pinballPosition = pinball.getBody().getPosition();
        float pinballDegrees = (float) Math.toDegrees(pinball.getBody().getAngle());
        Vector2 pusherPosition = pusher.getBody().getPosition();
        drawSprite(pinball.getSprite(), pinballPosition.x, pinballPosition.y, pinballDegrees);
        drawSprite(pusher.getSprite(), pusherPosition.x, pusherPosition.y, 0);
        drawSprite(pinballBoard.getSprite(), 0, 0, 0);
        drawSprite(paddleArea1.getSprite(), 0, 0, 0);
        drawSprite(paddleArea2.getSprite(), 0, 0, 0);

		batch.end();

        // DEBUG WIREFRAME:
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
        pinballBoard.setBody(prepareWorldPart(pinballBoard.getBody(), "pinball board"));
        paddleArea1.setBody(prepareWorldPart(paddleArea1.getBody(), "pinball board paddle area"));
        paddleArea2.setBody(prepareWorldPart(paddleArea2.getBody(), "pinball board paddle area2"));
        Vector2 jointOffsetBodyA = new Vector2(37.8f,6);
        Vector2 jointOffsetBodyB = new Vector2(pusher.getBody().getWorldCenter().x, pusher.getBody().getWorldCenter().y);
        jointDef = new DistanceJointDef();
        jointDef.initialize(pinballBoard.getBody(), pusher.getBody(), jointOffsetBodyA, jointOffsetBodyB);
        jointDef.collideConnected = true;
        jointDef.frequencyHz = 40f;
        jointDef.length = ORIGINAL_PUSHER_JOINT_LENGTH;
        jointDef.dampingRatio = 1f;
        world.createJoint(jointDef);
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
    static final float MAX_PUSHER_POWER = 5000000;
    float pusherChargedPercentPower = 0;
    boolean isPusherCharging = false;
    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                isPusherCharging = true;
                pusherChargedPercentPower += 2 * STEP_TIME;
                if (pusherChargedPercentPower > 1) {
                    pusherChargedPercentPower = 1;
                }
            }
            else {
                if (isPusherCharging) {
                    isPusherCharging = false;
                    pusher.getBody().applyForceToCenter(0, MAX_PUSHER_POWER * pusherChargedPercentPower, true);
                    pusherChargedPercentPower = 0;
                }
            }
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics but more was used
}
