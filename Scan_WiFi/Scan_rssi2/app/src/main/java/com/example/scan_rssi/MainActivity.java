package com.example.scan_rssi;

import static android.provider.Telephony.Mms.Part.FILENAME;
import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private boolean scanning = false;
    private TextView distanceTextView;
    public String Ssid;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView distanceTextView = findViewById(R.id.textView);
        distanceTextView.setText("");
        Button buttonScan = findViewById(R.id.buttonScan);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {   EditText editText = findViewById(R.id.textSsid); // Replace "editText" with the ID of your EditText view
                    Ssid= editText.getText().toString();
                    scanning = !scanning;
                    //dimiourgo handler gia na kano to scan cycle se ena sigkekrimeno xrono
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // kano to scan kai to distance calculation
                                startScan();
                                // ana poso tha kanei cycle to scan
                              handler.postDelayed(this, 500);
                            }
                        });
                        scanning = true;

                    }
            }
        });
    }
    public int i=0;
    private void startScan() {
        //sto proto cycle dimiourgo to wifimanager kai metepita apla xanakano scan se kathe call
          if (i==0) {
              wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
              wifiScanReceiver = new WifiScanReceiver(distanceTextView);
              registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
              wifiManager.startScan();

          }
          else
          {
              wifiManager.startScan();

          }
            i++;
          }

    private class WifiScanReceiver extends BroadcastReceiver {
        private TextView distanceTextView;
        public WifiScanReceiver(TextView distanceTextView) {
            this.distanceTextView = distanceTextView;
        }
    public int j=1;
        @Override
        public void onReceive(Context context, Intent intent) {
              //warning gia dikaiomata alla trexei sto nexus
              List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(Ssid)) {
                        // string tis apostasis
                        double distance = calculateDistance(scanResult.level);
                        String distanceStr = String.format("%.10f", distance) + " meters";

                        TextView distanceTextView = (TextView) (findViewById(R.id.textView));

                        Log.d("rssi",String.format("%d",scanResult.level));
                        distanceTextView.setText(distanceStr);


                        // Gia na paro tis metriseis gia to rssi pou ithela na stamataei sta 100
                        /*if (j == 100)
                        {
                            Toast.makeText(getApplicationContext(),"eftasa ta 100 scans stamatao",Toast.LENGTH_SHORT).show();
                            unregisterReceiver(wifiScanReceiver);
                            // reset to button gia na xanarxisei an patiso to button
                            i=0;
                            scanning = false;
                            handler.removeCallbacksAndMessages(null);
                        }
                        j++;
                        */
                    }
                }
            }
        private double calculateDistance(int rssi) {
            double C0 = -56; // Intercept value
            double n = 2.08; // Path loss exponent
            double distance = Math.pow(10, ((C0 - rssi) / (10 * n)));
            Log.d("Distance in meters",String.format("%.10f",distance));
            return distance;
        }

        }
}
