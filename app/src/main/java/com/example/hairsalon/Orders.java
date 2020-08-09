package com.example.hairsalon;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Orders extends AppCompatActivity implements CustomAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<OrderClass> mUploads;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        final TextView textView = findViewById(R.id.displayDetails);
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mAdapter = new CustomAdapter(Orders.this, Orders.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(Orders.this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(Main3Activity.key).child("Requests");

        if(Main3Activity.value!=null && Main3Activity.value.equals("order")){
            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mUploads.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        OrderClass upload = postSnapshot.getValue(OrderClass.class);

                        assert upload != null;
                        if (upload.getStatusofBooking() != null && upload.getStatusofBooking().equals("Accepted")) {

                            upload.getCustomerName();
                            upload.getCustomerEmail();
                            upload.getDateAndTime();
                            upload.getPrice();
                            upload.getHairStyle();
                            mUploads.add(upload);
                            count++;

                        }
                    }
                    if (count > 0) {
                        textView.setText("Your Orders");
                    }
                    if (count == 0) {
                        textView.setText("No Orders Yet");
                    }

                    mAdapter.notifyDataSetChanged();
                    mProgressCircle.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Orders.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            });
        }


        if(Main3Activity.value!=null && Main3Activity.value.equals("request")) {

            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mUploads.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        OrderClass upload = postSnapshot.getValue(OrderClass.class);
                        if (upload.getStatusofBooking() != null && upload.getStatusofBooking().equals("Deny")) {

                            upload.getCustomerName();
                            upload.getCustomerEmail();
                            upload.getDateAndTime();
                            upload.getPrice();
                            upload.getHairStyle();
                            mUploads.add(upload);
                            count++;

                        }

                    }
                    if (count == 0) {
                        textView.setText("No new Request found");
                    }
                    if (count > 0) {
                        textView.setText("Pending REQUESTS");
                    }


                    mAdapter.notifyDataSetChanged();
                    mProgressCircle.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Orders.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
