package com.ruthere.application.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ruthere.application.fragment.RejectReason_Prof;
import com.utilities.Queue;

public class AcceptRejectSchedPage extends AppCompatActivity {
    private RejectReason_Prof _reject = null;
    Firebase ref;
    private StorageReference storageReference;
    int idforQueue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_pending_page);
        Intent intentQueue = getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();
        int idQueue = intentQueue.getIntExtra("idQueue", 0);
        idforQueue = idQueue;
        final TextView textView_name_pending = (TextView) findViewById(R.id.textView_name_pending);
        final TextView textView_username_pending = (TextView) findViewById(R.id.textView_username_pending);
        final TextView textView_section_pending = (TextView) findViewById(R.id.textView_section_pending);
        final TextView textView_schedule_pending = (TextView) findViewById(R.id.textView_schedule_pending);
        final TextView textView_reason_pending = (TextView) findViewById(R.id.textView_reason_pending);
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            ref = new Firebase("https://ruthere-5a89f.firebaseio.com/Queue/" + idQueue);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Queue queue = dataSnapshot.getValue(Queue.class);
                    textView_name_pending.setText(queue.getName());
                    textView_username_pending.setText(queue.getFrom());
                    textView_section_pending.setText(queue.getGroup());
                    textView_schedule_pending.setText(queue.getDate() + " | " + queue.getTime());
                    textView_reason_pending.setText(queue.getReason());
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
            _reject = new RejectReason_Prof();
            getSupportFragmentManager().beginTransaction().add(R.id.FragmentContainer1, _reject).commit();
            getSupportFragmentManager().beginTransaction().hide(_reject).commit();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleFragmentView1(View view)
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Rejecting schedule")
                .setMessage("Are you sure you want to reject this schedule?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSupportFragmentManager().beginTransaction().show(_reject).commit();}

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void accept(View view)
    {
        if (isNetworkAvailable()) {
            startActivity(new Intent(this, ProfessorMain.class));
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ref.child("rejectreason").setValue("None");
                    ref.child("status").setValue("Approved");
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
            Toast.makeText(this, "Accepted the schedule!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void reject(View view)
    {
        if (isNetworkAvailable()) {
            startActivity(new Intent(this, ProfessorMain.class));
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait.");
            progressDialog.show();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    EditText editText_reject_reason = (EditText) findViewById(R.id.editText_reject_reason);
                    ref.child("rejectreason").setValue("Rejected: " + editText_reject_reason.getText().toString());
                    ref.child("status").setValue("Discarded");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            Toast.makeText(this, "Rejected the schedule!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void attachments(View view) {
        if (isNetworkAvailable()) {
            final ProgressDialog progressDialog = new ProgressDialog(AcceptRejectSchedPage.this);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
