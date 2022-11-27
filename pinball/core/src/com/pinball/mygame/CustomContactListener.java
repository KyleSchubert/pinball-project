package com.pinball.mygame;

import com.badlogic.gdx.physics.box2d.*;

import java.util.Arrays;

import static com.pinball.mygame.MyGame.pinball;

public class CustomContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA == null || fixtureB == null) return;
        if (fixtureA.getBody().getUserData() == null || fixtureB.getBody().getUserData() == null) return;
        //System.out.println(fixtureA.getBody().getUserData() + " <-- touched --> " + fixtureB.getBody().getUserData());

        if (checkFor(fixtureA, fixtureB, "pinball", "despawn line")) {
            pinball.setId("pinball", "dead");
        }
        else if (checkFor(fixtureA, fixtureB, "pinball", "bumper")) {
            pinball.setId("bumpedPinball", getNonPinballEntityData(fixtureA, fixtureB).differentiatingFactor());
        }
        else if (checkFor(fixtureA, fixtureB, "pinball", "permanentLoot")) {
            pinball.setId("lootingPinball", getNonPinballEntityData(fixtureA, fixtureB).differentiatingFactor());
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA == null || fixtureB == null) return;
        if (fixtureA.getUserData() == null || fixtureB.getUserData() == null) return;

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    /**
     * Checks for contact between the two types of entities by reading information from the fixtures.
     * @param fixtureA comes from contact.getFixtureA()
     * @param fixtureB comes from contact.getFixtureB()
     * @param entityType1 a type like "pinball", "bumper", "despawn line", etc. From EntityData class or Entity.getId()
     * @param entityType2 a type like "pinball", "bumper", "despawn line", etc. From EntityData class or Entity.getId()
     * @return returns a boolean about if contact occurred between those two types of Entities.
     */
    private boolean checkFor(Fixture fixtureA, Fixture fixtureB, String entityType1, String entityType2) {
        EntityData entityData1 = (EntityData) fixtureA.getBody().getUserData();
        EntityData entityData2 = (EntityData) fixtureB.getBody().getUserData();
        String realEntityType1 = entityData1.entityType();
        String realEntityType2 = entityData2.entityType();
        String realEntityDF1 = entityData1.differentiatingFactor();
        String realEntityDF2 = entityData2.differentiatingFactor();
        boolean check1 = Arrays.asList(realEntityType1, realEntityType2, realEntityDF1, realEntityDF2).contains(entityType1);
        boolean check2 = Arrays.asList(realEntityType1, realEntityType2, realEntityDF1, realEntityDF2).contains(entityType2);
        return check1 && check2;
    }

    private EntityData getNonPinballEntityData(Fixture fixtureA, Fixture fixtureB) {
        EntityData entityData1 = (EntityData) fixtureA.getBody().getUserData();
        EntityData entityData2 = (EntityData) fixtureB.getBody().getUserData();
        if (entityData1.entityType().equals("pinball")) {
            return entityData2;
        }
        else {
            return entityData1;
        }
    }
}
