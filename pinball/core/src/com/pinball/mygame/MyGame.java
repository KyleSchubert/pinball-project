package com.pinball.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	World world;
    ExtendViewport viewport;
    public static final float SCALE_FACTOR = 0.05f;
    Box2DDebugRenderer debugRenderer;
    PhysicsShapeCache physicsBodies;
    public static Pinball pinball;
    Board board;
    BoardPiece despawnLine;
    BoardPiece pinballBoard;
    BoardPiece leftFlipperArea;
    BoardPiece rightFlipperArea;
    Pusher pusher;
    Flipper leftPaddle;
    Flipper rightPaddle;
    Bumper bumper1;
    Bumper bumper2;
    Bumper bumper3;
    Bumper bumper4;
    ArrayList<Bumper> allBumpers = new ArrayList<>();
    ScoreBoard scoreBoard;
    BitmapFont highScoreFont;
    BitmapFont playerScoreFont;
    int playerScoreValue;
    public static final float ORIGINAL_PUSHER_JOINT_LENGTH = 1 / (8 * SCALE_FACTOR);

	@Override
	public void create () { // this is where assets are usually loaded, apparently
        Box2D.init();
        world = new World(new Vector2(0, -50), true);
        physicsBodies = new PhysicsShapeCache("physics.xml");
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
        viewport = new ExtendViewport(800*SCALE_FACTOR, 900*SCALE_FACTOR, camera);
        debugRenderer = new Box2DDebugRenderer();

        pinball = new Pinball(world, physicsBodies);
        // color palette -->  https://coolors.co/7fe57f-000000-5c6672-b8c5d6-f4faff
        // added noise of   17, 12, 26
        board = new Board();
        despawnLine = board.addNewPiece(world, physicsBodies, 12.3f, -3, "despawn line.png",
                247, 14, "despawn line");
        pinballBoard = board.addNewPiece(world, physicsBodies, 0, 0, "pinball board v2.png",
                800, 900, "pinball board");
        leftFlipperArea = board.addNewPiece(world, physicsBodies, 0, 0, "pinball board paddle area v2.png",
                800, 900, "pinball board left paddle area");
        rightFlipperArea = board.addNewPiece(world, physicsBodies, 0, 0, "pinball board paddle area2 v2.png",
                800, 900, "pinball board right paddle area");

        pusher = new Pusher(world, physicsBodies, 37.8f, 7, "pusher", pinballBoard);
        leftPaddle = new Flipper(world, physicsBodies, 12.5f, 4.5f, "left paddle.png",
                "left paddle", "left paddle", leftFlipperArea, true);
        rightPaddle = new Flipper(world, physicsBodies, 30, 7, "right paddle.png",
                "right paddle", "right paddle", rightFlipperArea, false);
        bumper1 = new Bumper(world, physicsBodies, 12, 30, "1");
        allBumpers.add(bumper1);
        bumper2 = new Bumper(world, physicsBodies, 17, 35, "2");
        allBumpers.add(bumper2);
        bumper3 = new Bumper(world, physicsBodies, 22, 30, "3");
        allBumpers.add(bumper3);
        bumper4 = new Bumper(world, physicsBodies, 17, 25, "4");
        allBumpers.add(bumper4);

        world.setContactListener(new CustomContactListener());

        scoreBoard = new ScoreBoard();
        try (Scanner scoreInput = new Scanner(Paths.get("scores.txt"))) {
            String potentialScoreString;
            String regexPatternString = "(.+)\\s+(\\d+)\\s+(.+)";
            Pattern regexPatten = Pattern.compile(regexPatternString);
            Matcher matches;
            while (scoreInput.hasNextLine()) {
                potentialScoreString = scoreInput.nextLine();
                matches = regexPatten.matcher(potentialScoreString);
                if (matches.find()) {
                    String[] someScoreParams = {matches.group(1), matches.group(3), matches.group(2)};
                    Score someScore = new Score(someScoreParams);
                    scoreBoard.add(someScore);
                }
            }
            scoreBoard.listHighScores();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        playerScoreValue = 0;

        highScoreFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        highScoreFont.setUseIntegerPositions(false);
        highScoreFont.getData().setScale(SCALE_FACTOR / 1.6f, SCALE_FACTOR / 1.6f);

        playerScoreFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        playerScoreFont.setUseIntegerPositions(false);
        playerScoreFont.getData().setScale(SCALE_FACTOR, SCALE_FACTOR);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.36f, 0.4f, 0.45f, 1);
        float timeElapsed = stepWorld();
		batch.begin();

        rightPaddle.drawEntity(batch);
        leftPaddle.drawEntity(batch);
        despawnLine.drawEntity(batch);
        pusher.drawEntity(batch);
        pinball.drawEntity(batch);
        board.drawBoard(batch);
        for (Bumper bumper : allBumpers) {
            bumper.drawAnimation(batch, timeElapsed);
        }
        highScoreFont.draw(batch, scoreBoard.listHighScores(), 42, 40);
        playerScoreFont.draw(batch, "Score: " + playerScoreValue, 42, 20);

		batch.end();

        // DEBUG WIREFRAME:
        debugRenderer.render(world, camera.combined);
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

	@Override
	public void dispose () {
		batch.dispose();
        world.dispose();
        debugRenderer.dispose();
        highScoreFont.dispose();
        playerScoreFont.dispose();
	}

    // START SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
    public static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;

    float accumulator = 0;
    private float stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);
        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            pusher.keyCheck();
            rightPaddle.keyCheck();
            leftPaddle.keyCheck();
            if (pinball.isDead()) {
                pinball.respawn(world, physicsBodies);
            }
            else if (pinball.isBumped()) {
                playerScoreValue += 100;
                for (Bumper bumper : allBumpers) {
                    if (bumper.getId().differentiatingFactor().equals(pinball.getId().differentiatingFactor())) {
                        pinball.executeBump(bumper);
                        break;
                    }
                }
            }
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            return STEP_TIME;
        }
        else { return 0; }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
}
