package com.example.shar;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUserDatabase;
    private DatabaseReference mReferenceVideoDatatbase;
    private DatabaseReference mReferenceAllPostsDatabase;
    private ArrayList<Post> posts = new ArrayList<>();
    private String mUID;
    private boolean mPoolUserData;



    public interface DataStatus{
        boolean DataIsLoaded(ArrayList<Post> posts , ArrayList<String> keys);
        void DataIsInserted();
        void DataIsUploaded();
        void DataIsDeleted();
    }




    public DataBaseHelper(){
        //mUID = uID;
        mDatabase = FirebaseDatabase.getInstance();


    }


    public void setReferenceTarget(String publicPosts) {
        mReferenceAllPostsDatabase = mDatabase.getReference(publicPosts);
        mPoolUserData = false;

    }


    public void setReferenceTarget(String uid, String videos) {

        mReferenceUserDatabase = mDatabase.getReference(mUID);
        mReferenceVideoDatatbase = mReferenceUserDatabase.child("videos");
        mPoolUserData = true;


    }



    public void readPosts(final DataStatus dataStatus){
        DatabaseReference database_ref;

        if(mPoolUserData){
            database_ref = mReferenceVideoDatatbase;
        } else {
            database_ref = mReferenceAllPostsDatabase;
        }




        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    String url = keyNode.getValue(String.class);
                    Post post = new Post(url);
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
