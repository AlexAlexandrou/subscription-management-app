package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class EditSubActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button SaveChanges;
    private EditText ServiceName;
    private EditText ServiceDescription;
    private EditText ServicePrice;
    private TextView PaymentDate;
    private Spinner PaymentFrequency;

    Subscriptions subs;

    long subId = 0;
    long amount = 0;

    DatabaseReference mSubRef = FirebaseDatabase.getInstance().getReference().child("Subs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sub);

        ServiceName = (EditText) findViewById(R.id.serviceName);
        ServiceDescription = (EditText) findViewById(R.id.serviceDescription);
        ServicePrice = (EditText) findViewById(R.id.servicePrice);
        PaymentDate = (TextView) findViewById(R.id.serviceDate);
        PaymentFrequency = (Spinner) findViewById(R.id.serviceFrequency);

        SaveChanges = (Button) findViewById(R.id.btnSaveChanges);

        Spinner frequency = findViewById(R.id.serviceFrequency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.payment_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(adapter);

        subs = new Subscriptions();

        mSubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //get the details of the current subscription
                    String name = String.valueOf(getIntent().getStringExtra("edit_service_name"));
                    String description = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("edit_service_name"))).child("description").getValue(String.class);
                    String price = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("edit_service_name"))).child("price").getValue(String.class);
                    String date = dataSnapshot.child(String.valueOf(getIntent().getStringExtra("edit_service_name"))).child("date").getValue(String.class);

                    // Set text of textview to subscription name
                    ServiceName.setText(name);

                    //Add sub details to the arraylist
                    ServiceDescription.setText(description);
                    ServicePrice.setText(price);
                    PaymentDate.setText(date);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(EditSubActivity.this, HomeScreenActivity.class);

                String sName = ServiceName.getText().toString();
                String sDescription = ServiceDescription.getText().toString();
                String sPrice = ServicePrice.getText().toString();
                String sDate = PaymentDate.getText().toString();
                String sFrequency = PaymentFrequency.getSelectedItem().toString();

                //check if required fields are empty
                if (sName.equals("") || sPrice.equals("") || sDate.equals("")){
                    Toast.makeText(EditSubActivity.this, "Please fill the required fields in order to continue", Toast.LENGTH_SHORT).show();
                }
                //check if service name field contain invalid Firebase characters
                else if (sName.contains(".") || sName.contains("#") || sName.contains("$") ||
                        sName.contains("[") || sName.contains("]")){

                    Toast.makeText(EditSubActivity.this,
                            "The characters '.', '#', '$', '[' and ']' are not allowed for a Service Name",
                            Toast.LENGTH_LONG).show();
                }
                //check if price is not a number
                else if (!sPrice.matches(".*\\d.*")){
                    Toast.makeText(EditSubActivity.this,
                            "Please enter a valid price", Toast.LENGTH_LONG).show();
                }
                else{
                    //delete the existing subscription so that it can be recreated with its new details
                    mSubRef.child(getIntent().getStringExtra("edit_service_name")).removeValue();

                    subs.setName(sName);
                    subs.setDescription(sDescription);
                    subs.setPrice(sPrice);
                    subs.setDate(sDate);
                    subs.setFrequency(sFrequency);
                    mSubRef.child(sName).setValue(subs);


                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(EditSubActivity.this, HomeScreenActivity.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    //Method that creates the calendar
    private void subDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    //subDatePicker is activated when the user clicks on the PaymentDate textbox to display the calendar
    public void onClick(View v){
        subDatePicker();
    }


    //Displays the selected date into PaymentDate
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        PaymentDate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
    }
}
