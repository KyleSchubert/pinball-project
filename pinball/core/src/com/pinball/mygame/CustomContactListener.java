package com.pinball.mygame;

import com.badlogic.gdx.physics.box2d.*;

public class CustomContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA == null || fixtureB == null) return;
        if (fixtureA.getBody().getUserData() == null || fixtureB.getBody().getUserData() == null) return;
        System.out.println(fixtureA.getBody().getUserData() + " <-- touched --> " + fixtureB.getBody().getUserData());

        if (checkFor(fixtureA, fixtureB, "pinball", "despawn line")) {
            getPinballFixture(fixtureA, fixtureB).getBody().setUserData("pinball dead");
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

    private boolean checkFor(Fixture fixtureA, Fixture fixtureB, String userData1, String userData2) {
        String string1 = fixtureA.getBody().getUserData().toString();
        String string2 = fixtureB.getBody().getUserData().toString();
        return string1.equals(userData1) && string2.equals(userData2)
                || (string1.equals(userData2) && string2.equals(userData1));
    }

    private Fixture getPinballFixture(Fixture fixtureA, Fixture fixtureB) {
        if (fixtureA.getBody().getUserData().toString().equals("pinball")) {
            return fixtureA;
        }
        else {
            return fixtureB;
        }
    }
}
