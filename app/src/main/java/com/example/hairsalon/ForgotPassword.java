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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText email;
    Button send;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.femail);
        send=findViewById(R.id.submit);
        mAuth= FirebaseAuth.getInstance();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailreset = email.getText().toString();
                if (!TextUtils.isEmpty(emailreset)) {
                    mAuth.sendPasswordResetEmail(emailreset).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this, "Send you password reset email check it", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPassword.this, Regist_Act.class));
                            }
                        }
                    });
                }
                else {
                    email.setError("Fill It");
                    email.requestFocus();
                }
            }
        });
    }
}






