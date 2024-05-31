package com.example.smart_e_voter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.TimeUnit;

public class qr_code_scanner extends AppCompatActivity {

    private Button scan, alert;
    String no, user;
    Boolean per = false;
    BottomSheetDialog dialog;
    DatabaseReference databaseReference, dbr;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(qr_code_scanner.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        scan = (Button) findViewById(R.id.scancode);
        dialog = new BottomSheetDialog(this);
        showdialog();

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator =new IntentIntegrator(qr_code_scanner.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan Qr code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult intentResult =IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult != null){
            String contents =intentResult.getContents();
            if (contents != null){
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle("QRCode Verification...");
                pd.show();
                dbr = FirebaseDatabase.getInstance().getReference("users");
                dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pd.setMessage("Searching Details..");
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            user = dataSnapshot.getKey();
                            if (user.trim().equals(contents.trim())){
                                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.trim());
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            no = snapshot.child("phone").getValue(String.class);
                                            per = snapshot.child("permit").getValue(Boolean.class);
                                            if (per) {
                                                pd.show();
                                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                        "+91" + no,
                                                        60,
                                                        TimeUnit.SECONDS,
                                                        qr_code_scanner.this,
                                                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                            @Override
                                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                                pd.dismiss();
                                                                Toast.makeText(qr_code_scanner.this, "Verification completed..", Toast.LENGTH_SHORT).show();
                                                            }
                                                            @Override
                                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                                pd.dismiss();
                                                                Toast.makeText(qr_code_scanner.this, "Error please check internet connection..", Toast.LENGTH_SHORT).show();
                                                            }
                                                            @Override
                                                            public void onCodeSent(@NonNull String vs, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                                pd.dismiss();
                                                                Intent intent = new Intent(qr_code_scanner.this, one_time_password.class);
                                                                intent.putExtra("mobile", no.trim().toString());
                                                                intent.putExtra("backotp", vs);
                                                                intent.putExtra("person", user);
                                                                startActivity(intent);
                                                                Toast.makeText(qr_code_scanner.this, "Code send to your phone.", Toast.LENGTH_SHORT).show();
                                                                //sara : 6382833135
                                                            }
                                                        }
                                                );
                                            }else {
                                                dialog.show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(qr_code_scanner.this, "Having error on getting user mobile number", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                pd.dismiss();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(qr_code_scanner.this, "Error", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    private void showdialog() {
        View view = getLayoutInflater().inflate(R.layout.alertmsg, null, false);
        alert = view.findViewById(R.id.alert);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(qr_code_scanner.this, voterHome.class);
                startActivity(intent);
            }
        });
        dialog.setContentView(view);
    }
}