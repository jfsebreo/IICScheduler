package com.ruthere.application.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ruthere.application.activities.R;
import com.ruthere.application.activities.ConsultationSchedule;
import com.utilities.Config;
import com.utilities.ListOfProfessorAdapter;
import com.utilities.Users;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListProfFragment extends Fragment {

    public ListProfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_listprof, container, false);

        Firebase.setAndroidContext(getActivity());
        final Firebase ref = new Firebase(Config.FIREBASE_USERS);

        ref.addValueEventListener(new ValueEventListener() {
            ListView list;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> name = new ArrayList<String> ();
                ArrayList<String> dept = new ArrayList<String> ();
                ArrayList<String> status = new ArrayList<String> ();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Users users = postSnapshot.getValue(Users.class);
                    if (users.getPosition().equals("PROFESSOR"))
                    {
                        name.add(users.getEmail());
                        dept.add(users.getGroup());
                    }
                }



                list = (ListView) view.findViewById(R.id.listview_listOfProf);

                ListOfProfessorAdapter adapter = new ListOfProfessorAdapter(getActivity(), name, dept, status);
                list.setAdapter(adapter);
                SharedPreferences prefs = getActivity().getSharedPreferences("IDOFUSER", MODE_PRIVATE);
                final String positionuser = prefs.getString("positionuser", "");
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (positionuser.equals("STUDENT"))
                        {
                            Intent intent = new Intent(getActivity(), ConsultationSchedule.class);
                            intent.putExtra("professor", (((TextView) view.findViewById(R.id.textView_name)).getText().toString()));
                            intent.putExtra("email", parent.getAdapter().getItem(position).toString());
                            startActivity(intent);
                        }
                    }
                });

                //ref.removeEventListener(this);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }


}
