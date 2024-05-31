package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class can_entry extends RecyclerView.Adapter<can_entry._MyViewholder> {

    Context context;
    ArrayList<dbhelper> list;

    public can_entry(Context context, ArrayList<dbhelper> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public can_entry._MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_can_entry,parent,false);
        return new _MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull can_entry._MyViewholder holder, int position) {
        dbhelper user = list.get(position);
        Glide.with(context).load(user.getImg()).into(holder.myimg);
        holder.name.setText(user.getCan_name());
        holder.party.setText(user.getParty_name());

        holder.cardcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, updatecan.class);
                intent.putExtra("cname", user.getCan_name());
                intent.putExtra("cpn", user.getParty_name());
                intent.putExtra("cimg", user.getImg());
                intent.putExtra("ckey", user.getCankey());
                intent.putExtra("ccount", user.getVotecount());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchcanlist(ArrayList<dbhelper> li){
        list = li;
        notifyDataSetChanged();
    }

    public static class _MyViewholder extends RecyclerView.ViewHolder{

        TextView name, party;
        CardView cardcan;
        ImageView myimg;
        public _MyViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id._symbolname);
            party = itemView.findViewById(R.id._partyname);
            cardcan = itemView.findViewById(R.id.cancard);
            myimg = itemView.findViewById(R.id.vote_symbol);
        }
    }
}