package com.example.shar;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String mVideo;
    private String mText;
    //private String thumbnail;


    public Post(){}

    public Post(String video, String text){
        this.mVideo = video;
        this.mText = text;
    }


    public String getmVideo() {
        return mVideo;
    }

    public void setmVideo(String str){ mVideo = str; }

    public void setmText(String str) { mText = str; }

    public String getmText(){ return mText; }


}
