package com.example.vart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Signup extends AppCompatActivity {

    Toolbar toolbar;
    EditText fullName, email, username, password, rePass;
    Button signup;
    TextView login;
    String name, mail, user_name, pass, re_Pass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

                if (name.isEmpty() || mail.isEmpty() || user_name.isEmpty() || pass.isEmpty() || re_Pass.isEmpty())
                {
//                  Toast.makeText(Signup.this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
                    fullName.setError("You must enter your full name to register");
                }
                else if (!re_Pass.equals(pass))
                {
                    Toast.makeText(Signup.this, "Passwords do not match! Try again.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Signup.this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Signup.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}