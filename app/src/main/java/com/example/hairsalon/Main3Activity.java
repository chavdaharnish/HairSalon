package com.example.hairsalon;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.security.NoSuchAlgorithmException;

public class Main3Activity extends AppCompatActivity {
    Button EditHairStyle;
  //  private static final int GALLERY_INTENT_CODE = 1023;
    FirebaseFirestore fstore;
    ImageView ProfileImage;
    TextView name;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    static String key = null;

    static String fname,mobile,city,dob,uri;
    static String address,email;
    Button showUpload;

    Button UpdatProfileInfo;
    public EditText PName,PMobile,PCity,PAddress,PDOB;
    DatabaseReference UpdateProfileRef;
    DatabaseReference removeProfileRef;
    private ValueEventListener destroy,destroy2;
    //for app lock
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    private TextView textView;

    //for orders request and expired orders
    TextView order,request,expiredOrder;
    static String value = null;
    /*private String ocount=0;
    private String rcount ;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main3 );

        textView = findViewById(R.id.locktext);
        textView.setVisibility(View.GONE);

        EditHairStyle=findViewById ( R.id.btnadd );
        PName=findViewById ( R.id.profileinfoname );
        PMobile=findViewById ( R.id.profileinfomobile );
        PCity=findViewById ( R.id.profileinfocity );
        PAddress=findViewById ( R.id.profileinfoaddress );
        PDOB=findViewById ( R.id.profileinfodob );
        UpdatProfileInfo=findViewById ( R.id.updateprofileinfo );
        showUpload = findViewById(R.id.text_view_show_uploads);
        ProfileImage=findViewById ( R.id.ProfilePic );
        name = findViewById(R.id.fname);
        order = findViewById(R.id.orders);
        request = findViewById(R.id.request);
        //expiredOrder = findViewById(R.id.expiredOrders);


        fstore=FirebaseFirestore.getInstance();
        findUser();

        email = GeneralFunctions.getEmail(this);

        final DatabaseReference mDatabaseRef;

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST");

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    assert key != null;
                    Iterable<DataSnapshot> requestList = dataSnapshot.child(key).child("Requests").getChildren();
                    /*DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    // get current date time with Date()
                    Date date = new Date();
                    String dt = dateFormat.format(date);

                    String current_Month = String.valueOf(dt.charAt(3))
                            ,currentDay_of_month = String.valueOf(dt.charAt(0)),current_hourOfDay = String.valueOf(dt.charAt(11));


                    for(DataSnapshot data : requestList) {
                        OrderClass upload = data.getValue(OrderClass.class);
                        assert upload != null;
                        int i = 0;
                        String dataBaseDate = upload.getDateAndTime();

                        String date1 = String.valueOf(dataBaseDate.charAt(0));
                        String date2 = String.valueOf(dataBaseDate.charAt(1));
                        String date6 = date1+date2;
                        String month = String.valueOf(dataBaseDate.charAt(3));
                        String hour = String.valueOf(dataBaseDate.charAt(11));

                        if(month.equals(current_Month))
                        {
                            if(date6.equals(currentDay_of_month))
                            {
                                if(current_hourOfDay>hour)
                                {
                                    //Remove Entry;
                                }
                            }
                            if(date6<currentDay_of_month)
                            {
                                //Remove Entry;
                            }
                        }
                        if(month<current_Month)
                        {
                            //Remove Entry;
                        }
                    }*/

                    int ordersCount = 0;
                    int requestCount = 0;
                    Iterable<DataSnapshot> orderList = dataSnapshot.child(key).child("Requests").getChildren();
                    for (DataSnapshot data1 : orderList) {
                        OrderClass upload = data1.getValue(OrderClass.class);
                        assert upload != null;
                        if (upload.getStatusofBooking() != null && upload.getStatusofBooking().equals("Accepted")) {

                            ordersCount++;

                        } else if (upload.getStatusofBooking() != null && upload.getStatusofBooking().equals("Deny")) {

                            requestCount++;
                        }
                    }
                    order.setText("Orders\n\t\t\t\t" + ordersCount);
                    request.setText("Requests\n\t\t\t\t\t" + requestCount);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = "order";
                Intent intent = new Intent(Main3Activity.this,Orders.class);
                startActivity(intent);
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = "request";
                Intent intent = new Intent(Main3Activity.this,Orders.class);
                startActivity(intent);
            }
        });



        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main3Activity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_pop_up_profile_image,null);
                final ImageView dp = mView.findViewById(R.id.dp);
                Button change = mView.findViewById(R.id.change);
                TextView name = mView.findViewById(R.id.hname);
                Button remove = mView.findViewById(R.id.remove);

                if(uri!=null) {
                    Picasso.get().load(uri).into(dp);
                    //dp.setImageURI(Uri.parse(uri));
                }
                if(name!=null){
                    name.setText(PName.getText().toString());
                }
                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent OpenGalleryIntent =new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                        startActivityForResult (OpenGalleryIntent,1000);
                    }
                });
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeProfilepic();
                    }
                });


                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });


        showUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main3Activity.this,ImageActivity.class));
            }
        });


        UpdateProfileRef = FirebaseDatabase.getInstance ().getReference ("HAIRSTYLIST");

        UpdatProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                city = PCity.getText().toString().trim();
                mobile = PMobile.getText().toString().trim();
                fname = PName.getText().toString().trim();
                address = PAddress.getText().toString().trim();
                dob = PDOB.getText().toString().trim();

                if (TextUtils.isEmpty(address)){
                    PAddress.setError("Enter Address");
                    return;
                }
                if(TextUtils.isEmpty(fname)){
                    PName.setError("Enter Full Name");
                }
                if (TextUtils.isEmpty(city)) {
                    PCity.setError("Enter Email");
                    return;
                }
                if (TextUtils.isEmpty(mobile)) {
                    PMobile.setError("Enter Mobile");
                    return;
                }
                if (TextUtils.isEmpty(dob)) {
                    PDOB.setError("Enter DOB");
                    return;
                }
                if (mobile.length() != 10) {
                    PMobile.setError("Enter Valid Mobile No.");
                }
                else {
                    addUser();
                }
            }
        });




        storageReference= FirebaseStorage.getInstance ().getReference ();
        /*StorageReference profileRef=storageReference.child ("users/"+key+"Profile.jpg");
      //  StorageReference profileRef=storageReference.child ("Profile.jpg");
        profileRef.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> ( ) {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get ().load ( uri ).into ( ProfileImage );
            }
        } );*/


        EditHairStyle.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main3Activity.this,ImageUploadAct.class);
                startActivity ( intent );
            }
        } );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ownerlogout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                Regist_Act.sp.edit().putBoolean("logged",false).apply();
                GeneralFunctions.savePassword("",this);
                GeneralFunctions.saveEmail("",this);
                startActivity(new Intent(this,Regist_Act.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeProfilepic() {

        removeProfileRef = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(key);
        removeProfileRef.child("imageuri").removeValue();
        Toast.makeText(Main3Activity.this,"Removed",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Main3Activity.this,Main3Activity.class));
    }

    /*private void UploadImageToFirebase(final Uri imageUri) {

        //uploadimage to firebaseStorage

        final StorageReference fileRef=storageReference.child ( "HAIRSTYLIST/"+key+"Profile.jpg" );
        fileRef.putFile (imageUri).addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> ( ) {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText (Main3Activity.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> ( ) {
                    @Override
                    public void onSuccess(Uri uri) {

                       if(key!=null){
                           databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(key);
                           Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                           while (!urlTask.isSuccessful());
                           Uri downloadUrl = urlTask.getResult();
                           userprofile database = new userprofile();
                           database.setImageUrl(downloadUrl.toString());
                           databaseReference.child("imageuri").setValue(database);
                           Picasso.get().load(downloadUrl).into(ProfileImage);
                        }

                    }
                } );
            }
        }).addOnFailureListener (new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText ( Main3Activity.this,"Failed",Toast.LENGTH_SHORT ).show ();
            }
        });

    }*/

    public void findUser(){

        databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST");

        destroy = databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot data : children) {
                    String Email = data.child("Info").child("email").getValue(String.class);
                    if (Email != null && Email.equals(GeneralFunctions.getEmail(Main3Activity.this))) {
                        key = data.getKey();
                        if (key != null) {

                            String Name = data.child("Info").child("name").getValue(String.class);
                            String Mobile = data.child("Info").child("mobile").getValue(String.class);
                            String Address = data.child("Info").child("address").getValue(String.class);
                            String DOB = data.child("Info").child("dob").getValue(String.class);
                            String City = data.child("Info").child("city").getValue(String.class);
                            uri = data.child("imageuri").child("imageUrl").getValue(String.class);
                            if (uri != null) {
                                Picasso.get().load(uri).into(ProfileImage);
                            }
                            if (Name != null) {
                                name.setText(Name);
                                PName.setText(Name);
                                fname = Name;
                            }
                            if (Mobile != null) {
                                PMobile.setText(Mobile);
                                mobile = Mobile;
                            }
                            if (DOB != null) {
                                PDOB.setText(DOB);
                            }
                            if (Address != null) {
                                PAddress.setText(Address);
                                address = Address;
                            }
                            if (City != null) {
                                PCity.setText(City);
                                city = City;
                            } else {
                                PMobile.setError("Update it");
                                PDOB.setError("Update it");
                                PAddress.setError("Update it");
                                PCity.setError("Update it");
                            }

                        }

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addUser(){

        destroy2 = UpdateProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot data : children) {
                    String Email = data.child("Info").child("email").getValue(String.class);
                    if (Email != null && Email.equals(GeneralFunctions.getEmail(Main3Activity.this))) {
                        key = data.getKey();
                        try {
                            addProfileInfo();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addProfileInfo() throws NoSuchAlgorithmException {

        if(key!=null) {

            UpdateProfileRef = FirebaseDatabase.getInstance ().getReference ("HAIRSTYLIST").child(key).child("Info");

            String name = PName.getText().toString();
            String mobile = PMobile.getText().toString();
            String city = PCity.getText().toString();
            String address = PAddress.getText().toString();
            String dob = PDOB.getText().toString();
            String Pass = GeneralFunctions.getPassword(this);
            String email = GeneralFunctions.getEmail(this);
            String pass = GeneralFunctions.hex(Pass);
            userprofile database = new userprofile();
            database.setName(name);
            database.setMobile(mobile);
            database.setEmail(email);
            database.setAddress(address);
            database.setDob(dob);
            database.setPass(pass);
            database.setCity(city);
            UpdateProfileRef.setValue(database);
            Toast.makeText(this,"Successfully Updated",Toast.LENGTH_LONG).show();

        }
        else {
            PCity.setError("Something Went Wrong");
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (destroy != null) {
            databaseReference.removeEventListener(destroy);
        }
        if (destroy2 != null) {
            UpdateProfileRef.removeEventListener(destroy2);
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Main3Activity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    //for upload profile pic
        if(requestCode==1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                //  ProfileImage.setImageURI(imageUri);

                UploadImageToFirebase(imageUri);
            }
        }
    }

    private void UploadImageToFirebase(final Uri imageUri) {

        //uploadimage to firebaseStorage

        final StorageReference fileRef=storageReference.child ( "HAIRSTYLIST/"+key+"Profile.jpg" );
        fileRef.putFile (imageUri).addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot>( ) {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText (Main3Activity.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> ( ) {
                    @Override
                    public void onSuccess(Uri uri) {

                        if(key!=null){
                            databaseReference = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(key);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            userprofile database = new userprofile();
                            database.setImageUrl(downloadUrl.toString());
                            databaseReference.child("imageuri").setValue(database);
                            Picasso.get().load(downloadUrl).into(ProfileImage);
                        }

                    }
                } );
            }
        }).addOnFailureListener (new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText ( Main3Activity.this,"Failed",Toast.LENGTH_SHORT ).show ();
            }
        });

    }
}
