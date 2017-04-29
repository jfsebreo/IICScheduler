package com.utilities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.ruthere.application.activities.R;


/**
 * Created by Jace on 10/14/2016.
 */
public class UserCursorAdapter extends CursorAdapter {

    public UserCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.template_for_list_of_prof, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView nameView = (TextView)view.findViewById(R.id.textView_name);
        TextView deptView = (TextView)view.findViewById(R.id.textView_dept);

        nameView.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserDetails.NAME_COLUMN)));
        deptView.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserDetails.DEPTSECT_COLUMN)));

    }
}
