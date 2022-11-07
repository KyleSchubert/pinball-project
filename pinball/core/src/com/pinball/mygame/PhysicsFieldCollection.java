package com.pinball.mygame;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class PhysicsFieldCollection {
    private final ArrayList<PhysicsField> allFields  = new ArrayList<>();
    private Vector2 vectorSum = new Vector2(0, 0);

    PhysicsFieldCollection() {}

    public void add(PhysicsField physicsField) {
        if (!contains(physicsField.getId())) {
            allFields.add(physicsField);
            calculateVectorSum();
        }
    }

    public void replace(PhysicsField physicsField) {
        for (PhysicsField pf : allFields) {
            if (pf.getId().equalsIgnoreCase(physicsField.getId())) {
                remove(pf);
                add(physicsField);
                return;
            }
        }
    }

    public void remove(PhysicsField physicsField) {
        if (allFields.remove(physicsField)) {
            calculateVectorSum();
        }
    }

    private void calculateVectorSum() {
        Vector2 newSum = new Vector2(0, 0);
        for (PhysicsField physicsField : allFields) {
            newSum.add(physicsField.getVector2());
        }
        vectorSum = newSum;
    }

    private boolean contains(String id) {
        for (PhysicsField physicsField : allFields) {
            if (physicsField.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public float getX() {
        return vectorSum.x;
    }

    public float getY() {
        return vectorSum.y;
    }

    public Vector2 getVectorSum() {
        return vectorSum;
    }
}
