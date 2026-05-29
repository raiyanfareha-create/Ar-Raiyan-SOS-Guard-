package com.arraiyan.sosguard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

public class SOSBackgroundService extends Service {
    private static final String CHANNEL_ID = "SOS_Guard_Channel";
    private BluetoothAdapter bluetoothAdapter;
    private String emergencyNumber = "01704100288"; // Default Number

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        
        // Initialize Bluetooth
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("emergency_number")) {
            emergencyNumber = intent.getStringExtra("emergency_number");
        }

        // Create Foreground Notification to keep service alive permanently
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Ar Raiyan SOS Guard")
                    .setContentText("Monitoring Smart Bracelet in background...")
                    .setSmallIcon(android.R.drawable.ic_menu_compass)
                    .build();
        }
        
        startForeground(1, notification);

        // Start scanning or monitoring bracelet hardware signals
        startBraceletMonitoring();

        return START_STICKY; // Restarts automatically if killed by Android system
    }

    private void startBraceletMonitoring() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // Your custom hardware bridge logic for the bracelet button goes here
            // When trigger received: call triggerSOSAlert()
            Toast.makeText(this, "SOS Guardian: Scanning for Bracelet...", Toast.LENGTH_SHORT).show();
        }
    }

    private void triggerSOSAlert() {
        // Core Logic: Fetch GPS Coordinates and send SMS/Alert to emergencyNumber
        Toast.makeText(this, "SOS TRIGGERED! Sending location to " + emergencyNumber, Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Ar Raiyan SOS Guard Monitoring",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
