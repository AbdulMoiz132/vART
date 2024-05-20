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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;


public class LikedFragment extends Fragment implements LikedAdapter.OnLikedArtClickListener {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    private LikedAdapter adapter;

    private ArrayList<LikedArts> likedArts;

    private String username;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_liked, container, false);

        recyclerView = rootview.findViewById(R.id.likedRecyclerView);

        likedArts = new ArrayList<>();

        if (getArguments() != null) {
            username = getArguments().getString("username");
        }


//        db.collection("liked").whereEqualTo("username", username)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            // Extract the data for each art piece
//                            String artistUsername = document.getString("artistUsername");
//                            String title = document.getString("title");
//                            String imageurl = document.getString("imageurl");
//                            LikedArts art = new LikedArts(imageurl, title, artistUsername);
//                            likedArts.add(art);
//                        }
//                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
//                        adapter = new LikedAdapter(likedArts, this);
//                        recyclerView.setAdapter(adapter);
//                    } else
//                    {
//                        // Handle errors
//                    }
//                });

        likedArts.add(new LikedArts("https://firebasestorage.googleapis.com/v0/b/vart-2329a.appspot.com/o/images%2Fart1.jpg?alt=media&token=34019c49-02cb-46ad-8b77-4d1945910640", "Murshid", "Ho"));
        likedArts.add(new LikedArts("https://firebasestorage.googleapis.com/v0/b/vart-2329a.appspot.com/o/images%2Fart2.jpg?alt=media&token=ece0b9c7-2ed5-4f51-8c7c-29acba8f9b06","Warrior", "Hi"));
        likedArts.add(new LikedArts("https://firebasestorage.googleapis.com/v0/b/vart-2329a.appspot.com/o/images%2F2024_05_16_13_18_02?alt=media&token=0c6626ac-c0fe-46a0-9ce0-6c96fa08b651","News", "Bye"));



        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new LikedAdapter(likedArts, this);
        recyclerView.setAdapter(adapter);

        return rootview;
    }

    public void onLikedArtClick(LikedArts art)
    {
//        Intent intent = new Intent(getContext(),Post.class);
//        intent.putExtra("username", username);
//        intent.putExtra("artistUsername", art.getUsername());
//        intent.putExtra("title", art.getTitle());
//        intent.putExtra("artUrl", art.getImage());
//        startActivity(intent);
        Toast.makeText(getContext(), art.getTitle(), Toast.LENGTH_SHORT).show();
    }
}

class LikedArts
{
    String title;
    String image;

    String username;

    public LikedArts(String image, String text, String username) {
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