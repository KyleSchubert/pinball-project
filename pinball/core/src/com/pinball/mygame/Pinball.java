package com.pinball.mygame;

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
        this.setId("pinball");
    }

    public boolean isDead() {
        return this.getId().equals("pinball dead");
    }
}
