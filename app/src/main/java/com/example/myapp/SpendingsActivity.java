package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SpendingsActivity extends AppCompatActivity {

    TextView monthly, threeMonths, sixMonths, yearly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spendings);

        monthly = (TextView) findViewById(R.id.txtMonthly);
        threeMonths = (TextView) findViewById(R.id.txtThreeMonths);
        sixMonths = (TextView) findViewById(R.id.txtSixMonths);
        yearly = (TextView) findViewById(R.id.txtYearly);

        String monthlySpending = getIntent().getStringExtra("monthly_spendings");

        final DecimalFormat formattedTotal = new DecimalFormat("#.##");

        //calculate the costs for monthly up to yearly
        Double monthlyTotal = Double.valueOf(formattedTotal.format(Double.valueOf(monthlySpending)));
        Double quarterTotal = Double.valueOf(formattedTotal.format(Double.valueOf(monthlyTotal*3)));
        Double halfTotal = Double.valueOf(formattedTotal.format(Double.valueOf(monthlyTotal*6)));
        Double yearlyTotal = Double.valueOf(formattedTotal.format(Double.valueOf(monthlyTotal*12)));

        monthly.setText("£" + monthlyTotal);
        threeMonths.setText("£" + quarterTotal);
        sixMonths.setText("£" + halfTotal);
        yearly.setText("£" + yearlyTotal);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(SpendingsActivity.this, HomeScreenActivity.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
