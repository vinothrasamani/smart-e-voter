package com.example.smart_e_voter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;

public class entry extends RecyclerView.Adapter<entry.MyViewholder> {
    Context context;
    ArrayList<store_dp> list;

    public entry(Context context, ArrayList<store_dp> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.entrydetails,parent,false);
        return new MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        store_dp user = list.get(position);
        holder.name.setText(user.getName());
        holder.addr.setText(user.getAddress());

        holder.mycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, updatevoter.class);
                intent.putExtra("vname", user.getName());
                intent.putExtra("vage", user.getAge());
                intent.putExtra("vphone", user.getPhone());
                intent.putExtra("vaddr", user.getAddress());
                intent.putExtra("vkey", user.getMykey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchvoterlist(ArrayList<store_dp> li){
        list = li;
        notifyDataSetChanged();
    }

    public static class MyViewholder extends RecyclerView.ViewHolder{

        TextView name, addr;
        CardView mycard;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nametext);
            addr = itemView.findViewById(R.id.textaddr);
            mycard = itemView.findViewById(R.id.mycard);
        }
    }

}
