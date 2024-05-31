package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class optionlist extends RecyclerView.Adapter<optionlist._MyViewholder> {

    private Context context;
    private ArrayList<dbhelper> list;
    private VoteClickListener voteClickListener; // Interface instance

    // Interface definition for handling vote clicks
    public interface VoteClickListener {
        void onVoteClicked(String personKey, long voteCount, String candidateName);
    }

    // Constructor to pass context, list, and interface instance
    public optionlist(Context context, ArrayList<dbhelper> list, VoteClickListener voteClickListener) {
        this.context = context;
        this.list = list;
        this.voteClickListener = voteClickListener;
    }

    @NonNull
    @Override
    public optionlist._MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_optionlist, parent, false);
        return new _MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull optionlist._MyViewholder holder, int position) {
        dbhelper user = list.get(position);
        Glide.with(context).load(user.getImg()).into(holder.myimg);
        holder.name.setText(user.getCan_name());
        holder.party.setText(user.getParty_name());

        holder.pid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbhelper user = list.get(holder.getAdapterPosition());
                voteClickListener.onVoteClicked(user.getCankey(), user.getVotecount() + 1, user.getCan_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchcanlist1(ArrayList<dbhelper> li) {
        list = li;
        notifyDataSetChanged();
    }

    public static class _MyViewholder extends RecyclerView.ViewHolder {

        TextView name, party;
        ImageView myimg;
        Button pid;

        public _MyViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id._symbol_Name);
            party = itemView.findViewById(R.id._party_name);
            myimg = itemView.findViewById(R.id.party_symbol);
            pid = itemView.findViewById(R.id.pID);
        }
    }
}
