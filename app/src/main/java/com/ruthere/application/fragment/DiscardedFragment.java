package com.ruthere.application.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ruthere.application.activities.R;
import com.ruthere.application.activities.ViewDiscardedSched;
import com.ruthere.application.activities.ViewSched;
import com.ruthere.application.customlist.CustomList_Approved;
import com.ruthere.application.customlist.CustomList_Discarded;
import com.utilities.Config;
import com.utilities.Queue;

import java.util.ArrayList;

/**
 * Created by Ratan on 7/29/2015.
 */
public class DiscardedFragment extends Fragment {
    ListView list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        SharedPreferences prefs = this.getActivity().getSharedPreferences("IDOFUSER", Context.MODE_PRIVATE);
        final String usernameuser = prefs.getString("usernameuser", "");
        final String positionuser = prefs.getString("positionuser", "");
        final View v = inflater.inflate(R.layout.discarded_layout, null);
        final Firebase ref = new Firebase(Config.FIREBASE_QUEUE);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> name = new ArrayList<String>();
                final ArrayList<String> subj = new ArrayList<String>();
                final ArrayList<Integer> profImg = new ArrayList<Integer>();
                final ArrayList<Integer> idQueue = new ArrayList();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Queue queue = postSnapshot.getValue(Queue.class);
                    if (positionuser.equals("PROFESSOR") && usernameuser.equals(queue.getTo()) && queue.getStatus().equals("Discarded"))
                    {
                        name.add(queue.getName());
                        subj.add(queue.getSubject());
                        idQueue.add(Integer.parseInt(postSnapshot.getKey().toString()));
                    }
                    if (positionuser.equals("STUDENT") && usernameuser.equals(queue.getFrom()) && queue.getStatus().equals("Discarded"))
                    {
                        name.add(queue.getTo());
                        subj.add(queue.getSubject());
                        idQueue.add(Integer.parseInt(postSnapshot.getKey().toString()));
                    }
                }
                for (int i = 0; i < name.size(); i++) {
                    profImg.add(R.mipmap.def_prof);
                }
                if (!name.isEmpty()) {
                    list = (ListView) v.findViewById(R.id.list_discarded);
                    CustomList_Discarded adapter = new
                            CustomList_Discarded(getActivity(), name, profImg, subj);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Intent intent = new Intent(getActivity(), ViewDiscardedSched.class);
                            intent.putExtra("idQueue", idQueue.get(position));
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Cancelled" + firebaseError.getMessage());
            }
        });

        return v;
    }


}
