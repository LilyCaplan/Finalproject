package com.example.shar;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;



public class PlayVideo extends AppCompatActivity {

    private String mUID;
    private DatabaseReference mDatabase;

    private Context mContext;
    public String TAG = "com.example.shar.PlayVideo";
    private LinearLayoutManager mLinearLayoutManager;


    private PostRecyclerView mRecyclerView;



    private ArrayList<Post> mPosts;
    private ArrayList<String> mKeys;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


        mRecyclerView = findViewById(R.id.recycler_view);

        Intent intent = getIntent();
        String message = intent.getStringExtra(VideoRecordingActivity.KEY);
        mUID = message;

        initRecyclerView();

    }

    private void initRecyclerView(){


        DataBaseHelper dbh = new DataBaseHelper(mUID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);

        dbh.readPosts(new DataBaseHelper.DataStatus() {
            @Override
            public boolean DataIsLoaded(ArrayList<Post> posts, ArrayList<String> keys) {
                ArrayList<Post> mediaObjects = posts;
                mRecyclerView.setMediaObjects(mediaObjects);
                PostAdapter adapter = new PostAdapter(mediaObjects, initGlide());
                mRecyclerView.setAdapter(adapter);


                return  true;

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



    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }


    @Override
    protected void onDestroy() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }







}
