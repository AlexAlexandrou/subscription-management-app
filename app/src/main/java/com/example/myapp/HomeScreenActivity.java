package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {

    double total;
    double limit = 0.0;
    TextView totalPrice;
    ListView listView;
    ArrayList<String> subscriptions = new ArrayList<>() ;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference().child("Subs");
    DatabaseReference mSubsRef = mRootRef;

    DatabaseReference mLimitRef = FirebaseDatabase.getInstance().getReference().child("Spending Limit");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        //Syncs data with database when offline changes occur after user goes back online
        mSubsRef.keepSynced(true);
        mLimitRef.keepSynced(true);

        totalPrice = (TextView) findViewById(R.id.totalMonthly);

        listView = (ListView) findViewById(R.id.subsList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_item, subscriptions);

        listView.setAdapter(arrayAdapter);

        final DecimalFormat formattedTotal = new DecimalFormat("#.##");

        mSubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 /*
                 *Reset ArrayList and the monthly total so they can be filled and calculated again
                 *with the updated details of the database every time a  change in its data occurs
                 */
                subscriptions.clear();
                total = 0;
                arrayAdapter.notifyDataSetChanged();

                //add subscriptions to listview and calculate monthly total
                if (!subscriptions.contains(null) || !subscriptions.contains("")){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                        String name = childSnapshot.child("name").getValue(String.class);
                        String frequency = childSnapshot.child("frequency").getValue(String.class);

                        try{
                            //format price number so that it is in the Double form
                            Double amountTemp = Double.valueOf(formattedTotal.format(Double.valueOf
                                    (childSnapshot.child("price").getValue(String.class))));

                            //Calculate the monthly cost of each subscription
                            if (frequency.equals("Yearly")){
                                amountTemp = amountTemp/12;
                            }
                            else if (frequency.equals("Every 6 months")){
                                amountTemp = amountTemp/6;
                            }
                            else if (frequency.equals("Every 3 months")){
                                amountTemp = amountTemp/3;
                            }

                            total = total + amountTemp; // calculate the monthly total of the subscriptions
                            subscriptions.add(name);
                        }
                        //delete the entry that causes the error so the app does not crash repeatedly
                        catch (Exception exception){

                            Toast.makeText(HomeScreenActivity.this,
                                    "There was an error while creating the price of the subscription "
                                            + name + " and had to be deleted. Please try again",
                                    Toast.LENGTH_LONG).show();

                            mSubsRef.child(name).removeValue();
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();

                    //format total to have 2 decimals and display it
                    total = Double.valueOf(formattedTotal.format(total));
                    totalPrice.setText("Total: £"+ total);


                    //compare Monthly Total with Spending Limit every time a subscription changes
                    mLimitRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //get limit from database and convert it to a double with 2 decimals
                            limit = Double.valueOf(formattedTotal.format(Double.valueOf(
                                    dataSnapshot.getValue(String.class))));

                            //change colour of Monthly total text accordingly
                            if (total>limit && limit>0){

                                totalPrice.setTextColor(Color.RED);
                            }
                            else{
                                totalPrice.setTextColor(Color.GREEN);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //go to the sub info screen of selected subscription
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(HomeScreenActivity.this, SubInfoActivity.class);
                if (subscriptions.get(position) != null){
                    intent.putExtra("service_id", subscriptions.get(position));
                    startActivity(intent);
                }



            }
        });

        //deleting a subscription
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String subName = subscriptions.get(position);

                AlertDialog.Builder altdial = new AlertDialog.Builder(HomeScreenActivity.this);
                altdial.setTitle("Delete Subscription");
                altdial.setMessage("Are you sure you want to delete " + subName + " from your subscriptions?");
                altdial.setCancelable(false);
                altdial.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSubsRef.child(subscriptions.get(position)).removeValue();
                            }
                }).setNegativeButton("No", null).show();
                return true;
            }
        });

        //go to add sub screen
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){


                if (total<=limit || limit == -1){
                    Intent intent = new Intent(HomeScreenActivity.this, AddSubActivity.class);
                    startActivity(intent);
                }
                else{

                    //display warning message when Spending Limit is exceeded
                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(HomeScreenActivity.this);
                    warningBuilder.setTitle("Exceeded Spending Limit");
                    warningBuilder.setMessage("You have exceeded the Monthly Spending Limit. " +
                            "Are you sure you want to add another subscription?");
                    warningBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(HomeScreenActivity.this, AddSubActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", null).show();

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.budget:
                intent = new Intent(HomeScreenActivity.this, SpendingsActivity.class);
                intent.putExtra("monthly_spendings", String.valueOf(total));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}