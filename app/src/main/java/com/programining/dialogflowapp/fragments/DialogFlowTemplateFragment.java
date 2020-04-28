package com.programining.dialogflowapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.programining.dialogflowapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFlowTemplateFragment extends Fragment {

    /**
     * in this class we will put sample code to communicate with DialogFlow
     */

    public DialogFlowTemplateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_dialog_flow_template, container, false);

        return parentView;
    }

}
