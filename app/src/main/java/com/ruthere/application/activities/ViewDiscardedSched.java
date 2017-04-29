package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ruthere.application.fragment.CancelReason_Prof;
import com.utilities.Queue;

public class ViewDiscardedSched extends AppCompatActivity {
    private CancelReason_Prof _cancel = null;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdiscardedsched);
        Intent intentQueue = getIntent();
        int idQueue = intentQueue.getIntExtra("idQueue", 0);
        final TextView textView_name_view = (TextView) findViewById(R.id.textView_name_view);
        final TextView textView_username_view = (TextView) findViewById(R.id.textView_username_view);
        final TextView textView_section_view = (TextView) findViewById(R.id.textView_section_view);
        final TextView textView_schedule_view = (TextView) findViewById(R.id.textView_schedule_view);
        final TextView textView_reason_view = (TextView) findViewById(R.id.textView_reason_view);
        final TextView textView_discardedreason_view = (TextView) findViewById(R.id.textView_discardreason_view);
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            ref = new Firebase("https://ruthere-5a89f.firebaseio.com/Queue/" + idQueue);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Queue queue = dataSnapshot.getValue(Queue.class);
                    textView_name_view.setText(queue.getName());
                    textView_username_view.setText(queue.getFrom());
                    textView_section_view.setText(queue.getGroup());
                    textView_schedule_view.setText(queue.getDate() + " | " + queue.getTime());
                    textView_reason_view.setText(queue.getReason());
                    textView_discardedreason_view.setText(queue.getRejectreason());
                    progressDialog.dismiss();
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
