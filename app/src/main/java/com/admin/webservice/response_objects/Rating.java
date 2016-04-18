package com.admin.webservice.response_objects;

/**
 * Created by admin on 17-04-2016.
 */
public class Rating {

    String questionId;
    Integer selectedOptionIndex;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(Integer selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }
}
