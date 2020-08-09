package com.example.hairsalon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Regist_Act extends AppCompatActivity {

    EditText login,pass;
    Button signIn;
    TextView newuser;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_regist_);
        firebaseAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        pass = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        newuser = findViewById(R.id.newuser);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                com.google.firebase.auth.FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null){

                    Toast.makeText(Regist_Act.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Regist_Act.this,FingerPrint.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(Regist_Act.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String email = login.getText().toString().trim();

                    if(email.length()==10){

                        Intent intent = new Intent(Regist_Act.this,OtpVar.class);
                        intent.putExtra("mobile", email);
                        startActivity(intent);

                    }
                    else {

                        String password = pass.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(Regist_Act.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(Regist_Act.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (password.length() < 6) {
                            Toast.makeText(Regist_Act.this, "Password too short", Toast.LENGTH_SHORT).show();
                        }
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Regist_Act.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                                startActivity(new Intent(getApplicationContext(), SearchingActivity.class));
                                                Toast.makeText(Regist_Act.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(Regist_Act.this, "please verify your email first", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(Regist_Act.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
        });
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Regist_Act.this, NewUser.class));
            }
        });

    }
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
