package com.example.vart;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Signup extends AppCompatActivity {

    Toolbar toolbar;
    EditText fullName, email, username, password, rePass;
    Button signup;
    TextView login;
    String name, mail, user_name, pass, re_Pass;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);

        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        fullName = findViewById(R.id.etFullName);
        email = findViewById(R.id.etEmail);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        rePass = findViewById(R.id.etRePass);
        signup = findViewById(R.id.btnSignup);
        login = findViewById(R.id.tvLogin);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = fullName.getText().toString().trim();
                mail = email.getText().toString().trim();
                user_name = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                re_Pass = rePass.getText().toString().trim();

                if (name.isEmpty()) {
                    fullName.setError("You must enter your full name to register");
                }
                else if (mail.isEmpty())
                {
                    email.setError("You must enter your email to register");
                }
                else if (user_name.isEmpty())
                {
                    username.setError("You must enter username to register");
                }
                else if(user_name.contains(" "))
                {
                    username.setError("Username cannot contain spaces");
                }
                else if (pass.isEmpty())
                {
                    password.setError("You must enter your password to register");
                }
                else if (pass.length() < 8)
                {
                    password.setError("Password must be at least 8 characters long");
                }
                else if (re_Pass.isEmpty())
                {
                    rePass.setError("You must re-enter your password to register");
                }
                else if (!re_Pass.equals(pass)) {
                    rePass.setError("Passwords do not match");
                }
                else
                {
                    progressDialog.show();
                    db.collection("users").whereEqualTo("username", user_name.trim())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // Username already exists, display error message
                                        progressDialog.dismiss();
                                        username.setError("Username already exists. Please try another username.");

                                    } else {

                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", name);
                                        user.put("email", mail);
                                        user.put("username", user_name);
                                        user.put("password", pass);

                                        db.collection("users").document(user_name).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Signup.this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Signup.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle failure
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Signup.this, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    // Handle errors
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this, "Failed to check username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public boolean userExists(String username)
    {
        AtomicBoolean exists = new AtomicBoolean(false);
        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean usernameExists = !task.getResult().isEmpty();
                        if (usernameExists) {
                            exists.set(true);
                        } else {
                            // Username does not exist in the database
                        }
                    } else {
                        // Handle errors
                    }
                });
        return exists.get();
    }
}