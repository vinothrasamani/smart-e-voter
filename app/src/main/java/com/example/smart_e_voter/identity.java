package com.example.smart_e_voter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class identity extends AppCompatActivity {

    TextView n, ad, a, p;
    ImageView img;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(identity.this, adminHome.class);
        startActivity(intent);

        // Call super.onBackPressed() to return to the previous activity
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);

        n = findViewById(R.id.getname);
        ad = findViewById(R.id.getaddr);
        a = findViewById(R.id.getage);
        p = findViewById(R.id.getphone);
        img = findViewById(R.id.qrcodeimg);

        String txt1,txt2,txt3,txt4, txt5;
        txt1 = getIntent().getStringExtra("Keyname");
        txt2 = getIntent().getStringExtra("keyaddr");
        txt3 = getIntent().getStringExtra("keyage");
        txt4 = getIntent().getStringExtra("keyph");
        txt5 = getIntent().getStringExtra("keyid");

        MultiFormatWriter multiFormatWriter= new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(txt5.toString(),
                    BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder =new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            img.setImageBitmap(bitmap);
        }catch (WriterException e){
            throw new RuntimeException(e);
        }

        n.setText(txt1);
        ad.setText(txt2);
        a.setText(txt3);
        p.setText(txt4);

        View rootView = getWindow().getDecorView().getRootView();
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                Intent i = new Intent(identity.this, adminHome.class);
                startActivity(i);
                return true;
            }
            // Return false to allow default back button behavior
            return false;
        });
    }
    public void takeScreenshot(View view) {
        verifyStoragePermissions();
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = "screenshot_" + new Date().getTime() + ".png";
            File file = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Screenshot saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Screenshot saved on : " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("MainActivity", "Error taking screenshot: " + e.getMessage());
            Toast.makeText(this, "Screenshot not saved..", Toast.LENGTH_SHORT).show();
        }
        Intent in = new Intent(identity.this, adminHome.class);
        startActivity(in);
        finish();
    }

    private void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }


}