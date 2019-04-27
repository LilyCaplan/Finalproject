package com.example.shar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LinkLoader extends AppCompatActivity {

    private Button addButton;

    private EditText linkText;

    private String mUID;

    private Context mContext;

    public String LINKLOADER_KEY = "Link Loader CLass";

    public static final String KEY = "KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkloader);

        linkText = findViewById(R.id.editText);
        addButton = findViewById(R.id.button);

        mContext = this;



        Intent intent = getIntent();


        mUID = intent.getStringExtra(SignInActivity.USER_KEY);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = linkText.getText().toString();

                Intent intent = new Intent(mContext, VideoRecordingActivity.class);
                Bundle b = new Bundle();
                b.putString("user", mUID);
                b.putString("link" , link);
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
                intent.putExtra(KEY , mUID );
                startActivity(intent);
                break;
            case R.id.Camera:
                intent = new Intent(this, LinkLoader.class);
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
