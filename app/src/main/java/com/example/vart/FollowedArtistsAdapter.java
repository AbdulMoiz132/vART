package com.example.vart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowedArtistsAdapter extends RecyclerView.Adapter<FollowedArtistsAdapter.FollowedArtistsViewHolder> {
    private final ArrayList<FollowedArtist> followedArtistsList; // Assuming each item is a URL to an image
    private OnFollowedClickListener listener;

    public interface OnFollowedClickListener {
        void onFollowedClick(FollowedArtist followedArtist);
    }

    public FollowedArtistsAdapter(ArrayList<FollowedArtist> followedArtistsList, OnFollowedClickListener listener) {
        this.followedArtistsList = followedArtistsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FollowedArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followed, parent, false);
        return new FollowedArtistsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowedArtistsViewHolder holder, int position) {
        FollowedArtist followedArtist = followedArtistsList.get(position);
        holder.bind(followedArtist, listener);
    }

    @Override
    public int getItemCount() {
        return followedArtistsList.size();
    }

    public static class FollowedArtistsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;

        public FollowedArtistsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile);
            textViewName = itemView.findViewById(R.id.user);
        }

        public void bind(FollowedArtist followedArtist, OnFollowedClickListener listener) {
            textViewName.setText(followedArtist.getUsername());
            Picasso.get().load(followedArtist.getImage()).placeholder(R.drawable.default_profile).into(imageView);
            itemView.setOnClickListener(v -> listener.onFollowedClick(followedArtist));
        }
    }
}
