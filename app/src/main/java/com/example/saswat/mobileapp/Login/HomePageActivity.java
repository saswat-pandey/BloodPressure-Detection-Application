package com.example.saswat.mobileapp.Login;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.saswat.mobileapp.BluetoothConnection.BluetoothConnection;
import com.example.saswat.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = "HomePageActivity";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference dbRef;
    private String userID, patientName, docNum;
    private Button accSettings, dataProcess, bluetooth;
    private EditText systolic, daistolic;
    private Integer systolicVal, daistolicVal;
    private ListView mListView;
    int MY_PERMISSIONS_REQUEST_SEND_SMS=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mListView = (ListView) findViewById(R.id.listview);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        accSettings = (Button) findViewById(R.id.accSetting);
        bluetooth = (Button) findViewById(R.id.bluetooth);
        dataProcess = (Button) findViewById(R.id.processData);
        systolic = (EditText) findViewById(R.id.sysPressure);
        daistolic = (EditText) findViewById(R.id.diaPressure);



        accSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, AccountSettingActivity.class));
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, BluetoothConnection.class));
            }
        });

        dataProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                systolicVal = Integer.parseInt(systolic.getText().toString().trim());
                daistolicVal = Integer.parseInt(daistolic.getText().toString().trim());

                if (systolic.getText().toString().trim().length() < 2 || systolic.getText().toString().trim().length() > 3) {
                    Toast.makeText(HomePageActivity.this, "Enter the Systolic Pressure Correctly", Toast.LENGTH_LONG).show();
                    return;
                }

                if (daistolic.getText().toString().trim().length() < 2 || daistolic.getText().toString().trim().length() > 3) {
                    Toast.makeText(HomePageActivity.this, "Enter the diastolic Pressure Correctly", Toast.LENGTH_LONG).show();
                    return;
                }

                if(systolic.getText().toString().trim()=="" ||daistolic.getText().toString().trim()==""){
                    Toast.makeText(HomePageActivity.this, "Enter the Both the Values", Toast.LENGTH_LONG).show();
                     return;
                }

                if (systolicVal>300 || daistolicVal>300){
                    Toast.makeText(HomePageActivity.this, "Entered Values out of range", Toast.LENGTH_LONG).show();
                    return;
                }
                calculatePressure(systolicVal, daistolicVal);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void calculatePressure(final Integer systolicVal, final Integer daistolicVal) {

        String alertMsg = "";

        if (systolicVal <= 120 && systolicVal > 100 && daistolicVal == 80) {
            Toast.makeText(HomePageActivity.this, "Pressure is Normal", Toast.LENGTH_LONG).show();
        } else if (systolicVal > 130 && systolicVal < 140) {

            if (daistolicVal > 90 && daistolicVal < 100) {
                alertMsg = "The Blood Pressure is in the Upper Limits.Do you wish to Notify the Registered Doctor";
            }
            if (daistolicVal < 90) {
                alertMsg = "The Systolic Reading is on the Upper Limit.Do you wish to Notify the Registered Doctor";
            }
            if (daistolicVal < 100) {
                alertMsg = "The diastolic reading is on the Upper Limit.Do you wish to Notify the Registered Doctor";
            }
        } else if (systolicVal >= 140 && daistolicVal >= 90) {
            alertMsg = "The Blood Pressure Reading is critically high!!.Send an Alert to the Doctor?";
        } else if (systolicVal < 90) {
            if (daistolicVal > 60) {
                alertMsg = "The Systolic pressure is in the Lower Limits.Do you wish to Notify the Registered Doctor?";
            }
            if(daistolicVal<60){
               alertMsg= "The Blood Pressure Reading is critically Low!!.Send an Alert to the Doctor?";
            }
        } else {
            alertMsg = "Abnormality Detected!. Would you like to send the alert ?";
        }

        if (alertMsg != "") {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomePageActivity.this);
            alertBuilder.setTitle("Anomaly Detected");
            alertBuilder.setMessage(alertMsg);
            alertBuilder.setCancelable(false);
            alertBuilder.setIcon(R.drawable.alert);

            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String message = "<----BPHADNS ALERT MESSAGE------>\n"+"The systolic and diastolic pressure of patient:" + patientName + " has shown critical abnormality:-\n" + "Systolic: " + systolicVal + "/Diastolic: " + daistolicVal;
                    if (ContextCompat.checkSelfPermission(HomePageActivity.this, Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomePageActivity.this,new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                    else{
                        SmsManager.getDefault().sendTextMessage(docNum, null, message, null,null);
                    }


//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + docNum));
//                    intent.putExtra("sms_body", message);
//                    startActivity(intent);
                    Toast.makeText(HomePageActivity.this, "Message Sent to the Medical Personnel", Toast.LENGTH_SHORT).show();
                }
            });

            alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }


    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            RegisteredUser uInfo = new RegisteredUser();
            uInfo.setFullname(ds.child(userID).getValue(RegisteredUser.class).getFullname());
            uInfo.setEmail(ds.child(userID).getValue(RegisteredUser.class).getEmail());
            uInfo.setDesignation(ds.child(userID).getValue(RegisteredUser.class).getDesignation());
            uInfo.setDocName(ds.child(userID).getValue(RegisteredUser.class).getDocName());
            uInfo.setDocPhone(ds.child(userID).getValue(RegisteredUser.class).getDocPhone());

            //display all the information
            Log.d(TAG, "showData: name: " + uInfo.getFullname());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: Designation: " + uInfo.getDesignation());
            Log.d(TAG, "showData: DocName: " + uInfo.getDocName());
            Log.d(TAG, "showData: docPh: " + uInfo.getDocPhone());

            patientName = uInfo.getFullname();
            docNum = uInfo.getDocPhone();

            ArrayList<String> array = new ArrayList<>();
            array.add("Patient Name:"+uInfo.getFullname());
            array.add("Patient Email:"+uInfo.getEmail());
            array.add("Designation:"+uInfo.getDesignation());
            if(uInfo.getDesignation().equalsIgnoreCase("Patient")) {
                array.add("Doctor's Name:"+uInfo.getDocName());
                array.add("Doctor's Phone Number:"+uInfo.getDocPhone());
            }
            ArrayAdapter adapter = new ArrayAdapter(HomePageActivity.this, android.R.layout.simple_list_item_1, array);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}



