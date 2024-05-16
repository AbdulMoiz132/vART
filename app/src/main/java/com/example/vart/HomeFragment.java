package com.example.vart;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    FirebaseFirestore db;

    CollectionReference artsRef;

    private RecyclerView recyclerView1, recyclerView2;

    private TrendingArtsAdapter trendingArtsAdapter;

    private TopArtistsAdapter topArtistAdapter;
    private ArrayList<TrendingArts> trendingArtsArrayList;

    private ArrayList<TopArtist> topArtistArrayList;

    StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView1 = rootview.findViewById(R.id.trendingArtsRecyclerView);

        recyclerView2 = rootview.findViewById(R.id.topArtistsRecyclerView);

        trendingArtsArrayList = new ArrayList<>();

        topArtistArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        db.collection("artwork").orderBy("likes", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract the data for each art piece
                            String imageurl = document.getString("imageurl");
                            String title = document.getString("title");
                            int likes = document.getLong("likes").intValue();
                            TrendingArts trendingArts = new TrendingArts(imageurl, title);
                            trendingArtsArrayList.add(trendingArts);
                        }
                        trendingArtsAdapter = new TrendingArtsAdapter(getContext(), trendingArtsArrayList);
                        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        recyclerView1.setAdapter(trendingArtsAdapter);
                    } else {
                        // Handle errors
                    }
                });

        // Fetch top artists based on followers
        db.collection("artist").orderBy("followers", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> usernames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            usernames.add(username);
                        }

                        // Fetch profiles of the top artists
                        db.collection("users").whereIn("username", usernames)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            String username = document.getString("username");
                                            String profileImageUrl = document.getString("profile");
                                            TopArtist topArtist = new TopArtist(profileImageUrl, username);
                                            topArtistArrayList.add(topArtist);
                                        }
                                        topArtistAdapter = new TopArtistsAdapter(getContext(), topArtistArrayList);
                                        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                        recyclerView2.setAdapter(topArtistAdapter);
                                    } else {
                                        // Handle errors
                                    }
                                });
                    } else {
                        // Handle errors
                    }
                });

        return rootview;
    }
}


class TrendingArts {
    private String image;
    private String title;

    public TrendingArts(String image, String text) {
        this.image = image;
        this.title = text;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}

class TopArtist {
    private String image;
    private String username;

    public TopArtist(String image, String username) {
        this.image = image;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public String getusername() {
        return username;
    }
}

class FollowedArtist {
    private String image;
    private String username;

    public FollowedArtist(String image, String username) {
        this.image = image;
        this.username = username;
    }
    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }
}



