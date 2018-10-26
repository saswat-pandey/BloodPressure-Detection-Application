package com.example.saswat.mobileapp.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.saswat.mobileapp.R;

import java.util.Set;

public class BluetoothConnection extends AppCompatActivity {

    private Button bluetoothSwitch,search,makedDiscoverable;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        bluetoothSwitch=(Button)findViewById(R.id.bluetoothSwitch);
        search=(Button)findViewById(R.id.SearchDevice);
        makedDiscoverable=(Button)findViewById(R.id.discover);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        rv = (RecyclerView)findViewById(R.id.my_recycler_view);


        bluetoothSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                    Toast.makeText(getApplicationContext(), " Bluetooth Turned on",Toast.LENGTH_LONG).show();
                } else {
                    bluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "Bluetooth Turned Off", Toast.LENGTH_LONG).show();
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        makedDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent beDisciverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(beDisciverable, 0);
                Toast.makeText(getApplicationContext(), "Device is Discoverable", Toast.LENGTH_LONG).show();

            }
        });
    }
}
