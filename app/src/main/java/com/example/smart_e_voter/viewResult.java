package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class viewResult extends AppCompatActivity {

    RecyclerView recyclerResult_voter;
    ArrayList<dbhelper> listResult;
    SearchView searchView4;
    TextView tv;
    DatabaseReference databaseReferences;
    Result adapter5;
    Boolean b;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(viewResult.this, MainActivity.class);
        startActivity(intent);

        // Call super.onBackPressed() to return to the previous activity
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);

        recyclerResult_voter = findViewById(R.id.cycleviewresultforvoter);
        searchView4 = findViewById(R.id.search_viewed_result);
        tv = findViewById(R.id.textView15_result);
        databaseReferences = FirebaseDatabase.getInstance().getReference("candidates");
        listResult = new ArrayList<>();
        recyclerResult_voter.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter5 = new Result(getApplicationContext(), listResult);
        searchView4.clearFocus();

        FirebaseDatabase.getInstance().getReference().child("Permission").child("NhdteDlOLesWE").child("bool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b = snapshot.getValue(Boolean.class);
                if (b == true){
                    tv.setVisibility(View.GONE);
                    recyclerResult_voter.setAdapter(adapter5);
                    recyclerResult_voter.setVisibility(View.VISIBLE);
                }else {
                    tv.setVisibility(View.VISIBLE);
                    recyclerResult_voter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error retrieving data", error.toException());

            }
        });

        databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dbhelper candi = dataSnapshot.getValue(dbhelper.class);
                    candi.setCankey(dataSnapshot.getKey());
                    listResult.add(candi);
                }
                adapter5.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView4.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResultvoter(newText);
                return true;
            }
        });

    }
    public void searchResultvoter(String cText) {
        ArrayList<dbhelper> sl = new ArrayList<>();
        for (dbhelper dh : listResult){
            if (dh.getCan_name().toLowerCase().contains(cText.toLowerCase())){
                sl.add(dh);
            }
        }
        adapter5.searchcanlist2(sl);
    }
}