package com.example.hairsalon;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class OwnerLogin extends AppCompatActivity {

    static EditText login,password;
    Button signIn;
    ImageButton newuser;
    TextView forgotpassword;
    DatabaseReference databaseReference;
    private ValueEventListener destroy;
    int counter = 4;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    static EditText username;
    EditText otp;
    // String emailForForgotPassword;
   // SharedPreferences sp;

    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    TextView textView;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        initViews();

        login = findViewById(R.id.ownerlogin);
        password = findViewById(R.id.ownerpassword);
        signIn = findViewById(R.id.ownersignIn);
        newuser = findViewById(R.id.ownernewuser);
        forgotpassword = findViewById(R.id.ownerforgotpass);
        progressBar = findViewById(R.id.ownerLoginProgressBar);

        textView = findViewById(R.id.lockingperpose);

        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);
       // sp = getSharedPreferences("login",MODE_PRIVATE);

        /*if(sp.getBoolean("logged",false)){
            startActivity(new Intent(OwnerLogin.this,Main3Activity.class));
        }*/

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(login.getText())){
                    login.setError("Fill It");
                }
                if(TextUtils.isEmpty(password.getText())){
                    password.setError("Fill It");
                }
                else {
                    try {
                        setLogin();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerLogin.this, OwnerSignUP.class));
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OwnerLogin.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_pop_up_owner,null);
                username = mView.findViewById(R.id.username);
                Button submit = mView.findViewById(R.id.btnSubmit);



                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!TextUtils.isEmpty(username.getText())) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(username.getText().toString());
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String mobile = dataSnapshot.child("Info").child("mobile").getValue(String.class);
                                    if (!TextUtils.isEmpty(mobile)) {
                                        sendMobile(mobile);
                                    }
                                    else {
                                        username.setError("username is incorrect or mobile no. was not added by user please contact owner");
                                        username.requestFocus();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            username.setError("Fill IT");
                        }
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void sendMobile(String mobile) {

        if(!TextUtils.isEmpty(mobile)){

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(OwnerLogin.this);
            View mView = getLayoutInflater().inflate(R.layout.activity_pop_up_for_otp,null);

            mBuilder.setCancelable(false);

            otp = mView.findViewById(R.id.otp);
            Button submit = mView.findViewById(R.id.otpsubmit);
            TextView display ;
            display = mView.findViewById(R.id.displayNo);
            String star = mobile.substring(6,10);
            display.setText("OTP Send to : ******" + star);
            sendVerificationCode(mobile);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String code = otp.getText().toString().trim();
                    if (code.isEmpty() || code.length() < 6) {
                        otp.setError("Enter valid code");
                        otp.requestFocus();
                        return;
                    }

                    //verifying the code entered manually
                    verifyVerificationCode(code);


                }
            });

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();

        }
    }

    private void sendVerificationCode(String mobile) {



        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
           // EditText otp = findViewById(R.id.otp);
           String code = phoneAuthCredential.getSmsCode();


            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
               otp.setText(code);
                //verifying the code
               verifyVerificationCode(code);
            }
            mAuth.signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OwnerLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OwnerLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            //verification successful we will start the profile activity
                            Intent intent = new Intent(OwnerLogin.this, HairStylistForgotPassword.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(OwnerLogin.this,OwnerLogin.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void setLogin() throws NoSuchAlgorithmException {

        final String email = login.getText().toString();
        String Pass = password.getText().toString();
        final String pass = GeneralFunctions.hex(Pass);
        final int[] count = {0};

        databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST");
        destroy = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot match = null;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot data : children){
                    String Email =  data.child("Info").child("email").getValue(String.class);
                    if(Email!=null && Email.equals(email)){
                        match = data;
                        count[0] = 1;
                        break;
                    }
                }

                if(match!=null) {

                    String Pass = match.child("Info").child("pass").getValue(String.class);

                    if (Pass != null && Pass.equals(pass)) {

                        GeneralFunctions.saveEmail(email,OwnerLogin.this);
                        GeneralFunctions.savePassword(pass,OwnerLogin.this);
                        Toast.makeText(OwnerLogin.this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OwnerLogin.this, Main3Activity.class));
                        finish();

                        // sp.edit().putBoolean("logged",true).apply();

                    }
                    else if (counter == 0)
                    // Disable button after 5 failed attempts
                    {   signIn.setEnabled(false);

                        Toast alert = Toast.makeText(OwnerLogin.this, "Too Many Failed Attempts\nLogin Disabled For 5 mins ", Toast.LENGTH_LONG);
                        alert.show();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {   @Override
                        public void run()
                        {   signIn.setEnabled(true);
                            counter = 2;
                        }
                        }, 300000);
                    }
                    else {
                        //Toast.makeText(OwnerLogin.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        counter--;
                        password.setError("Incorrect Password");
                    }
                }
                /*else
                // Wrong password
                {   Toast alert = Toast.makeText(OwnerLogin.this, "Wrong Credentials", Toast.LENGTH_SHORT);
                    alert.show();
                    counter--;
                };*/
                else{
                    //Toast.makeText(OwnerLogin.this, "First Register Your Account", Toast.LENGTH_LONG).show();
                    login.setError("Fill Correct Data First");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void initViews(){

        GeneralFunctions generalFunctions = new GeneralFunctions();
        if (generalFunctions.getEmail(this) != null && !generalFunctions.getEmail(this).equals("")){

            dialogueBox();
            //authenticateApp();
            /*Intent intent = new Intent(OwnerLogin.this, Main3Activity.class);
            startActivity(intent);
            finish();*/
        }else{

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(destroy!=null) {
            databaseReference.removeEventListener(destroy);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Regist_Act.sp.edit().putBoolean("logged",false).apply();
        startActivity(new Intent(OwnerLogin.this,Regist_Act.class));
        finish();
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
                    startActivity(new Intent(OwnerLogin.this,Main3Activity.class));
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


    public void dialogueBox(){

        builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message) .setTitle("HAIRSTLIST");
        builder.setMessage("Do you want to continue as : "+ GeneralFunctions.getEmail(this))
                .setCancelable(false)
                .setPositiveButton("Login continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Regist_Act.sp.edit().putBoolean("logged",true).apply();
                        authenticateApp();
                        //startActivity(new Intent(FingerPrint.this,HairStyle.class));
                        //finish();

                    }
                })
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        Regist_Act.sp.edit().putBoolean("logged",false).apply();
                        GeneralFunctions.savePassword("",OwnerLogin.this);
                        GeneralFunctions.saveEmail("",OwnerLogin.this);
                        Intent intent = new Intent(OwnerLogin.this , Regist_Act.class);
                        startActivity(intent);
                        finish();


                    }
                });

        AlertDialog alert = builder.create();

        alert.setTitle("HAIRSTYLIST");
        alert.show();

    }


}
