package com.example.techblogs;

import com.google.firebase.database.Exclude;

/**
 * Created by hp on 1/30/2020.
 */

public class User_Profile_pics {

    private String mName;
    private String mImageUrl;
    private String mKey;

    public User_Profile_pics() {
        //empty constructor needed
    }

    public User_Profile_pics(String name, String downloadUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = downloadUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
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
