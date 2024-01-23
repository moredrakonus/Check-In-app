package com.konus.pereklichka.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.konus.pereklichka.Adapters.GG_RecyclerViewAdapter;
import com.konus.pereklichka.MainActivity;
import com.konus.pereklichka.SQLiteManager;
import com.konus.pereklichka.rv_models.GroupModel;
import com.konus.pereklichka.GroupsInterface;
import com.konus.pereklichka.PopupDialog;
import com.konus.pereklichka.R;
import com.konus.pereklichka.rv_models.MemberModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GropsList extends AppCompatActivity implements GroupsInterface {
    private GG_RecyclerViewAdapter adapter;
    final PopupDialog popupDialog = new PopupDialog();

    public Button button2;

    SQLiteManager sqLiteManager;


    ArrayList<com.konus.pereklichka.rv_models.GroupModel> GroupModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grops_list);
        button2 = findViewById(R.id.button2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Групи");

        this.sqLiteManager= SQLiteManager.instanceOfDatabase(this,"groups_table");

        RecyclerView recyclerView = findViewById(R.id.rcv_group_menu);
        try {
            SetupGroupModels();
        }
        catch (Exception e){
            System.err.println(e);
        }
        this.adapter = new GG_RecyclerViewAdapter(this,GroupModel,this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GropsList.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void SetupGroupModels(){
        ArrayList<GroupModel> GroupsArray = sqLiteManager.loadGroupsFromDB();
        try{
            GroupModel.addAll(GroupsArray);

        }
        catch (Exception e){
            System.err.println(e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groupstoolbarmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addGoupMi){
            popupDialog.showPopup(this, new PopupDialog.PopupListener() {
                @Override
                public void onTextEntered(String text) {
                    ArrayList<GroupModel> GroupsArray = sqLiteManager.loadGroupsFromDB();
                    if (isThere(GroupsArray,text)){
                        System.out.println("Group " + text + " is already there");
                    }
                    else {
                        GroupModel model = new GroupModel("0", text);
                        sqLiteManager.addGroupToDB(model);
                        GroupModel.add(model);

                        try{
                        adapter.notifyItemInserted(GroupsArray.size());
                    }
                        catch (Exception e){
                            adapter.notifyItemInserted(0);
                            System.err.println(e);
                        }
                    }



                }
            });
        }
        return true;
    }

    public boolean isThere(ArrayList<GroupModel> array, String str) {
        try {
            for (GroupModel elem : array) {
                if (Objects.equals(elem.getgroup_name_txt(), str)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
        return false;
    }

    public String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preference", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String[] array = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    public void saveArray(List<String> array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preference", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(GropsList.this, ReceiveMenu.class);
        intent.putExtra("group_name", GroupModel.get(position).getgroup_name_txt());
        startActivity(intent);
    }
}