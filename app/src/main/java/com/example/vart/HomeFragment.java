package com.example.vart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    FirebaseFirestore db;

    CollectionReference artsRef;

    private RecyclerView recyclerView;
    private TrendingArtsAdapter trendingArtsAdapter;

    private ArrayList<TrendingArts> trendingArtsArrayList;

    StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = rootview.findViewById(R.id.trendingArtsRecyclerView);
        trendingArtsArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        storageRef = FirebaseStorage.getInstance().getReference().child("images");

        artsRef = FirebaseFirestore.getInstance().collection("artwork");
        artsRef.orderBy("likes", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract the data for each art piece
                            String imageurl = document.getString("imageurl");
                            String title = document.getString("title");
                            assert imageurl != null;
                            StorageReference imageRef = storageRef.child("images/" + imageurl);
                            int likes = document.getLong("likes").intValue();
                            TrendingArts trendingArts = new TrendingArts(imageRef, title);
                            trendingArtsArrayList.add(trendingArts);
                        }

                        trendingArtsAdapter = new TrendingArtsAdapter(requireContext(), trendingArtsArrayList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setAdapter(trendingArtsAdapter);
                    } else {
                        // Handle errors
                    }
                });

        return rootview;
    }
}


class TrendingArts {
    private StorageReference image;
    private String title;

    public TrendingArts(StorageReference image, String text) {
        this.image = image;
        this.title = text;
    }

    public StorageReference getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}


