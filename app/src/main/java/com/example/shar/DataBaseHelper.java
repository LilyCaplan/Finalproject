package com.example.shar;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUserDatabase;
    private DatabaseReference mReferenceVideoDatatbase;
    private ArrayList<Post> posts = new ArrayList<>();
    private String mUID;
    private String mUserName;



    public interface DataStatus{
        void DataIsLoaded(ArrayList<Post> posts , ArrayList<String> keys);
        void DataIsLoaded(String userName);
        void DataIsInserted();
        void DataIsUploaded();
        void DataIsDeleted();
    }



    public DataBaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();

    }

    public DataBaseHelper(String uID){
        mUID = uID;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUserDatabase = mDatabase.getReference(mUID);
        mReferenceVideoDatatbase = mReferenceUserDatabase.child("posts");

    }

    public void findUserNames(String username , String email,  final DataStatus dataStatus){
        mUserName = "";
        DatabaseReference referenceUserNames = mDatabase.getReference("username");
        referenceUserNames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(username.equals(keyNode.child("mUsername").getValue(String.class)) && !(email.equals(keyNode.child("mEmail").getValue(String.class))) ){
                        mUserName = username;
                    }
                }
                dataStatus.DataIsLoaded(mUserName);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void readPosts(final DataStatus dataStatus){
        mReferenceVideoDatatbase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    String url = keyNode.child("mVideo").getValue(String.class);
                    String username = keyNode.child("mText").getValue(String.class);
                    Post post = new Post(url, username);
                    posts.add(post);
                }
                dataStatus.DataIsLoaded(posts, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
