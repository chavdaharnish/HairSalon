package com.example.hairsalon;

import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class FingerPrint extends AppCompatActivity {

    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    public static String name ;
    public static String value;
    public static String email;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference databaseReference = NewUser.databaseReference;
    public TextView fname;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        textView = findViewById(R.id.texview);
        email = getInstance().getCurrentUser().getEmail();

        fname = findViewById(R.id.name);

        fname = findViewById(R.id.name);

        builder = new AlertDialog.Builder(this);

        email = getInstance().getCurrentUser().getEmail();

        databaseReference = FirebaseDatabase.getInstance().getReference("CUSTOMER").child("New User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot match = null;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot data : children){
                    String Email =  data.child("Email").getValue(String.class);
                    if(Email!=null && Email.equals(email)){
                        match = data;
                        break;
                    }
                }
                if(match != null) {
                    value = match.child("Name").getValue(String.class);
                    if(value != null) {
                        fname.setText(value);
                    }
                    else {
                        Toast.makeText(FingerPrint.this,"Contact App Developer",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(FingerPrint.this,"Contact App Developer",Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);
        builder.setMessage("Do you want to login hair saloon ?")
                .setCancelable(false)
                .setPositiveButton("Login continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        authenticateApp();
                        //startActivity(new Intent(FingerPrint.this,HairStyle.class));
                        //finish();

                    }
                })
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button

                        getInstance().signOut();
                        Intent intent = new Intent(FingerPrint.this , Regist_Act.class);
                        startActivity(intent);
                        finish();


                    }
                });

        AlertDialog alert = builder.create();

        alert.setTitle("Hair Saloon");
        alert.show();


        /*startActivity(new Intent(FingerPrint.this,HairStyle.class));
        finish();*/


        //authenticateApp();


    }

    private void authenticateApp() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {
                    textView.setText(getResources().getString(R.string.setting_label));
                }
            }
        }
        //startActivity(new Intent(FingerPrint.this,HairStyle.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    textView.setText(getResources().getString(R.string.unlock_success));
                    onBackPressed();
                    startActivity(new Intent(FingerPrint.this,HairStyle.class));
                    //finish();
                } else {
                    textView.setText(getResources().getString(R.string.unlock_failed));
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                if (isDeviceSecure()) {
                    Toast.makeText(this, getResources().getString(R.string.device_is_secure), Toast.LENGTH_SHORT).show();
                    authenticateApp();
                } else {
                    textView.setText(getResources().getString(R.string.security_device_cancelled));
                }

                break;
        }
    }
    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}