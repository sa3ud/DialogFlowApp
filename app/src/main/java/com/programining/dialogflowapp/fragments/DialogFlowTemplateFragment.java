package com.programining.dialogflowapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.programining.dialogflowapp.R;
import com.programining.dialogflowapp.models.MyConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFlowTemplateFragment extends Fragment {

    private final static String AGENT_TOKEN = "ya29.c.Ko8ByAcm1YzDOU0wbo_yWRCznRW2aKLyyLLAeRf9RQJMfkmDgZrnryrSfB1UPx0ZWHZKZblzakHrLZWIIhKvWSOh2394wHotV00C1AmmiiPFvutIG3E0YHN1ZoOUyynfz7-vzk8MTeRr0xWA5L7VM6rF7ifoQMs8LsMTO_EtRMSnpp0z4jn9aymkJo8Y4xqXflQ";
    private TextInputEditText etAppointment;
    private TextInputEditText etResponse;
    private Context mContext;

    /**
     * in this class we will put sample code to communicate with DialogFlow
     */

    public DialogFlowTemplateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_dialog_flow_template, container, false);

        etAppointment = parentView.findViewById(R.id.tiet_appointment);
        etResponse = parentView.findViewById(R.id.tiet_response);

        Button btnBookAppointment = parentView.findViewById(R.id.btn_book);
        Button btnResetAppointment = parentView.findViewById(R.id.btn_reset_appointment);
        Button btnClearResponse = parentView.findViewById(R.id.btn_reset_response);

        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appointment = etAppointment.getText().toString();
                if (appointment.isEmpty()) {
                    etAppointment.setError("Please Write an appointment");
                } else {
                    callAgent(appointment);
                }
            }
        });

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

    /**
     * this function will call the agent + send the appointment to the agent
     */
    private void callAgent(String appointmentMessage) {

        /**
         * in order to call the agent we need to use volley
         *  TODO : agent call Prerequisite :
         *      #1 Generate Token using cmd : gcloud auth application-default print-access-token , this command won't work unless you set the environment variable :GOOGLE_APPLICATION_CREDENTIALS = ["pathToJsonKeyFile"]
         *      #2 Intent URL : you need get intent url, intent url format https://dialogflow.googleapis.com/v2/projects/" + agentId + "/agent/sessions/" + session + ":detectIntent"; agentId = project id / agent id ; session is a number , can be some random number
         *      #3 generate this json obj and pass it to the agent : - we can call it AgentMessageJson
         *      {
         *          "query_input": {
         *          "text": {
         *          "text": "book",
         *          "language_code": "en-US"
         *          }
         *          }
         *          }
         *
         *
         *       IMPORTANT  :
         *          #1 AGENT_TOKEN will hold generated token
         *          #2 MyConstants.getIntentUrl() will give use intent url
         *          #3 getAgentMessageJsonObj will generate AgentMessageJson
         *
         */


        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MyConstants.getIntentUrl(), getAgentMessageJsonObj(appointmentMessage)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //    Gson gson = new Gson();
                // AgentResponse agentResponse  = gson.fromJson(response.toString(), AgentResponse.class);
                //  addMessage(agentResponse.getAgentResponseMessage(), false);
//                    Toast.makeText(mContext, "Resopnse!", Toast.LENGTH_SHORT).show();
                //DisplayAgentMessage(response);
                etResponse.setText(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response.error", error.toString() + "/" + error.getMessage());
                Toast.makeText(mContext, "NO-Resopnse!", Toast.LENGTH_SHORT).show();

                error.printStackTrace();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", MyConstants.API_KEY_BEARER + AGENT_TOKEN);
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);


    }


    /**
     * this function should generate the following json obj
     * {
     * "query_input": {
     * "text": {
     * "text": "?",
     * "language_code": "en-US"
     * }
     * }
     * }
     *
     * @param msgForAgent : the message you want to pass to the agent
     * @return the above json obj
     */
    private JSONObject getAgentMessageJsonObj(String msgForAgent) {

        try {
            JSONObject jsonObj = new JSONObject();
            JSONObject jsonObjQueryInput = new JSONObject();

            JSONObject jsonObjText = new JSONObject();
            jsonObjText.put(MyConstants.API_JSON_KEY_TEXT, msgForAgent);
            jsonObjText.put(MyConstants.API_JSON_KEY_LANGUAGE_CODE, MyConstants.API_JSON_VALUE_LANGUAGE_CODE);
            jsonObjQueryInput.put(MyConstants.API_JSON_KEY_TEXT, jsonObjText);
            jsonObj.put(MyConstants.API_JSON_KEY_QUERY_INPUT, jsonObjQueryInput);

            return jsonObj;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
