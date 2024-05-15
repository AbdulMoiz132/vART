package com.example.vart;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    String username, fullName, profile;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    boolean isArtist;
    Bundle profileBundle;
    FirebaseFirestore db;
    private static final int EDIT_NAME_REQUEST = 1;
    private static final int EDIT_BIO_REQUEST = 2;
    private static final int CHANGE_PASSWORD_REQUEST = 3;
    private static final int UPDATE_PROFILE_PIC_REQUEST = 4;

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
                                profile = document.getString("profile");
                                profileBundle.putString("fullName", fullName);
                                profileBundle.putString("profile", profile);
                            }
                        }
                    } else {
                        // Handle errors
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == EDIT_NAME_REQUEST || requestCode == EDIT_BIO_REQUEST || requestCode == CHANGE_PASSWORD_REQUEST || requestCode == UPDATE_PROFILE_PIC_REQUEST) && resultCode == RESULT_OK)
        {
            bottomNavigationView.setSelectedItemId(R.id.navProfile);
        }
    }
}