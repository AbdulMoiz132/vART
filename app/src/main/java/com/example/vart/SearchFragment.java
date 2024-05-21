package com.example.vart;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.List;


public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ListView mylistView;

    ArrayList <String> arrayList;
    ArrayAdapter <String> adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String username;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = rootview.findViewById(R.id.searchView);
        mylistView = rootview.findViewById(R.id.listView);
        mylistView.setVisibility(View.GONE);

        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        arrayList = new ArrayList<>();

        db.collection("artist")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        arrayList.add(documentSnapshot.getString("username"));
                    }
                });

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);

        mylistView.setAdapter(adapter);

        mylistView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUsername = adapter.getItem(position);
            if (selectedUsername != null) {
                searchView.setQuery(selectedUsername, true);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                db.collection("users")
                        .whereEqualTo("username", query)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                                String artistname = documentSnapshot.getString("username");
                                String imageurl = documentSnapshot.getString("profile");

                                Intent intent = new Intent(getContext(), OpenArtistProfile.class);
                                intent.putExtra("username", username);
                                intent.putExtra("artistUsername", artistname);
                                intent.putExtra("profile", imageurl);
                                startActivity(intent);
                            } else {
                                // Handle case where no document matches the query
                            }
                        });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mylistView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(newText);
                return false;


            }
        });






        return rootview;
    }

}