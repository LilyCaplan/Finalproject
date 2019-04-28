package com.example.shar;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

class PostItemView extends RecyclerView.ViewHolder{


    FrameLayout media_container;
    TextView title;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;

    private String key;

    public PostItemView(@NonNull View itemView)  {
        super(itemView);


        parent = itemView;
        title = itemView.findViewById(R.id.title);
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);



    }

    public void onBind(Post postObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
         title.setText(postObject.getmText());
//            this.requestManager
//                    .load(mediaObject.getThumbnail())
//                    .into(thumbnail);
    }


}