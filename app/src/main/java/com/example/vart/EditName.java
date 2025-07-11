package com.example.vart;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class EditName extends AppCompatActivity {

    EditText fullName;
    Button changeName;
    String username;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        username = getIntent().getStringExtra("username");

        fullName = findViewById(R.id.etFullName);
        changeName = findViewById(R.id.btnChangeName);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(EditName.this);
        progressDialog.setMessage("Updating name...");
        progressDialog.setCancelable(false);

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullName.getText().toString();

                if (name.isEmpty()) {
                    fullName.setError("Name is required");
                    return;
                }
                else
                {
                    progressDialog.show();

                    Map<String, Object> newData = new HashMap<>();
                    newData.put("name", name);

                    db.collection("users").document(username)
                            .update(newData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditName.this, "Name updated successfully!", Toast.LENGTH_SHORT).show();

                                    setResult(RESULT_OK);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditName.this, "Failed to update name. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

            }
        });

    }
}