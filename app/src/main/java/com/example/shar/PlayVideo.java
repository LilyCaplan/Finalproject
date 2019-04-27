package com.example.shar;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;



public class PlayVideo extends AppCompatActivity {

    private String mUID;
    private boolean mPoolUserData;
    private DatabaseReference mDatabase;

    private Context mContext;
    public String TAG = "com.example.shar.PlayVideo";
    private LinearLayoutManager mLinearLayoutManager;


    private PostRecyclerView mRecyclerView;



    private ArrayList<Post> mPosts;
    private ArrayList<String> mKeys;

    public static final String KEY = "KEY";

    public static final String USER_ID = "user_id";

    public static final String PULL_USER_DATA = "pull user data";








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


        mRecyclerView = findViewById(R.id.recycler_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mUID = intent.getStringExtra(USER_ID);
        mPoolUserData = extras.getBoolean(PULL_USER_DATA);


        initRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle extras;
        switch(item.getItemId()) {
            case R.id.Feed:
                intent = new Intent(this, FeedActivity.class);
                extras = new Bundle();
                extras.putString(USER_ID, mUID);
                extras.putBoolean(PULL_USER_DATA, false);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.Camera:
                intent = new Intent(this, VideoRecordingActivity.class);
                extras = new Bundle();
                extras.putString(USER_ID, mUID);
                extras.putBoolean(PULL_USER_DATA, false);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.Profile:
                intent = new Intent(this, PlayVideo.class);
                extras = new Bundle();
                extras.putString(USER_ID, mUID);
                extras.putBoolean(PULL_USER_DATA, true);
                intent.putExtras(extras);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerViewForUserPosts(){


        DataBaseHelper dbh = new DataBaseHelper();
        dbh.setReferenceTarget(mUID, "videos");


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

    private void initRecyclerViewForAllPosts(){


        DataBaseHelper dbh = new DataBaseHelper();
        dbh.setReferenceTarget("all_posts");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);


        //FIX THIS TOMORROW

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
