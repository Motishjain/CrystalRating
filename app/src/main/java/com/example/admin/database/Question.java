package com.example.admin.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "QUESTION")
public class Question implements Serializable {

    @DatabaseField
    private String questionId;

    @DatabaseField
    private String name;

    @DatabaseField
    private String ratingValues;

    @DatabaseField
    private String emoticonIds;

    @DatabaseField
    private String selected;

    @DatabaseField
    private String questionType;

    private String selectedOption;

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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getEmoticonIds() {
        return emoticonIds;
    }

    public void setEmoticonIds(String emoticonIds) {
        this.emoticonIds = emoticonIds;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    @Override
    public boolean equals (Object question) {
        if(question instanceof Question && this.questionId.equals(((Question)question).questionId)) {
            return true;
        }
        return false;
    }
}
