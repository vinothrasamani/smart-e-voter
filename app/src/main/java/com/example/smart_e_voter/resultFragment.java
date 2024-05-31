package com.example.smart_e_voter;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link resultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class resultFragment extends Fragment {

    RecyclerView recyclerResult;
    ArrayList<dbhelper> listResult;
    SearchView searchView3;
    DatabaseReference databaseReferences, DReference;
    Result adapter4;

    Button publish, cancel;
    BottomSheetDialog dialog;
    Boolean permission, b;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public resultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment resultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static resultFragment newInstance(String param1, String param2) {
        resultFragment fragment = new resultFragment();
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
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton add_result_btn = view.findViewById(R.id.resultfab);
        recyclerResult = view.findViewById(R.id.cycleviewresult);
        searchView3 = view.findViewById(R.id.search_result);
        databaseReferences = FirebaseDatabase.getInstance().getReference("candidates");
        listResult = new ArrayList<>();
        DReference = FirebaseDatabase.getInstance().getReference();
        recyclerResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter4 = new Result(getActivity(), listResult);
        recyclerResult.setAdapter(adapter4);
        searchView3.clearFocus();
        dialog = new BottomSheetDialog(getContext());
        showdialog();
        permit();

        databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dbhelper candi = dataSnapshot.getValue(dbhelper.class);
                    candi.setCankey(dataSnapshot.getKey());
                    listResult.add(candi);
                }
                adapter4.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView3.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResult(newText);
                return true;
            }
        });

        add_result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permit();
                dialog.show();
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

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

    private void permit() {
        FirebaseDatabase.getInstance().getReference().child("Permission").child("NhdteDlOLesWE").child("bool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b = snapshot.getValue(Boolean.class);
                if (b == true){
                    publish.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                }else {
                    publish.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error retrieving data", error.toException());

            }
        });
    }

    private void showdialog() {
        View view = getLayoutInflater().inflate(R.layout.activity_myadapter, null, false);
        publish = view.findViewById(R.id.publishresult);
        cancel = view.findViewById(R.id.cancelresult);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                permission = true;
                ResultPermission rp = new ResultPermission(permission);
                DReference.child("Permission").child("NhdteDlOLesWE").setValue(rp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Now result published to the voter!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                permission = false;
                ResultPermission rp = new ResultPermission(permission);
                DReference.child("Permission").child("NhdteDlOLesWE").setValue(rp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Published results are canceled..", Toast.LENGTH_SHORT).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("users");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("permit").setValue(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("Error", databaseError.toString());
                                }
                            });

                            DatabaseReference reference1 = database.getReference("candidates");
                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("votecount").setValue(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("Error", databaseError.toString());
                                }
                            });
                        }
                    }
                });
                Intent intent = new Intent(getContext(), adminHome.class);
                startActivity(intent);
            }
        });
        dialog.setContentView(view);
    }

    public void searchResult(String cText) {
        ArrayList<dbhelper> sl = new ArrayList<>();
        for (dbhelper dh : listResult){
            if (dh.getCan_name().toLowerCase().contains(cText.toLowerCase())){
                sl.add(dh);
            }
        }
        adapter4.searchcanlist2(sl);
    }

}