package com.india.rapchik.Settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.india.rapchik.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.india.rapchik.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends RootFragment implements View.OnClickListener {
    View view;
    Context context;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_about_us, container, false);
        context=getContext();
        view.findViewById(R.id.Goback).setOnClickListener(this);
        view.findViewById(R.id.rateAppTxt).setOnClickListener(this);
        view.findViewById(R.id.meetRichieTxt).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.Goback:
                getActivity().onBackPressed();
                break;

            case R.id.rateAppTxt:
                openRateInPlaystore();
                break;

            case R.id.meetRichieTxt:
                open_meet_Richie_Fragment();
                break;
        }

    }

    public void openRateInPlaystore(){
        final String appPackageName = context.getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void open_meet_Richie_Fragment(){
        MeetRichieFragmant meetRichieFragmant = new MeetRichieFragmant();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, meetRichieFragmant).commit();
    }
}
