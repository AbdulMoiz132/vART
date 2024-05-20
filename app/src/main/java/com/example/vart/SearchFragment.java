package com.example.vart;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private ArrayList<String> artistList;

    private FirebaseFirestore db;
    private CollectionReference artistsRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = rootView.findViewById(R.id.searchView);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        artistList = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artistList);
        recyclerView.setAdapter(artistAdapter);

        db = FirebaseFirestore.getInstance();
        artistsRef = db.collection("artist");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchArtists(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    artistList.clear();
                    artistAdapter.notifyDataSetChanged();
                } else {
                    searchArtists(newText);
                }
                return false;
            }
        });

        return rootView;
    }

    private void searchArtists(String query) {
        artistsRef.whereEqualTo("username", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        artistList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String artistName = document.getString("username");
                            if (artistName != null) {
                                artistList.add(artistName);
                            }
                        }
                        artistAdapter.notifyDataSetChanged();
                    }
                });
    }
}



