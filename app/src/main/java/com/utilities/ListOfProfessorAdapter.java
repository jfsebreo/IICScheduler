package com.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruthere.application.activities.R;

import java.util.ArrayList;


/**
 * Created by Jace on 11/10/2016.
 */

public class ListOfProfessorAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name;
    private final ArrayList<String> dept;
    private final ArrayList<String> status;



    public ListOfProfessorAdapter(Activity context, ArrayList<String> name, ArrayList<String> dept,
                                  ArrayList<String> status) {
        super(context, R.layout.template_for_list_of_prof, name);
        this.context = context;
        this.name = name;
        this.dept = dept;
        this.status = status;
    }



    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_for_list_of_prof, null, true);

        ImageView profView = (ImageView) rowView.findViewById(R.id.profile);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_name);
        TextView txtDept = (TextView) rowView.findViewById(R.id.textView_dept);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        //ImageView image = (ImageView) rowView.findViewById(R.id.imageView_prof_list_bg);

        profView.setImageResource(R.mipmap.def_prof);
        txtTitle.setText(name.get(position));
        txtDept.setText(dept.get(position));


        if (position == 0) {
            //image.setImageResource(R.drawable.list_bg_sample);

        }
        else if (position % 2 == 1) {
            //image.setImageResource(R.drawable.list_bg_alt2);
        }
        else if (position % 2 == 0) {
           // image.setImageResource(R.drawable.list_bg_sample);
        }

        //notifyDataSetChanged();
        return rowView;
    }
}
