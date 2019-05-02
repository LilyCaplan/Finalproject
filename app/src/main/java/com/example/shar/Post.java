package com.example.shar;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String mVideo;
    private String mText;
    private String mUID;
    //private String thumbnail;


    public Post(){}

    public Post(String video, String text, String uid){
        this.mVideo = video;
        this.mText = text;
        this.mUID = uid;
    }


    public String getmVideo() {
        return mVideo;
    }

    public void setmVideo(String str){ mVideo = str; }

    public void setmText(String str) { mText = str; }

    public String getmText(){ return mText; }

    public String getmUID(){ return mUID; }


}
