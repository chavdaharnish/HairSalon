package com.example.hairsalon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUser extends AppCompatActivity {
    public static  EditText textInputEmail;
    public static  EditText textInputPassword;
    public static  EditText textInputPassword_c, FullName;
    public static String Name;
    private FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_new_user);
        super.onCreate(savedInstanceState);
        textInputEmail = findViewById(R.id.user_eid);
        textInputPassword = findViewById(R.id.ps);
        textInputPassword_c = findViewById(R.id.ps2);
        register = findViewById(R.id.register);
        FullName = findViewById(R.id.name);
        firebaseAuth = FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textInputEmail.getText().toString().trim();
                String password = textInputPassword.getText().toString().trim();
                String password_c = textInputPassword_c.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(NewUser.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(NewUser.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password_c)) {
                    Toast.makeText(NewUser.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(NewUser.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                if (!(password.equals(password_c))) {
                    Toast.makeText(NewUser.this, "Password not match", Toast.LENGTH_SHORT).show();
                }
                if (password.equals(password_c)) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(NewUser.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    addDetail();
                                                    Toast.makeText(NewUser.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(NewUser.this, Regist_Act.class));
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(NewUser.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    private void addDetail() {

        databaseReference = FirebaseDatabase.getInstance().getReference("CUSTOMER").child("New User");

        Name=FullName.getText ().toString ();
        String Email = textInputEmail.getText().toString();
        String id = databaseReference.push().getKey();
        userprofile database=new userprofile ();
        database.setName(Name);
        database.setEmail(Email);
        databaseReference.child(id).setValue ( database );
        //Toast.makeText ( this,"added", Toast.LENGTH_SHORT ).show ();
        //Toast.makeText(NewUser.this, "Please verify your email", Toast.LENGTH_SHORT).show();
    }
}



