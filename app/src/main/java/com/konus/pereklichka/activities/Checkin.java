package com.konus.pereklichka.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.konus.pereklichka.MainActivity;
import com.konus.pereklichka.R;


public class Checkin extends AppCompatActivity {

    private Button btngbcksend, btnCheckIn;
    private EditText editTextId;
    private CheckBox isSharingWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        InitViews();

        btngbcksend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checkin.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSharingWifi.isChecked()) {

                    String serverIP = null;
                    try {
                        serverIP = getHostIp(Checkin.this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    new SendMessageTask().execute(serverIP);
                }
                else {
                    String serverIP = "192.168.0." + editTextId.getText().toString();
                    new SendMessageTask().execute(serverIP);
                }
            }
        });
    }

    private void InitViews() {
        btngbcksend = findViewById(R.id.btngbcksend);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        editTextId = findViewById(R.id.editTextId);
        isSharingWifi = findViewById(R.id.isSharingWifi);
    }

    private class SendMessageTask extends AsyncTask<String, Void, Void> {
        private static final String TAG = "SendMessageTask";
        private static final int SERVER_PORT = 12345;

        @Override
        protected Void doInBackground(String... params) {
            if (params.length == 0) return null;

            String serverIP = params[0];
            Socket clientSocket = null;
            PrintWriter out = null;

            try {
                clientSocket = new Socket(serverIP, SERVER_PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                SharedPreferences preferences =getSharedPreferences("myPrefs", MODE_PRIVATE);
                String message = preferences.getString("account", "error");
                out.println(message);
                Log.d(TAG, "Sent: " + message);
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to server: " + e.getMessage());
            } finally {
                try {
                    if (out != null) out.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }



    public static String getHostIp(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager != null) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                    if (dhcpInfo != null) {
                        int gateway = dhcpInfo.gateway;
                        System.out.println(formatIP((gateway)));
                        return formatIP(gateway);
                    }
                }
            }
        }
        return null;
    }

    private static String formatIP(int ipAddress) {
        return String.format(
                "%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

}