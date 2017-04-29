package com.ruthere.application.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utilities.Config;
import com.utilities.Queue;
import com.utilities.ListOfScheduleAdapter;
import com.utilities.Schedule;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConsultationSchedule extends AppCompatActivity implements View.OnClickListener {
    private StorageReference storageReference;
    private EditText fromDateEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateFormatter2;
    ArrayList<String> datenope = new ArrayList<String>();
    ArrayList<String> timenope = new ArrayList<String>();
    ListView list;
    ListOfScheduleAdapter adapter;
    String timeSelected;
    String email,name, newnums1;
    int nums1;
    Spinner spinner;
    String[] s = {"Monday", "Tuesday", "Wednesday ", "Thursday", "Friday", "Saturday"};
    private Uri filePath;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_consultation_schedule);
        TextView tv = (TextView)findViewById(R.id.textView_Professor);

        Intent receivedIntent = getIntent();
        name = receivedIntent.getStringExtra("professor");
        email = receivedIntent.getStringExtra("email");

        tv.setText(name);

        dateFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        dateFormatter2 = new SimpleDateFormat("EEEE");
        findViewsById();
        setDateTimeField();

        final Button schedule = (Button) findViewById(R.id.button_submit_sched);
        final EditText reason = (EditText) findViewById(R.id.editText_reason_sched);
        final EditText dateSelected = (EditText) findViewById(R.id.editText_date_sched);
        schedule.setEnabled(false);


        reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")||dateSelected.getText().toString().equals("")) {
                    schedule.setEnabled(false);
                } else {
                    schedule.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        final Firebase ref2 = new Firebase(Config.FIREBASE_QUEUE);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Queue queue = postSnapshot.getValue(Queue.class);
                    if (email.equals(queue.getTo()))
                    {
                        datenope.add(queue.getDate());
                        timenope.add(queue.getTime());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Default Schedule");
        list.add("Custom Schedule");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " is selected.", Toast.LENGTH_SHORT).show();
                if (position == 1)
                {
                    ListView list = (ListView) findViewById(R.id.listView_list_sched);
                    list.setVisibility(View.INVISIBLE);
                    TextView textLabel = (TextView) findViewById(R.id.textNotApplicable);
                    textLabel.setVisibility(View.VISIBLE);
                    TextView text = (TextView) findViewById(R.id.textView_sel_sched);
                    text.setText("");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsultationSchedule.this);
                    alertDialog.setTitle("Set Consultation Time");
                    alertDialog.setMessage("Click the field below to select a schedule.");
                    final Spinner sp = new Spinner(ConsultationSchedule.this);
                    final EditText input = new EditText(ConsultationSchedule.this);
                    final TextView extra = new TextView(ConsultationSchedule.this);
                    extra.setText("to");
                    final EditText input2 = new EditText(ConsultationSchedule.this);
                    input.setGravity(Gravity.CENTER_HORIZONTAL);
                    input.setFocusable(false);
                    extra.setGravity(Gravity.CENTER_HORIZONTAL);
                    input2.setGravity(Gravity.CENTER_HORIZONTAL);
                    input2.setFocusable(false);
                    sp.setGravity(Gravity.CENTER_HORIZONTAL);
                    LinearLayout layout = new LinearLayout(ConsultationSchedule.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(input);
                    layout.addView(extra);
                    layout.addView(input2);
                    alertDialog.setView(layout);
                    input.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final java.util.Calendar c = java.util.Calendar.getInstance();
                            int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
                            int minute = 00;

                            TimePickerDialog timePickerDialog = new TimePickerDialog(ConsultationSchedule.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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

                            TimePickerDialog timePickerDialog = new TimePickerDialog(ConsultationSchedule.this, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
                                        Toast.makeText(ConsultationSchedule.this,  "Blank spaces are not allowed.", Toast.LENGTH_SHORT).show();
                                        input.setText("");
                                        input2.setText("");
                                    }
                                    else {
                                        TextView text = (TextView) findViewById(R.id.textView_sel_sched);
                                        text.setText(input.getText().toString() + " - " + input2.getText().toString());
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
                if (position == 0){
                    ListView list = (ListView) findViewById(R.id.listView_list_sched);
                    list.setVisibility(View.VISIBLE);
                    TextView textLabel = (TextView) findViewById(R.id.textNotApplicable);
                    textLabel.setVisibility(View.INVISIBLE);
                    TextView text = (TextView) findViewById(R.id.textView_sel_sched);
                    text.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void findViewsById() {
        fromDateEtxt = (EditText) findViewById(R.id.editText_date_sched);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();
    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                final String datenew = dateFormatter2.format(newDate.getTime());
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                final ArrayList<String> time = new ArrayList<String>();
                final ArrayList<String> day = new ArrayList<String>();
                if (isNetworkAvailable()) {
                    final ProgressDialog progressDialog = new ProgressDialog(ConsultationSchedule.this);
                    progressDialog.setMessage("Please wait.");
                    progressDialog.show();
                    final Firebase ref = new Firebase(Config.FIREBASE_SCHEDULE);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Schedule schedule = postSnapshot.getValue(Schedule.class);
                                if (schedule.getEmail() != null && schedule.getEmail().equals(email) && schedule.getDay().equals(datenew)) {
                                    int a = 0;
                                    for (int k = 0; k < datenope.size(); k++) {
                                        if (schedule.getTime() == timenope.get(k) && schedule.getDay() == datenope.get(k)) {
                                            a = 1;

                                        }
                                    }
                                    if (a == 0) {
                                        time.add(schedule.getTime());
                                        day.add(schedule.getDay());
                                    }

                                }
                            }
                            adapter = new ListOfScheduleAdapter(ConsultationSchedule.this, time, day);
                            list = (ListView) findViewById(R.id.listView_list_sched);
                            list.setAdapter(adapter);
                            ref.removeEventListener(this);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    list = (ListView) findViewById(R.id.listView_list_sched);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView selSched = (TextView) findViewById(R.id.textView_sel_sched);
                            for (int i = 0; i < parent.getCount(); i++) {
                                View v = parent.getChildAt(i);
                                RadioButton radio = (RadioButton) v.findViewById(R.id.rdbtn);
                                radio.setChecked(false);
                            }
                            Toast.makeText(ConsultationSchedule.this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
                            selSched.setText(parent.getAdapter().getItem(position).toString());
                            timeSelected = parent.getAdapter().getItem(position).toString();
                            RadioButton radio = (RadioButton) view.findViewById(R.id.rdbtn);
                            radio.setChecked(true);
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        }
    }

    public void onCancelClick(View view){
        startActivity(new Intent(this, StudentMain.class));
    }

    private void uploadFile(){

        TextView textViewpath = (TextView) findViewById(R.id.textViewPath);
        if (!textViewpath.getText().toString().equals("")) {
            final ProgressDialog progressDialog = new ProgressDialog(ConsultationSchedule.this);
            progressDialog.setTitle("Uploading..");
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            try {
                StorageReference riversRef = storageReference.child("images/" + newnums1);

                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(ConsultationSchedule.this, StudentMain.class);
                                intent.putExtra("id", 0);
                                startActivity(intent);
                                Toast.makeText(getBaseContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage(((int) progress + "% Uploaded"));
                            }
                        });
            }
            catch(Exception exception){
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ConsultationSchedule.this, StudentMain.class);
            }
        }
        else{
            Intent intent = new Intent(ConsultationSchedule.this, StudentMain.class);
            intent.putExtra("id", 0);
            startActivity(intent);
        }

    }

    public void onClickSubmit(View view){
        TextView text = (TextView) findViewById(R.id.textView_sel_sched);
        EditText date = (EditText) findViewById(R.id.editText_date_sched);
        Firebase.setAndroidContext(this);
        final EditText input = new EditText(ConsultationSchedule.this);
        final Queue cs = new Queue();
        SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
        cs.setDate(((EditText) findViewById(R.id.editText_date_sched)).getText().toString());
        cs.setFrom(prefs.getString("usernameuser", ""));
        cs.setGroup(prefs.getString("groupuser", ""));
        cs.setName(prefs.getString("nameuser", ""));
        cs.setReason(((EditText)findViewById(R.id.editText_reason_sched)).getText().toString());
        cs.setRejectreason("None");
        cs.setStatus("Pending");
        cs.setTime(text.getText().toString());
        cs.setTo(email);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsultationSchedule.this);
        alertDialog.setTitle("Confirm?");
        alertDialog.setMessage("Enter Subject");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("SUBMIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if (!password.equals("")) {
                            cs.setSubject(input.getText().toString());
                            if (isNetworkAvailable()) {
                                final Firebase ref2 = new Firebase(Config.FIREBASE_QUEUE);
                                ref2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dsnapshot) {
                                        if (count == 0) {
                                            long maxNum = dsnapshot.getChildrenCount();
                                            nums1 = (int) maxNum + 1;
                                            newnums1 = Integer.toString(nums1);
                                            ref2.child(newnums1).setValue(cs);
                                            uploadFile();
                                            ref2.removeEventListener(this);
                                            count++;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        System.out.println("Cancelled" + firebaseError.getMessage());
                                    }
                                });

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Input Subject!", Toast.LENGTH_SHORT).show();
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

    public void showFileChooser(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), 234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 234 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            TextView textViewPath = (TextView) findViewById(R.id.textViewPath);
            textViewPath.setText(filePath.getPath());

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

