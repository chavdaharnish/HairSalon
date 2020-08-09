package com.example.hairsalon;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class SearchingActivity extends AppCompatActivity {
    SearchView searchView;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    int count = 0;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.lv1);
        itemList = new ArrayList<>();

        databaseReference = getInstance().getReference("Customer").child("New User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    count++;
                    SearchingDatabase info = data.getValue(SearchingDatabase.class);
                    itemList.add(info.Hairstyle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(itemList.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(SearchingActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}

/*

databaseReference = getInstance().getReference("CUSTOMER").child("Details").child(Frag1.id);
        databaseReference.addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot match = null;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot data : children) {
                    String tableno = data.child("Place").child("tableno").getValue(String.class);
                    String demo = table_no.getText().toString();
                    if (tableno != null && tableno.equals(table_no.getText().toString())) {
                        match = data;
                    }
                }
        Iterable<DataSnapshot> foodList = dataSnapshot.child("Food Name").getChildren();
        List<FullDatabse> fullDatabses = new ArrayList<>();
        List<String> itemList = new ArrayList<>();
        StringBuilder itemString = new StringBuilder("");
        StringBuilder quantityString = new StringBuilder("");
        StringBuilder priceString = new StringBuilder("");
        List<Integer> quantityList = new ArrayList<>();
        List<String> priceList = new ArrayList<>();
        List<Integer> itemPriceList = new ArrayList<>();
        int sum = 0;
        for (DataSnapshot food : foodList) {
        FullDatabse dt = food.getValue(FullDatabse.class);
        if (dt != null) {
        itemList.add(dt.getFoodname());
        itemString.append(dt.getFoodname() + "\n");
        quantityList.add(dt.getQuantity());
        quantityString.append(dt.getQuantity() + "\n");
        priceList.add(getPriceStr(dt.getPrice()) + "\n");
        String price = getPriceStr(dt.getPrice());
        Integer finalPrice = Integer.parseInt(price) * dt.getQuantity();
        itemPriceList.add(finalPrice);
        sum += finalPrice;
        priceString.append(finalPrice + "\n");
        fullDatabses.add(dt);
        }
        }
        for(FullDatabse data : fullDatabses){
                item.setText(data.getFoodname());
                quantity.;
                }

        System.out.println(itemString);
        String finalName = itemString.toString();
        item.setText(finalName);
        quantity.setText(quantityString);
        price.setText(priceString);
        Total.setText(new Integer(sum).toString());
        databaseReference = getInstance().getReference("CUSTOMER").child("Details").child(Frag1.id).child("Amount");
        int total = Integer.parseInt(Total.getText().toString().trim());
        Cust_loginData database = new Cust_loginData(total);
        databaseReference.setValue(database);
        }

@Override
public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });*/
