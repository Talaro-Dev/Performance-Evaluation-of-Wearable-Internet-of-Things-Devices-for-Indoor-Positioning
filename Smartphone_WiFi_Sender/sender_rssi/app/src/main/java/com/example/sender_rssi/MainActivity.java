package com.example.sender_rssi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private WifiManager wifiManager;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;
    private boolean scanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // elegxo an exo ta dikaiomata gia na trexei allios ta zitao
        if (!arePermissionsGranted()) {
            requestPermissions();
        }


        Button buttonSignal = findViewById(R.id.ButtonSignal);
        buttonSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xekinao to hotspot gia na doulepsei os beacon
                Toast.makeText(MainActivity.this, "starting the beacon", Toast.LENGTH_SHORT).show();
                startHotspot();

            }
        });
    }
    //elegxo an exo permissions
    private boolean arePermissionsGranted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    //zitao ta permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
    }

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private String[] permissions = {Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION};

    private void startHotspot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //dimiourgo to hotspot
            if (wifiManager != null) {
                wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                    @Override
                    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                        super.onStarted(reservation);
                        // xekinao to hotspot
                        hotspotReservation = reservation;
                        //me vazei diko tou ssid kai password (den exo vrei tropo na to allaxo xoris na me krasharei
                        //i na doulepsei sosta)
                        WifiConfiguration config = reservation.getWifiConfiguration();
                        String ssid = config.SSID;
                        String password = config.preSharedKey;
                        //printaro to ssid kai to password
                        Toast.makeText(getApplicationContext(),"Username "+ssid+" password "+password,Toast.LENGTH_LONG).show();

                    }
                    //se periptosi pou kleisei na me to kanei release
                    @Override
                    public void onStopped() {
                        super.onStopped();
                        // Hotspot stopped
                        releaseHotspot();
                    }
                    //omoios an failarei
                    @Override
                    public void onFailed(int reason) {
                        super.onFailed(reason);
                        // Hotspot failed to start
                        releaseHotspot();
                    }
                }, new Handler());
            }


        } else {
            //Se periptosei pou den einai supported (pio palio API)
            Toast.makeText(this, "Hotspot is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }
    //sinartisi gia na kleiso to hotspot
    private void releaseHotspot() {
        if (hotspotReservation != null) {
            hotspotReservation.close();
            hotspotReservation = null;
        }
    }
    //otan manually kleiso to hotspot
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseHotspot();
    }
    // gia na elegxo ta permission request kano ovveride
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, start the hotspot
                startHotspot();
            } else {
                // Permissions denied, show an error message or disable the hotspot feature
                Toast.makeText(this, "Permissions are required to use the hotspot feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}