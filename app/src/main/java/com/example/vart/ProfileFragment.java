package com.example.vart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    TextView name, followerCount, followingCount, artCount, becomeArtist;
    View  bio, followers, arts, artistPrivilege;
    private String username;

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

        bio.setVisibility(View.GONE);
        followers.setVisibility(View.GONE);
        arts.setVisibility(View.GONE);
        artistPrivilege.setVisibility(View.GONE);

        becomeArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You are a creator now", Toast.LENGTH_SHORT).show();

                becomeArtist.setVisibility(View.GONE);
                bio.setVisibility(View.VISIBLE);
                followers.setVisibility(View.VISIBLE);
                arts.setVisibility(View.VISIBLE);
                artistPrivilege.setVisibility(View.VISIBLE);
            }
        });

//        if (isArtist(username))
//        {
//            bio.setVisibility(View.VISIBLE);
//            followers.setVisibility(View.VISIBLE);
//            arts.setVisibility(View.VISIBLE);
//            artistPrivilege.setVisibility(View.VISIBLE);
//        }



        return rootView;
    }

//    private boolean isArtist (String username)
//    {
//
//    }
}