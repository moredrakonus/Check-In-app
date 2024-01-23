package com.konus.pereklichka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.konus.pereklichka.activities.Checkin;
import com.konus.pereklichka.activities.GropsList;
import com.konus.pereklichka.activities.LogIn;

public class MainActivity extends AppCompatActivity {

    private Button btnSendMenu, btnLogInMenu, btnReceiveMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        if (getLoginStatus()){
            btnLogInMenu.setText("Вийти з акаунту");
        }


        btnSendMenu.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {
                if (getLoginStatus()) {
                    Intent intent = new Intent(MainActivity.this, Checkin.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                    startActivity(intent);
                }
            }
        });

        btnLogInMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLoginStatus()){
                    popUp();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                    startActivity(intent);
                }
            }
        });

        btnReceiveMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLoginStatus()) {
                    Intent intent = new Intent(MainActivity.this, GropsList.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean getLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return preferences.getBoolean("LoggedIn", false);
    }

    private void popUp() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Ви впевнені?").setPositiveButton("ТАК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("LoggedIn", false);
                editor.remove("account");
                editor.apply();
                btnLogInMenu.setText("Війти в акаунт");
                dialogInterface.dismiss();

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

    private void initViews() {
        btnSendMenu = findViewById(R.id.btnSendMenu);
        btnLogInMenu = findViewById(R.id.btnLogInMenu);
        btnReceiveMenu = findViewById(R.id.btnReceiveMenu);
    }


}