package com.example.vart;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements TrendingArtsAdapter.OnArtClickListener, TopArtistsAdapter.OnArtistClickListener, FollowedArtistsAdapter.OnFollowedClickListener {

    FirebaseFirestore db;

    CollectionReference artsRef;

    private RecyclerView recyclerView1, recyclerView2, recyclerView3;

    private TrendingArtsAdapter trendingArtsAdapter;

    private TopArtistsAdapter topArtistAdapter;

    private FollowedArtistsAdapter followedArtistAdapter;

    private ArrayList<TrendingArts> trendingArtsArrayList;

    private ArrayList<TopArtist> topArtistArrayList;

    private ArrayList<FollowedArtist> followedArrayList;

    StorageReference storageRef;

    String username;

    TextView NoArtists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView1 = rootview.findViewById(R.id.trendingArtsRecyclerView);

        recyclerView2 = rootview.findViewById(R.id.topArtistsRecyclerView);

        recyclerView3 = rootview.findViewById(R.id.followedArtistsRecyclerView);

        trendingArtsArrayList = new ArrayList<>();

        topArtistArrayList = new ArrayList<>();

        followedArrayList = new ArrayList<>();

        NoArtists = rootview.findViewById(R.id.tvNoArtists);
        NoArtists.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        db.collection("artwork").orderBy("likes", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract the data for each art piece
                            String imageurl = document.getString("imageUrl");
                            String title = document.getString("title");
                            String username = document.getString("username");
                            int likes = document.getLong("likes").intValue();
                            TrendingArts trendingArts = new TrendingArts(imageurl, title, username);
                            trendingArtsArrayList.add(trendingArts);
                        }
                        trendingArtsAdapter = new TrendingArtsAdapter(trendingArtsArrayList, this);
                        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        recyclerView1.setAdapter(trendingArtsAdapter);
                    } else
                    {
                        // Handle errors
                    }
                });

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

                        if (!usernames.isEmpty()) {
                            // Fetch profiles of the top artists in a single query
                            db.collection("users").whereIn("username", usernames)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Create a map to store user profiles by username
                                            Map<String, TopArtist> userProfileMap = new HashMap<>();
                                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                                String username = document.getString("username");
                                                String profileImageUrl = document.getString("profile");
                                                TopArtist topArtist = new TopArtist(profileImageUrl, username);
                                                userProfileMap.put(username, topArtist);
                                            }

                                            // Clear the list before adding new data
                                            topArtistArrayList.clear();

                                            // Add the user profiles to the list in the order of the usernames
                                            for (String username : usernames) {
                                                TopArtist topArtist = userProfileMap.get(username);
                                                if (topArtist != null) {
                                                    topArtistArrayList.add(topArtist);
                                                }
                                            }

                                            // Ensure this runs on the main thread

                                                topArtistAdapter = new TopArtistsAdapter(topArtistArrayList, this);
                                                recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                                recyclerView2.setAdapter(topArtistAdapter);
                                            
                                        } else {
                                            // Handle errors for the users query
                                            Log.e("Firestore", "Error getting user documents: ", task1.getException());
                                        }
                                    });
                        }
                    } else {
                        // Handle errors for the artist query
                        Log.e("Firestore", "Error getting artist documents: ", task.getException());
                    }
                });


        db.collection("follower").whereEqualTo("user", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<String> artistnames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String artistname = document.getString("artist");
                                artistnames.add(artistname);
                            }

                            if (!artistnames.isEmpty()) {
                                // Fetch profiles of the followed artists
                                db.collection("users").whereIn("username", artistnames)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                ArrayList<FollowedArtist> followedArrayList = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                                    String artistUsername = document.getString("username");
                                                    String profileImageUrl = document.getString("profile");
                                                    FollowedArtist followedArtist = new FollowedArtist(profileImageUrl, artistUsername);
                                                    followedArrayList.add(followedArtist);
                                                }

                                                // Update RecyclerView with followed artists
                                                followedArtistAdapter = new FollowedArtistsAdapter(followedArrayList, this);
                                                recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                                recyclerView3.setAdapter(followedArtistAdapter);
                                            } else {
                                                // Handle errors
                                                Toast.makeText(getContext(), "Error: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // No artists followed
                                NoArtists.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        return rootview;
    }

    public void onArtClick(TrendingArts art) {
        Intent intent = new Intent(requireContext(), Post.class);
       intent.putExtra("username", username);
       intent.putExtra("artistUsername", art.getusername());
       intent.putExtra("title", art.getTitle());
       intent.putExtra("artUrl", art.getImage());
       startActivity(intent);
    }

    @Override
    public void onArtistClick(TopArtist artist) {
        Intent intent = new Intent(requireContext(), OpenArtistProfile.class);
        intent.putExtra("username", username);
        intent.putExtra("artistUsername", artist.getusername());
        intent.putExtra("profile", artist.getImage());
        startActivity(intent);
    }

    public void onFollowedClick(FollowedArtist artist)
    {
        Intent intent = new Intent(requireContext(), OpenArtistProfile.class);
        intent.putExtra("username", username);
        intent.putExtra("artistUsername", artist.getUsername());
        intent.putExtra("profile", artist.getImage());
        startActivity(intent);
    }
}



class TrendingArts
{
    private String image;
    private String title;
    private String username;

    public TrendingArts(String image, String text, String username) {
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

    public String getusername() { return username; }
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




