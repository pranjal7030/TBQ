package com.example.techblogs;

/**
 * Created by hp on 2/8/2020.
 */

public class Saved {

    String userId;
    String blogId;

    public Saved()
    {

    }

    public Saved(String userId, String blogId) {
        this.userId = userId;
        this.blogId = blogId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
}
