package com.example.mjai37.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "QUESTION")
public class Question implements Serializable {

    public Question(String name,String ratingValues){
        this.name = name;
        this.ratingValues = ratingValues;
    }

    public Question() {

    }

    @DatabaseField
    private String questionId;

    @DatabaseField
    private String name;

    @DatabaseField
    private String ratingValues;

    @DatabaseField
    private String selected;


    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRatingValues() {
        return ratingValues;
    }

    public void setRatingValues(String ratingValues) {
        this.ratingValues = ratingValues;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
