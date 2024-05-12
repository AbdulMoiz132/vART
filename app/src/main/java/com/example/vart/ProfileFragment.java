package com.example.vart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    FirebaseFirestore db;
    ImageView profilePic;
    TextView name, followerCount, followingCount, artCount, becomeArtist;
    View  bio, followers, arts, artistPrivilege;
    String username, fullName;
    boolean isArtist;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = rootView.findViewById(R.id.profilePic);
        name = rootView.findViewById(R.id.profileName);
        bio = rootView.findViewById(R.id.bio);
        followerCount = rootView.findViewById(R.id.followerCount);
        followingCount = rootView.findViewById(R.id.followingCount);
        artCount = rootView.findViewById(R.id.artCount);
        becomeArtist = rootView.findViewById(R.id.becomeArtist);
        followers = rootView.findViewById(R.id.followers);
        arts = rootView.findViewById(R.id.arts);
        artistPrivilege = rootView.findViewById(R.id.artistPrivilege);

        db = FirebaseFirestore.getInstance();

        // progress dialogue for becoming a creator
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);

        if (getArguments() != null) {
            username = getArguments().getString("username");
            fullName = getArguments().getString("fullName");
            isArtist = getArguments().getBoolean("isArtist");
        }

        if (fullName != null)
        {
            name.setText(fullName);
        }

        if (isArtist) {
            becomeArtist.setVisibility(View.GONE);
            bio.setVisibility(View.VISIBLE);
            followers.setVisibility(View.VISIBLE);
            arts.setVisibility(View.VISIBLE);
            artistPrivilege.setVisibility(View.VISIBLE);
        } else {
            becomeArtist.setVisibility(View.VISIBLE);
            bio.setVisibility(View.GONE);
            followers.setVisibility(View.GONE);
            arts.setVisibility(View.GONE);
            artistPrivilege.setVisibility(View.GONE);
        }

        becomeArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("bio", "");
                user.put("follows", 0);
                user.put("arts", 0);

                db.collection("artist").document(username).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "You are a creator now", Toast.LENGTH_SHORT).show();

                                becomeArtist.setVisibility(View.GONE);
                                bio.setVisibility(View.VISIBLE);
                                followers.setVisibility(View.VISIBLE);
                                arts.setVisibility(View.VISIBLE);
                                artistPrivilege.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Error occurred. Could not become a creator", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return rootView;
    }
}