package com.example.hairsalon;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.security.NoSuchAlgorithmException;

public class OwnerSignUP extends AppCompatActivity {

    protected EditText textInputEmail;
    protected EditText textInputPassword;
    protected EditText textInputPassword_c, FullName,userName;
    DatabaseReference databaseReference;
    Button register;
    String email,password,uName,fName;
    private boolean flagEmail = false;
    private boolean flagUser = false;
    private ValueEventListener destroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_sign_u_p);

        textInputEmail = findViewById(R.id.owneremail);
        textInputPassword = findViewById(R.id.ownerps);
        textInputPassword_c = findViewById(R.id.ownerps2);
        register = findViewById(R.id.ownersignup);
        FullName = findViewById(R.id.ownername);
        userName = findViewById(R.id.ownerusername);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = textInputEmail.getText().toString().trim();
                password = textInputPassword.getText().toString().trim();
                uName = userName.getText().toString().trim();
                fName = FullName.getText().toString().trim();
                String password_c = textInputPassword_c.getText().toString().trim();

                if (TextUtils.isEmpty(uName)){
                        userName.setError("Enter Username");
                        return;
                    }
                    if(TextUtils.isEmpty(fName)){
                        FullName.setError("Enter Full Name");
                    }
                    if (TextUtils.isEmpty(email)) {
                        //Toast.makeText(OwnerSignUP.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                        textInputEmail.setError("Enter Email");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        //Toast.makeText(OwnerSignUP.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        textInputPassword.setError("Enter Password");
                        return;
                    }
                    if (TextUtils.isEmpty(password_c)) {
                        //Toast.makeText(OwnerSignUP.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                        textInputPassword_c.setError("Confirm Password");
                        return;
                    }
                    if (password.length() < 6) {
                        //Toast.makeText(OwnerSignUP.this, "Password too short", Toast.LENGTH_SHORT).show();
                        textInputPassword.setError("Enter long Password for Security");
                    }

                    if (!(password.equals(password_c))) {
                        //Toast.makeText(OwnerSignUP.this, "Password not match", Toast.LENGTH_SHORT).show();
                        textInputPassword_c.setError("Password not matched");
                    }
                    if (password.length() >= 6 && password.equals(password_c)) {

                        exsistUser();

                    }
            }
        });
    }

    private void addDetail() throws NoSuchAlgorithmException {

        databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST");

        String Name=FullName.getText ().toString ();
        String Email = textInputEmail.getText().toString();
        String Pass = textInputPassword.getText().toString();
        String pass = GeneralFunctions.hex(Pass);
        String Username = userName.getText().toString();
        userprofile database=new userprofile ();
        database.setEmail(Email);
        database.setName(Name);
        database.setPass(pass);
        databaseReference.child(Username).child("Info").setValue ( database );
        Toast.makeText ( this,"added", Toast.LENGTH_LONG ).show ();
        startActivity(new Intent(OwnerSignUP.this,OwnerLogin.class));
        finish();
    }

    private void exsistUser() {

       databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST");
        final String email = textInputEmail.getText().toString();
        final String username = userName.getText().toString();

        destroy = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                changeEmail(false);
                changeUser(false);
                //DataSnapshot match = null;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot data : children){
                    String key = data.getKey();
                    String Email =  data.child("Info").child("email").getValue(String.class);
                    if(key!=null && key.equals(username)){
                        changeUser(true);
                    }
                    if(Email!=null && Email.equals(email)){
                        changeEmail(true);
                    }
                }
                    if (!flagUser) {

                        if (!flagEmail) {
                            try {
                                addDetail();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //Toast.makeText(OwnerSignUP.this, "email already exsist", Toast.LENGTH_LONG).show();
                            textInputEmail.setError("email already exsist");
                        }

                    } else {
                        //Toast.makeText(OwnerSignUP.this, "", Toast.LENGTH_SHORT).show();
                        userName.setError("Change Username");
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void changeEmail(boolean b) {
        flagEmail = b;
    }

    private void changeUser(boolean b) {
        flagUser = b;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,OwnerLogin.class));
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null) {
            databaseReference.removeEventListener(destroy);
        }
    }

}
