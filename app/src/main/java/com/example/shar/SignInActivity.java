package com.example.shar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mUserName;
    private EditText mPasswordField;
    private Context mContext;
    private DatabaseReference mDatabase;

    public static final String USER_KEY = "USER_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";
    private String mUID;
    private String mUsernameString;
    private boolean mAlreadyUsedUserName;
    private boolean mHasFinishedChecking;
    private FirebaseAuth mAuth;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        mContext = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mHasFinishedChecking = false;

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mUserName = findViewById(R.id.fieldUserName);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }


    private void createAccount(String email, String username,  String password) {


        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mUID = user.getUid();
                            User usernameObj = new User(mUID, username, email);

                            mDatabase.child("username").push().setValue(usernameObj);

                            mUsernameString = username;




                            Intent intent = new Intent(mContext, LinkLoader.class);
                            Bundle b = new Bundle();
                            b.putString(USER_KEY , user.getUid());
                            b.putString(USERNAME_KEY ,mUsernameString);
                            intent.putExtras(b);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void signIn(String email, String username, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mUsernameString = username;

                            Intent intent = new Intent(mContext, LinkLoader.class);
                            Bundle b = new Bundle();
                            b.putString(USER_KEY , user.getUid());
                            b.putString(USERNAME_KEY , username);
                            intent.putExtras(b);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();



                        }

                    }
                });
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String username = mUserName.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUserName.setError("Required.");
            valid = false;
        } else {

            DataBaseHelper dbh = new DataBaseHelper();
            dbh.findUserNames(username, email, new DataBaseHelper.DataStatus() {
                @Override
                public void  DataIsLoaded(ArrayList<Post> posts, ArrayList<String> keys) {


                }
                @Override
                public  void  DataIsLoaded(String username){
                    if(username.isEmpty()){
                        mAlreadyUsedUserName = false;
                        mUserName.setError(null);
                    } else {
                        mAlreadyUsedUserName = true;
                        mUserName.setError("Already Used");

                    }
                    mHasFinishedChecking = true;

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
            //mUserName.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        if(mAlreadyUsedUserName){
            return false;
        }

        if(mHasFinishedChecking){
            return valid;
        } else {
            return false;
        }



    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mUserName.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(),mUserName.getText().toString(), mPasswordField.getText().toString());
        }
    }
}

