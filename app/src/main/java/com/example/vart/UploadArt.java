package com.example.vart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UploadArt extends AppCompatActivity {

    EditText title, description;
    ImageView art;
    TextView selectArt;
    Button uploadArt;
    ProgressDialog progressDialog;
    String username;
    StorageReference storageReference;
    FirebaseFirestore db;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_art);

        title = findViewById(R.id.artTitle);
        description = findViewById(R.id.artDescription);
        art = findViewById(R.id.art);
        selectArt = findViewById(R.id.selectArt);
        uploadArt = findViewById(R.id.uploadArt);

        username = getIntent().getStringExtra("username");

        db = FirebaseFirestore.getInstance();

        selectArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        uploadArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadArtwork();
            }
        });

    }

    private void uploadArtwork() {
        progressDialog = new ProgressDialog(UploadArt.this);
        progressDialog.setTitle("Updating Art...");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String artName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+artName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> artUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                        artUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                Map<String, Object> artData = new HashMap<>();
                                artData.put("username", username);
                                artData.put("artUrl", downloadUrl);
                                artData.put("artTitle", title);
                                artData.put("description", description);
                                artData.put("likes", 0);
                                artData.put("comments", 0);

                                db.collection("artist").document(artName)
                                        .set(artData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.collection("artist")
                                                        .whereEqualTo("username", username)
                                                        .get()
                                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                                                int currentArts = document.getLong("arts").intValue();
                                                                int updatedArts = currentArts + 1;

                                                                // Update the "arts" field for the artist
                                                                document.getReference().update("arts", updatedArts);

                                                                Toast.makeText(UploadArt.this,"ArtCount updated",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });

                                art.setImageURI(null);
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText(UploadArt.this,"Art Uploaded",Toast.LENGTH_SHORT).show();

                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(UploadArt.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            art.setImageURI(imageUri);
        }
    }
}