package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class one_time_password extends AppCompatActivity {


    TextView tx;
    EditText notp;
    Button verify;
    String backendotp;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(one_time_password.this, voterHome.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_password);

        tx = findViewById(R.id.textphone);
        notp = findViewById(R.id.enterotp);
        verify = findViewById(R.id.verifybutton);

        tx.setText(String.format("Verification code is send to +91-%s", getIntent().getStringExtra("mobile")));
        backendotp = getIntent().getStringExtra("backotp");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(one_time_password.this);
                pd.setTitle("OTP Verification...");
                pd.show();
                pd.setMessage("Verifying..");
                if (!notp.getText().toString().trim().isEmpty()){
                    if (notp.getText().toString().trim().length() == 6){
                        String entercodeotp = notp.getText().toString();
                        if (backendotp!= null){
                            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                    backendotp, entercodeotp
                            );
                            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                pd.dismiss();
                                                Intent intent = new Intent(one_time_password.this, VOTE.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("personid", getIntent().getStringExtra("person"));
                                                intent.putExtra("num", getIntent().getStringExtra("mobile"));
                                                startActivity(intent);
                                                Toast.makeText(one_time_password.this, "Verified Succssfully!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(one_time_password.this, "Enter correct OTP..", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(one_time_password.this, "Please check internt connection..", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(one_time_password.this, "Please enter six(6) digit OTP..", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(one_time_password.this, "Please Enter the OTP!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.resendotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        one_time_password.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(one_time_password.this, "Error please check internet connection!!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String news, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                backendotp = news;
                                Toast.makeText(one_time_password.this, "OTP send Successfully..", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

    }
}