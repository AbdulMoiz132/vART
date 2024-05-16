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

// Remove com.squareup.picasso

public class FollowedArtistsAdapter extends RecyclerView.Adapter<FollowedArtistsAdapter.FollowedArtistsViewHolder> {
    private final ArrayList<FollowedArtist> followedArtistsList; // List of followed artists
    private final Context context;

    public FollowedArtistsAdapter(Context context, ArrayList<FollowedArtist> followedArtistsList) {
        this.context = context;
        this.followedArtistsList = followedArtistsList;
    }

    @NonNull
    @Override
    public FollowedArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.art_layout, parent, false);
        return new FollowedArtistsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FollowedArtistsViewHolder holder, int position) {
        FollowedArtist followedArtist = followedArtistsList.get(position);
        Picasso.get().load(followedArtist.getImage()).into(holder.imageView);

        holder.name.setText(followedArtist.getUsername()); // Assuming a method to get artist name
    }

    @Override
    public int getItemCount() {
        return followedArtistsList.size();
    }

    public static class FollowedArtistsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;

        public FollowedArtistsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.user);
        }
    }
}
