package com.ruthere.application.customlist;

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

public class CustomList_Pending extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name;
    private final ArrayList<Integer> profImg;
    private final ArrayList<String> subj;

    public CustomList_Pending(Activity context, ArrayList<String> name , ArrayList<Integer> profImg, ArrayList<String> subj) {
        super(context, R.layout.template_for_list_of_pending, name);
        this.context = context;
        this.name = name;
        this.profImg = profImg;
        this.subj = subj;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_for_list_of_pending, null, true);
        ImageView profView = (ImageView) rowView.findViewById(R.id.profilePending);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtPending);
        TextView txtSubj = (TextView) rowView.findViewById(R.id.subjPending);
        profView.setImageResource(profImg.get(position));
        txtTitle.setText(name.get(position));
        txtSubj.setText(subj.get(position));
        return rowView;
    }
}
