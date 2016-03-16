package com.example.admin.webservice.response_objects;

/**
 * Created by Admin on 3/16/2016.
 */
public class QuestionResponse {
    private String questionId;
    private String questionName;
    private String[] optionValues;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String[] getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(String[] optionValues) {
        this.optionValues = optionValues;
    }
}