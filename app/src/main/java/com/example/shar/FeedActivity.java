package com.example.shar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class FeedActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Intent intent = getIntent();
        String message = intent.getStringExtra(VideoRecordingActivity.KEY);
        mUID = message;




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.Feed:
                intent = new Intent(this, FeedActivity.class);
                intent.putExtra(KEY , mUID );
                startActivity(intent);
                break;
            case R.id.Camera:
                intent = new Intent(this, VideoRecordingActivity.class);
                intent.putExtra(KEY , mUID );
                startActivity(intent);
                break;
            case R.id.Profile:
                intent = new Intent(this, PlayVideo.class);
                intent.putExtra(KEY , mUID );
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
