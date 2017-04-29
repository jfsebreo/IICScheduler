package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.utilities.Config;
import com.utilities.Users;

public class RegisterUser extends AppCompatActivity {

    Firebase ref = null;
    DataSnapshot snapshot;
    private int nums;
    String newnums;
    String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Firebase.setAndroidContext(this);
        ref = new Firebase(Config.FIREBASE_USERS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dsnapshot) {
                snapshot = dsnapshot;
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Cancelled" + firebaseError.getMessage());
            }
        });
    }

    public void onRegisterClick(View view){
        final String email = ((EditText) findViewById(R.id.editText_email_reg)).getText().toString();
        final String password = ((EditText) findViewById(R.id.editText_pass_reg)).getText().toString();
        final String confirmPassword = ((EditText) findViewById(R.id.editText_confirmPass_reg)).getText().toString();
        final String fname = ((EditText) findViewById(R.id.editText_fname_reg)).getText().toString();
        final String lname = ((EditText) findViewById(R.id.editText_lname_reg)).getText().toString();



        if(email.matches(".*\\d+.*")){
            position = "STUDENT";
        }else{
            position = "PROFESSOR";
        }
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            if (password.equals(confirmPassword)) {
                long maxNum = snapshot.getChildrenCount();
                nums = (int) maxNum;
                newnums = Integer.toString(nums + 1);
                Users users = new Users();
                users.setEmail(email);
                users.setPassword(password);
                users.setFirstName(fname);
                users.setLastName(lname);
                users.setPosition(position);
                users.setGroup(" ");
                users.setStatus("0");
                ref.child(newnums).setValue(users);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Register Successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Passwords does not match!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancel(View view){
        startActivity(new Intent(this, LoginPage.class));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
