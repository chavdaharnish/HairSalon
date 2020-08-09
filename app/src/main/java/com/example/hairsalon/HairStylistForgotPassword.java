package com.example.hairsalon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class HairStylistForgotPassword extends AppCompatActivity {

    EditText newpass , renewpass;
    Button accept;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_stylist_forgot_password);

        newpass = findViewById(R.id.newpass);
        renewpass = findViewById(R.id.renewpass);
        accept = findViewById(R.id.passaccept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPass = newpass.getText().toString();
                String reNewPass = renewpass.getText().toString();

                if (TextUtils.isEmpty(newPass)) {
                    newpass.setError("Fill It");
                    newpass.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(reNewPass)) {
                    renewpass.setError("Fill It");
                    renewpass.requestFocus();
                    return;
                }
                if (newPass.length() < 6) {
                    newpass.setError("Too Short");
                    newpass.requestFocus();
                    return;
                }
                if (newPass.equals(reNewPass)) {
                    try {
                        changePass(newPass);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(HairStylistForgotPassword.this, "Successfully Changed", Toast.LENGTH_LONG).show();
                    Regist_Act.sp.edit().putBoolean("logged",false).apply();
                    startActivity(new Intent(HairStylistForgotPassword.this, OwnerLogin.class));
                    finish();
                }
            }
        });

    }
    protected void changePass(String Pass) throws NoSuchAlgorithmException {

        final String pass = GeneralFunctions.hex(Pass);
        String username = OwnerLogin.username.getText().toString();
        if (!TextUtils.isEmpty(username)) {
            databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(username).child("Info");
            HashMap map = new HashMap();
            map.put("pass", pass);
            databaseReference.updateChildren(map);
            /*databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userprofile userprofile = new userprofile();
                    userprofile.setPass(pass);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        }
    }
}
