package com.example.vart;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OpenArtistProfile extends AppCompatActivity {

    ImageView profilePic;
    TextView toolbarTitle, fullName, artistBio, followerCount, followingCount, artCount;
    View bio;
    Button follow;
    String username, artistUsername, artistName, profile, bioText;
    int artistFollowingCount, artistFollowerCount, artistArtCount;
    private Toolbar toolbar;
    FirebaseFirestore db;
    boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_artist_profile);

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

        db = FirebaseFirestore.getInstance();

        checkFollowingStatus();

        db.collection("users").whereEqualTo("username", artistUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("name") != null) {
                                artistName = document.getString("name");
                                artistFollowingCount = document.getLong("following").intValue();
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });

        db.collection("artist").whereEqualTo("username", artistUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("username") != null) {
                                artistFollowerCount = document.getLong("followers").intValue();
                                artistArtCount = document.getLong("arts").intValue();
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });

        toolbarTitle.setText(artistUsername);

        if (artistName != null) {
            fullName.setText(artistName);
        }

        if (!Objects.equals(profile, "null")) {
            Picasso.get().load(profile).placeholder(R.drawable.default_profile).into(profilePic);
        }

        if (!bioText.equals("null")) {
            artistBio.setText(bioText);
            bio.setVisibility(View.VISIBLE);
        } else {
            bio.setVisibility(View.GONE);
        }

        if (artistFollowerCount != 0) {
            followerCount.setText(String.valueOf(artistFollowerCount));
        }
        if (artistFollowingCount != 0) {
            followingCount.setText(String.valueOf(artistFollowingCount));
        }
        if (artistArtCount != 0) {
            artCount.setText(String.valueOf(artistArtCount));
        }

        follow.setOnClickListener(v -> {
            if (isFollowing) {
                unfollowArtist();
            } else {
                followArtist();
            }
        });
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
                        followingCount.setText(String.valueOf(updatedFollowing));
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
                        followingCount.setText(String.valueOf(updatedFollowing));
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
}
