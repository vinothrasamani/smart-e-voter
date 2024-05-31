package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Result extends RecyclerView.Adapter<Result._MyViewholder> {

    Context context;
    long c;
    ArrayList<dbhelper> list;

    public Result(Context context, ArrayList<dbhelper> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public Result._MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_result,parent,false);
        return new Result._MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Result._MyViewholder holder, int position) {
        dbhelper user = list.get(position);
        Glide.with(context).load(user.getImg()).into(holder.myimg);
        holder.name.setText(user.getCan_name());
        holder.party.setText(user.getParty_name());
        c = user.getVotecount();
        holder.counts.setText(" " + c + " " );

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchcanlist2(ArrayList<dbhelper> li){
        list = li;
        notifyDataSetChanged();
    }

    public static class _MyViewholder extends RecyclerView.ViewHolder{

        TextView name, party, counts;
        ImageView myimg;
        public _MyViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Result_symbol_Name_);
            party = itemView.findViewById(R.id.Result_party_name_);
            myimg = itemView.findViewById(R.id.Result_party_symbol_);
            counts = itemView.findViewById(R.id.display_count);
        }
    }
}