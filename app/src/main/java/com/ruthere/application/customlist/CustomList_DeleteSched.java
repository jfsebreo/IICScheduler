package com.ruthere.application.customlist;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.ruthere.application.activities.DeleteSched;
import com.ruthere.application.activities.R;
import com.utilities.Config;
import com.utilities.Schedule;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jinnilin on 3/5/2017.
 */

public class CustomList_DeleteSched extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> time;
    private final ArrayList<String> day;
    String username;
    String keyVal;
    String[] s = {"Monday", "Tuesday", "Wednesday ", "Thursday", "Friday", "Saturday"};



    public CustomList_DeleteSched(Activity context, ArrayList<String> time , ArrayList<String> day, String username) {
        super(context, R.layout.template_for_list_of_delete_sched, time);
        this.context = context;
        this.time = time;
        this.day = day;
        this.username = username;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_for_list_of_delete_sched, null, true);
        TextView txtboxtime = (TextView) rowView.findViewById(R.id.time);
        TextView txtboxday = (TextView) rowView.findViewById(R.id.day);
        Button deleteImageView = (Button) rowView.findViewById(R.id.chk_box);
        Button updateImageView = (Button) rowView.findViewById(R.id.editbutton);
        deleteImageView.setTag(position);
        updateImageView.setTag(position);
        txtboxtime.setText(time.get(position));
        txtboxday.setText(day.get(position));
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int position=(Integer)v.getTag();
                final Firebase ref1 = new Firebase(Config.FIREBASE_SCHEDULE);
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String output = "";
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Schedule sched = postSnapshot.getValue(Schedule.class);
                            if (username.equals(sched.getEmail()) && (time.get(position).equals(sched.getTime()))) {
                                ref1.child(postSnapshot.getKey()).setValue(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
        updateImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int position=(Integer)v.getTag();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Update Consultation Time");
                alertDialog.setMessage("Click the field below to select a schedule.");
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, s);
                final Spinner sp = new Spinner(getContext());
                sp.setAdapter(adp);
                final EditText input = new EditText(getContext());
                final TextView extra = new TextView(getContext());
                extra.setText("to");
                final EditText input2 = new EditText(getContext());
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                input.setFocusable(false);
                extra.setGravity(Gravity.CENTER_HORIZONTAL);
                input2.setGravity(Gravity.CENTER_HORIZONTAL);
                input2.setFocusable(false);

                final Firebase ref1 = new Firebase(Config.FIREBASE_SCHEDULE);
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String output = "";
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Schedule sched = postSnapshot.getValue(Schedule.class);
                            if (username.equals(sched.getEmail()) && (time.get(position).equals(sched.getTime()))) {
                                input.setText(sched.getTime().substring(0,8));
                                input2.setText(sched.getTime().substring(11,19));
                                sp.setSelection(adp.getPosition(sched.getDay()));
                                keyVal =  postSnapshot.getKey();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


                sp.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout layout = new LinearLayout(getContext());
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

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
                                        Toast.makeText(getContext(), formatter.format(d)+ " is greater than " + formatter.format(f) + ". Kindly change the time.", Toast.LENGTH_SHORT).show();
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

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
                                        Toast.makeText(getContext(), formatter.format(d)+ " is greater than " + formatter.format(f) + ". Kindly change the time.", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getContext(),  "Blank spaces are not allowed.", Toast.LENGTH_SHORT).show();
                                    input.setText("");
                                    input2.setText("");
                                }
                                else {
                                    final Firebase ref = new Firebase(Config.FIREBASE_SCHEDULE);
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Schedule sched = new Schedule();
                                            sched.setDay(sp.getSelectedItem().toString());
                                            sched.setEmail(username);
                                            sched.setTime(input.getText().toString() + " - " + input2.getText().toString());
                                            ref.child(keyVal).setValue(sched);
                                            ref.removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
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
        });
        return rowView;
    }
}
