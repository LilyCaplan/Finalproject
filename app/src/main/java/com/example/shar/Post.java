package com.example.shar;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String mVideo;
    //private String thumbnail;


    public Post(){}

    public Post(String video){
        this.mVideo = video;
    }


    public String getmVideo() {
        return mVideo;
    }

    public void setmVideo(String str){ mVideo = str; }


}
