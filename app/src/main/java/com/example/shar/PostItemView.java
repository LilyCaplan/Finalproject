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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.VISIBLE;

class PostItemView extends RecyclerView.ViewHolder{


    FrameLayout media_container;
    TextView title;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    String generatedPath;
    ImageView close;
    private PostAdapter mPostAdapterRef;


    private String key;

    public PostItemView(@NonNull View itemView)  {
        super(itemView);


        parent = itemView;
        title = itemView.findViewById(R.id.title);
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        close = itemView.findViewById(R.id.close);
        close.setVisibility(View.INVISIBLE);




    }

    public void onBind(Post postObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
        title.setText(postObject.getmText());
        String str = postObject.getmThumbnail();
        String userID = findUserName(str);
        String thumbFileName = findFileName(str);

        FirebaseStorage storage = FirebaseStorage.getInstance();
// Creating a reference to the link
        StorageReference httpsReference = storage.getReference();
        StorageReference user = httpsReference.child(userID);
        StorageReference thumb = user.child("thumbnail");
        StorageReference pic = thumb.child(thumbFileName);

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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(getAdapterPosition());
                DataBaseHelper dbh = new DataBaseHelper();
                dbh.removePosts(userID, str, new DataBaseHelper.DataStatus(){
                    @Override
                    public void DataIsLoaded(ArrayList<Post> posts, ArrayList<String> keys) {

                    }
                    @Override
                    public  void DataIsLoaded(String username){

                    }
                    @Override
                    public  void DataFound(String username){

                    }
                    @Override
                    public void DataIsInserted() {

                    }

                    @Override
                    public void DataIsUploaded() {

                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });


                String videoFileName = findVideoFilename(postObject.getmVideo());
                StorageHelper sh = new StorageHelper(userID,videoFileName ,thumbFileName );
                sh.deleteFromStorage();


            }

        });

        if(postObject.getmUID().equals(FirebaseAuth.getInstance().getUid())){
            close.setVisibility(VISIBLE);
        }



    }

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

    public void removeItem(int position){
        mPostAdapterRef.remove(position);
        mPostAdapterRef.notifyItemRemoved(position);
    }

    public void setAdapterReference(PostAdapter ps){
        this.mPostAdapterRef = ps;
    }

    public String findVideoFilename(String str){
        String temp;
        for (int i =0; i<str.length()-9; i++){
            temp = str.substring(i, i+9);
            if(temp.equals("videos%2F"))
                return extractVideoFileName(str, i+9);


        }
        return "";

    }

    public String extractVideoFileName(String str, int index){
        String finalStr = "";
        for(int i = index; i< str.length()-4; i++){
            if(str.substring(i, i+4).equals(".mp4")){
                return finalStr + ".mp4";
            } else {
                finalStr += str.charAt(i);
            }


        }
        return finalStr;

    }



}