package com.example.techblogs;

import com.google.firebase.database.Exclude;

/**
 * Created by hp on 1/26/2020.
 */

public class Upload {
    private String BlogId;
    private String Title;
    private String ImageUrl;
    private String Content;
    private String mKey;
    private String userId;



    public Upload() {

    }

    public Upload(String blogId, String title, String downloadUrl, String content, String userId) {
        BlogId = blogId;
        Title = title;
        ImageUrl = downloadUrl;
        Content = content;
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBlogId() {
        return BlogId;
    }

    public void setBlogId(String blogId) {
        BlogId = blogId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}

