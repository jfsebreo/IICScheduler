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

public class CustomList_Status extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;


    public CustomList_Status(Activity context, String[] web, Integer[] imageId) {
        super(context, R.layout.template_for_list_of_prof, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_for_list_of_prof, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
