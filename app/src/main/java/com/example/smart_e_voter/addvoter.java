package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addvoter extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvoter);

        Button add_voter_side = findViewById(R.id.add_voter_btn);
        EditText name = findViewById(R.id.add_name_voter);
        EditText phone = findViewById(R.id.add_phone_voter);
        EditText address = findViewById(R.id.add_address_voter);
        EditText age = findViewById(R.id.add_age_of_voter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // old
        add_voter_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString();
                String userphone = phone.getText().toString();
                String userage = age.getText().toString();
                String useraddr = address.getText().toString();
                String userid = databaseReference.push().getKey();
                if (!username.isEmpty() && !userphone.isEmpty() && !useraddr.isEmpty() && !userage.isEmpty()) {
                    store_dp user = new store_dp(username, userphone, useraddr, userage, true);
                    databaseReference.child("users").child(userid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(addvoter.this, "New user added successfully..", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(addvoter.this, identity.class);
                                i.putExtra("Keyname", username);
                                i.putExtra("keyaddr", useraddr);
                                i.putExtra("keyage", userage);
                                i.putExtra("keyph", userphone);
                                i.putExtra("keyid", userid);
                                startActivity(i);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(addvoter.this, "All the information are Required", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}