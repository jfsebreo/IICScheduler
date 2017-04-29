package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ruthere.application.fragment.CancelReason_Prof;
import com.utilities.Queue;

import java.io.File;
import java.io.IOException;

public class ViewSched extends AppCompatActivity {
    private CancelReason_Prof _cancel = null;
    Firebase ref;
    String positionuser;
    private StorageReference storageReference;
    int idforQueue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(ViewSched.this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            storageReference = FirebaseStorage.getInstance().getReference();
            setContentView(R.layout.activity_viewsched);
            Intent intentQueue = getIntent();
            SharedPreferences prefs = getSharedPreferences("IDOFUSER", MODE_PRIVATE);
            positionuser = prefs.getString("positionuser", "");
            int idQueue = intentQueue.getIntExtra("idQueue", 0);
            idforQueue = idQueue;
            final TextView textView_name_cancel = (TextView) findViewById(R.id.textView_name_cancel);
            final TextView textView_username_cancel = (TextView) findViewById(R.id.textView_username_cancel);
            final TextView textView_section_cancel = (TextView) findViewById(R.id.textView_section_cancel);
            final TextView textView_schedule_cancel = (TextView) findViewById(R.id.textView_schedule_cancel);
            final TextView textView_reason_cancel = (TextView) findViewById(R.id.textView_reason_cancel);
            ref = new Firebase("https://ruthere-5a89f.firebaseio.com/Queue/" + idQueue);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Queue queue = dataSnapshot.getValue(Queue.class);
                    textView_name_cancel.setText(queue.getName());
                    textView_username_cancel.setText(queue.getFrom());
                    textView_section_cancel.setText(queue.getGroup());
                    textView_schedule_cancel.setText(queue.getDate() + " | " + queue.getTime());
                    textView_reason_cancel.setText(queue.getReason());
                    storageReference.child("images/" + idforQueue).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Button buttonattachment  = (Button) findViewById(R.id.buttonAttachment);
                            buttonattachment.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Button buttonattachment  = (Button) findViewById(R.id.buttonAttachment);
                            buttonattachment.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
            _cancel = new CancelReason_Prof();
            getSupportFragmentManager().beginTransaction().add(R.id.FragmentContainer1, _cancel).commit();
            getSupportFragmentManager().beginTransaction().hide(_cancel).commit();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleFragmentView1(View view)
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cancelling schedule")
                .setMessage("Are you sure you want to cancel this schedule?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSupportFragmentManager().beginTransaction().show(_cancel).commit();}

                })
                .setNegativeButton("No", null)
                .show();
    }


    public void cancel(View view)
    {
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            if (positionuser.equals("STUDENT")) {
                startActivity(new Intent(this, StudentMain.class));
            } else if (positionuser.equals("PROFESSOR")) {
                startActivity(new Intent(this, ProfessorMain.class));
            }
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    EditText editText_cancel_reason = (EditText) findViewById(R.id.editText_cancel_reason);
                    ref.child("rejectreason").setValue("Cancelled: " + editText_cancel_reason.getText().toString());
                    ref.child("status").setValue("Discarded");
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

    public void attachments(View view) {
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(ViewSched.this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            storageReference.child("images/" + idforQueue).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    progressDialog.dismiss();
                    startActivity(browserIntent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Loading Failed.", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

}
