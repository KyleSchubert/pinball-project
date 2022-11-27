package com.pinball.mygame;

public class Score {
    private final String firstName;
    private final String lastName;
    private int score;

    public Score(String[] args) {
        this.firstName = args[0];
        this.lastName = args[1];
        try {
            this.score = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getScore() {
        return score;
    }
}
