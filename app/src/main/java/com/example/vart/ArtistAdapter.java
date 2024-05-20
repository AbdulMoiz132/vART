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
import java.util.List;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<TopArtist> artists = new ArrayList<>();

    public void setArtists(List<TopArtist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.followed, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        TopArtist artist = artists.get(position);
        // Bind artist data to the view
        holder.bind(artist);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private ImageView profileImageView;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.user);
            profileImageView = itemView.findViewById(R.id.profile);
        }

        public void bind(TopArtist artist) {
            // Bind artist data to views
            usernameTextView.setText(artist.getusername());
            // Load artist profile image using Glide or Picasso
            Picasso.get().load(artist.getImage()).placeholder(R.drawable.default_profile).into(profileImageView);
        }
    }
}
