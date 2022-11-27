package com.pinball.mygame;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreBoard {
    private final ArrayList<Score> scores = new ArrayList<>();

    public void add(Score someScore) {
        scores.add(someScore);
    }

    public String listHighScores() {
        return "High Scores:\n" + scores.stream()
                .sorted(Comparator.comparingInt(Score::getScore).reversed())
                .map(score -> "- " + score.getFirstName() + " " + score.getLastName() + " : " + score.getScore())
                .collect(Collectors.joining("\n"));
    }
}
