package com.admin.webservice.response_objects;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Admin on 3/16/2016.
 */
public class QuestionResponse {
    private String _id;
    private String questionName;
    private String[] ratingValues;
    private String[] emoticonIds;
    private String questionType;
    private String questionInputType;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String[] getEmoticonIds() {
        return emoticonIds;
    }

    public void setEmoticonIds(String[] emoticonIds) {
        this.emoticonIds = emoticonIds;
    }

    public String[] getRatingValues() {
        return ratingValues;
    }

    public void setRatingValues(String[] ratingValues) {
        this.ratingValues = ratingValues;
    }

    public String getQuestionInputType() {
        return questionInputType;
    }

    public void setQuestionInputType(String questionInputType) {
        this.questionInputType = questionInputType;
    }
}