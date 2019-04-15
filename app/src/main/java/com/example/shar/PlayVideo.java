package com.example.shar;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class PlayVideo extends AppCompatActivity {

    private String mUID;
    private DatabaseReference mDatabase;

    private Context mContext;
    public String TAG = "com.example.shar.PlayVideo";
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mVideoRecyclerView;
    private List<Post> mPosts;
    private List<String> mKeys;
    private VideoView tempVV;
    private VideoView tempVV1;
    private VideoView tempVV2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


        mContext = this;

        mVideoRecyclerView = findViewById(R.id.videoRecyclerView);
        //mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mVideoRecyclerView.setAdapter(new RecyclerViewConfig.PostAdapter());

        Intent intent = getIntent();
        String message = intent.getStringExtra(VideoRecordingActivity.KEY);
        mUID = message;

        DataBaseHelper dbh = new DataBaseHelper(mUID);

        mPosts = new List<Post>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<Post> iterator() {
                return null;
            }

            @Nullable
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(@Nullable T[] a) {
                return null;
            }

            @Override
            public boolean add(Post post) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends Post> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends Post> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Post get(int index) {
                return null;
            }

            @Override
            public Post set(int index, Post element) {
                return null;
            }

            @Override
            public void add(int index, Post element) {

            }

            @Override
            public Post remove(int index) {
                return null;
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<Post> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<Post> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<Post> subList(int fromIndex, int toIndex) {
                return null;
            }
        };

        mKeys = new List<String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Nullable
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(@Nullable T[] a) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public String get(int index) {
                return null;
            }

            @Override
            public String set(int index, String element) {
                return null;
            }

            @Override
            public void add(int index, String element) {

            }

            @Override
            public String remove(int index) {
                return null;
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<String> subList(int fromIndex, int toIndex) {
                return null;
            }
        };

        PostAdapter mPostAdapter = new PostAdapter(mPosts,mKeys );
        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mVideoRecyclerView.setAdapter(mPostAdapter);

       dbh.readPosts(new DataBaseHelper.DataStatus() {
            @Override
            public boolean DataIsLoaded(List<Post> posts, List<String> keys) {
                mPosts = posts;
                mKeys = keys;
                if(posts != null){
                    mPostAdapter.update( mPosts,mKeys);
                }


                return  true;

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
    }

    class PostItemView extends RecyclerView.ViewHolder{
        private VideoView mVideoView;

        private String key;

        public PostItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false));

            mVideoView = itemView.findViewById(R.id.vView);



        }

        public void bind(Post post , String key){

            Uri uri = Uri.parse(post.getmVideo()); //Declare your url here.

            mVideoView.setMediaController(new MediaController(mContext));
            mVideoView.setVideoURI(uri);
            mVideoView.requestFocus();
            mVideoView.start();

            this.key = key;

        }

    }

    class PostAdapter extends RecyclerView.Adapter<PostItemView> {
        private List<Post> mPosts;
        private List<String> mKeys;



        public PostAdapter(List<Post> mPostList, List<String> mKeys){
            this.mPosts = mPostList;
            this.mKeys = mKeys;
        }

        public PostItemView onCreateViewHolder(ViewGroup parent, int viewType){
            return new PostItemView(parent);
        }

        public void onBindViewHolder(PostItemView holder, int position){
            holder.bind(mPosts.get(position), mKeys.get(position));

        }

        public int getItemCount(){
            if (mPosts == null) {
                return 0;
            }
            return mPosts.size();
        }

        public void update(List<Post> posts, List<String> keys ){
            if (!(posts.isEmpty()) && !(keys.isEmpty())){
                for(int i =0; i<posts.size(); i++){
                    if(posts.get(i) != null && keys.get(i) != null){
                        mPosts.add(posts.get(i));
                        mKeys.add(keys.get(i));

                    }

                }


            }

        }
    }

}
