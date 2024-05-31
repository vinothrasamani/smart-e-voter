package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class comments extends AppCompatActivity {

    ImageView sendmsg;
    EditText getmsg;
    TextView displaymsg;
    private Handler handler = new Handler();
    private Runnable runnable;

    DatabaseReference dbr;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(comments.this, voterHome.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        sendmsg = findViewById(R.id.msgsend);
        getmsg = findViewById(R.id.getmsg);
        displaymsg = findViewById(R.id.displaymsg);
        dbr = FirebaseDatabase.getInstance().getReference();

        int interval = 1000;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!getmsg.getText().toString().isEmpty()){
                    sendmsg.setVisibility(View.VISIBLE);
                }
                else {
                    sendmsg.setVisibility(View.INVISIBLE);
                }
                handler.postDelayed(this, interval);
            }
        };
        handler.postDelayed(runnable, interval);

        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getmsg.getText().toString().isEmpty()){
                    String key = dbr.push().getKey();
                    dataholder val = new dataholder(getmsg.getText().toString());
                    dbr.child("comments").child(key).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                displaymsg.setText(getmsg.getText().toString());
                                getmsg.setText("");
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(comments.this, "Enter your Message...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}