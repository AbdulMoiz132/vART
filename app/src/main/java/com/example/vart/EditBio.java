package com.example.vart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditBio extends AppCompatActivity {

    EditText bio;
    Button updateBio;
    String username;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bio);

        username = getIntent().getStringExtra("username");

        bio = findViewById(R.id.bio);
        updateBio = findViewById(R.id.updateBio);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(EditBio.this);
        progressDialog.setMessage("Updating bio...");
        progressDialog.setCancelable(false);

        updateBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bioText = bio.getText().toString();

                if (bioText.isEmpty()) {
                    bio.setError("Bio cannot be empty");
                }
                else
                {
                    progressDialog.show();

                    Map<String, Object> newData = new HashMap<>();
                    newData.put("bio", bioText);

                    db.collection("artist").document(username)
                            .update(newData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditBio.this, "Bio updated successfully!", Toast.LENGTH_SHORT).show();

                                    setResult(RESULT_OK);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditBio.this, "Failed to update bio. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}