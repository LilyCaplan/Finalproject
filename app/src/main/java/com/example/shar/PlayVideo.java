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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;



public class PlayVideo extends AppCompatActivity {

    private String mUID;
    private String mUserName;
    private DatabaseReference mDatabase;

    private Context mContext;
    public String TAG = "com.example.shar.PlayVideo";
    private LinearLayoutManager mLinearLayoutManager;


    private PostRecyclerView mRecyclerView;



    private ArrayList<Post> mPosts;
    private ArrayList<String> mKeys;

    public static final String KEY = "KEY";
    public static final String USER_KEY = "USER_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


        mRecyclerView = findViewById(R.id.recycler_view);

        Intent intent = getIntent();
        mUID  = intent.getStringExtra(USER_KEY);
        mUserName = intent.getStringExtra(USERNAME_KEY);


        initRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle extras;
        if(!mUID.equals(FirebaseAuth.getInstance().getUid())){
            mUID = FirebaseAuth.getInstance().getUid();
            DataBaseHelper dbh = new DataBaseHelper();
            dbh.getUserNamefromUID(mUID, new DataBaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(ArrayList<Post> posts, ArrayList<String> keys) {

                }
                @Override
                public  void DataIsLoaded(String username){

                }
                @Override
                public void DataFound(String username){
                    mUserName = username;
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
        switch(item.getItemId()) {
            case R.id.Feed:
                intent = new Intent(this, FeedActivity.class);
                extras = new Bundle();
                extras.putString("USER_KEY" , mUID);
                extras.putString( "USERNAME_KEY" , mUserName);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.Camera:
                intent = new Intent(this, LinkLoader.class);
                extras = new Bundle();
                extras.putString("USER_KEY" , mUID);
                extras.putString( "USERNAME_KEY" , mUserName);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.Profile:
                intent = new Intent(this, PlayVideo.class);
                extras = new Bundle();
                extras.putString("USER_KEY" , mUID);
                extras.putString( "USERNAME_KEY" , mUserName);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.SignOut:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void initRecyclerView(){


        DataBaseHelper dbh = new DataBaseHelper(mUID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //mRecyclerView.resetInit();
        mRecyclerView.setLayoutManager(layoutManager);
        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);

        dbh.readPosts(new DataBaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<Post> posts, ArrayList<String> keys) {
                ArrayList<Post> mediaObjects = posts;
                mRecyclerView.setMediaObjects(mediaObjects);
                PostAdapter adapter = new PostAdapter(mediaObjects, initGlide());
                mRecyclerView.setAdapter(adapter);



            }
            @Override
            public  void DataIsLoaded(String username){

            }
            @Override
            public void DataFound(String username){
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
