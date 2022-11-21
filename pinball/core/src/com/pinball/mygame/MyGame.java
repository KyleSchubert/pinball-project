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
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
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
    Entity paddleAreaLeft;
    Entity paddleAreaRight;
    Entity pusher;
    DistanceJointDef pusherJointDef;
    Entity leftPaddle;
    Entity rightPaddle;
    RevoluteJointDef leftPaddleJointDef;
    RevoluteJointDef rightPaddleJointDef;
    RevoluteJoint rightPaddleJoint;
    RevoluteJoint leftPaddleJoint;
    Entity despawnLine;
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
        paddleAreaLeft = new Entity();
        paddleAreaRight = new Entity();
        pusher = new Entity();
        leftPaddle = new Entity();
        rightPaddle = new Entity();
        despawnLine = new Entity();


        pinball.setSprite(prepareGenericSprite("pinball v3.png", 32, 32));
        pinball.setBody(createBody("pinball v2", 37.8f, 12));
        pinball.getBody().setUserData("pinball");

        // color palette -->  https://coolors.co/7fe57f-000000-5c6672-b8c5d6-f4faff
        // added noise of   17, 12, 26
        pusher.setSprite(prepareGenericSprite("pusher.png", 43, 44));
        pusher.setBody(createBody("pusher", 37.8f, 7));

        pinballBoard.setSprite(prepareGenericSprite("pinball board v2.png", 800, 900));
        paddleAreaLeft.setSprite(prepareGenericSprite("pinball board paddle area v2.png", 800, 900));
        paddleAreaRight.setSprite(prepareGenericSprite("pinball board paddle area2 v2.png", 800, 900));

        despawnLine.setSprite(prepareGenericSprite("despawn line.png", 247, 14)); // you can't see it either way, though
        despawnLine.setBody(createBody("despawn line", 12.3f, -3));
        despawnLine.getBody().setUserData("despawn line");

        leftPaddle.setSprite(prepareGenericSprite("left paddle.png", 102, 38));
        leftPaddle.setBody(createBody("left paddle", 12.5f, 4.5f));

        rightPaddle.setSprite(prepareGenericSprite("right paddle.png", 102, 38));
        rightPaddle.setBody(createBody("right paddle", 30, 7));

        world.setContactListener(new CustomContactListener());
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.36f, 0.4f, 0.45f, 1);
        stepWorld();
		batch.begin();

        drawSprite(pusher.getSprite(), pusher.getX(), pusher.getY(), 0);
        drawSprite(pinball.getSprite(), pinball.getX(), pinball.getY(), pinball.getDegrees());
        drawSprite(rightPaddle.getSprite(), rightPaddle.getX(), rightPaddle.getY(), rightPaddle.getDegrees());
        drawSprite(leftPaddle.getSprite(), leftPaddle.getX(), leftPaddle.getY(), leftPaddle.getDegrees());
        drawSprite(pinballBoard.getSprite(), 0, 0, 0);
        drawSprite(paddleAreaLeft.getSprite(), 0, 0, 0);
        drawSprite(paddleAreaRight.getSprite(), 0, 0, 0);
        drawSprite(despawnLine.getSprite(), despawnLine.getX(), despawnLine.getY(), 0);

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

    private Body createBody(String name, float x, float y) {
        Body body = physicsBodies.createBody(name, world, SCALE_FACTOR, SCALE_FACTOR);
        body.setTransform(x, y, 0);
        return body;
    }

    private void createPinballBoard() {
        pinballBoard.setBody(prepareWorldPart(pinballBoard.getBody(), "pinball board"));
        paddleAreaLeft.setBody(prepareWorldPart(paddleAreaLeft.getBody(), "pinball board paddle area"));
        paddleAreaRight.setBody(prepareWorldPart(paddleAreaRight.getBody(), "pinball board paddle area2"));

        // PUSHER JOINT (so the pusher can move with the SPACE bar)
        Vector2 jointOffsetBodyA = new Vector2(37.8f,6);
        Vector2 jointOffsetBodyB = new Vector2(pusher.getBody().getWorldCenter().x, pusher.getBody().getWorldCenter().y);
        pusherJointDef = new DistanceJointDef();
        pusherJointDef.initialize(pinballBoard.getBody(), pusher.getBody(), jointOffsetBodyA, jointOffsetBodyB);
        pusherJointDef.collideConnected = true;
        pusherJointDef.frequencyHz = 40f;
        pusherJointDef.length = ORIGINAL_PUSHER_JOINT_LENGTH;
        pusherJointDef.dampingRatio = 1f;
        world.createJoint(pusherJointDef);

        // LEFT PADDLE JOINT
        Vector2 leftPaddleAnchorA = new Vector2(11.2f, 5.5f);
        Vector2 leftPaddleAnchorB = new Vector2(0.5f, 1.5f);
        leftPaddleJointDef = new RevoluteJointDef();
        leftPaddleJointDef.initialize(paddleAreaLeft.getBody(), leftPaddle.getBody(), leftPaddle.getBody().getPosition());
        leftPaddleJointDef.referenceAngle = 0;
        leftPaddleJointDef.enableLimit = true;
        leftPaddleJointDef.lowerAngle = 0;
        leftPaddleJointDef.upperAngle = 0.8f;
        leftPaddleJointDef.localAnchorA.set(leftPaddleAnchorA);
        leftPaddleJointDef.localAnchorB.set(leftPaddleAnchorB);
        leftPaddleJointDef.enableMotor = false;
        leftPaddleJointDef.maxMotorTorque = 120000;
        leftPaddleJointDef.motorSpeed = 4000;
        world.createJoint(leftPaddleJointDef);
        leftPaddleJoint = (RevoluteJoint) world.createJoint(leftPaddleJointDef);

        // RIGHT PADDLE JOINT
        Vector2 rightPaddleAnchorA = new Vector2(24.4f, 5.5f);
        Vector2 rightPaddleAnchorB = new Vector2(4.6f, 1.5f);
        rightPaddleJointDef = new RevoluteJointDef();
        rightPaddleJointDef.initialize(paddleAreaRight.getBody(), rightPaddle.getBody(), rightPaddle.getBody().getPosition());
        rightPaddleJointDef.referenceAngle = 0;
        rightPaddleJointDef.enableLimit = true;
        rightPaddleJointDef.lowerAngle = -0.8f;
        rightPaddleJointDef.upperAngle = 0;
        rightPaddleJointDef.localAnchorA.set(rightPaddleAnchorA);
        rightPaddleJointDef.localAnchorB.set(rightPaddleAnchorB);
        rightPaddleJointDef.enableMotor = false;
        rightPaddleJointDef.maxMotorTorque = 120000;
        rightPaddleJointDef.motorSpeed = -4000;
        rightPaddleJoint = (RevoluteJoint) world.createJoint(rightPaddleJointDef);
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
    static final float MAX_PUSHER_POWER = 5500000;
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
            rightPaddleJoint.enableMotor(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
            leftPaddleJoint.enableMotor(Gdx.input.isKeyPressed(Input.Keys.LEFT));
            if (pinball.getBody().getUserData().equals("pinball dead")) {
                pinball.setBody(createBody("pinball v2", 37.8f, 12));
                pinball.getBody().setUserData("pinball");
            }
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics but more was used
}
