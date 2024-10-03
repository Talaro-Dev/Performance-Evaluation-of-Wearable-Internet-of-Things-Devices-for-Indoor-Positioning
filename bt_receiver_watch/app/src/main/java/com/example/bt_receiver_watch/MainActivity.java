package com.example.bt_receiver_watch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bt_receiver_watch.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private BluetoothAdapter BTAdapter;
    private TextView mTextView;
    private ActivityMainBinding binding;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    public int i = 0;

    final Handler handler = new Handler();

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Get a reference to the current window
        Window window = getWindow();

    // Set the FLAG_KEEP_SCREEN_ON flag to keep the screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        // Request Bluetooth permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
        }

        // Request Location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        Button boton = findViewById(R.id.ButtonScan);
        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Check if Bluetooth is enabled
                        if (!BTAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                        // Check if Location permission is granted
                        else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            startDiscovery();
                            handler.postDelayed(this, 1000);
                        }
                    }
                });
            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

                if (name != null && (name.equals("testing"))) {
                    if (rssi == -100) {

                    } else {
                        TextView rssi_msg = findViewById(R.id.RssiText);
                        rssi_msg.setText(name + " => " + rssi + "dBm\n");
                        Date date = new Date();

                        Log.d("rssi", String.valueOf(rssi));
                        i++;
                    }
                }

                BTAdapter.cancelDiscovery();
            }
        }
    };

    private void startDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(receiver, filter);
        BTAdapter.startDiscovery();
    }
}
