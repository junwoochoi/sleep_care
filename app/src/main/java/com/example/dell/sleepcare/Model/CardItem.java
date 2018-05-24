package com.example.dell.sleepcare.Model;


import java.util.HashMap;

public class CardItem {

    private HashMap<String, String> mHashMap;
    private int mQuestionResource;

    public CardItem(int question, HashMap<String, String> hashMap) {
        mQuestionResource = question;
        mHashMap = hashMap;
    }

    public HashMap<String, String> getmHashMap() {
        return mHashMap;
    }

    public void setmHashMap(HashMap<String, String> mHashMap) {
        this.mHashMap = mHashMap;
    }

    public int getmQuestionResource() {
        return mQuestionResource;
    }

    public void setmQuestionResource(int mQuestionResource) {
        this.mQuestionResource = mQuestionResource;
    }

    public int getQuestion() {
        return mQuestionResource;
    }
}