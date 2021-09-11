package com.example.techblogs;

public class SolutionData {
    String Solution;
    String QuestionId;
    String UserId;

    public SolutionData(String solution, String questionId, String userId) {
        Solution = solution;
        QuestionId = questionId;
        UserId = userId;
    }

    public SolutionData() {
    }

    public String getSolution() {
        return Solution;
    }

    public void setSolution(String solution) {
        Solution = solution;
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
}
