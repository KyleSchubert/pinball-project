package com.pinball.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.pinball.mygame.MyGame.ORIGINAL_PUSHER_JOINT_LENGTH;
import static com.pinball.mygame.MyGame.STEP_TIME;

public class Pusher extends BoardPiece {
    private static final float MAX_PUSHER_POWER = 5500000;
    private float pusherChargedPercentPower = 0;
    private boolean isPusherCharging = false;

    public Pusher(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY, String id, BoardPiece pinballBoard) {
        super(world, physicsBodies, spawnX, spawnY, "pusher.png", 43, 44, "pusher", id);
        // PUSHER JOINT (so the pusher can move with the SPACE bar)
        Vector2 jointOffsetBodyA = new Vector2(spawnX,spawnY-1);
        Vector2 jointOffsetBodyB = new Vector2(this.getBody().getWorldCenter().x, this.getBody().getWorldCenter().y);
        DistanceJointDef pusherJointDef = new DistanceJointDef();
        pusherJointDef.initialize(pinballBoard.getBody(), this.getBody(), jointOffsetBodyA, jointOffsetBodyB);
        pusherJointDef.collideConnected = true;
        pusherJointDef.frequencyHz = 40f;
        pusherJointDef.length = ORIGINAL_PUSHER_JOINT_LENGTH;
        pusherJointDef.dampingRatio = 1f;
        world.createJoint(pusherJointDef);
    }

    public void keyCheck() {
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
                this.getBody().applyForceToCenter(0, MAX_PUSHER_POWER * pusherChargedPercentPower, true);
                pusherChargedPercentPower = 0;
            }
        }
    }
}
