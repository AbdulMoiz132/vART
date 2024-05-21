package com.example.vart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment implements OpenProfileAdapter.OnArtClickListener {

    FirebaseFirestore db;
    ImageView profilePic, editProfilePic;
    ImageButton addArt;
    TextView name, followerCount, followingCount, artCount, becomeArtist, artistBio;
    View  bio, followers, arts, artistPrivilege;
    String username, fullName, profile, bioText;
    boolean isArtist;
    int following, artistFollowerCount, artistArtCount;
    ProgressDialog progressDialog;
    private static final int EDIT_NAME_REQUEST = 1;
    private static final int EDIT_BIO_REQUEST = 2;
    private static final int CHANGE_PASSWORD_REQUEST = 3;
    private static final int UPDATE_PROFILE_PIC_REQUEST = 4;
    private static final int ADD_ART_REQUEST = 5;

    RecyclerView recyclerView;

    ArrayList<Arts> Arts;

    TextView ArtText;

    private OpenProfileAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = rootView.findViewById(R.id.ArtRecyclerView);
        ArtText = rootView.findViewById(R.id.tvNoArt);
        ArtText.setVisibility(View.GONE);

        Arts = new ArrayList<>();

        profilePic = rootView.findViewById(R.id.profilePic);
        editProfilePic = rootView.findViewById(R.id.editProfilePic);
        name = rootView.findViewById(R.id.profileName);
        addArt = rootView.findViewById(R.id.addArt);
        bio = rootView.findViewById(R.id.bio);
        followerCount = rootView.findViewById(R.id.followerCount);
        followingCount = rootView.findViewById(R.id.followingCount);
        artCount = rootView.findViewById(R.id.artCount);
        becomeArtist = rootView.findViewById(R.id.becomeArtist);
        followers = rootView.findViewById(R.id.followers);
        arts = rootView.findViewById(R.id.arts);
        artistPrivilege = rootView.findViewById(R.id.artistPrivilege);
        artistBio = rootView.findViewById(R.id.artistBio);

        if (getArguments() != null) {
            username = getArguments().getString("username");
            fullName = getArguments().getString("fullName");
            isArtist = getArguments().getBoolean("isArtist");
            profile = getArguments().getString("profile");
            bioText = getArguments().getString("bio");
        }

        db = FirebaseFirestore.getInstance();

        // progress dialogue for becoming a creator
        //noinspection deprecation
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);

        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("name") != null)
                            {
                                following = document.getLong("following").intValue();
                                if (following != 0)
                                {
                                    followingCount.setText(String.valueOf(following));
                                }
                            }
                        }
                    }
                });

        if (fullName != null)
        {
            name.setText(fullName);
        }

        if (isArtist) {

            db.collection("artist").whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("username") != null)
                                {
                                    artistFollowerCount = document.getLong("followers").intValue();
                                    artistArtCount = document.getLong("arts").intValue();

                                    if (artistArtCount != 0) {
                                        artCount.setText(String.valueOf(artistArtCount));
                                    }
                                    if (artistFollowerCount != 0) {
                                        followerCount.setText(String.valueOf(artistFollowerCount));
                                    }
                                }
                            }
                        } else {
                            // Handle errors
                        }
                    });

            db.collection("artwork").whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Extract the data for each art piece
                                    String artistUsername = document.getString("username");
                                    String title = document.getString("title");
                                    String imageurl = document.getString("imageUrl");
                                    Arts art = new Arts(imageurl, title, artistUsername);
                                    Arts.add(art);
                                }
                            }
                            else
                            {
                                ArtText.setVisibility(View.VISIBLE);
                            }
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                            adapter = new OpenProfileAdapter(Arts, this);
                            recyclerView.setAdapter(adapter);
                        }
                    });

            if (!bioText.equals("null"))
            {
                artistBio.setText(bioText);
                bio.setVisibility(View.VISIBLE);
            } else {
                bio.setVisibility(View.GONE);
            }

            becomeArtist.setVisibility(View.GONE);
            followers.setVisibility(View.VISIBLE);
            arts.setVisibility(View.VISIBLE);
            artistPrivilege.setVisibility(View.VISIBLE);

        } else {
            becomeArtist.setVisibility(View.VISIBLE);
            bio.setVisibility(View.GONE);
            followers.setVisibility(View.GONE);
            arts.setVisibility(View.GONE);
            artistPrivilege.setVisibility(View.GONE);
            ArtText.setVisibility(View.GONE);
        }

        if (!Objects.equals(profile, "null"))
        {
            Picasso.get().load(profile).placeholder(R.drawable.default_profile).into(profilePic);
        }

        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateProfilePic.class);
                intent.putExtra("username", username);
                intent.putExtra("profile", profile);
                startActivityForResult(intent, UPDATE_PROFILE_PIC_REQUEST);
            }
        });

        becomeArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("bio", "null");
                user.put("followers", 0);
                user.put("arts", 0);

                db.collection("artist").document(username).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "You are a creator now", Toast.LENGTH_SHORT).show();

                                becomeArtist.setVisibility(View.GONE);
                                followers.setVisibility(View.VISIBLE);
                                arts.setVisibility(View.VISIBLE);
                                artistPrivilege.setVisibility(View.VISIBLE);
                                ArtText.setVisibility(View.VISIBLE);
                                isArtist = true;
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

        addArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadArt.class);
                intent.putExtra("username", username);
                startActivityForResult(intent, ADD_ART_REQUEST);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        if(isArtist)
        {
           menu.findItem(R.id.editBio).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.editBio).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.editFullName)
        {
            Intent intent = new Intent(getContext(), EditName.class);
            intent.putExtra("username", username);
            startActivityForResult(intent, EDIT_NAME_REQUEST);
        }
        if (itemId == R.id.editBio) {
            Intent intent = new Intent(getContext(), EditBio.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
        if (itemId == R.id.changePass)
        {
            Intent intent = new Intent(getContext(), ChangePass.class);
            intent.putExtra("username", username);
            startActivityForResult(intent, CHANGE_PASSWORD_REQUEST);
        }
        if (itemId == R.id.logout)
        {
            AlertDialog.Builder builder = getBuilder();
            builder.show();
        }
        if (itemId == R.id.deleteAcc)
        {
            showDeleteAccountDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the logout method
                LogoutUtil.logout(getActivity());
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
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

    public void onArtClick(Arts art)
    {
        Intent intent = new Intent(getContext(), Post.class);
        intent.putExtra("username", username);
        intent.putExtra("artistUsername", art.getUsername());
        intent.putExtra("title", art.getTitle());
        intent.putExtra("artUrl", art.getImage());
        startActivity(intent);
    }

    private void deleteAcc() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Deleting Account...");
        progressDialog.show();

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    deleteFromArtistCollection();
                                    user_deleteFromLikedCollection();
                                    artist_deleteFromLikedCollection();
                                    user_deleteFromSavedCollection();
                                    artist_deleteFromSavedCollection(progressDialog);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Could not remove user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to fetch artwork collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void deleteFromArtistCollection() {
        db.collection("artist")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to update artist art count: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void artist_deleteFromLikedCollection() {
        db.collection("Liked")
                .whereEqualTo("artist", username)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        doc.getReference().delete();
                    }
                });
    }

    private void user_deleteFromLikedCollection() {
        db.collection("Liked")
                .whereEqualTo("user", username)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        doc.getReference().delete();
                    }
                });
    }

    private void user_deleteFromSavedCollection() {
        db.collection("saved")
                .whereEqualTo("user", username)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        doc.getReference().delete();
                    }
                });
    }

    private void artist_deleteFromSavedCollection(ProgressDialog progressDialog) {
        db.collection("saved")
                .whereEqualTo("artist", username)
                .get()
                .addOnSuccessListener(queryDocSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocSnapshots) {
                        doc.getReference().delete();
                    }
                });
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the deleteAcc method
                deleteAcc();

                progressDialog.dismiss();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


}