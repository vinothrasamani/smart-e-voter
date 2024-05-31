package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VOTE extends AppCompatActivity implements optionlist.VoteClickListener {

    RecyclerView recycler;
    ArrayList<dbhelper> listcan;
    SearchView searchView2;
    DatabaseReference mDatabase;
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    DatabaseReference databaseReferences;
    optionlist adapter2;
    static String lper, num, name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        recycler = findViewById(R.id.voteoptions);
        num = getIntent().getStringExtra("num");
        databaseReferences = FirebaseDatabase.getInstance().getReference("candidates");
        listcan = new ArrayList<>();
        searchView2 = findViewById(R.id.search_candi1);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new optionlist(this, listcan, this); // Pass interface instance
        recycler.setAdapter(adapter2);

        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchcan1(newText);
                return true;
            }
        });

        lper = getIntent().getStringExtra("personid");
        Toast.makeText(this, lper, Toast.LENGTH_SHORT).show();

        databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listcan.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dbhelper candi = dataSnapshot.getValue(dbhelper.class);
                    candi.setCankey(dataSnapshot.getKey());
                    listcan.add(candi);
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VOTE.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public void searchcan1(String cText) {
        ArrayList<dbhelper> sl = new ArrayList<>();
        for (dbhelper dh : listcan) {
            if (dh.getCan_name().toLowerCase().contains(cText.toLowerCase())) {
                sl.add(dh);
            }
        }
        adapter2.searchcanlist1(sl);
    }

    void sendSMS(String name) {
        name1 = name;
        String phoneNumber = num;

        if (ContextCompat.checkSelfPermission(VOTE.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VOTE.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Congratulations!! \n your vote is added successfully to " +
                    "the candidate (" + name + "). \n \n Thank you for your contribution...", null, null);
            Toast.makeText(VOTE.this, "SMS sent!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS(name1);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onVoteClicked(String personKey, long voteCount, String candidateName) {
        updateDataInFirebase(personKey, voteCount, candidateName);
    }

    private void updateDataInFirebase(String yourNodeKey, long votes, String pname) {
        mDatabase = FirebaseDatabase.getInstance().getReference("candidates");
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(VOTE.this);
        View bottomview = LayoutInflater.from(VOTE.this).inflate(R.layout.confirm, null);

        Button btnvote = bottomview.findViewById(R.id.yes);
        Button btncancel = bottomview.findViewById(R.id.no);
        TextView txt = bottomview.findViewById(R.id.confirmation);
        txt.setText("Do you want to cast your vote to " + pname + "?");
        btnvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmvote(yourNodeKey, votes, pname);
                bottomSheetDialog.dismiss();
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomview);
        bottomSheetDialog.show();
    }

    private void confirmvote(String yourNodeKey, long votes, String pname) {
        mDatabase.child(yourNodeKey).child("votecount").setValue(votes)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(VOTE.this, "Your vote added successfully to " + pname, Toast.LENGTH_SHORT).show();
                    Log.d("UpdateFirebase", "Data updated successfully!");
                    DatabaseReference permitRef = FirebaseDatabase.getInstance().getReference().child("users").child(lper.toString().trim()).child("permit");
                    permitRef.setValue(false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("change permit", "false");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("change permit", "failed: " + e.getMessage());
                                }
                            });
                    sendSMS(pname);
                    NotificationHelper.createNotificationChannel(VOTE.this);
                    NotificationHelper.sendNotification(VOTE.this, "Online Smart Voter", "Congratulations!! \n your vote is added successfully to " +
                            "the candidate (" + pname + "). \n \n Thank you for your contribution...");
                    Intent intent = new Intent(VOTE.this, MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VOTE.this, "Having an error for vote to " + pname, Toast.LENGTH_SHORT).show();
                    Log.e("UpdateFirebase", "Failed to update data", e);
                    Intent intent = new Intent(VOTE.this, MainActivity.class);
                    startActivity(intent);
                });
    }
}
