package com.konus.pereklichka.activities;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.konus.pereklichka.MainActivity;
import com.konus.pereklichka.R;

import java.util.Objects;


public class LogIn extends AppCompatActivity {

    public Button btnGoBack, btnDone;
    public EditText edtTextName, edtTextLName;
    public TextView txtWarnName, txtWarnLName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        InitViews();

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, MainActivity.class);
                txtWarnName.setVisibility(View.INVISIBLE);
                txtWarnLName.setVisibility(View.INVISIBLE);
                startActivity(intent);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {//ae4fcb9a45aa0e01
            @Override
            public void onClick(View v) {
                InitRegister();
            }
        });

    }

    private void InitRegister() {
        if (ValidateData()){
            String account;
            try {
                String android_id = Secure.getString(LogIn.this.getContentResolver(), Secure.ANDROID_ID);
                account = edtTextName.getText().toString() + "@" + edtTextLName.getText().toString() + "@" + android_id;
                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("account", account);
                editor.apply();
            }
            catch (Exception e){
                System.out.println("Error:" + e);
            }
            SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("LoggedIn", true);
            editor.apply();
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            startActivity(intent);

        }
    }

    private boolean ValidateData() {
        if (edtTextName.getText().toString().equals("")) {
            txtWarnName.setVisibility(View.VISIBLE);
            txtWarnName.setText("Введіть своє ім'я!");
            return false;
        }
        else if (edtTextLName.getText().toString().equals("")) {
            txtWarnLName.setVisibility(View.VISIBLE);
            txtWarnLName.setText("Введіть своє прізвище!");
            return false;
        }
        else if (isThere(edtTextName.getText().toString().toCharArray(), '@')){
            txtWarnName.setVisibility(View.VISIBLE);
            txtWarnName.setText("Символ @ не підтримується");
            return false;
        }
        else if (isThere(edtTextLName.getText().toString().toCharArray(), '@')){
            txtWarnLName.setVisibility(View.VISIBLE);
            txtWarnLName.setText("Символ @ не підтримується");
            return false;
        }
        txtWarnName.setVisibility(View.INVISIBLE);
        txtWarnLName.setVisibility(View.INVISIBLE);
        return true;

    }


    public boolean isThere(char[] array, char chr){
        for (char elem: array){
            if (Objects.equals(elem, chr)){
                return true;
            }
        }
        return false;
    }


    private void InitViews() {
        btnGoBack = findViewById(R.id.btnGoBack);
        btnDone = findViewById(R.id.btnDone);
        edtTextName = findViewById(R.id.edtTextName);
        edtTextLName = findViewById(R.id.edtTextLName);
        txtWarnName = findViewById(R.id.txtWarnName);
        txtWarnLName = findViewById(R.id.txtWarnLName);

    }
}