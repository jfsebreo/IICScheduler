package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.utilities.SendMail;
import com.utilities.Users;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

public class LoginPage extends AppCompatActivity {
    Firebase ref = null;
    String emailView = "";
    String passwordView = "";
    String newpass = "";
    int counter = -1;
    int counteremail = 0;
    int counterinvalid = 0;
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Firebase.setAndroidContext(this);
        ref = new Firebase(Config.FIREBASE_USERS);
    }

    public void userLogin(final View view) {
        if (isNetworkAvailable()) {
            emailView = ((EditText) findViewById(R.id.editText_emailadd)).getText().toString();
            passwordView = ((EditText) findViewById(R.id.editText_password)).getText().toString();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging in..");
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            ref = new Firebase(Config.FIREBASE_USERS);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean isFound = false;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Users users = postSnapshot.getValue(Users.class);
                        if (emailView.equals(users.getEmail())) {
                            SharedPreferences.Editor editor = getSharedPreferences("IDOFUSER", MODE_PRIVATE).edit();
                            editor.putInt("iduser", Integer.parseInt(postSnapshot.getKey().toString())).commit();
                            editor.putString("usernameuser", users.getEmail()).commit();
                            editor.putString("positionuser", users.getPosition()).commit();
                            editor.putString("groupuser", users.getGroup()).commit();
                            editor.putString("nameuser", users.getFirstName() + " " + users.getLastName()).commit();
                            editor.putString("firstNameuser", users.getFirstName()).commit();
                            editor.putString("lastNameuser", users.getLastName()).commit();

                            if (((emailView.equals(users.getEmail())) && (passwordView.equals(users.getPassword())) && users.getPosition().equals("STUDENT"))) {
                                loginStudent();
                            } else if (((emailView.equals(users.getEmail())) && (passwordView.equals(users.getPassword())) && users.getPosition().equals("PROFESSOR"))) {
                                loginProfessor();
                            } else if (((emailView.equals(users.getEmail())) && (passwordView.equals(users.getPassword())) && users.getPosition().equals("ADMIN"))) {
                                loginAdmin();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid Credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                            isFound = true;
                            progressDialog.dismiss();
                            break;
                        }
                    }
                    if (!isFound) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "User does not exists!",
                                Toast.LENGTH_SHORT).show();

                    }
                    ref.removeEventListener(this);

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

    public void loginStudent(){

        Intent intent = new Intent(this, StudentMain.class);
        intent.putExtra("id", 1);
        startActivity(intent);
    }
    public void loginProfessor(){
        Intent intent = new Intent(this, ProfessorMain.class);
        //intent.putExtra("id", 1);
        startActivity(intent);
    }
    public void loginAdmin(){
        startActivity(new Intent(this, AdminMain.class));
    }
    public void userRegister(View view){
        startActivity(new Intent(this, RegisterUser.class));
    }

    @Override
    protected void onDestroy()
    {
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        int iduser = prefs.getInt("iduser", 0);
        if (iduser != 0) {
            ref.child(Integer.toString(iduser)).child("status").setValue("0");
        }
        super.onDestroy();
    }

    public void forgotpass(View view)
    {
        counter = -1;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        final StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000)));
        for (int i = 0; i < 5; i++) {
            sb.append(chars[rnd.nextInt(chars.length)]);
        }
        newpass = sb.toString();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Type the email you would like to set a new password:");
        final EditText input = new EditText(this);
        final TextView extra = new TextView(this);
        extra.setText("");
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        extra.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(extra);
        layout.addView(input);
        alertDialog.setView(layout);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNetworkAvailable()) {
                            final Firebase ref1 = new Firebase(Config.FIREBASE_USERS);
                            ref1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String output = "";
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        Users users = postSnapshot.getValue(Users.class);
                                        counter = 0;
                                        if (input.getText().toString().equals(users.getEmail())) {
                                            users.setPassword(newpass);
                                            ref1.child(postSnapshot.getKey()).setValue(users);
                                            counter += 1;

                                            if (counter == 1 && counteremail == 0) {

                                                SendMail sm = new SendMail(LoginPage.this, input.getText().toString(), "Forgot Password", "Your password has changed. Your current password is " + newpass + " -- Please use the given password on your next login. Thank you.");
                                                sm.execute();
                                                counteremail++;
                                            }
                                        } else {
                                            counterinvalid++;
                                        }
                                    }
                                    if (counter == 0 && counterinvalid > 0 && counteremail == 0) {
                                        Toast.makeText(getBaseContext(), "INVALID EMAIL", Toast.LENGTH_SHORT).show();
                                    }
                                    ref1.removeEventListener(this);
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
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        counter = -1;
        counteremail = 0;
        counterinvalid = 0;
        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
