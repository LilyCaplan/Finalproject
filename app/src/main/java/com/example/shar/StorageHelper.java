package com.example.shar;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class StorageHelper {

    private DatabaseReference mDatabase;
    private StorageReference  mStorageRef;
    private FirebaseStorage mStorage;
    private File mVideoPath;
    private String mUID;
    private String mVideoFileName;
    private Uri mVideoUri;
    private String mThumbnailFileName;
    private StorageReference mThumbnailStorageRef;
    private Uri mThumbnailURI;
    private String mUserName;

    public String TAG = "StorageHelper";

    StorageHelper(File videoPath, String uid, String username, String videofileName, String thumbnailFileName){

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        this.mVideoPath = videoPath;
        this.mUID = uid;
        this.mVideoFileName = videofileName;
        this.mThumbnailFileName = thumbnailFileName;
        this.mUserName = username;

    }

    StorageHelper(String uid,  String videofileName, String thumbnailFileName){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        this.mUID = uid;
        this.mVideoFileName = videofileName;
        this.mThumbnailFileName = thumbnailFileName;

    }

    private void sendVideoandThumbnailToCloud(){
        Uri file = Uri.fromFile(mVideoPath);

        StorageReference riversRef = mStorageRef.child(mUID + "/videos/" + mVideoFileName);
        UploadTask uploadTask = riversRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    mVideoUri = task.getResult();
                    sendThumbnailtoCloud();

                } else {
                    Log.d(TAG,"Shit's gone down");
                }
            }
        });

    }

    private void sendThumbnailtoCloud(){

        //String thumbnailFileName = videoBaseName + mUniqueTime + ".png";

        mThumbnailStorageRef = mStorageRef.child(mUID + "/thumbnail/" + mThumbnailFileName);

        Bitmap bmp = ThumbnailUtils.createVideoThumbnail(mVideoPath.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadThumnailTask = mThumbnailStorageRef.putBytes(data);

        uploadThumnailTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "THIS IS A FUCK UP ");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mThumbnailURI = taskSnapshot.getUploadSessionUri();
                Post post = new Post(mVideoUri.toString(), mUserName, mUID, mThumbnailURI.toString());
                DataBaseHelper dbh = new DataBaseHelper();
                dbh.sendVideoandThumbnail(post, mUID);
            }
        });

    }

    public void send(){
        sendVideoandThumbnailToCloud();
    }

    public void deleteFromStorage(){
        StorageReference videoRef = mStorageRef.child(mUID + "/videos/" + mVideoFileName);
        mThumbnailStorageRef = mStorageRef.child(mUID + "/thumbnail/" + mThumbnailFileName);

        videoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Failure To Delete");
            }
        });

        mThumbnailStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Failure To Delete");
            }
        });



    }

}
