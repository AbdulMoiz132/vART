package com.example.vart;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    String username, fullName;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    Bundle profileBundle;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = getIntent().getStringExtra("username");
        profileBundle = new Bundle();
        profileBundle.putString("username", username);

        bottomNavigationView = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);
        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        toolbarTitle = findViewById(R.id.toolbarTitle);

        db = FirebaseFirestore.getInstance();

        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("name") != null)
                            {
                                fullName = document.getString("name");
                                profileBundle.putString("fullName", fullName);
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });

        db.collection("artist").whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean isArtist = !queryDocumentSnapshots.isEmpty();
                        profileBundle.putBoolean("isArtist", isArtist);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error querying user", e);
                    }
                });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.navHome)
                {
                    toolbarTitle.setText(R.string.app_name);
                    loadFragment(new HomeFragment());
                }
                else if (itemId == R.id.navSearch)
                {
                    loadFragment(new SearchFragment());
                }
                else if (itemId == R.id.navLiked)
                {
                    toolbarTitle.setText(R.string.app_name);
                    loadFragment(new LikedFragment());
                }
                else
                {
                    ProfileFragment profile = new ProfileFragment();
                    profile.setArguments(profileBundle);
                    toolbarTitle.setText(username);
                    loadFragment(profile);
                }


                return true;
            }
        });

        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, fragment);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}