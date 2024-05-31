package com.example.smart_e_voter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class updatecan extends AppCompatActivity {

    EditText cn, cpn;
    ImageView cimg1;
    String cankey = "", imageUrl = "";
    String cname1, cpname, imgurl;
    Uri uri;
    long c;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Button cupdate, cdelkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecan);

        cn = findViewById(R.id.up_name_can);
        cpn = findViewById(R.id.up_symbol_name);
        cupdate = findViewById(R.id.up_can_btn);
        cimg1 = findViewById(R.id.symbolcan);
        cdelkey = findViewById(R.id.delcan);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK){
                            Intent data = o.getData();
                            uri = Objects.requireNonNull(data).getData();
                            cimg1.setImageURI(uri);
                        }else {
                            Toast.makeText(updatecan.this, "No image selected..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            cn.setText(bundle.getString("cname"));
            cpn.setText(bundle.getString("cpn"));
            Glide.with(this).load(bundle.getString("cimg")).into(cimg1);
            imgurl = bundle.getString("cimg");
            cankey = bundle.getString("ckey");
            c = bundle.getLong("ccount");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("candidates").child(cankey);

        cimg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker = new Intent(Intent.ACTION_PICK);
                photopicker.setType("image/**a"); // Corrected MIME type
                activityResultLauncher.launch(photopicker);
            }
        });

        cupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrl = imgurl;
                savedetails();
                Intent intent = new Intent(updatecan.this, adminHome.class);
                startActivity(intent);
                finish();
            }
        });

        cdelkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("candidates");
                StorageReference Reference = FirebaseStorage.getInstance().getReferenceFromUrl(imgurl);
                Reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(cankey).removeValue();
                        Toast.makeText(updatecan.this, "Candidate Details Deleted..", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(updatecan.this, adminHome.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    public void savedetails() {
        if (uri != null) { // Check if an image has been selected
            storageReference = FirebaseStorage.getInstance().getReference().child("images").child(Objects.requireNonNull(uri.getLastPathSegment()));
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("loading...");
            pd.show();

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();
                            updateinfo();
                            pd.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(updatecan.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image changes..", Toast.LENGTH_SHORT).show();
            updateinfo();
        }
    }

    public void updateinfo() {
        cname1 = cn.getText().toString().trim();
        cpname = cpn.getText().toString().trim();

        dbhelper can = new dbhelper(cname1, cpname, imageUrl, c);
        databaseReference.setValue(can).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(imgurl);
                    reference.delete();
                    Toast.makeText(updatecan.this, "Updated Successfully..", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(updatecan.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}