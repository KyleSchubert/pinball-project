package com.pinball.mygame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class Pinball extends Entity {
    private static final float SPAWN_X = 37.8f;
    private static final float SPAWN_Y = 12;

    public Pinball(World world, PhysicsShapeCache physicsBodies) {
        respawn(world, physicsBodies);
        this.makeSprite("pinball v3.png", 32, 32);
    }

    public void respawn(World world, PhysicsShapeCache physicsBodies) {
        this.makeBody("pinball", SPAWN_X, SPAWN_Y, world, physicsBodies);
        resetId();
    }

    private void resetId() {
        this.setId("pinball", "alive");
    }

    public boolean isDead() {
        return this.getId().differentiatingFactor().equals("dead");
    }

    public boolean isBumped() {
        return this.getId().entityType().equals("bumpedPinball");
    }

    public boolean isThroughOneWayLineTrigger() {
        return this.getId().differentiatingFactor().equals("passedOneWayLineTrigger");
    }

    public void executeBump(Bumper bumper) {
        Vector2 directionVector = this.getBody().getWorldCenter().sub(bumper.getBody().getWorldCenter());
        this.getBody().applyForceToCenter(directionVector.nor().scl(12000), true);
        resetId();
    }

    public boolean isGettingLoot() {
        return this.getId().entityType().equals("lootingPinball");
    }

    public int calculatePointsLoot() {
        int amountToReward;
        switch (this.getId().differentiatingFactor()) {
            case ("permanentLoot1") -> amountToReward = 1500;
            case ("permanentLoot2") -> amountToReward = 2500;
            case ("permanentLoot3") -> amountToReward = 6000;
            default -> amountToReward = 0;
        }
        this.resetId();
        return amountToReward;
    }
}
