package com.example.vart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Post extends AppCompatActivity {

    ImageView profilePic, post;
    TextView tvUsername, artTitle, artDescription, likes, comments, saves;
    ImageButton likeButton, commentButton, saveButton, deleteButton;
    String username, artistUsername, artistProfile, description, title, artUrl;
    private String source;
    int artLikeCount, artCommentCount, artSaveCount;
    FirebaseFirestore db;
    boolean isLiked = false, isSaved = false, isArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        username = getIntent().getStringExtra("username");
        artistUsername = getIntent().getStringExtra("artistUsername");
        title = getIntent().getStringExtra("title");
        artUrl = getIntent().getStringExtra("artUrl");
        artistProfile = getIntent().getStringExtra("artistProfile");
        source = getIntent().getStringExtra("SOURCE");

        profilePic = findViewById(R.id.profilePic);
        post = findViewById(R.id.art);
        tvUsername = findViewById(R.id.tvUsername);
        artTitle = findViewById(R.id.artTitle);
        artDescription = findViewById(R.id.artDescription);
        likes = findViewById(R.id.likes);
        comments = findViewById(R.id.comments);
        saves = findViewById(R.id.saves);
        likeButton = findViewById(R.id.likeButton);
        commentButton = findViewById(R.id.commentButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        db = FirebaseFirestore.getInstance();

        checkLikedStatus();
        checkSaveStatus();

        if (username.equals(artistUsername))
        {
            deleteButton.setVisibility(View.VISIBLE);
        }
        else
        {
            deleteButton.setVisibility(View.GONE);
        }

        if (isLiked) {
            Drawable liked = ContextCompat.getDrawable(this, R.drawable.filled_like);
            likeButton.setBackground(liked);
        }
        else
        {
            Drawable notLiked = ContextCompat.getDrawable(this, R.drawable.hollow_like);
            likeButton.setBackground(notLiked);
        }

        if (isSaved)
        {
            Drawable saved = ContextCompat.getDrawable(this, R.drawable.filled_save);
            saveButton.setBackground(saved);
        }
        else
        {
            Drawable notSaved = ContextCompat.getDrawable(this, R.drawable.save);
            saveButton.setBackground(notSaved);
        }

        db.collection("users").whereEqualTo("username", artistUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("name") != null)
                            {
                                artistProfile = document.getString("profile");

                                if (artistProfile != null)
                                {
                                    Picasso.get().load(artistProfile).placeholder(R.drawable.default_profile).into(profilePic);
                                }
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });

        db.collection("artwork")
                .whereEqualTo("username", artistUsername)
                .whereEqualTo("title", title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("title") != null)
                            {
                                description = document.getString("description");
                                artLikeCount = document.getLong("likes").intValue();
                                artCommentCount = document.getLong("comments").intValue();
                                artSaveCount = document.getLong("saves").intValue();

                                if (description != null)
                                {
                                    artDescription.setText(description);
                                }
                                if (artLikeCount != 0)
                                {
                                    likes.setText(String.valueOf(artLikeCount));
                                }
                                if (artCommentCount != 0)
                                {
                                    comments.setText(String.valueOf(artCommentCount));
                                }
                                if (artSaveCount != 0)
                                {
                                    saves.setText(String.valueOf(artSaveCount));
                                }
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });

        if (artistUsername != null)
        {
            tvUsername.setText(artistUsername);
        }

        if (artUrl != null)
        {
            Picasso.get().load(artUrl).placeholder(R.drawable.default_art).into(post);
        }

        if (title != null)
        {
            artTitle.setText(title);
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked)
                {
                    unlikeArt();
                }
                else
                {
                    likeArt();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSaved)
                {
                    unsaveArt();
                }
                else
                {
                    saveArt();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = getBuilder();
                builder.show();
            }
        });

    }

    private void checkLikedStatus() {
        db.collection("liked")
                .whereEqualTo("user", username)
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    isLiked = !queryDocumentSnapshots.isEmpty();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Post.this, "Failed to check Liked status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkSaveStatus() {
        db.collection("saved")
                .whereEqualTo("user", username)
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    isSaved = !queryDocumentSnapshots.isEmpty();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Post.this, "Failed to check saved status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void likeArt() {
        Drawable liked = ContextCompat.getDrawable(this, R.drawable.filled_like);
        likeButton.setBackground(liked);

        db.collection("artwork")
                .whereEqualTo("username", artistUsername)
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentLikes = document.getLong("likes").intValue();
                        int updatedLikes = currentLikes + 1;

                        document.getReference().update("likes", updatedLikes);
                        likes.setText(String.valueOf(updatedLikes));
                    }
                });

        Map<String, Object> liker = new HashMap<>();
        liker.put("user", username);
        liker.put("title", title);
        liker.put("imageUrl", artUrl);
        liker.put("artist", artistUsername);

        db.collection("liked").add(liker)
                .addOnSuccessListener(aVoid -> {
                    isLiked = true;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Post.this, "Failed to add liker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void unlikeArt() {
        Drawable notLiked = ContextCompat.getDrawable(this, R.drawable.hollow_like);
        likeButton.setBackground(notLiked);

        db.collection("artwork")
                .whereEqualTo("username", artistUsername)
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentLikes = document.getLong("likes").intValue();
                        int updatedLikes = currentLikes - 1;

                        document.getReference().update("likes", updatedLikes);
                        likes.setText(String.valueOf(updatedLikes));
                    }
                });

        db.collection("liked")
                .whereEqualTo("user", username)
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    isLiked = false;
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Post.this, "Failed to unlike art: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void saveArt() {
        Drawable saved = ContextCompat.getDrawable(this, R.drawable.filled_save);
        saveButton.setBackground(saved);

        db.collection("artwork")
                .whereEqualTo("username", artistUsername)
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentSaves = document.getLong("saves").intValue();
                        int updatedSaves = currentSaves + 1;

                        document.getReference().update("saves", updatedSaves);
                        saves.setText(String.valueOf(updatedSaves));
                    }
                });

        Map<String, Object> saver = new HashMap<>();
        saver.put("user", username);
        saver.put("title", title);
        saver.put("imageUrl", artUrl);
        saver.put("artist", artistUsername);

        db.collection("saved").add(saver)
                .addOnSuccessListener(aVoid -> {
                    isSaved = true;
                    Toast.makeText(Post.this, "Art added to collection", Toast.LENGTH_SHORT).show();
                    })
                .addOnFailureListener(e -> {
                    Toast.makeText(Post.this, "Failed to add saver: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void unsaveArt() {
        Drawable notSaved = ContextCompat.getDrawable(this, R.drawable.save);
        saveButton.setBackground(notSaved);

        db.collection("artwork")
                .whereEqualTo("username", artistUsername)
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int currentSaves = document.getLong("saves").intValue();
                        int updatedSaves = currentSaves - 1;

                        document.getReference().update("saves", updatedSaves);
                        saves.setText(String.valueOf(updatedSaves));
                    }
                });

        db.collection("saved")
                .whereEqualTo("user", username)
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    isSaved = false;
                                    Toast.makeText(Post.this, "Art removed from collection", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Post.this, "Failed to unsave art: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void deletePost() {
        ProgressDialog progressDialog = new ProgressDialog(Post.this);
        progressDialog.setTitle("Deleting Art...");
        progressDialog.show();

        db.collection("artwork")
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Update artist collection and delete from other collections
                                        updateArtistCollection();
                                        deleteFromLikedCollection();
                                        deleteFromSavedCollection(progressDialog);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Post.this, "Failed to delete from artwork collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    });
                        }
                    } else {
                        Toast.makeText(Post.this, "No matching artwork found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Post.this, "Failed to fetch artwork collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void updateArtistCollection() {
        db.collection("artist")
                .whereEqualTo("username", artistUsername)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        int currentArts = doc.getLong("arts").intValue();
                        int updatedArts = currentArts - 1;
                        doc.getReference().update("arts", updatedArts);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Post.this, "Failed to update artist art count: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteFromLikedCollection() {
        db.collection("Liked")
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        doc.getReference().delete()
                                .addOnSuccessListener(bVoid -> {
                                    Toast.makeText(Post.this, "Deleted info from liked collection", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Post.this, "Failed to delete from liked collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void deleteFromSavedCollection(ProgressDialog progressDialog) {
        db.collection("saved")
                .whereEqualTo("title", title)
                .whereEqualTo("artist", artistUsername)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        doc.getReference().delete()
                                .addOnSuccessListener(bVoid -> {
                                    Toast.makeText(Post.this, "Deleted info from saved collection", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    redirectToSource();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Post.this, "Failed to delete from saved collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                });
                    }
                });
    }

    private void redirectToSource() {
        switch (source) {
            case "HOME":
                // Redirect back to Home Fragment
                Intent homeIntent = new Intent(Post.this, HomeActivity.class);
                homeIntent.putExtra("FRAGMENT", "HOME");
                startActivity(homeIntent);
                break;
            case "PROFILE":
                // Redirect back to Profile Fragment
                Intent profileIntent = new Intent(Post.this, HomeActivity.class);
                profileIntent.putExtra("FRAGMENT", "PROFILE");
                startActivity(profileIntent);
                break;
            case "ARTIST_PROFILE":
                // Redirect back to OpenArtistProfile Activity
                Intent artistProfileIntent = new Intent(Post.this, OpenArtistProfile.class);
                startActivity(artistProfileIntent);
                break;
            default:
                finish();
        }
        finish();
    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
        builder.setTitle("Delete Post");
        builder.setMessage("Are you sure you want to delete the post?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Post.this, HomeActivity.class);
                deletePost();
                Toast.makeText(Post.this, "Artwork deleted", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }
}