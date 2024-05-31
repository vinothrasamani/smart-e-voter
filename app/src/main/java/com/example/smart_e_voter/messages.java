package com.example.smart_e_voter;

import static androidx.core.app.ActivityCompat.recreate;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class messages extends RecyclerView.Adapter<messages.MyViewholder> {
    Context context;
    ArrayList<dataholder> list;
    String mckey;

    public messages(Context context, ArrayList<dataholder> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.messages,parent,false);
        return new MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        dataholder user = list.get(position);
        holder.msg.setText(user.getMsg());
        holder.msgcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mckey = user.getMsgkey();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments");
                reference.child(mckey).removeValue();
                Intent intent = new Intent(context, viewcomments.class);
                context.startActivity(intent);
                NotificationHelper.sendNotification(context, "Online Smart Voter", "Message deleted Successfully...");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewholder extends RecyclerView.ViewHolder{
        TextView msg;
        ImageView msgcancel;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msgs);
            msgcancel = itemView.findViewById(R.id.msgcancel);
        }
    }

}
