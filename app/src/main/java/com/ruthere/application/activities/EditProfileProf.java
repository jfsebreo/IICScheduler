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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.utilities.Config;
import com.utilities.Schedule;
import com.utilities.Users;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileProf extends AppCompatActivity {

    String firstName, lastName, username, group, iduser, position;
    EditText editText_firstName_user;
    EditText editText_lastName_user;
    EditText editText_username_user;
    EditText editText_group_user;
    TextView textView_sched_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        username = prefs.getString("usernameuser", "");
        firstName = prefs.getString("firstNameuser", "");
        lastName = prefs.getString("lastNameuser", "");
        group = prefs.getString("groupuser", "");
        position = prefs.getString("positionuser", "");

        iduser = Integer.toString(prefs.getInt("iduser", 0));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_prof);
        editText_firstName_user = (EditText)findViewById(R.id.editText_firstName_user);
        editText_lastName_user = (EditText)findViewById(R.id.editText_lastName_user);
        editText_username_user = (EditText)findViewById(R.id.editText_username_user);
        editText_group_user = (EditText)findViewById(R.id.editText_group_user);
        textView_sched_user = (TextView)findViewById(R.id.textView_sched_user);
        editText_firstName_user.setText(firstName);
        editText_lastName_user.setText(lastName);
        editText_group_user.setText(group);
        editText_username_user.setText(username);
        Firebase ref1 = new Firebase(Config.FIREBASE_SCHEDULE);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String output = "";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Schedule sched = postSnapshot.getValue(Schedule.class);
                    if (username.equals(sched.getEmail())) {
                        output = output + sched.getDay() + " | " + sched.getTime() + "\n";
                    }
                }
                textView_sched_user.setText(output);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        if (position.equals("STUDENT"))
        {
            TextView textView_sched = (TextView) findViewById(R.id.textView_sched);
            textView_sched.setText("");
            textView_sched_user.setText("");
            Button button2 = (Button) findViewById(R.id.button3);
            button2.setVisibility(View.INVISIBLE);
        }
    }


    public void savechanges(View view)
    {
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            SharedPreferences.Editor editor = getSharedPreferences("IDOFUSER", MODE_PRIVATE).edit();
            editor.putString("groupuser", editText_group_user.getText().toString()).commit();
            editor.putString("nameuser", editText_firstName_user.getText().toString() + " " + editText_lastName_user.getText().toString()).commit();
            editor.putString("firstNameuser", editText_firstName_user.getText().toString()).commit();
            editor.putString("lastNameuser", editText_lastName_user.getText().toString()).commit();
            final Firebase ref2 = new Firebase(Config.FIREBASE_USERS);
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ref2.child(iduser).child("firstName").setValue(editText_firstName_user.getText().toString());
                    ref2.child(iduser).child("lastName").setValue(editText_lastName_user.getText().toString());
                    ref2.child(iduser).child("email").setValue(editText_username_user.getText().toString());
                    ref2.child(iduser).child("group").setValue(editText_group_user.getText().toString());
                    ref2.removeEventListener(this);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            Toast.makeText(getBaseContext(), "The changes are saved.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSched(View view){
        startActivity(new Intent(this, DeleteSched.class));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
