package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class SettingsActivity extends AppCompatActivity {

    EditText txtLimit;
    TextView currentLimit;
    Button setLimit;
    Button removeLimit;

    Double savedlimit = 0.0;

    DatabaseReference mSetLimitRef = FirebaseDatabase.getInstance().getReference().child("Spending Limit");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentLimit = (TextView) findViewById(R.id.txtCurrentLimit);
        txtLimit = (EditText) findViewById(R.id.txtLimit);
        setLimit = (Button) findViewById(R.id.btnSetLimit);
        removeLimit = (Button) findViewById(R.id.btnRemoveLimit);

        final DecimalFormat formattedTotal = new DecimalFormat("#.##");

        mSetLimitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //display price with two decimals
                savedlimit = Double.valueOf(formattedTotal.format(Double.valueOf(dataSnapshot.getValue(String.class))));

                if (savedlimit>0){
                    currentLimit.setText("Current limit is: £" + savedlimit);
                }
                else{
                    currentLimit.setText("No current limit active");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(SettingsActivity.this, HomeScreenActivity.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    //method that sets/updates the Spending Limit
    public void setLimit(View v) {

        String limit = txtLimit.getText().toString();
        Double numLimit = 0.0;
        try{
            if (!limit.equals("")){
                numLimit = Double.valueOf(limit);
            }

            //Does not update limit when the field empty or contains 0 or negative numbers
            if (numLimit>=0.01){
                mSetLimitRef.setValue(limit);
                Toast.makeText(SettingsActivity.this, "Spending Limit Set",
                        Toast.LENGTH_SHORT).show();
                txtLimit.setText("");
            }
            else{
                Toast.makeText(SettingsActivity.this, "You have to enter a limit of 0.1 and above ",
                        Toast.LENGTH_SHORT).show();
            }
        }
        //display message when an error occurs
        catch(Exception ex){
            Toast.makeText(SettingsActivity.this,
                    "There was a problem setting the Spending Limit. Please make sure that you entered a valid number",
                    Toast.LENGTH_LONG).show();
        }
    }

    //method that removes the Spending Limit
    public void removeLimit (View v){
        // Set to negative to make sure it does not overlap with monthly total
        mSetLimitRef.setValue("-1");
        Toast.makeText(SettingsActivity.this, "Spending Limit Removed",
                Toast.LENGTH_SHORT).show();
        txtLimit.setText("");
    }

    public void toPrivacy (View v){
        Intent intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
        startActivity(intent);
    }

    public void toAbout (View v){
        Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
        startActivity(intent);
    }

}
