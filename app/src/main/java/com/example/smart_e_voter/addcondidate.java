package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class addcondidate extends AppCompatActivity {

    ImageView select;
    public Uri imageuri;
    String randomKey = null;
    String username, partyname, userid;
    int img;
    DatabaseReference databaseReference;
    private FirebaseStorage storage;
    String link = null;
    private StorageReference storageReference, riversRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcondidate);

        //fetch id's
        select = findViewById(R.id.symbol);
        Button addcan = findViewById(R.id.add_can_btn);
        EditText Can_name = findViewById(R.id.add_name_can);
        EditText add_party = findViewById(R.id.add_symbol_name);
        ImageView imgs = findViewById(R.id.symbol);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //add image
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        //add details
        addcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = Can_name.getText().toString();
                partyname = add_party.getText().toString();
                img = imgs.getImageAlpha();
                userid = databaseReference.push().getKey();
                uploadPicture();
            }
        });
    }

    private void storedetails(Uri path) {
        link= path.toString();
        dbhelper can = new dbhelper(username, partyname, link, 0);
        databaseReference.child("candidates").child(userid).setValue(can).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(addcondidate.this, "New Candidate added successfully..", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(addcondidate.this, adminHome.class);
                    startActivity(i);
                }
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/**a");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null){
            imageuri =data.getData();
            select.setImageURI(imageuri);
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Details...");
        pd.show();

        randomKey = UUID.randomUUID().toString();
        riversRef = storageReference.child("images/" + randomKey);
        riversRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri muri) {
                        storedetails(muri);
                        Snackbar.make(findViewById(android.R.id.content), "Image uploaded...", Snackbar.LENGTH_LONG).show();
                    }
                });
                pd.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(addcondidate.this, "Faild to upload image..", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * (double) snapshot.getBytesTransferred()) / (double) snapshot.getTotalByteCount();
                pd.setMessage("Progress : " + (int) progressPercent + "%");
            }
        });
    }
}