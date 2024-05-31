package com.example.smart_e_voter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link candidateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class candidateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    RecyclerView recycler;
    ArrayList<dbhelper> listcan;
    SearchView searchView1;
    DatabaseReference databaseReferences;
    can_entry adapter1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public candidateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment candidateFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static candidateFragment newInstance(String param1, String param2) {
        candidateFragment fragment = new candidateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_candidate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton add_can_btn = view.findViewById(R.id.floatingActionButton_add_candidate);
        recycler = view.findViewById(R.id.cycleviewcan);
        searchView1 = view.findViewById(R.id.search_candi);
        databaseReferences = FirebaseDatabase.getInstance().getReference("candidates");
        listcan = new ArrayList<>();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter1 = new can_entry(getActivity(), listcan);
        recycler.setAdapter(adapter1);
        searchView1.clearFocus();

        databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dbhelper candi = dataSnapshot.getValue(dbhelper.class);
                    candi.setCankey(dataSnapshot.getKey());
                    listcan.add(candi);
                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchcan(newText);
                return true;
            }
        });

        add_can_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), addcondidate.class);
                startActivity(intent);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                Intent i = new Intent(getContext(), MainActivity.class);
                startActivity(i);
                return true;
            }
            // Return false to allow default back button behavior
            return false;
        });
    }

    public void searchcan(String cText) {
        ArrayList<dbhelper> sl = new ArrayList<>();
        for (dbhelper dh : listcan){
            if (dh.getCan_name().toLowerCase().contains(cText.toLowerCase())){
                sl.add(dh);
            }
        }
        adapter1.searchcanlist(sl);
    }
}