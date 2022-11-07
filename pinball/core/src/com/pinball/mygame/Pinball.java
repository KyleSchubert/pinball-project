package com.pinball.mygame;

import com.badlogic.gdx.graphics.Texture;

public class Pinball {
    private float radius;


    public Pinball(float mass, float x, float y, float radius) {
        if (radius < 0) {
            this.radius = 1;
            System.out.println("Fix the pinball radius. It was defaulted to 1 because it was   " + radius);
        }
        else {
            this.radius = radius;
        }

    }
    
    public void setRadius(float radius) {
        if (radius > 0) {
            this.radius = radius;
        }
    }
}
