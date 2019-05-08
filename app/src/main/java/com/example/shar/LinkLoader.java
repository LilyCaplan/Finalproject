package com.example.shar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LinkLoader extends AppCompatActivity {

    private Button addButton;

    private EditText linkText;

    private String mUID;

    private String mUserName;

    private Context mContext;

    public static final String LINKLOADER_KEY = "Link Loader CLass";
    public static final String USER_KEY = "USER_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";

    public static final String KEY = "KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkloader);

        linkText = findViewById(R.id.editText);
        addButton = findViewById(R.id.button);

        mContext = this;



        Intent intent = getIntent();
        mUID = intent.getExtras().getString(SignInActivity.USER_KEY);
        mUserName = intent.getExtras().getString(SignInActivity.USERNAME_KEY);
        Log.d(KEY, "THIS IS HERE");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = linkText.getText().toString();

                Intent intent = new Intent(mContext, VideoRecordingActivity.class);
                Bundle b = new Bundle();
                b.putString(USER_KEY, mUID);
                b.putString(USERNAME_KEY, mUserName);
                b.putString(LINKLOADER_KEY, link);
                intent.putExtras(b);
                startActivity(intent);

            }
        });




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
                intent.putExtra(KEY , mUID );
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
