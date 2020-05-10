package com.programining.dialogflowapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.programining.dialogflowapp.R;
import com.programining.dialogflowapp.fragments.UploadImageFragment;
import com.programining.dialogflowapp.interfaces.MediatorInterface;

public class MainActivity extends AppCompatActivity implements MediatorInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeFragmentTo(new UploadImageFragment(), UploadImageFragment.class.getSimpleName());
    }

    /**
     * enables changing fragments dynamically
     *
     * @param fragmentToDisplay : allow you to pass the fragment you want to display
     * @param fragmentTag : allow to pass the fragment tag
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


        //,kh.ih;oih;oig;ouhlb.kg;hv



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
