package com.myapplicationdev.android.p06_taskmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

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

        CharSequence reply = null;
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            reply = remoteInput.getCharSequence("status");
        }
        if (reply != null) {
            if (reply.toString().equalsIgnoreCase("Completed")) {
                Toast.makeText(MainActivity.this, "You have : " + reply,
                        Toast.LENGTH_SHORT).show();
                dbh.deleteTask(id);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });

        registerForContextMenu(lv);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 1, Menu.NONE, "Edit");
        menu.add(Menu.NONE, 2, Menu.NONE, "Delete");

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long selectid = menuinfo.id; //_id from database in this case
        final int selectpos = menuinfo.position; //position in the adapter
        switch (item.getItemId()) {
            case 1:{
                LayoutInflater inflater =
                        (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout updateTask = (LinearLayout) inflater.inflate(R.layout.activity_edit, null);

                final EditText etName = (EditText)updateTask.findViewById(R.id.etName);
                final EditText etDescription = (EditText) updateTask.findViewById(R.id.etDescription);
                final EditText etSeconds = (EditText) updateTask.findViewById(R.id.etTime);
                Task task = tasks.get(selectpos);
                etName.setText(task.getName());
                etDescription.setText(task.getDescription());
                etSeconds.setText("");
                final int Tid = task.getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Update Task")
                        .setView(updateTask)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                DBHelper dbh = new DBHelper(MainActivity.this);
                                String name = etName.getText().toString();
                                String desc = etDescription.getText().toString();
                                Toast.makeText(MainActivity.this,name,Toast.LENGTH_LONG).show();
                                Task updatedTask = new Task(Tid,name,desc);
                                dbh.updateTask(updatedTask);
                                tasks.clear();
                                tasks.addAll(dbh.getAllTasks());
                                dbh.close();
                                adapter.notifyDataSetChanged();

                                Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.SECOND, Integer.parseInt(etSeconds.getText().toString()));

                                Intent iReminder = new Intent(MainActivity.this,
                                        TaskReminderReceiver.class);
                                iReminder.putExtra("id", updatedTask.getId());
                                iReminder.putExtra("name", updatedTask.getName());
                                iReminder.putExtra("desc", updatedTask.getDescription());

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                        MainActivity.this, 123,
                                        iReminder, PendingIntent.FLAG_CANCEL_CURRENT);

                                AlarmManager am = (AlarmManager)
                                        getSystemService(Activity.ALARM_SERVICE);

                                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                        pendingIntent);
                            }
                        });
                builder.setNegativeButton("Cancel",null);
                AlertDialog myDialog = builder.create();
                myDialog.show();
            }
            break;
            case 2:{
                DBHelper dbh = new DBHelper(this);
                Task task = tasks.get(selectpos);
                dbh.deleteTask(task.getId());
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
            break;
        }
        return super.onContextItemSelected(item);
    }
}
