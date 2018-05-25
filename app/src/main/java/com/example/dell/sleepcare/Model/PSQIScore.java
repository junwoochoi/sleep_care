package com.example.dell.sleepcare.Model;

import android.util.Pair;

import java.util.ArrayList;

public class PSQIScore {
    ArrayList<String> answers;
    Pair<Integer,Integer> scores;

    public PSQIScore(ArrayList<String> answers) {
        this.answers = answers;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public Pair<Integer, Integer> getScores() {
        return scores;
    }

    public void setScores(Pair<Integer, Integer> scores) {
        this.scores = scores;
    }
}
