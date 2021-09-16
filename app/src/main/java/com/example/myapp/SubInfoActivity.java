package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubInfoActivity extends AppCompatActivity {


    DatabaseReference mSubRef;
    ArrayList<String> subDetails = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private TextView ServiceName;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_info);


        ServiceName = (TextView) findViewById(R.id.subTitile);
        ServiceName.setText(getIntent().getStringExtra("service_id"));

        mSubRef = FirebaseDatabase.getInstance().getReference("Subs");
        list = findViewById(R.id.detailList);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subDetails);
        list.setAdapter(arrayAdapter);

        mSubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = String.valueOf(getIntent().getStringExtra("service_id"));
                    String description = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("service_id"))).child("description").getValue(String.class);
                    String price = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("service_id"))).child("price").getValue(String.class);
                    String date = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("service_id"))).child("date").getValue(String.class);
                    String frequency = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("service_id"))).child("frequency").getValue(String.class);

                    // Set text of textview to subscription name
                    ServiceName.setText(name);

                    //Add sub details to the arraylist
                    subDetails.add("Name:  " + name);
                    subDetails.add("Description:  " + description);
                    subDetails.add("Price:  " + "£" + price);
                    subDetails.add("Date Subscribed:  " + date);
                    subDetails.add("Payment Frequency:  " + frequency);
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_info_menu, menu);
        return true;
    }

    // go to edit sub screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_sub:
                Intent intent = new Intent(SubInfoActivity.this, EditSubActivity.class);
                intent.putExtra("edit_service_name", ServiceName.getText());
                startActivity(intent);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(SubInfoActivity.this, HomeScreenActivity.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
