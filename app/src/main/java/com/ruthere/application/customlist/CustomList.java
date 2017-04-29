package com.ruthere.application.customlist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruthere.application.activities.R;


/**
 * Created by Jace on 11/10/2016.
 */

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    private final Integer[] profImg;
    private final String[] dept;



    public CustomList(Activity context, String[] web, Integer[] imageId, Integer[] profImg, String [] dept) {
        super(context, R.layout.template_for_list_of_prof, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.profImg = profImg;
        this.dept = dept;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_for_list_of_prof, null, true);

        ImageView profView = (ImageView) rowView.findViewById(R.id.profile);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_name);
        TextView txtDept = (TextView) rowView.findViewById(R.id.textView_dept);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        profView.setImageResource(profImg[position]);
        txtTitle.setText(web[position]);
        txtDept.setText(dept[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
