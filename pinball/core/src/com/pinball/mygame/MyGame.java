package com.pinball.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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
    public static BoardPiece oneWayLine;
    public static BoardPiece oneWayLineTrigger;
    BoardPiece pinballBoard;
    BoardPiece leftFlipperArea;
    BoardPiece rightFlipperArea;
    Pusher pusher;
    Flipper leftPaddle;
    Flipper rightPaddle;
    ArrayList<Bumper> allBumpers = new ArrayList<>();
    ScoreBoard scoreBoard;
    BitmapFont highScoreFont;
    BitmapFont playerScoreFont;
    int playerScoreValue;
    int playerBallsValue;
    Sound soundOfHittingLoot;
    Sound soundOfHittingBumper;
    static final float GLOBAL_VOLUME = 0.5f;
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

        // Despawn line
        board.addNewPiece(world, physicsBodies, 12.3f, -3, "despawn line.png",
                247, 14, "despawn line", "despawn line", "bottom despawn line");

        // The line at the exit of the pusher area that prevents you from going back in there from the board
        oneWayLine = board.addNewPiece(world, physicsBodies, 34.2f, 41.1f, "one way line.png",
                5, 61, "one way line", "one way line");
        oneWayLine.disableBody();


        oneWayLineTrigger = board.addNewPiece(world, physicsBodies, 29, 39, "one way line TRIGGER.png",
                117, 117, "one way line TRIGGER", "one way line TRIGGER");
        oneWayLineTrigger.disableDrawing();


        // Pinball board (the outside-most solid parts of the board)
        pinballBoard = board.addNewPiece(world, physicsBodies, 0, 0, "pinball board v2.png",
                800, 900, "pinball board", "generic boardPiece");

        // Left flipper area
        leftFlipperArea = board.addNewPiece(world, physicsBodies, 0, 0, "pinball board paddle area v2.png",
                800, 900, "pinball board left paddle area", "generic boardPiece");

        // Right flipper area
        rightFlipperArea = board.addNewPiece(world, physicsBodies, 0, 0, "pinball board paddle area2 v2.png",
                800, 900, "pinball board right paddle area", "generic boardPiece");

        // Permanent loot (the crystals/ores)  spawnX and Y have to be manually figured out (takes about 30 seconds of guessing and checking)
        board.addNewPermanentLoot(BoardPiece.PermanentLootType.SMALL, world, physicsBodies, 4.5f, 19.5f, Entity.RotationRadians.LEFT);
        board.addNewPermanentLoot(BoardPiece.PermanentLootType.SMALL, world, physicsBodies, 31.2f, 17.2f, Entity.RotationRadians.RIGHT);
        board.addNewPermanentLoot(BoardPiece.PermanentLootType.MEDIUM, world, physicsBodies, 1.6f, 35, Entity.RotationRadians.LEFT);
        board.addNewPermanentLoot(BoardPiece.PermanentLootType.MEDIUM, world, physicsBodies, 34.1f, 31.8f, Entity.RotationRadians.RIGHT);
        board.addNewPermanentLoot(BoardPiece.PermanentLootType.LARGE, world, physicsBodies, 21, 44, Entity.RotationRadians.TOP);

        pusher = new Pusher(world, physicsBodies, 37.8f, 7, "pusher", pinballBoard);
        leftPaddle = new Flipper(world, physicsBodies, 12.5f, 4.5f, "left paddle.png",
                "left paddle", "left paddle", leftFlipperArea, true);
        rightPaddle = new Flipper(world, physicsBodies, 30, 7, "right paddle.png",
                "right paddle", "right paddle", rightFlipperArea, false);

        // Bumpers 1 through 4
        allBumpers.add(new Bumper(world, physicsBodies, 10, 30, "1"));
        allBumpers.add(new Bumper(world, physicsBodies, 17, 37, "2"));
        allBumpers.add(new Bumper(world, physicsBodies, 24, 30, "3"));
        allBumpers.add(new Bumper(world, physicsBodies, 17, 23, "4"));
        allBumpers.add(new Bumper(world, physicsBodies, 14.5f, 32.5f, "5"));
        allBumpers.add(new Bumper(world, physicsBodies, 19.5f, 32.5f, "6"));
        allBumpers.add(new Bumper(world, physicsBodies, 14.5f, 27.5f, "7"));
        allBumpers.add(new Bumper(world, physicsBodies, 19.5f, 27.5f, "8"));

        world.setContactListener(new CustomContactListener());

        scoreBoard = new ScoreBoard();
        FileHandle file = Gdx.files.internal("scores.txt");
        Scanner scoreInput = new Scanner(file.read());
        String potentialScoreString;
        String regexPatternString = "(.+)\\s+(\\d+)\\s+(.+)"; // to meet a requirement (?) I could easily make this unneeded
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

        playerScoreValue = 0;
        playerBallsValue = 2;

        highScoreFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        highScoreFont.setUseIntegerPositions(false);
        highScoreFont.getData().setScale(SCALE_FACTOR / 1.6f, SCALE_FACTOR / 1.6f);

        // This is just a slightly larger version of the above font
        playerScoreFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
        playerScoreFont.setUseIntegerPositions(false);
        playerScoreFont.getData().setScale(SCALE_FACTOR, SCALE_FACTOR);

        soundOfHittingLoot = Gdx.audio.newSound(Gdx.files.internal("permanentLoot.mp3"));
        soundOfHittingBumper = Gdx.audio.newSound(Gdx.files.internal("hitBumper.mp3"));
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.36f, 0.4f, 0.45f, 1);
        float timeElapsed = stepWorld();
		batch.begin();

        rightPaddle.drawEntity(batch);
        leftPaddle.drawEntity(batch);
        pusher.drawEntity(batch);
        pinball.drawEntity(batch);
        board.drawBoard(batch);
        for (Bumper bumper : allBumpers) {
            bumper.drawAnimation(batch, timeElapsed);
        }
        highScoreFont.draw(batch, scoreBoard.listHighScores(), 42, 40);
        playerScoreFont.draw(batch, "Score: " + playerScoreValue, 42, 20);
        playerScoreFont.draw(batch, "Remaining Balls: " + playerBallsValue, 42, 15);
        playerScoreFont.draw(batch, "Controls: ", 41, 10);
        playerScoreFont.draw(batch, "Flippers: L/R Arrow Keys", 42, 8);
        playerScoreFont.draw(batch, "Plunger: hold Space Bar", 42, 6);

		batch.end();

        // DEBUG WIREFRAME:
        //debugRenderer.render(world, camera.combined);
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
        soundOfHittingLoot.dispose();
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
                if (playerBallsValue > 0) {
                    playerBallsValue--;
                    pinball.respawn(world, physicsBodies);
                    oneWayLineTrigger.enableBody();
                    oneWayLine.disableBody();
                }
                else {
                    // game over
                    String[] scoreArgs = {"YOUR", "SCORE", Integer.toString(playerScoreValue)};
                    Score playerScore = new Score(scoreArgs);
                    scoreBoard.add(playerScore);
                    pinball.setId("pinball", "completely dead");
                }
            }
            else if (pinball.isBumped()) {
                playerScoreValue += 100;
                for (Bumper bumper : allBumpers) {
                    if (bumper.getId().differentiatingFactor().equals(pinball.getId().differentiatingFactor())) {
                        soundOfHittingBumper.play(GLOBAL_VOLUME);
                        pinball.executeBump(bumper);
                        break;
                    }
                }
            }
            else if (pinball.isGettingLoot()) {
                soundOfHittingLoot.play(GLOBAL_VOLUME);
                playerScoreValue += pinball.calculatePointsLoot();
            }
            else if (pinball.isThroughOneWayLineTrigger()) {
                oneWayLineTrigger.disableBody();
                oneWayLine.enableBody();
            }
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            return STEP_TIME;
        }
        else { return 0; }
    }
    // END SUGGESTED CODE FROM -> https://www.codeandweb.com/physicseditor/tutorials/libgdx-physics
}
