package com.example.vart;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OpenArtistProfile extends AppCompatActivity implements OpenProfileAdapter.OnArtClickListener{

    ImageView profilePic;
    TextView toolbarTitle, fullName, artistBio, followerCount, followingCount, artCount;
    View bio;
    Button follow;
    String username, artistUsername, artistName, profile, bioText;
    int artistFollowingCount, artistFollowerCount, artistArtCount;
    private Toolbar toolbar;
    FirebaseFirestore db;
    boolean isFollowing = false;
    ListenerRegistration listenerRegistration;

    RecyclerView recyclerView;

    ArrayList<Arts> Arts;

    TextView ArtText;

    private OpenProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_artist_profile);

        recyclerView = findViewById(R.id.ArtRecyclerView);


        Arts = new ArrayList<>();

        ArtText = findViewById(R.id.tvNoArts);
        ArtText.setVisibility(View.GONE);

        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username");
        artistUsername = getIntent().getStringExtra("artistUsername");
        profile = getIntent().getStringExtra("profile");

        profilePic = findViewById(R.id.profilePic);
        fullName = findViewById(R.id.profileName);
        bio = findViewById(R.id.bio);
        artistBio = findViewById(R.id.artistBio);
        followerCount = findViewById(R.id.followerCount);
        followingCount = findViewById(R.id.followingCount);
        artCount = findViewById(R.id.artCount);
        follow = findViewById(R.id.follow);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        bio.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        checkFollowingStatus();

        // Set up the real-time listener
        startFirestoreListener();

        if (username.equals(artistUsername))
        {
            follow.setVisibility(View.GONE);
        }
        else
        {
            follow.setVisibility(View.VISIBLE);
        }

        toolbarTitle.setText(artistUsername);

        if (!Objects.equals(profile, "null")) {
            Picasso.get().load(profile).placeholder(R.drawable.default_profile).into(profilePic);
        }

        follow.setOnClickListener(v -> {
            if (isFollowing) {
                unfollowArtist();
            } else {
                followArtist();
            }
        });

        db.collection("artwork").whereEqualTo("username", artistUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extract the data for each art piece
                                String artistUsername = document.getString("username");
                                String title = document.getString("title");
                                String imageurl = document.getString("imageUrl");
                                Arts art = new Arts(imageurl, title, artistUsername);
                                Arts.add(art);
                            }
                        }
                        else
                        {
                            ArtText.setVisibility(View.VISIBLE);
                        }
                        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
                        adapter = new OpenProfileAdapter(Arts, this);
                        recyclerView.setAdapter(adapter);
                    } else
                    {

                    }
                });
    }

    private void startFirestoreListener() {
        listenerRegistration = db.collection("users").whereEqualTo("username", artistUsername)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot document : value) {
                            artistName = document.getString("name");
                            artistFollowingCount = document.getLong("following").intValue();

                            if (artistName != null) {
                                fullName.setText(artistName);
                            }
                            if (artistFollowingCount != 0) {
                                followingCount.setText(String.valueOf(artistFollowingCount));
                            }
                        }
                    }
                });

        listenerRegistration = db.collection("artist").whereEqualTo("username", artistUsername)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot document : value) {
                            bioText = document.getString("bio");
                            artistFollowerCount = document.getLong("followers").intValue();
                            artistArtCount = document.getLong("arts").intValue();

                            if (artistArtCount != 0) {
                                artCount.setText(String.valueOf(artistArtCount));
                            }
                            if (artistFollowerCount != 0) {
                                followerCount.setText(String.valueOf(artistFollowerCount));
                            }
                            if (bioText != null && !bioText.equals("null")) {
                                artistBio.setText(bioText);
                                bio.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void checkFollowingStatus() {
        db.collection("follower")
                .whereEqualTo("user", username)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        isFollowing = true;
                        updateFollowButton();
                    } else {
                        isFollowing = false;
                        updateFollowButton();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OpenArtistProfile.this, "Failed to check following status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFollowButton() {
        if (isFollowing) {
            Drawable following = ContextCompat.getDrawable(this, R.drawable.following_button);
            follow.setBackground(following);
            follow.setText(R.string.following);
        } else {
            Drawable followDrawable = ContextCompat.getDrawable(this, R.drawable.follow_button);
            follow.setBackground(followDrawable);
            follow.setText(R.string.follow);
        }
    }

    private void followArtist() {
        Drawable following = ContextCompat.getDrawable(this, R.drawable.following_button);
        follow.setBackground(following);
        follow.setText(R.string.following);

        db.collection("artist")
                .whereEqualTo("username", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentFollowers = document.getLong("followers").intValue();
                        int updatedFollowers = currentFollowers + 1;

                        document.getReference().update("followers", updatedFollowers);
                        followerCount.setText(String.valueOf(updatedFollowers));
                    }
                });

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentFollowing = document.getLong("following").intValue();
                        int updatedFollowing = currentFollowing + 1;

                        document.getReference().update("following", updatedFollowing);
                    }
                });

        Map<String, Object> follower = new HashMap<>();
        follower.put("user", username);
        follower.put("artist", artistUsername);

        db.collection("follower").add(follower)
                .addOnSuccessListener(aVoid -> {
                    isFollowing = true;
                    updateFollowButton();
                    Toast.makeText(OpenArtistProfile.this, "Follower added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OpenArtistProfile.this, "Failed to add follower: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void unfollowArtist() {
        Drawable followDrawable = ContextCompat.getDrawable(this, R.drawable.follow_button);
        follow.setBackground(followDrawable);
        follow.setText(R.string.follow);

        db.collection("artist")
                .whereEqualTo("username", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentFollowers = document.getLong("followers").intValue();
                        int updatedFollowers = currentFollowers - 1;

                        document.getReference().update("followers", updatedFollowers);
                        followerCount.setText(String.valueOf(updatedFollowers));
                    }
                });

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentFollowing = document.getLong("following").intValue();
                        int updatedFollowing = currentFollowing - 1;

                        document.getReference().update("following", updatedFollowing);
                    }
                });

        db.collection("follower")
                .whereEqualTo("user", username)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    isFollowing = false;
                                    updateFollowButton();
                                    Toast.makeText(OpenArtistProfile.this, "Unfollowed artist", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(OpenArtistProfile.this, "Failed to unfollow artist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }



    public void onArtClick(Arts art)
    {
        Intent intent = new Intent(this,Post.class);
        intent.putExtra("username", username);
        intent.putExtra("artistUsername", art.getUsername());
        intent.putExtra("title", art.getTitle());
        intent.putExtra("artUrl", art.getImage());
        intent.putExtra("SOURCE", "ARTIST_PROFILE");
        startActivity(intent);
    }
}
