package com.pinball.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class Flipper extends BoardPiece {
    private final RevoluteJoint joint;
    private final int keyToCheck;

    public Flipper(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY, String textureFile,
                   String physicsName, String id, BoardPiece flipperAreaLeftOrRight, boolean isLeft) {
        super(world, physicsBodies, spawnX, spawnY, textureFile, 102, 38, physicsName, id);
        RevoluteJointDef flipperJointDef = new RevoluteJointDef();
        flipperJointDef.initialize(flipperAreaLeftOrRight.getBody(), this.getBody(), this.getBody().getPosition());
        flipperJointDef.referenceAngle = 0;
        flipperJointDef.enableLimit = true;
        flipperJointDef.collideConnected = false;
        flipperJointDef.enableMotor = false;
        flipperJointDef.maxMotorTorque = 120000;
        if (isLeft) { // left flipper
            keyToCheck = Input.Keys.LEFT;
            flipperJointDef.lowerAngle = 0;
            flipperJointDef.upperAngle = 0.8f;
            flipperJointDef.motorSpeed = 4000;
            flipperJointDef.localAnchorA.set(new Vector2(11.2f, 5.5f));
            flipperJointDef.localAnchorB.set(new Vector2(0.5f, 1.5f));
        }
        else { // right flipper
            keyToCheck = Input.Keys.RIGHT;
            flipperJointDef.lowerAngle = -0.8f;
            flipperJointDef.upperAngle = 0;
            flipperJointDef.motorSpeed = -4000;
            flipperJointDef.localAnchorA.set(new Vector2(24.4f, 5.5f));
            flipperJointDef.localAnchorB.set(new Vector2(4.6f, 1.5f));
        }
        joint = (RevoluteJoint) world.createJoint(flipperJointDef);
    }

    public void keyCheck() {
        this.joint.enableMotor(Gdx.input.isKeyPressed(keyToCheck));
    }
}
