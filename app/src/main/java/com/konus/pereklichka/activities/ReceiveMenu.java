package com.konus.pereklichka.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.util.*;
import android.content.Context;

import android.net.wifi.WifiManager;

import com.google.android.material.snackbar.Snackbar;
import com.konus.pereklichka.Adapters.MM_RecyclerViewAdapter;
import com.konus.pereklichka.rv_models.MemberModel;
import com.konus.pereklichka.R;
import com.konus.pereklichka.SQLiteManager;


public class ReceiveMenu extends AppCompatActivity {
    private boolean listening = false;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private String digits = "Code";
    public Button btnBack, btnListen;
    public TextView  txtrcvOutput;
    public CheckBox IsSharingWifi,allowNewUsers;
    private MM_RecyclerViewAdapter adapter;
    ArrayList<com.konus.pereklichka.rv_models.MemberModel> MemberModel = new ArrayList<>();
    public String group_name;
    SQLiteManager sqLiteManager;
    private static final String TAG = "RouterIPGetter";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_menu);
        Toolbar toolbar = findViewById(R.id.toolbar_rcv);
        setSupportActionBar(toolbar);

        InitViews();

        this.group_name = getIntent().getStringExtra("group_name");

        Objects.requireNonNull(getSupportActionBar()).setTitle("   "+group_name);

        this.sqLiteManager = SQLiteManager.instanceOfDatabase(this, group_name);
        this.sqLiteManager.addNewTable();

        RecyclerView recyclerView = findViewById(R.id.gRecyclerView);
        try {
            SetupMemberModels();
        }
        catch (Exception e){
            System.err.println(e);
        }
        this.adapter = new MM_RecyclerViewAdapter(this,MemberModel);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setAttendanceFalse();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listening){stopListening();}
                setAttendanceFalse();
                Intent intent = new Intent(ReceiveMenu.this, GropsList.class);
                startActivity(intent);
            }
        });

        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listening) {
                    listening = true;
                    btnListen.setText("Закінчити");
                    new Thread(() -> startListening(v)).start();
                } else {
                    stopListening();
                }
            }
        });

    }
    private void SetupMemberModels(){
        ArrayList<MemberModel> MembersArray = sqLiteManager.loadMembersFromDB();
        MemberModel.addAll(MembersArray);
        System.out.println("Users amount at start: "+MembersArray.size());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_group){
            PopUp();
        }
        return true;
    }


    private void PopUp() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Ви впевнені що хочете видалити цю групу?").setPositiveButton("ТАК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listening){
                    stopListening();
                }
                sqLiteManager.deleteTable();
                dialogInterface.dismiss();
                Intent intent = new Intent(ReceiveMenu.this, GropsList.class);
                startActivity(intent);

            }
        }).setNegativeButton("НІ",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rcv_toolbar_menu, menu);
        return true;
    }


    public static String[] parseBoxes(String box) {
        String[] parts = box.split("@");
        if (parts.length == 3) {
            System.out.println("Box parsed: "+ Arrays.toString(parts));
        } else {
            System.err.println("Invalid box format: " + box);
            }

        return parts;
    }

    private void InitViews() {
        btnBack = findViewById(R.id.btnBack);
        btnListen = findViewById(R.id.btnListen);
        txtrcvOutput = findViewById(R.id.txtrcvOutput);
        IsSharingWifi = findViewById(R.id.IsSharingWifi);
        allowNewUsers = findViewById(R.id.allowNewUsers);

    }
    private void handleClient(Socket clientSocket) {

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                String data = in.readLine();
                if (data == null || data.isEmpty()) {
                    break;
                }
                if(!listening){
                    break;
                }

                System.out.println("Received from " + clientSocket.getRemoteSocketAddress() + ": " + data);
                String[] dataset = parseBoxes(data);
                ArrayList<MemberModel> members = new ArrayList<>();
                try{
                    members = sqLiteManager.loadMembersFromDB();
                }
                catch (Exception e){
                    System.err.println(e);
                }
                MemberModel model2 = new MemberModel(dataset[0],dataset[1],true,dataset[2]);
                if (allowNewUsers.isChecked()){
                    try {
                        if (members.size()!=0){
                    for (MemberModel model: members){
                        boolean found = Objects.equals(model.getSpecial_id(), model2.getSpecial_id());
                        MemberModel model1 = new MemberModel(model.getTxtName(), model.getTxtLName(), true,model.getSpecial_id());

                        if (found){
                            sqLiteManager.updateUserInDB(model1);
                            MemberModel.set(members.indexOf(model),model1);
                            ArrayList<com.konus.pereklichka.rv_models.MemberModel> finalMembers = members;
                            runOnUiThread(() -> {
                            adapter.notifyItemChanged(finalMembers.indexOf(model));});
                            System.out.println("User " + model.getTxtName() + " " + model.getTxtLName() + " found and added from and updated in DB!");

                        }else {
                            sqLiteManager.addUserToDB(model2);
                            MemberModel.add(model2);
                            runOnUiThread(() -> {
                            adapter.notifyItemChanged(MemberModel.size()-1);});
                            System.out.println("User " + model2.getTxtName() + " " + model2.getTxtLName() + " not found and added to DB!");
                            sqLiteManager.updateUserAmountInDB(group_name);
                        }

                    }}
                    else {
                            sqLiteManager.addUserToDB(model2);
                            MemberModel.add(model2);
                            runOnUiThread(() -> {adapter.notifyItemChanged(MemberModel.size()-1);});
                            System.out.println("User " + model2.getTxtName() + " " + model2.getTxtLName() + " not found in DB as it is empty! Added to DB!");
                            sqLiteManager.updateUserAmountInDB(group_name);
                    }
                    }
                    catch (Exception e){
                        System.err.println(e);
                        sqLiteManager.addUserToDB(model2);
                        MemberModel.add(model2);
                        runOnUiThread(() -> {
                        adapter.notifyItemChanged(MemberModel.size()-1);});
                        sqLiteManager.updateUserAmountInDB(group_name);
                    }
                }
                else {
                    if (members.size()!=0){
                    for (MemberModel model: members){
                        boolean found= Objects.equals(model.getSpecial_id(), model2.getSpecial_id());
                        if (found){
                            MemberModel model1 = new MemberModel(model.getTxtName(), model.getTxtLName(), true,model.getSpecial_id());
                            sqLiteManager.updateUserInDB(model1);
                            MemberModel.set(members.indexOf(model),model1);
                            ArrayList<com.konus.pereklichka.rv_models.MemberModel> finalMembers1 = members;
                            runOnUiThread(() -> {
                            adapter.notifyItemChanged(finalMembers1.indexOf(model));});
                        }else {
                            System.err.println("User "+ model.getTxtName() + " " + model.getTxtLName() + " is not in the list and new users are not allowed!");
                        }
                    }}
                    else {
                        System.out.println("Error: DB is empty while new users are not allowed!");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Socket error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startListening(View v) {

        String serverIp = getLocalIp();
        System.out.println(serverIp);
        if (serverIp.length() == 13) {
            digits = String.valueOf(serverIp.charAt(10)) + serverIp.charAt(11) + serverIp.charAt(12);
        } else if (serverIp.length() == 12) {
            digits = String.valueOf(serverIp.charAt(10) + serverIp.charAt(11));
        }
        System.out.println(digits);
        try {
            txtrcvOutput.setText(digits);
        }
        catch (Exception e){
            System.err.println(e);
        }
        int serverPort = 12345;

        try {
            serverSocket = new ServerSocket(serverPort, 5); // Increased backlog for multiple connections
            System.out.println("Server listening on " + serverIp + ":" + serverPort);

            while (listening) {
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                    // Create a new thread for each accepted connection
                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.setDaemon(true); // Daemonize the thread to close on program exit
                    clientThread.start();

                } catch (IOException e) {
                    System.out.println("Socket error: " + e.getMessage());
                    break;
                }
            }
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
            showSnackbar(v, "Щось пішло не так, спробуйте ще раз");
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    System.out.println("ServerSocket closed");
                    startListening(v);
                }
            } catch (IOException i) {
                System.out.println("Error closing ServerSocket: " + i.getMessage());
            }
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private String getLocalIp( ) {
        if (IsSharingWifi.isChecked()){
            return getRouterIPAddress();
        }
        else {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.connect(InetAddress.getByName("8.8.8.8"), 80);
                String ip = socket.getLocalAddress().getHostAddress();
                socket.close();
                return ip;
            } catch (IOException e) {
                return "Could not retrieve IP";
            }
        }
    }
    private void closeSocket(){
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("ServerSocket closed");
            }
        } catch (IOException e) {
            System.out.println("Error closing ServerSocket: " + e.getMessage());
        }
    }
    private void stopListening() {
        listening = false;
        btnListen.setText("Listen");
        txtrcvOutput.setText("Код");
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread != Thread.currentThread()) {
                thread.interrupt();
            }
        }
        closeSocket();
        System.out.println("All threads forcefully stopped.");
    }

    public void setAttendanceFalse(){
        try {
        ArrayList<MemberModel> members = sqLiteManager.loadMembersFromDB();
        for (MemberModel model: members){
            MemberModel model1 = new MemberModel(model.getTxtName(), model.getTxtLName(), false,model.getSpecial_id());
            sqLiteManager.updateUserInDB(model1);
        }}
        catch (Exception e){
            System.err.println(e);
        }
    }

    public String getRouterIPAddress() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "WifiManager not available");
            return null;
        }

        WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("multicastLock");
        multicastLock.acquire();

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo == null) {
            Log.e(TAG, "DHCP Info not available");
            return null;
        }

        multicastLock.release();

        // Get the router IP address
        int ipAddress = dhcpInfo.gateway;
        return formatIPAddress(ipAddress);
    }

    private static String formatIPAddress(int ipAddress) {
        return ((ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                ((ipAddress >> 24) & 0xFF));
    }
}


