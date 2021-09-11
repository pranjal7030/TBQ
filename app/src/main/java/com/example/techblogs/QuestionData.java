package com.example.techblogs;

public class QuestionData {
    String QuestionId;
    String UserId;
    String QuestionTitle;
    String QuestionDescription;

    public  QuestionData()
    {

    }

    public QuestionData(String questionId, String userId, String questionTitle, String questionDescription) {
        QuestionId = questionId;
        UserId = userId;
        QuestionTitle = questionTitle;
        QuestionDescription = questionDescription;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String questionId) {
        QuestionId = questionId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getQuestionTitle() {
        return QuestionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        QuestionTitle = questionTitle;
    }

    public String getQuestionDescription() {
        return QuestionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        QuestionDescription = questionDescription;
    }
}
