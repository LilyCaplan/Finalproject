package com.example.shar;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.view.View.VISIBLE;

class PostItemView extends RecyclerView.ViewHolder{


    FrameLayout media_container;
    TextView title;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    String generatedPath;

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
        String str = postObject.getmThumbnail();

        FirebaseStorage storage = FirebaseStorage.getInstance();
// Creating a reference to the link
        StorageReference httpsReference = storage.getReference();
        StorageReference user = httpsReference.child(findUserName(str));
        StorageReference thumb = user.child("thumbnail");
        StorageReference pic = thumb.child(findFileName(str));

        pic.getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    generatedPath = task.getResult().toString();
                    Glide.with(thumbnail.getContext())
                            .load(generatedPath)
                            .centerCrop()
                            .into(thumbnail);
                    thumbnail.setVisibility(VISIBLE);

                } else {
                    Log.w("POST ITEM VIEW", "Getting download url was not successful.",
                            task.getException());
                }
            }
        });


//
//
//        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.white_background)
//                .error(R.drawable.white_background);
//
//
//
//
       // Picasso.get().load(str).into(thumbnail);
        //thumbnail.setVisibility(VISIBLE);




        //this.requestManager.load(httpsReference).into(thumbnail);



    }

    //https://firebasestorage.googleapis.com/v0/b/shar-2abcd.appspot.com/o?name=Pq2chsQ0n5agTtZQubAEJgOnTDF2%2Fthumbnail%2FSample16a898f999f.png&uploadType=resumable&upload_id=AEnB2Uo0n-X5vZAEaELiy2OfdbTDDPBKXsDkBkHH4KxlxUapqJY5RPYTQBFBueSJ4sQRcQvtR5eWdE7xY5Ti6nP8OfrZ7ANQZDy8mZzapaHQDVJkPnsstlA&upload_protocol=resumable


    public String findUserName(String str){
        String temp;
        for (int i =0; i<str.length()-5; i++){
            temp = str.substring(i, i+5);
            if(temp.equals("name="))
                return extractUserName(str, i+5);


        }
        return "";

    }

    public String extractUserName(String str, int index){
        String finalStr = "";
        for(int i = index; i< str.length()-3; i++){
            if(str.substring(i, i+3).equals("%2F")){
                return finalStr;
            } else {
                finalStr += str.charAt(i);
            }


        }
        return finalStr;

    }

    public String findFileName(String str){
        String temp;
        for (int i =0; i<str.length()-12; i++){
            temp = str.substring(i, i+12);
            if(temp.equals("thumbnail%2F"))
                return extractFieName(str, i+12);


        }
        return "";
    }

    public String extractFieName(String str, int index){
        String finalStr = "";
        for(int i = index; i< str.length()-4; i++){
            if(str.substring(i, i+4).equals(".png")){
                return finalStr + ".png";
            } else {
                finalStr += str.charAt(i);
            }


        }
        return finalStr;

    }



}