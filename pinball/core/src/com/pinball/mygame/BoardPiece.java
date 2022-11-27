package com.pinball.mygame;

import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

public class BoardPiece extends Entity {
    public enum PermanentLootType {
        SMALL, MEDIUM, LARGE
    }
    public BoardPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY, String textureFile,
                      int width, int height, String physicsName, String entityType, String id) {
        this.makeBody(physicsName, spawnX, spawnY, world, physicsBodies);
        this.makeSprite(textureFile, width, height);
        this.setId(entityType, id);
    }

    public BoardPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY, String textureFile,
                      int width, int height, String physicsName, String entityType) {
        this.makeBody(physicsName, spawnX, spawnY, world, physicsBodies);
        this.makeSprite(textureFile, width, height);
        this.setId(entityType, physicsName);
    }

    public BoardPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY, String textureFile,
                      int width, int height, String physicsName, String entityType, Entity.RotationRadians rotationRadians) {
        this.makeBody(physicsName, spawnX, spawnY, rotationRadians.getRadians(), world, physicsBodies);
        this.makeSprite(textureFile, width, height);
        this.setId(entityType, physicsName);
    }
}
