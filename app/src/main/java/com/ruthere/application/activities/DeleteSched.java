package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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
import com.utilities.Config;
import com.utilities.ListOfProfessorAdapter;
import com.utilities.Schedule;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;


public class DeleteSched extends AppCompatActivity {

    ListView list;
    String username, positionuser;
    String[] s = {"Monday", "Tuesday", "Wednesday ", "Thursday", "Friday", "Saturday"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sched);
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        username = prefs.getString("usernameuser", "");
        positionuser = prefs.getString("positionuser", "");
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            Firebase ref1 = new Firebase(Config.FIREBASE_SCHEDULE);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String output = "";
                    ArrayList<String> time = new ArrayList<String>();
                    ArrayList<String> date = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Schedule sched = postSnapshot.getValue(Schedule.class);
                        if (username.equals(sched.getEmail())) {
                            time.add(sched.getTime());
                            date.add(sched.getDay());
                        }
                    }

                    list = (ListView) findViewById(R.id.listview_deletesched);
                    CustomList_DeleteSched adapter = new CustomList_DeleteSched(DeleteSched.this, time, date, username);
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

    public void timepick(View view)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Set Consultation Time");
        alertDialog.setMessage("Click the field below to select a schedule.");
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, s);
        final Spinner sp = new Spinner(this);
        sp.setAdapter(adp);
        final EditText input = new EditText(this);
        final TextView extra = new TextView(this);
        extra.setText("to");
        final EditText input2 = new EditText(this);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setFocusable(false);
        extra.setGravity(Gravity.CENTER_HORIZONTAL);
        input2.setGravity(Gravity.CENTER_HORIZONTAL);
        input2.setFocusable(false);
        sp.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(extra);
        layout.addView(input2);
        layout.addView(sp);
        alertDialog.setView(layout);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c = java.util.Calendar.getInstance();
                int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = 00;

                TimePickerDialog timePickerDialog = new TimePickerDialog(DeleteSched.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Time tme = new Time(selectedHour, selectedMinute ,0);//seconds by default set to zero
                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                        try {
                            input.setText(formatter.format(tme).toString());
                            Date d,  f;
                            d = formatter.parse(input.getText().toString());
                            f = formatter.parse(input2.getText().toString());
                            if ((d.after(f)) && ((!input.getText().toString().equals("")) && (!input2.getText().toString().equals("")))){
                                Toast.makeText(getBaseContext(), formatter.format(d)+ " is greater than " + formatter.format(f) + ". Kindly change the time.", Toast.LENGTH_SHORT).show();
                                input.setText("");
                                input2.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        input2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c2 = java.util.Calendar.getInstance();
                int hour2 = c2.get(Calendar.HOUR_OF_DAY);
                int minute2 = 00;

                TimePickerDialog timePickerDialog = new TimePickerDialog(DeleteSched.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Time tme = new Time(selectedHour, selectedMinute ,0);//seconds by default set to zero
                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                        try {
                            input2.setText(formatter.format(tme).toString());
                            Date d,  f;
                            d = formatter.parse(input.getText().toString());
                            f = formatter.parse(input2.getText().toString());
                            if ((d.after(f)) && ((!input.getText().toString().equals("")) && (!input2.getText().toString().equals("")))){
                                Toast.makeText(getBaseContext(), formatter.format(d)+ " is greater than " + formatter.format(f) + ". Kindly change the time.", Toast.LENGTH_SHORT).show();
                                input.setText("");
                                input2.setText("");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, hour2, minute2, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        alertDialog.setPositiveButton("YES",

                new DialogInterface.OnClickListener() {
                    int i = 1;
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (input.getText().toString().equals("") || input2.getText().toString().equals("")){
                            Toast.makeText(getBaseContext(),  "Blank spaces are not allowed.", Toast.LENGTH_SHORT).show();
                            input.setText("");
                            input2.setText("");
                        }
                        else {
                            if (isNetworkAvailable()) {
                                final ProgressDialog progressDialog = new ProgressDialog(DeleteSched.this);
                                progressDialog.setMessage("Please wait.");
                                progressDialog.show();
                                final Firebase ref = new Firebase(Config.FIREBASE_SCHEDULE);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<String> maxNum = new ArrayList<String>();
                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                            maxNum.add(postSnapshot.getKey());
                                        }
                                        int counter = 1;
                                        int nums = 0;
                                        for (int i = 0; i < maxNum.size(); i++){
                                            if (counter != Integer.parseInt(maxNum.get(i))){
                                                nums = counter;
                                            }
                                            else{
                                                counter++;
                                            }
                                        }
                                        if (nums == 0){
                                            nums = (Integer.parseInt(maxNum.get(maxNum.size()-1)))+1;
                                        }

                                        String newnums = Integer.toString(nums);
                                        Schedule sched = new Schedule();
                                        sched.setDay(sp.getSelectedItem().toString());
                                        sched.setEmail(username);
                                        sched.setTime(input.getText().toString() + " - " + input2.getText().toString());
                                        ref.child(newnums).setValue(sched);
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
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    public void updatesched(View view)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Consultation Time");
        alertDialog.setMessage("Click the field below to select a schedule.");
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, s);
        final Spinner sp = new Spinner(this);
        sp.setAdapter(adp);
        final EditText input = new EditText(this);
        final TextView extra = new TextView(this);
        extra.setText("to");
        final EditText input2 = new EditText(this);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setFocusable(false);
        extra.setGravity(Gravity.CENTER_HORIZONTAL);
        input2.setGravity(Gravity.CENTER_HORIZONTAL);
        input2.setFocusable(false);
        sp.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(extra);
        layout.addView(input2);
        layout.addView(sp);
        alertDialog.setView(layout);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c = java.util.Calendar.getInstance();
                int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = 00;

                TimePickerDialog timePickerDialog = new TimePickerDialog(DeleteSched.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Time tme = new Time(selectedHour, selectedMinute ,0);//seconds by default set to zero
                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                        try {
                            input.setText(formatter.format(tme).toString());
                            Date d,  f;
                            d = formatter.parse(input.getText().toString());
                            f = formatter.parse(input2.getText().toString());
                            if ((d.after(f)) && ((!input.getText().toString().equals("")) && (!input2.getText().toString().equals("")))){
                                Toast.makeText(getBaseContext(), formatter.format(d)+ " is greater than " + formatter.format(f) + ". Kindly change the time.", Toast.LENGTH_SHORT).show();
                                input.setText("");
                                input2.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        input2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c2 = java.util.Calendar.getInstance();
                int hour2 = c2.get(Calendar.HOUR_OF_DAY);
                int minute2 = 00;

                TimePickerDialog timePickerDialog = new TimePickerDialog(DeleteSched.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Time tme = new Time(selectedHour, selectedMinute ,0);//seconds by default set to zero
                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                        try {
                            input2.setText(formatter.format(tme).toString());
                            Date d,  f;
                            d = formatter.parse(input.getText().toString());
                            f = formatter.parse(input2.getText().toString());
                            if ((d.after(f)) && ((!input.getText().toString().equals("")) && (!input2.getText().toString().equals("")))){
                                Toast.makeText(getBaseContext(), formatter.format(d)+ " is greater than " + formatter.format(f) + ". Kindly change the time.", Toast.LENGTH_SHORT).show();
                                input.setText("");
                                input2.setText("");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, hour2, minute2, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        alertDialog.setPositiveButton("YES",

                new DialogInterface.OnClickListener() {
                    int i = 1;
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (input.getText().toString().equals("") || input2.getText().toString().equals("")){
                            Toast.makeText(getBaseContext(),  "Blank spaces are not allowed.", Toast.LENGTH_SHORT).show();
                            input.setText("");
                            input2.setText("");
                        }
                        else {
                            if (isNetworkAvailable()) {
                                final ProgressDialog progressDialog = new ProgressDialog(DeleteSched.this);
                                progressDialog.setMessage("Please wait.");
                                progressDialog.show();
                                final Firebase ref = new Firebase(Config.FIREBASE_SCHEDULE);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        long maxNum = dataSnapshot.getChildrenCount();
                                        int nums = (int) maxNum;
                                        String newnums = Integer.toString(nums + 1);
                                        Schedule sched = new Schedule();
                                        sched.setDay(sp.getSelectedItem().toString());
                                        sched.setEmail(username);
                                        sched.setTime(input.getText().toString() + " - " + input2.getText().toString());
                                        ref.child(newnums).setValue(sched);
                                        ref.removeEventListener(this);
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
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
