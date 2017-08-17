package com.myapplicationdev.android.p06_taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Task> tasks;
    ArrayAdapter<Task> adapter;
    Button btnAdd;
    int actReqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        DBHelper dbh = new DBHelper(this);
        tasks = dbh.getAllTasks();
        adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
        lv.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == actReqCode) {
            if (resultCode == RESULT_OK) {
                DBHelper dbh = new DBHelper(MainActivity.this);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
