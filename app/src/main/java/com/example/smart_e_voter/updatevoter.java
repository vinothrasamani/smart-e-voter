package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class updatevoter extends AppCompatActivity {

    EditText vn, va, vad, vp;
    Button updatebtn, delbtn;
    String delkey = "", vname, vage, vphone, vaddr;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatevoter);

        vn = findViewById(R.id.editTextTextn);
        va = findViewById(R.id.editTextTexta);
        vp = findViewById(R.id.editTextTextp);
        vad = findViewById(R.id.editTextTextad);
        delbtn = findViewById(R.id.delbtn);
        updatebtn = findViewById(R.id.updatebtn);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            vn.setText(bundle.getString("vname"));
            va.setText(bundle.getString("vage"));
            delkey = bundle.getString("vkey");
            vp.setText(bundle.getString("vphone"));
            vad.setText(bundle.getString("vaddr"));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(delkey);
        
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vname = vn.getText().toString().trim();
                vage = va.getText().toString().trim();
                vphone = vp.getText().toString().trim();
                vaddr = vad.getText().toString().trim();
                if (!vname.isEmpty() && !vage.isEmpty() && !vphone.isEmpty() && !vaddr.isEmpty()){
                    store_dp user = new store_dp(vname, vphone, vaddr, vage, true);
                    databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(updatevoter.this, "Updated Successfully..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(updatevoter.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(updatevoter.this, "Fill all the contents", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(updatevoter.this, adminHome.class);
                startActivity(intent);
            }
        });

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference.child(delkey).removeValue();
                Toast.makeText(updatevoter.this, "Voter Details Deleted..", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(updatevoter.this, adminHome.class);
                startActivity(intent);
                finish();
            }
        });
    }
}