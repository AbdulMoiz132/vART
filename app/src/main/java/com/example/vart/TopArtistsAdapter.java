package com.example.vart;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

public class TopArtistsAdapter extends RecyclerView.Adapter<TopArtistsAdapter.TopArtistsViewHolder> {
    private final ArrayList<TopArtist> topArtistsList; // Assuming each item has an image URL
    private final  OnArtistClickListener listener;


    public interface OnArtistClickListener {
        void onArtistClick(TopArtist artist);
    }

    public TopArtistsAdapter(ArrayList<TopArtist> topArtistsList, OnArtistClickListener listener) {
        this.topArtistsList = topArtistsList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public TopArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_layout, parent, false);
        return new TopArtistsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TopArtistsViewHolder holder, int position) {
        TopArtist topArtist = topArtistsList.get(position);
        holder.bind(topArtist, listener); // Assuming there's a title field
    }

    @Override
    public int getItemCount() {
        return topArtistsList.size();
    }

    public static class TopArtistsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;

        public TopArtistsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.user);
        }
        public void bind(TopArtist artist, OnArtistClickListener listener) {
            name.setText(artist.getusername());
            Picasso.get().load(artist.getImage()).placeholder(R.drawable.default_profile).into(imageView);
            itemView.setOnClickListener(v -> listener.onArtistClick(artist));
        }


    }
}

