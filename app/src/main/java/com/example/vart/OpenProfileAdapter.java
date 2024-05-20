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

public class OpenProfileAdapter extends RecyclerView.Adapter<OpenProfileAdapter.ProfileViewHolder> {
    private final ArrayList<Arts> artList;
    private final OnArtClickListener listener;

    public interface OnArtClickListener {
        void onArtClick(Arts art);
    }

    public OpenProfileAdapter(ArrayList<Arts> artList, OnArtClickListener listener) {
        this.artList = artList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.art_feed_layout, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Arts art = artList.get(position);
        holder.bind(art, listener);
    }

    @Override
    public int getItemCount() {
        return artList.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivArt);
        }

        public void bind(Arts art, OnArtClickListener listener) {
            Picasso.get().load(art.getImage()).into(imageView);
            itemView.setOnClickListener(v -> listener.onArtClick(art));
        }
    }
}

