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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddSubActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Button AddSub;
    private EditText ServiceName;
    private EditText ServiceDescription;
    private EditText ServicePrice;
    private TextView PaymentDate;
    private Spinner PaymentFrequency;

    Subscriptions subs;

    long subId = 0;
    long amount = 0;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference().child("Subs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        ServiceName = (EditText)findViewById(R.id.serviceName);
        ServiceDescription = (EditText)findViewById(R.id.serviceDescription);
        ServicePrice = (EditText)findViewById(R.id.servicePrice);
        PaymentDate = (TextView) findViewById(R.id.serviceDate);
        PaymentFrequency = (Spinner)findViewById(R.id.serviceFrequency);

        AddSub = (Button)findViewById(R.id.btnAdd);

        Spinner frequency = findViewById(R.id.serviceFrequency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.payment_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(adapter);

        subs = new Subscriptions();


        AddSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddSubActivity.this, HomeScreenActivity.class);

                String sName = ServiceName.getText().toString();
                String sDescription = ServiceDescription.getText().toString();
                String sPrice = ServicePrice.getText().toString();
                String sDate = PaymentDate.getText().toString();
                String sFrequency = PaymentFrequency.getSelectedItem().toString();

                //check if required fields are empty
                if (sName.equals("") || sPrice.equals("") || sDate.equals("")){
                    Toast.makeText(AddSubActivity.this,
                            "Please fill all the required fields in order to continue",
                            Toast.LENGTH_SHORT).show();
                }
                //check if name contains invalid Firebase characters
                else if (sName.contains(".") || sName.contains("#") || sName.contains("$") ||
                        sName.contains("[") || sName.contains("]")){

                    Toast.makeText(AddSubActivity.this,
                            "The characters '.', '#', '$', '[' and ']' are not allowed for a Service Name",
                            Toast.LENGTH_LONG).show();
                }
                //check if price is not a number (.)
                else if (!sPrice.matches(".*\\d.*")){
                    Toast.makeText(AddSubActivity.this,
                            "Please enter a valid price", Toast.LENGTH_LONG).show();
                }
                else{
                    subs.setName(sName);
                    subs.setDescription(sDescription);
                    subs.setPrice(sPrice);
                    subs.setDate(sDate);
                    subs.setFrequency(sFrequency);
                    mRootRef.child(sName).setValue(subs);


                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(AddSubActivity.this, HomeScreenActivity.class);
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


    //Displays the selected date into PaymentDate (+1 month because January is 0)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        PaymentDate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
    }
}
