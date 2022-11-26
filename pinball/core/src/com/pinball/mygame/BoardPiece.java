package com.pinball.mygame;

import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class BoardPiece extends Entity {
    public BoardPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY,
                      String textureFile, int width, int height, String physicsName, String id) {
        this.makeBody(physicsName, spawnX, spawnY, world, physicsBodies);
        this.makeSprite(textureFile, width, height);
        this.setId(id);
    }

    public BoardPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY,
                      String textureFile, int width, int height, String physicsName) {
        this.makeBody(physicsName, spawnX, spawnY, world, physicsBodies);
        this.makeSprite(textureFile, width, height);
        this.setId(physicsName);
    }
}
