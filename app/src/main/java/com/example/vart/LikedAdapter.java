package com.example.vart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.LikedViewHolder> {
    private final ArrayList<LikedArts> likedArtsList;
    private final OnLikedArtClickListener listener;

    public interface OnLikedArtClickListener {
        void onLikedArtClick(LikedArts art);
    }

    public LikedAdapter(ArrayList<LikedArts> likedArtsList, OnLikedArtClickListener listener) {
        this.likedArtsList = likedArtsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_layout, parent, false);
        return new LikedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedViewHolder holder, int position) {
        LikedArts likedArt = likedArtsList.get(position);
        holder.bind(likedArt, listener);
    }

    @Override
    public int getItemCount() {
        return likedArtsList.size();
    }

    public static class LikedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;

        public LikedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivLiked);
            textViewTitle = itemView.findViewById(R.id.title);
        }

        public void bind(LikedArts likedArt, OnLikedArtClickListener listener) {
            Picasso.get().load(likedArt.getImage()).into(imageView);
            itemView.setOnClickListener(v -> listener.onLikedArtClick(likedArt));
        }
    }
}
