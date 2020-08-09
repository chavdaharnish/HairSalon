package com.example.hairsalon;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
public class DatePicker extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    DatePickerDialog.OnDateSetListener setListener;
    CalendarView calendarView;
    // Button ScheduleButton;
    DatabaseReference ScheduleRef,QueryRef,Reference,myref,rootRef;
    FirebaseAuth auth;
    public static String Date,Time,DateAndTime,HairStylistId;
    double DisatanceToHairStylist;
    int imageView,i;
    ImageView image;
    TextView name;
    TextView discription;
    TextView namehead;
    Button button;
    public static String recievedName;
    public static int recievedImage;
    public static String recievedEmail;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        //date = findViewById(R.id.date);
        calendarView = findViewById(R.id.CalendarView);
        // ScheduleButton = findViewById(R.id.Schedule);
        Calendar calendar = Calendar.getInstance();
        final int originalyear = calendar.get(Calendar.YEAR);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int originalmonth = calendar.get(Calendar.MONTH);
        final int originalday = calendar.get(Calendar.DAY_OF_MONTH);
        Reference = FirebaseDatabase.getInstance().getReference().child("HAIRSTYLIST");
        auth = FirebaseAuth.getInstance();

        image = (ImageView)findViewById(R.id.image2);
        name  = (TextView)findViewById(R.id.name2);
        discription = (TextView)findViewById(R.id.discription1);
        toolbar =  findViewById(R.id.header);
        //setSupportActionBar(toolbar);


        toolbar.inflateMenu(R.menu.mymenu);
        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.settings) {
                    Intent intent = new Intent(DatePicker.this , Setting.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        Intent intent = getIntent();
        recievedName = intent.getStringExtra("name");
        recievedImage = intent.getIntExtra("image",0);
        recievedEmail = intent.getStringExtra("email");

        name.setText(recievedName);
        image.setImageResource(recievedImage);
        discription.setText(recievedEmail);


        /*DatePickerDialog datePicker = new DatePickerDialog(DatePicker.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
             }
        },year,month,day);
        datePicker.show();*/

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            DialogFragment TimePicker = new TimePickerFragment();
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(year<originalyear)
                {
                    Toast.makeText(DatePicker.this,"You have selected Invalid Date, Please select Another Date",Toast.LENGTH_SHORT).show();
                }
                else if(year == originalyear){
                    if(month<originalmonth)
                    {
                        Toast.makeText(DatePicker.this,"You have selected Invalid Date, Please select Another Date",Toast.LENGTH_SHORT).show();
                    }
                    else if(month==originalmonth){
                        if(dayOfMonth<originalday)
                            Toast.makeText(DatePicker.this,"You have selected Invalid Date, Please select Another Date",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Toast.makeText(DatePicker.this,"Please Select Time...",Toast.LENGTH_SHORT).show();
                            TimePicker.show(getSupportFragmentManager(),"Time Picker");
                            month = month + 1;
                            Date = dayOfMonth + "/" + month + "/" + year;
                        }
                    }
                    else
                    {
                        Toast.makeText(DatePicker.this,"Please Select Time...",Toast.LENGTH_SHORT).show();
                        TimePicker.show(getSupportFragmentManager(),"Time Picker");
                        month = month + 1;
                        Date = dayOfMonth + "/" + month + "/" + year;
                    }
                }
                else
                {
                    Toast.makeText(DatePicker.this,"Please Select Time...",Toast.LENGTH_SHORT).show();
                    TimePicker.show(getSupportFragmentManager(),"Time Picker");
                    month = month + 1;
                    Date = dayOfMonth + "/" + month + "/" + year;
                }
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if((hourOfDay>9 && hourOfDay<14) || (hourOfDay>16 && hourOfDay<22))
        {
            if(minute==0)
            {
                // final int[] i = {0};
                Reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot data : children) {
                            String Mail = data.child("Info").child("email").getValue(String.class);
                            if (Mail != null && Mail.equals(recievedEmail))
                            {
                                HairStylistId = data.getKey();
                                ScheduleRef = FirebaseDatabase.getInstance().getReference().child("HAIRSTYLIST").child(data.getKey());
                                QueryRef = FirebaseDatabase.getInstance().getReference().child("HAIRSTYLIST").child(data.getKey()).child("Requests");
                                myref = FirebaseDatabase.getInstance().getReference().child("HAIRSTYLIST").child(data.getKey()).child("Requests").child(FingerPrint.value);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // DatabaseReference Query = QueryRef.child("Customers");
                Time = hourOfDay + ":" + minute;
                //EndTime = hourOfDay + 1;
                DateAndTime = Date + "\t\t" + Time;
                rootRef = FirebaseDatabase.getInstance().getReference().child("HAIRSTYLIST");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        for(DataSnapshot data : children) {
                            String Mail = data.child("Info").child("email").getValue(String.class);
                            if (Mail != null && Mail.equals(recievedEmail))
                            {
                                if (data.hasChild("Requests")) {
                                    QueryRef.orderByChild("DateAndTime").equalTo(DateAndTime).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists())
                                                Toast.makeText(DatePicker.this,"Sorry,Please Select Other Time Beacuse At this Saloon is Already booked...",Toast.LENGTH_LONG).show();
                                            else
                                            {
                                                i++;
                                                //  myref.child("CustomerName").setValue(FingerPrint.value);
                                                /*myref.child("CustomerEmail").setValue(FingerPrint.email);
                                                //  myref.child("CustomerName").setValue(re);
                                                myref.child("Date").setValue(Date);
                                                // Toast.makeText(DatePicker.this,Date,Toast.LENGTH_SHORT).show();
                                                myref.child("Time").setValue(Time);
                                                myref.child("DateAndTime").setValue(DateAndTime);
                                                myref.child("StatusofBooking").setValue("Deny");*/
                                                OrderClass orderClass = new OrderClass();
                                                orderClass.setCustomerName(FingerPrint.value);
                                                orderClass.setCustomerEmail(FingerPrint.email);
                                                orderClass.setHairStyle(ImageAdapter.printName);
                                                orderClass.setPrice(ImageAdapter.printPrice);
                                                orderClass.setDate(Date);
                                                orderClass.setTime(Time);
                                                orderClass.setDateAndTime(DateAndTime);
                                                orderClass.setStatusofBooking("Deny");
                                                myref.setValue(orderClass);
                                                sendMail1();
                                                Toast.makeText(DatePicker.this,"Congratulations Dear, Your Booking Request is sent to Your Select HairStylist You will be notified by Email If Your is Confirmed .. ",Toast.LENGTH_LONG).show();
                                                // sendMail();
                                                //sendMail2();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                                else
                                {
//                                    //   myref.child("CustomerName").setValue(FingerPrint.value);
//                                    myref.child("CustomerEmail").setValue(FingerPrint.email);
//                                    myref.child("Date").setValue(Date);
//                                    // Toast.makeText(DatePicker.this,Date,Toast.LENGTH_SHORT).show();
//                                    myref.child("Time").setValue(Time);
//                                    myref.child("DateAndTime").setValue(DateAndTime);
//                                    myref.child("StatusofBooking").setValue("Deny");


                                    OrderClass orderClass = new OrderClass();
                                    orderClass.setCustomerName(FingerPrint.value);
                                    orderClass.setCustomerEmail(FingerPrint.email);
                                    orderClass.setDate(Date);
                                    orderClass.setTime(Time);
                                    orderClass.setHairStyle(ImageAdapter.printName);
                                    orderClass.setPrice(ImageAdapter.printPrice);
                                    orderClass.setDateAndTime(DateAndTime);
                                    orderClass.setStatusofBooking("Deny");
                                    myref.setValue(orderClass);

                                    Toast.makeText(DatePicker.this,"Congratulations Dear, Your Booking Request is sent to Your Select HairStylist You will be notified by Email If Your is Confirmed ..  ",Toast.LENGTH_LONG).show();
                                    //sendMail();
                                    //sendMail2();
                                    sendMail();
                                    startActivity(new Intent(DatePicker.this,HairStyle.class));
                                    finish();
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
            else
                Toast.makeText(DatePicker.this,"Please Select Proper Time Like 10:00 Am... ",Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(DatePicker.this,"Please Select Time Between 9:00 AM To 2:00 PM and 4:00 PM To 10:00 PM ",Toast.LENGTH_LONG).show();
    }


    public void sendMail() {
        String mail = recievedEmail;
        JavaMailAPI javaMailAPI = new JavaMailAPI(DatePicker.this , mail);
        javaMailAPI.execute();
    }

    public void sendMail1()
    {
        String mail2 = FingerPrint.email;
        Mail2API mailAPI = new Mail2API(DatePicker.this,mail2);
        mailAPI.execute();
    }

}

