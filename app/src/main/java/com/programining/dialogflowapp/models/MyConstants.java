package com.programining.dialogflowapp.models;

import java.util.Random;

public class MyConstants {
    /**
     * This class will hold the common Constants
     */


    public static final String API_KEY_BEARER = "Bearer ";

    public static final String API_JSON_KEY_QUERY_INPUT = "query_input";
    public static final String API_JSON_KEY_TEXT = "text";
    public static final String API_JSON_KEY_LANGUAGE_CODE = "language_code";
    public static final String API_JSON_VALUE_LANGUAGE_CODE = "en-US";
    public static final String KEY_VISION_API_KEY = "AIzaSyB4SjDMWWJC2TrApjP0w-UQIrNhcRfYuOk";





    /**
     * Agent Id or Project id > you can find it at DialogFlow settings page named as Project ID, also it appears in the url
     * https://dialogflow.cloud.google.com/#/editAgent/yousuf-dialogflow/  >> yousuf-dialogflow is the ProjectID
     */


    private static final String API_KEY_AGENT_ID_ = "saud-agent-ylkpej";


    /**
     * this function will Generate intent url
     *
     * @return : return intent url as a String
     */


    //  You successfully booked an appointment on  #AppointmentContext.date , #AppointmentContext.city ,#AppointmentContext.time

    public static final String getIntentUrl() {
        int session = getSessionNumber();

        String url = "https://dialogflow.googleapis.com/v2/projects/" + API_KEY_AGENT_ID_ + "/agent/sessions/" + session + ":detectIntent";

        return url;
    }


    /**
     * this function will generate a random number between 0 and 9999
     *
     * @return random number as int
     */

    private static int getSessionNumber() {
        //generate a random number
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(9999);
    }


}
