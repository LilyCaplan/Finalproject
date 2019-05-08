package com.example.shar;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.SceneView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Video Recorder class handles recording the contents of a SceneView. It uses MediaRecorder to
 * encode the video. The quality settings can be set explicitly or simply use the CamcorderProfile
 * class to select a predefined set of parameters.
 */
public class VideoRecorder {
    private static final String TAG = "VideoRecorder";
    private static final int DEFAULT_BITRATE = 10000000;
    private static final int DEFAULT_FRAMERATE = 30;

    // recordingVideoFlag is true when the media recorder is capturing video.
    private boolean recordingVideoFlag;

    private MediaRecorder mediaRecorder;

    private Size videoSize;

    private SceneView sceneView;
    private int videoCodec;
    private File videoDirectory;
    private String videoBaseName;
    private File videoPath;
    private int bitRate = DEFAULT_BITRATE;
    private int frameRate = DEFAULT_FRAMERATE;
    private Surface encoderSurface;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private  StorageReference mVideoRef;
    private String mFileName;
    private String mUID;
    private URL mURL;
    private String mUserName;
    private DatabaseReference mDatabase;
    private StorageReference mThumbnailStorageRef;
    private Uri mThumbnailURI;
    private Uri mVideoUri;
    private String mThumbnailHasLoaded;
    private String mUniqueTime;

    private static final int[] FALLBACK_QUALITY_LEVELS = {
            CamcorderProfile.QUALITY_HIGH,
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
    };

    public VideoRecorder() {
        recordingVideoFlag = false;
    }

    public File getVideoPath() {
        return videoPath;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public void setSceneView(SceneView sceneView) {
        this.sceneView = sceneView;
    }

    public  void setUserID(String uID) { mUID = uID; }

    public  void setStorageRef( FirebaseStorage storage ) { mStorageRef= storage.getReference();};

    /**
     * Toggles the state of video recording.
     *
     * @return true if recording is now active.
     */
    public boolean onToggleRecord() {
        if (recordingVideoFlag) {
            stopRecordingVideo();
        } else {
            startRecordingVideo();
        }
        return recordingVideoFlag;
    }

    private void startRecordingVideo() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        try {
            buildFilename();
            setUpMediaRecorder();
            setUpFireBaseStorage();


        } catch (IOException e) {
            Log.e(TAG, "Exception setting up recorder", e);
            return;
        }

        // Set up Surface for the MediaRecorder
        encoderSurface = mediaRecorder.getSurface();

        sceneView.startMirroringToSurface(
                encoderSurface, 0, 0, videoSize.getWidth(), videoSize.getHeight());

        recordingVideoFlag = true;
    }

    private void setUpFireBaseStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

    }

    private void buildFilename() {

        if (videoDirectory == null) {
            videoDirectory =
                    new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                    + "/Sceneform");
        }
        if (videoBaseName == null || videoBaseName.isEmpty()) {
            videoBaseName = "Sample";
        }
        videoPath =
                new File(
                        videoDirectory, videoBaseName + Long.toHexString(System.currentTimeMillis()) + ".mp4");
        File dir = videoPath.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }



    }




    private void sendtoCloud(){

        mThumbnailHasLoaded = "not loaded";

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mUniqueTime = Long.toHexString(System.currentTimeMillis());

        mFileName = videoBaseName + mUniqueTime + ".mp4";

        Uri file = Uri.fromFile(videoPath);

        StorageReference riversRef = mStorageRef.child(mUID + "/videos/" + mFileName);
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

        String thumbnailFileName = videoBaseName + mUniqueTime + ".png";

        mThumbnailStorageRef = mStorageRef.child(mUID + "/thumbnail/" + thumbnailFileName);

        Bitmap bmp = ThumbnailUtils.createVideoThumbnail(videoPath.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);

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
                mDatabase.child(mUID).child("posts").push().setValue(post);
                mDatabase.child("allposts").push().setValue(post);
            }
        });

    }

    public void setUserName(String username){
        mUserName = username;
    }

    private void stopRecordingVideo() {
        // UI
        recordingVideoFlag = false;

        if (encoderSurface != null) {
            sceneView.stopMirroringToSurface(encoderSurface);
            encoderSurface = null;
        }
        // Stop recording
        mediaRecorder.stop();
        mediaRecorder.reset();
        sendtoCloud();
    }

    private void setUpMediaRecorder() throws IOException {

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);


        mediaRecorder.setOutputFile(videoPath.getAbsolutePath());
        mediaRecorder.setVideoEncodingBitRate(bitRate);
        mediaRecorder.setVideoFrameRate(frameRate);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(videoCodec);

        mediaRecorder.prepare();

        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Exception starting capture: " + e.getMessage(), e);
        }
    }

    public void setVideoSize(int width, int height) {
        videoSize = new Size(width, height);
    }

    public void setVideoQuality(int quality, int orientation) {
        CamcorderProfile profile = null;
        if (CamcorderProfile.hasProfile(quality)) {
            profile = CamcorderProfile.get(quality);
        }
        if (profile == null) {
            // Select a quality  that is available on this device.
            for (int level : FALLBACK_QUALITY_LEVELS) {
                if (CamcorderProfile.hasProfile(level)) {
                    profile = CamcorderProfile.get(level);
                    break;
                }
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        } else {
            setVideoSize(profile.videoFrameHeight, profile.videoFrameWidth);
        }
        setVideoCodec(profile.videoCodec);
        setBitRate(profile.videoBitRate);
        setFrameRate(profile.videoFrameRate);
    }

    public void setVideoCodec(int videoCodec) {
        this.videoCodec = videoCodec;
    }

    public boolean isRecording() {
        return recordingVideoFlag;
    }
}
