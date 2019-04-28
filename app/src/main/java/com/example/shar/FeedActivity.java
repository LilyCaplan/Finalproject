package com.example.shar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class FeedActivity extends AppCompatActivity {

    public static final String KEY = "KEY";
    public static final String USER_KEY = "USER_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";
    private String mUID;
    private String mUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Intent intent = getIntent();
        mUID  = intent.getStringExtra(USER_KEY);
        mUserName = intent.getStringExtra(USERNAME_KEY);





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

        }
        return super.onOptionsItemSelected(item);
    }


}
