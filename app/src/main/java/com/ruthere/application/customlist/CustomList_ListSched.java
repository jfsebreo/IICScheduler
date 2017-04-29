package com.ruthere.application.customlist;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ruthere.application.activities.AcceptRejectSchedPage;
import com.ruthere.application.activities.R;
import com.ruthere.application.activities.ViewDiscardedSched;
import com.ruthere.application.activities.ViewSched;
import com.utilities.Config;
import com.utilities.Queue;
import com.utilities.Schedule;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jinnilin on 3/5/2017.
 */

public class CustomList_ListSched extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> time;
    private final ArrayList<String> to;
    private final ArrayList<String> from;
    private final ArrayList<String> status;
    private final ArrayList<Integer> idQueue;
    private final String username;
    private final String positionuser;

    public CustomList_ListSched(Activity context, ArrayList<String> time , ArrayList<String> to, ArrayList<String> from, ArrayList<String> status,  ArrayList<Integer> idQueue, String username, String positionuser) {
        super(context, R.layout.template_for_list_of_list_sched, time);
        this.context = context;
        this.time = time;
        this.to = to;
        this.from = from;
        this.status = status;
        this.idQueue = idQueue;
        this.username = username;
        this.positionuser = positionuser;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_for_list_of_list_sched, null, true);
        TextView txtboxtime = (TextView) rowView.findViewById(R.id.time);
        TextView txtboxto = (TextView) rowView.findViewById(R.id.to);
        TextView txtboxfrom = (TextView) rowView.findViewById(R.id.from);
        TextView txtboxstatus = (TextView) rowView.findViewById(R.id.status);
        Button updateImageView = (Button) rowView.findViewById(R.id.editbutton);
        updateImageView.setTag(position);
        txtboxtime.setText(time.get(position));
        txtboxto.setText(to.get(position));
        txtboxfrom.setText(from.get(position));
        txtboxstatus.setText(status.get(position));
        updateImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int position=(Integer)v.getTag();
                final Firebase ref1 = new Firebase(Config.FIREBASE_QUEUE);
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent intent = new Intent();
                        if (status.get(position).equals("Approved")){
                            if (positionuser.equals("STUDENT")) {
                                intent = new Intent(getContext(), ViewSched.class);
                            }
                            if (positionuser.equals("PROFESSOR")) {
                                intent = new Intent(getContext(), AcceptRejectSchedPage.class);
                            }
                        }
                        else if (status.get(position).equals("Discarded")){
                            intent = new Intent(getContext(), ViewDiscardedSched.class);
                        }
                        else if (status.get(position).equals("Pending")){
                            if (positionuser.equals("STUDENT")) {
                                intent = new Intent(getContext(), ViewSched.class);
                            }
                            if (positionuser.equals("PROFESSOR")) {
                                intent = new Intent(getContext(), AcceptRejectSchedPage.class);
                            }
                        }
                        intent.putExtra("idQueue", idQueue.get(position));
                        getContext().startActivity(intent);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
        return rowView;
    }
}
