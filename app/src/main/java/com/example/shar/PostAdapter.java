package com.example.shar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //        private ArrayList<Post> mPosts;
//        private ArrayList<String> mKeys;
    private ArrayList<Post> postObjects;
    private RequestManager requestManager;



    public PostAdapter(ArrayList<Post> post_objects, RequestManager requestManager) {
        this.postObjects = post_objects;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PostItemView(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((PostItemView)viewHolder).setAdapterReference(this);
        ((PostItemView)viewHolder).onBind(postObjects.get(i), requestManager);
    }

    @Override
    public int getItemCount() {
        return postObjects.size();
    }

    public void remove(int position){
        postObjects.remove(position);
    }



}