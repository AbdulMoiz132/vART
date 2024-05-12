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

import java.util.ArrayList;

public class TrendingArtsAdapter extends RecyclerView.Adapter<TrendingArtsAdapter.TrendingArtsViewHolder> {
    private final ArrayList<TrendingArts> trendingArtsList; // Assuming each item is a URL to an image
    private final Context context;

    public TrendingArtsAdapter(Context context, ArrayList<TrendingArts> trendingArtsList) {
        this.context = context;
        this.trendingArtsList = trendingArtsList;
    }

    @NonNull
    @Override
    public TrendingArtsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.art_layout, parent, false);
        return new TrendingArtsViewHolder(view);
    }


    public void onBindViewHolder(@NonNull TrendingArtsViewHolder holder, int position) {
        TrendingArts trendingArts = trendingArtsList.get(position);
        Glide.with(context).load(trendingArts.getImage()).into(holder.imageView);
        holder.textViewTitle.setText(trendingArts.getTitle());
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
            imageView = itemView.findViewById(R.id.artview);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }
}
