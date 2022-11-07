package com.pinball.mygame;

import com.badlogic.gdx.math.Vector2;

public class PhysicsField {
    private final Vector2 vector2;
    private final String id;

    PhysicsField(Vector2 vector2, String id) {
        this.vector2 = vector2;
        this.id = id;
    }

    public Vector2 getVector2() {
        return vector2;
    }

    public String getId() {
        return id;
    }
}
