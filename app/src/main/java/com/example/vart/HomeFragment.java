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

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TrendingArtsAdapter trendingArtsAdapter;

    private ArrayList<TrendingArts> trendingArtsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);

        HomeActivity activity = (HomeActivity) getActivity();
        if (activity != null)
        {
            String username = activity.username;
            // Now you can use the username in this fragment
        }
        recyclerView = rootview.findViewById(R.id.trendingArtsRecyclerView);
        trendingArtsArrayList = new ArrayList<>();

        trendingArtsAdapter = new TrendingArtsAdapter(requireContext(), trendingArtsArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(trendingArtsAdapter);


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

