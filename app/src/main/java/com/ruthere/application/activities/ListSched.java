package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ruthere.application.customlist.CustomList_DeleteSched;
import com.ruthere.application.customlist.CustomList_ListSched;
import com.utilities.Config;
import com.utilities.Queue;
import com.utilities.Schedule;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ListSched extends AppCompatActivity {

    ListView list;
    String username, positionuser;
    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> to = new ArrayList<String>();
    ArrayList<String> from = new ArrayList<String>();
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<Integer> idQueue = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sched);
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        username = prefs.getString("usernameuser", "");
        positionuser = prefs.getString("positionuser", "");
        Intent intent = getIntent();
        final String date = intent.getStringExtra("date");
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            Firebase ref1 = new Firebase(Config.FIREBASE_QUEUE);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String output = "";


                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Queue queue = postSnapshot.getValue(Queue.class);
                        if ((positionuser.equals("STUDENT")) && (username.equals(queue.getFrom())) &&  (queue.getDate().equals(date))) {
                            time.add(queue.getTime());
                            to.add(queue.getTo());
                            from.add(queue.getFrom());
                            status.add(queue.getStatus());
                            idQueue.add(Integer.parseInt(postSnapshot.getKey().toString()));
                        }
                        else if ((positionuser.equals("PROFESSOR")) && (username.equals(queue.getTo())) &&  (queue.getDate().equals(date))){
                            time.add(queue.getTime());
                            to.add(queue.getTo());
                            from.add(queue.getFrom());
                            status.add(queue.getStatus());
                            idQueue.add(Integer.parseInt(postSnapshot.getKey().toString()));
                        }
                    }

                    list = (ListView) findViewById(R.id.listview_deletesched);
                    CustomList_ListSched adapter = new CustomList_ListSched(ListSched.this, time, to, from, status, idQueue, username, positionuser);
                    list.setAdapter(adapter);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
