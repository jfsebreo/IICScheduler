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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ruthere.application.fragment.ListProfFragment;
import com.ruthere.application.fragment.QueueFragment;
import com.utilities.Config;
import com.utilities.SendMail;
import com.utilities.Users;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StudentMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    View headerView = null;
    Toolbar toolbar = null;
    Firebase ref;
    int counter = -1;
    int counteremail = 0;
    int counterinvalid = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_student_main);
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        int iduser = prefs.getInt("iduser", 0);
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            ref = new Firebase("https://ruthere-5a89f.firebaseio.com/Users/" + iduser);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    equate(users);
                    ref.removeEventListener(this);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        String firstname = prefs.getString("firstNameuser", "");
        String lastname = prefs.getString("lastNameuser", "");
        navigationView = (NavigationView) findViewById(R.id.nav_student);
        headerView = navigationView.getHeaderView(0);
        TextView nameText = (TextView) headerView.findViewById(R.id.textView_nav_stud_name);
        nameText.setText(firstname+" "+lastname);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_changepass) {
            counter = -1;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Change Password");
            alertDialog.setMessage("Type your new password:");
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
            alertDialog.setPositiveButton("CHANGE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (isNetworkAvailable()) {
                                final Firebase ref1 = new Firebase(Config.FIREBASE_USERS);
                                ref1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String output = "";
                                        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
                                        String username = prefs.getString("usernameuser", "");

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            Users users = postSnapshot.getValue(Users.class);
                                            counter = 0;
                                            if (username.equals(users.getEmail())) {
                                                users.setPassword(input.getText().toString());
                                                ref1.child(postSnapshot.getKey()).setValue(users);
                                                counter += 1;
                                                if (counter == 1 && counteremail == 0) {
                                                    SendMail sm = new SendMail(StudentMain.this, username, "Change Password", "Your password has changed. Your current password is " + input.getText().toString() + " -- Please use the given password on your next login. Thank you.");
                                                    sm.execute();
                                                    counteremail++;
                                                }
                                            }
                                            else {
                                                counterinvalid++;
                                            }
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

            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_queue) {
            QueueFragment fragment = new QueueFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_listprof) {
            ListProfFragment fragment = new ListProfFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_calendar) {
                startActivity(new Intent(this, Calendar.class));
        }else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LoginPage.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void equate(Users user)
    {
        navigationView = (NavigationView) findViewById(R.id.nav_student);
        headerView = navigationView.getHeaderView(0);
        TextView emailText = (TextView) headerView.findViewById(R.id.textView_nav_stud_email);
        TextView nameText = (TextView) headerView.findViewById(R.id.textView_nav_stud_name);
        emailText.setText(user.getEmail());
        nameText.setText(user.getFirstName()+" "+user.getLastName());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        if(id == 1){
            ListProfFragment fragment = new ListProfFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else if(id == 0){
            QueueFragment queueFrag =new QueueFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, queueFrag);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void toEdit(View view)
    {
        startActivity(new Intent(this, EditProfileProf.class));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

