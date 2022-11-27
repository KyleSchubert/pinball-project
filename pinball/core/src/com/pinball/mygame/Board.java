package com.pinball.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.ArrayList;

public class Board  {
    private final ArrayList<BoardPiece> pieces = new ArrayList<>();

    public void add(final BoardPiece piece) {
        if (isIdAlreadyInUse(piece.getId())) {
            System.out.printf("[Should not happen]: a duplicate piece was trying to join the Board. (id: %s)",
                    piece.getId());
        }
        else {
            pieces.add(piece);
        }
    }

    private boolean isIdAlreadyInUse(EntityData pieceId) {
        return pieces.stream().anyMatch((somePiece) -> somePiece.getId().equals(pieceId));
    }

    public void drawBoard(SpriteBatch batch) {
        pieces.forEach((piece) -> piece.drawEntity(batch));
    }

    public void addNewPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY,
                       String textureFile, int width, int height, String physicsName, String id) {
        if (isIdAlreadyInUse(new EntityData("boardPiece", id))) {
            System.out.printf("[Should not happen]: a duplicate piece was prevented from being made. (id: %s)", id);
        }
        else {
            BoardPiece piece = new BoardPiece(world, physicsBodies, spawnX, spawnY, textureFile, width, height, physicsName, id);
            pieces.add(piece);
        }
    }

    public BoardPiece addNewPiece(World world, PhysicsShapeCache physicsBodies, float spawnX, float spawnY,
                      String textureFile, int width, int height, String physicsName) {
        if (isIdAlreadyInUse(new EntityData("boardPiece", physicsName))) {
            System.out.printf("[Should not happen]: a duplicate piece was prevented from being made. (id: %s)", physicsName);
            return null;
        }
        else {
            BoardPiece piece = new BoardPiece(world, physicsBodies, spawnX, spawnY, textureFile, width, height, physicsName);
            pieces.add(piece);
            return piece;
        }
    }
}
