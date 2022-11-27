package com.pinball.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;

public class Board  {
    private final ArrayList<BoardPiece> pieces = new ArrayList<>();

    public void add(final BoardPiece piece) {
        pieces.add(piece);
    }

    public void drawBoard(SpriteBatch batch) {
        pieces.forEach((piece) -> piece.drawEntity(batch));
    }

    public BoardPiece addNewPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY,
                       String textureFile, int width, int height, String physicsName, String entityType, String id) {
        BoardPiece piece = new BoardPiece(world, physicsBodies, spawnX, spawnY, textureFile, width, height, physicsName, entityType, id);
        pieces.add(piece);
        return piece;
    }

    public BoardPiece addNewPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY,
                      String textureFile, int width, int height, String physicsName, String entityType) {
        BoardPiece piece = new BoardPiece(world, physicsBodies, spawnX, spawnY, textureFile, width, height, physicsName, entityType);
        pieces.add(piece);
        return piece;
    }
}
