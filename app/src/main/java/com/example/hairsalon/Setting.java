package com.example.hairsalon;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class Setting extends AppCompatActivity {

    private  static final String TAG = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ListView list = (ListView)findViewById(R.id.theList);
        Log.d(TAG,"OnCreate: Started");
        ArrayList <String> names = new ArrayList<>();
        names.add("Logout");

        ArrayAdapter adapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1,names);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Setting.this , Regist_Act.class);
                startActivity(intent);

            }
        });

    }
}
