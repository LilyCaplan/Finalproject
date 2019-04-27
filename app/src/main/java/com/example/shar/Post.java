package com.example.shar;

import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String mVideo;
    private String mThumbnail;


    public Post(){}

    public Post(String video, String thumbnail){
        this.mVideo = video;
        this.mThumbnail = thumbnail;
    }


    public String getmVideo() {
        return mVideo;
    }

    public String getmThumbnail(){
        return mThumbnail;
    }

    public void setmVideo(String str){ this.mVideo = str; }

    public void  setmThumbnail(String str){ this.mThumbnail = str; }


}
