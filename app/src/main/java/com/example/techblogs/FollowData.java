package com.example.techblogs;

public class FollowData {

    String UserId;

    public FollowData()
    {

    }


    public FollowData(String userId) {
        UserId = userId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
