package com.programining.dialogflowapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.programining.dialogflowapp.R;
import com.programining.dialogflowapp.interfaces.MediatorInterface;

public class MainActivity extends AppCompatActivity implements MediatorInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * enables changing fragments dynamically
     *
     * @param fragmentToDisplay
     * @param fragmentTag
     */
    @Override
    public void changeFragmentTo(Fragment fragmentToDisplay, String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fl_host, fragmentToDisplay, fragmentTag);
        if (fm.findFragmentByTag(fragmentTag) == null) {
            ft.addToBackStack(fragmentTag);
        }
        ft.commit();
    }


    /**
     * enables fragments to call dynamically!
     */
    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
