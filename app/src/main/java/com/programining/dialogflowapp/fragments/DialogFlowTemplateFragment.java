package com.programining.dialogflowapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
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

        final TextInputEditText etAppointment = parentView.findViewById(R.id.tiet_appointment);
        final TextInputEditText etResponse = parentView.findViewById(R.id.tiet_response);

        Button btnBookAppointment = parentView.findViewById(R.id.btn_book);
        Button btnResetAppointment = parentView.findViewById(R.id.btn_reset_appointment);
        Button btnClearResponse = parentView.findViewById(R.id.btn_reset_response);


        btnResetAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * remove what ever written in etAppointment and write the default appointment
                 */
                etAppointment.setText(getString(R.string.text_default_appointment));
            }
        });
        btnClearResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * remove what ever written in etResponse
                 */
                etResponse.setText("");
            }
        });




        return parentView;
    }

}
