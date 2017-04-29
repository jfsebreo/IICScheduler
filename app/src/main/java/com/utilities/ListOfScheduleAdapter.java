package com.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ruthere.application.activities.R;

import java.util.ArrayList;


/**
 * Created by Jace on 11/10/2016.
 */

public class ListOfScheduleAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> time;
    private final ArrayList<String> day;


    public ListOfScheduleAdapter(Activity context, ArrayList<String> time, ArrayList<String> day) {
        super(context, R.layout.template_for_list_schedule, time);
        this.context = context;
        this.time = time;
        this.day = day;
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        String sched;
        View rowView = inflater.inflate(R.layout.template_for_list_schedule, null, true);


        TextView timeView = (TextView) rowView.findViewById(R.id.textView_time);
        sched = time.get(position) + " - " + day.get(position);
        timeView.setText(sched);


        return rowView;
    }
}
