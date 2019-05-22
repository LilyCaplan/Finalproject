package com.example.shar;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;


import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;


/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class VideoRecordingActivity extends AppCompatActivity
        implements ModelLoader.ModelLoaderCallbacks {
    private static final String TAG = VideoRecordingActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private WritingArFragment arFragment;
    private ModelRenderable mRenderable;
    // Model loader class to avoid leaking the activity context.
    private ModelLoader modelLoader;

    // VideoRecorder encapsulates all the video recording functionality.
    private VideoRecorder videoRecorder;

    // The UI to record.
    private FloatingActionButton recordButton;

    private String mUID;


    private Uri mUri;

    private String mUserName;

    public static final String KEY = "KEY";
    public static final String USER_KEY = "USER_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }





        //StorageReference imagesRef = mStorageRef.child("images");
        Intent intent = getIntent();

        mUID = intent.getExtras().getString(USER_KEY);
        mUserName = intent.getExtras().getString(USERNAME_KEY);
        String link = intent.getExtras().getString(LinkLoader.LINKLOADER_KEY);

        if( link.isEmpty()){
            mUri = Uri.parse("https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf");

        } else {
            mUri = Uri.parse(link);
        }



        setContentView(R.layout.activity_main);
        arFragment = (WritingArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);



        modelLoader = new ModelLoader(this);
        modelLoader.loadModel(this, mUri);


        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (mRenderable == null) {
                        return;
                    }


                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(mRenderable);
                    andy.select();
                });

        // Initialize the VideoRecorder.
        videoRecorder = new VideoRecorder();

        //videoRecorder.setStorageRef(mStorage);
        videoRecorder.setUserID(mUID);
        videoRecorder.setUserName(mUserName);

        int orientation = getResources().getConfiguration().orientation;
        videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_2160P, orientation);
        videoRecorder.setSceneView(arFragment.getArSceneView());

        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(this::toggleRecording);
        recordButton.setEnabled(true);
        //recordButton.setImageResource(R.drawable.round_videocam);
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
                extras.putString(USER_KEY , mUID);
                extras.putString(USERNAME_KEY , mUserName);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.Camera:
                intent = new Intent(this, LinkLoader.class);
                extras = new Bundle();
                extras.putString(USER_KEY, mUID);
                extras.putString( USERNAME_KEY , mUserName);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.Profile:
                intent = new Intent(this, PlayVideo.class);
                extras = new Bundle();
                extras.putString(USER_KEY, mUID);
                extras.putString( USERNAME_KEY , mUserName);
                intent.putExtras(extras);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (videoRecorder.isRecording()) {
            toggleRecording(null);
        }
        super.onPause();
    }

    /*
     * Used as a handler for onClick, so the signature must match onClickListener.
     */
    private void toggleRecording(View unusedView) {
        if (!arFragment.hasWritePermission()) {
            Log.e(TAG, "Video recording requires the WRITE_EXTERNAL_STORAGE permission");
            Toast.makeText(
                    this,
                    "Video recording requires the WRITE_EXTERNAL_STORAGE permission",
                    Toast.LENGTH_LONG)
                    .show();
            arFragment.launchPermissionSettings();
            return;
        }
        boolean recording = videoRecorder.onToggleRecord();
        if (recording) {
            recordButton.setImageResource(R.drawable.round_stop);
        } else {
            //recordButton.setImageResource(R.drawable.round_videocam);
            String videoPath = videoRecorder.getVideoPath().getAbsolutePath();
            Toast.makeText(this, "Video saved: " + videoPath, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video saved: " + videoPath);

            // Send  notification of updated content.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, "Sceneform Video");
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, videoPath);
            getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            //Post post = new Post();


            Intent intent = new Intent(this, PlayVideo.class);
            Bundle extras = new Bundle();
            extras.putString("USER_KEY" , mUID);
            extras.putString( "USERNAME_KEY" , mUserName);
            intent.putExtras(extras);
            startActivity(intent);



        }
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    public void setRenderable(ModelRenderable modelRenderable) {
        mRenderable = modelRenderable;
    }

    @Override
    public void onLoadException(Throwable throwable) {
        Toast toast = Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Log.e(TAG, "Unable to load andy renderable", throwable);
    }
}

