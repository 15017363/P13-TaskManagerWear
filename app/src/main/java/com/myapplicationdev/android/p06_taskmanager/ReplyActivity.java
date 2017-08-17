package com.myapplicationdev.android.p06_taskmanager;

import android.content.Intent;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ReplyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        CharSequence reply = null;
        Intent intent = getIntent();
        int Id = intent.getIntExtra("id",-1);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence("status");
        }

        if(reply != null){
            Toast.makeText(ReplyActivity.this, "You have : " + reply,
                    Toast.LENGTH_SHORT).show();
            if(reply.equals("Completed")){
                DBHelper dbh = new DBHelper(ReplyActivity.this);
                dbh.deleteTask(Id);
                dbh.close();
            }
        }

    }
}
