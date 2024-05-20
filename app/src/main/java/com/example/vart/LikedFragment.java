package com.example.vart;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;


public class LikedFragment extends Fragment implements LikedAdapter.OnLikedArtClickListener {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    private LikedAdapter adapter;

    private ArrayList<Arts> likedArts;

    private String username;

    private TextView likedText;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_liked, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = rootview.findViewById(R.id.likedRecyclerView);

        likedArts = new ArrayList<>();

        likedText = rootview.findViewById(R.id.tvNoLikedArts);
        likedText.setVisibility(View.GONE);


        if (getArguments() != null) {
            username = getArguments().getString("username");
        }


        db.collection("liked").whereEqualTo("user", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extract the data for each art piece
                                String artistUsername = document.getString("artist");
                                String title = document.getString("title");
                                String imageurl = document.getString("imageUrl");
                                Arts art = new Arts(imageurl, title, artistUsername);
                                likedArts.add(art);
                            }
                        }
                        else
                        {
                            likedText.setVisibility(View.VISIBLE);
                        }
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                        adapter = new LikedAdapter(likedArts, this);
                        recyclerView.setAdapter(adapter);

                    } else
                    {

                    }
                });

        return rootview;
    }

    public void onLikedArtClick(Arts art)
    {
        Intent intent = new Intent(getContext(),Post.class);
        intent.putExtra("username", username);
        intent.putExtra("artistUsername", art.getUsername());
        intent.putExtra("title", art.getTitle());
        intent.putExtra("artUrl", art.getImage());
        startActivity(intent);
    }
}

class Arts
{
    String title;
    String image;

    String username;

    public Arts(String image, String text, String username) {
        this.image = image;
        this.title = text;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() { return username; }
}