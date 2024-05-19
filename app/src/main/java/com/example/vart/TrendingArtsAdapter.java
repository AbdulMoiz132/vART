package com.example.vart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class TrendingArtsAdapter extends RecyclerView.Adapter<TrendingArtsAdapter.TrendingArtsViewHolder> {
    private final ArrayList<TrendingArts> trendingArtsList; // Assuming each item is a URL to an image
    private OnArtClickListener listener;


    public interface OnArtClickListener {
        void onArtClick(TrendingArts art);
    }

    public TrendingArtsAdapter(ArrayList<TrendingArts> trendingArtsList, OnArtClickListener listener )
    {
        this.trendingArtsList = trendingArtsList;
        this.listener = listener;
        ;
    }

    @NonNull
    @Override
    public TrendingArtsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.art_layout, parent, false);
        return new TrendingArtsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TrendingArtsViewHolder holder, int position) {
        TrendingArts trendingart = trendingArtsList.get(position);
        holder.bind(trendingart, listener);
    }

    @Override
    public int getItemCount() {
        return trendingArtsList.size();
    }

    public static class TrendingArtsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;

        public TrendingArtsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivLiked);
            textViewTitle = itemView.findViewById(R.id.title);
        }

        public void bind(TrendingArts trendingArt, OnArtClickListener listener) {
            textViewTitle.setText(trendingArt.getTitle());
            Glide.with(itemView.getContext()).load(trendingArt.getImage()).into(imageView);
            Picasso.get().load(trendingArt.getImage()).into(imageView);
            itemView.setOnClickListener(v -> listener.onArtClick(trendingArt));
        }

    }
}
