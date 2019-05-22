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
        void DataFound(String userName);
        void DataIsInserted();
        void DataIsUploaded();
        void DataIsDeleted();
    }



    public DataBaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();

    }

    /**
    * inputs: userid
    * Creates specific post database reference for specific user
    * */

    public DataBaseHelper(String uID){
        mUID = uID;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUserDatabase = mDatabase.getReference(mUID);
        mReferenceVideoDatatbase = mReferenceUserDatabase.child("posts");

    }

    /**
     *
     * inputs: username, email, datastatuc
     * looks up specific username if it's been used
     * @retuns username if it's been taken by someone via DataStatus
     *
     * */

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

    /**
     * inputs: DataStatus
     * returns posts for public feed
     * @returns array of posts via datastatus
     *
     *
     * */
    public void readAllPosts(final DataStatus dataStatus){
        DatabaseReference referenceAllPosts = mDatabase.getReference("allposts");
        referenceAllPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    String url = keyNode.child("mVideo").getValue(String.class);
                    String username = keyNode.child("mText").getValue(String.class);
                    String uid = keyNode.child("mUID").getValue(String.class);
                    String thumbnail = keyNode.child("mThumbnail").getValue(String.class);
                    Post post = new Post(url, username, uid, thumbnail);
                    posts.add(post);
                }
                dataStatus.DataIsLoaded(posts, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * inputs: DataStatus
     * returns posts for profile
     * @returns array of posts via datastatus
     *
     *
     * */
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
                    String uid = keyNode.child("mUID").getValue(String.class);
                    String thumbnail = keyNode.child("mThumbnail").getValue(String.class);
                    Post post = new Post(url, username, uid, thumbnail);
                    posts.add(post);
                }
                dataStatus.DataIsLoaded(posts, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * inputs: uid, thumbnail, datastatus
     *
     * removes posts that user would like to delete
     *
     *
     * */

    public void removePosts(String uid, String thumbnailRef ,final DataStatus dataStatus){
        DatabaseReference userRef = mDatabase.getReference(uid).child("posts");
        DatabaseReference allpostRef = mDatabase.getReference("allposts");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("mThumbnail").getValue(String.class).equals(thumbnailRef)){
                        keyNode.getRef().removeValue();

                    }
                }
                dataStatus.DataIsDeleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        allpostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("mThumbnail").getValue(String.class).equals(thumbnailRef)){
                        keyNode.getRef().removeValue();

                    }
                }
                dataStatus.DataIsDeleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * inputs: uid,  datastatus
     *
     * finds user from clicking on their name in feed
     *
     *
     * */
    public void getUserNamefromUID(String uid, final DataStatus dataStatus){
        DatabaseReference userNameRef = mDatabase.getReference("username");

        userNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    String tempUser = keyNode.child("mUID").getValue(String.class);
                    if(tempUser.equals(uid)){
                        String username = keyNode.child("mUsername").getValue(String.class);
                        dataStatus.DataFound(username);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * inputs: post, uid
     *
     * send posts to database
     *
     * */

    public void sendVideoandThumbnail(Post post, String uid){
        this.mUID = uid;
        DatabaseReference mDatabaseRef = mDatabase.getReference();
        mReferenceUserDatabase = mDatabase.getReference(mUID);

        mReferenceUserDatabase.child("posts").push().setValue(post);
        mDatabaseRef.child("allposts").push().setValue(post);

    }


}
