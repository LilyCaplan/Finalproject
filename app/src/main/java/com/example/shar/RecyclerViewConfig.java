package com.example.shar;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.List;

public class RecyclerViewConfig {

    private Context mContext;
    //private PostAdapter mPostAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Post> posts, List<String> keys){

        mContext = context;
        //mPostAdapter = new PostAdapter(posts,keys );
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.setAdapter(mPostAdapter);
    }

//    class PostItemView extends RecyclerView.ViewHolder{
//        private VideoView mVideoView;
//
//        private String key;
//
//        public PostItemView(ViewGroup parent) {
//            super(LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false));
//
//            mVideoView = itemView.findViewById(R.id.vView);
//
//
//
//        }
//
//        public void bind(Post post , String key){
//
//            Uri uri = Uri.parse(post.getmVideo()); //Declare your url here.
//
//            mVideoView.setMediaController(new MediaController(mContext));
//            mVideoView.setVideoURI(uri);
//            mVideoView.requestFocus();
//            mVideoView.start();
//
//            this.key = key;
//
//        }
//
//    }
//
//    class PostAdapter extends RecyclerView.Adapter<PostItemView> {
//        private List<Post> mPosts;
//        private List<String> mKeys;
//
//
//
//        public PostAdapter(List<Post> mPostList, List<String> mKeys){
//            this.mPosts = mPostList;
//            this.mKeys = mKeys;
//        }
//
//        public PostItemView onCreateViewHolder(ViewGroup parent, int viewType){
//            return new PostItemView(parent);
//        }
//
//        public void onBindViewHolder(PostItemView holder, int position){
//            holder.bind(mPosts.get(position), mKeys.get(position));
//
//        }
//
//        public int getItemCount(){
//            return mPosts.size();
//        }
//    }


}
