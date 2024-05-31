package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class viewcomments extends AppCompatActivity {

    RecyclerView recyclerView6;
    ArrayList<dataholder> list6;
    DatabaseReference databaseReference6;
    messages adapter6;
    Button allclear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcomments);

        recyclerView6 = findViewById(R.id.comcycleview);
        allclear = findViewById(R.id.clearall);
        databaseReference6 = FirebaseDatabase.getInstance().getReference("comments");
        list6 = new ArrayList<>();
        recyclerView6.setLayoutManager(new LinearLayoutManager(viewcomments.this));
        adapter6 = new messages(this, list6);
        recyclerView6.setAdapter(adapter6);

        loadDetails();

        allclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewcomments.this, adminHome.class);
                startActivity(intent);
                Toast.makeText(viewcomments.this, "All messages are deleted successfully..", Toast.LENGTH_SHORT).show();
                databaseReference6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }
                        NotificationHelper.sendNotification(viewcomments.this, "Online Smart Voter", "All Message are deleted Successfully...");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(viewcomments.this, "Error :" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected void loadDetails() {
        databaseReference6.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataholder m = dataSnapshot.getValue(dataholder.class);
                    m.setMsgkey(dataSnapshot.getKey());
                    list6.add(m);
                }
                adapter6.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(viewcomments.this, adminHome.class);
        startActivity(intent);
        super.onBackPressed();
    }
}