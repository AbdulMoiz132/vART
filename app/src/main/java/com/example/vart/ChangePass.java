package com.example.vart;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

interface OnResultListener {
    void onResult(boolean result);
    void onFailure(String errorMessage);
}

public class ChangePass extends AppCompatActivity {

    EditText currentPass, newPass, confirmPass;
    Button changePass;
    String username;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        username = getIntent().getStringExtra("username");

        currentPass = findViewById(R.id.currentPass);
        newPass = findViewById(R.id.newPass);
        confirmPass = findViewById(R.id.confirmPass);
        changePass = findViewById(R.id.btnChangePass);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(ChangePass.this);
        progressDialog.setMessage("Changing password...");
        progressDialog.setCancelable(false);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_pass = currentPass.getText().toString();
                String new_pass = newPass.getText().toString();
                String confirm_pass = confirmPass.getText().toString();

                if (current_pass.isEmpty()) {
                    currentPass.setError("Current password is required");
                }
                else if (new_pass.isEmpty())
                {
                    newPass.setError("New password is required");
                }
                else if (new_pass.length() < 8)
                {
                    newPass.setError("Password must be at least 8 characters long");
                }
                else if (confirm_pass.isEmpty())
                {
                    confirmPass.setError("Confirm password is required");
                }
                else if (!new_pass.equals(confirm_pass))
                {
                    confirmPass.setError("Password does not match");
                }
                else
                {
                    db.collection("users").whereEqualTo("password", current_pass)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        currentPass.setError("Wrong password");
                                    } else
                                    {
                                        progressDialog.show();

                                        Map<String, Object> newData = new HashMap<>();
                                        newData.put("password", new_pass);

                                        db.collection("users").document(username)
                                                .update(newData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChangePass.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                                                        setResult(RESULT_OK);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChangePass.this, "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error querying user", e);
                                }
                            });

                }
            }
        });
    }
}