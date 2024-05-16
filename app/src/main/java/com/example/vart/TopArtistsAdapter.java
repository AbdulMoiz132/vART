package com.example.vart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;

public class TopArtistsAdapter extends RecyclerView.Adapter<TopArtistsAdapter.TopArtistsViewHolder> {
    private final ArrayList<TopArtist> topArtistsList; // Assuming each item has an image URL
    private final Context context;

    public TopArtistsAdapter(Context context, ArrayList<TopArtist> topArtistsList) {
        this.context = context;
        this.topArtistsList = topArtistsList;
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
        Picasso.get().load(topArtist.getImage()).into(holder.imageView);
        holder.name.setText(topArtist.getusername()); // Assuming there's a title field
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
    }
}

