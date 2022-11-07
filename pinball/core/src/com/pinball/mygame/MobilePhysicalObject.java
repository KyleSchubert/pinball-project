package com.pinball.mygame;

import com.badlogic.gdx.math.Vector2;

public class MobilePhysicalObject extends PhysicalObject {
    private float mass;
    private final Vector2 velocity = new Vector2(0, 0);
    private final Vector2 acceleration = new Vector2(0, 0);
    private final PhysicsFieldCollection feltForces = new PhysicsFieldCollection();
    private final PhysicsFieldCollection feltAccelerationFields = new PhysicsFieldCollection();
    private final PhysicsFieldCollection feltVelocityFields = new PhysicsFieldCollection();

    MobilePhysicalObject(float mass, float x, float y, String textureFile) {
        super(x, y, textureFile);
        this.mass = mass;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        if (mass > 0) {
            this.mass = mass;
        }
    }

    public void advance(float timePassed) {
        velocity.x += (acceleration.x + feltAccelerationFields.getX()) * timePassed;
        velocity.y += (acceleration.y + feltAccelerationFields.getY()) * timePassed;
        float changeInVelocityX = (acceleration.x + feltAccelerationFields.getX()) * timePassed;
        float changeInVelocityY = (acceleration.y + feltAccelerationFields.getY()) * timePassed;

        float velocityLimit = 1500;
        if (velocity.x + changeInVelocityX > velocityLimit) {
            velocity.x = velocityLimit;
        }
        else if (velocity.x + changeInVelocityX < -velocityLimit) {
            velocity.x = -velocityLimit;
        }
        else {
            velocity.x += changeInVelocityX;
        }
        if (velocity.y + changeInVelocityY > velocityLimit) {
            velocity.y = velocityLimit;
        }
        else if (velocity.y + changeInVelocityY < -velocityLimit) {
            velocity.y = -velocityLimit;
        }
        else {
            velocity.y += changeInVelocityY;
        }

        x += (velocity.x + feltVelocityFields.getX()) * timePassed;
        y += (velocity.y + feltVelocityFields.getY()) * timePassed;
    }

    public void addForce(Vector2 vector2, String id) {
        PhysicsField physicsField = new PhysicsField(vector2, id);
        feltForces.add(physicsField);
    }

    public void addAccelerationField(Vector2 vector2, String id) {
        PhysicsField physicsField = new PhysicsField(vector2, id);
        feltAccelerationFields.add(physicsField);
    }

    public void addVelocityField(Vector2 vector2, String id) {
        PhysicsField physicsField = new PhysicsField(vector2, id);
        feltVelocityFields.add(physicsField);
    }
}
