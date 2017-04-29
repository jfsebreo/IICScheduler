package com.ruthere.application.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruthere.application.activities.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CancelReason_Prof extends Fragment {


    public CancelReason_Prof() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel_reason_prof, container, false);
    }

}
