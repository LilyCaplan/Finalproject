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
    private ArrayList<Post> posts = new ArrayList<>();
    private String mUID;



    public interface DataStatus{
        boolean DataIsLoaded(ArrayList<Post> posts , ArrayList<String> keys);
        void DataIsInserted();
        void DataIsUploaded();
        void DataIsDeleted();
    }



    public DataBaseHelper() {

    }

    public DataBaseHelper(String uID){
        mUID = uID;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUserDatabase = mDatabase.getReference(mUID);
        mReferenceVideoDatatbase = mReferenceUserDatabase.child("videos");

    }

    public void readPosts(final DataStatus dataStatus){
        mReferenceVideoDatatbase.addValueEventListener(new ValueEventListener() {
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
