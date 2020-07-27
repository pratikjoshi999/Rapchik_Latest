package com.india.rapchik.Settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.india.rapchik.Accounts.Request_Varification_F;
import com.india.rapchik.Main_Menu.MainMenuActivity;
import com.india.rapchik.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.india.rapchik.R;
import com.india.rapchik.SimpleClasses.Variables;
import com.india.rapchik.SimpleClasses.Webview_F;

/**
 * A simple {@link Fragment} subclass.
 */
public class Setting_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    public Setting_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_setting, container, false);
        context=getContext();

        view.findViewById(R.id.Goback).setOnClickListener(this);
        view.findViewById(R.id.request_verification_txt).setOnClickListener(this);
        view.findViewById(R.id.privacy_policy_txt).setOnClickListener(this);
        view.findViewById(R.id.logout_txt).setOnClickListener(this);
        view.findViewById(R.id.logoutBtn).setOnClickListener(this);
        view.findViewById(R.id.about_us_txt).setOnClickListener(this);
        view.findViewById(R.id.feedback_txt).setOnClickListener(this);
        view.findViewById(R.id.meetRichieTxt).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.Goback:
                getActivity().onBackPressed();
                break;

            case R.id.request_verification_txt:
                Open_request_verification();
                break;

            case R.id.privacy_policy_txt:
                Open_Privacy_url();
                break;

            case R.id.logout_txt:
                Logout();
                break;

            case R.id.logoutBtn:
                Logout();
                break;

            case R.id.about_us_txt:
                open_Aboutus_Fragment();
                break;

            case R.id.feedback_txt:
                open_Mail_for_feedback();
                break;

            case R.id.meetRichieTxt:
                open_meet_Richie_Fragment();
                break;
        }
    }


    public void Open_request_verification(){
        Request_Varification_F request_varification_f = new Request_Varification_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, request_varification_f).commit();
    }

    public void Open_Privacy_url(){
        Webview_F webview_f = new Webview_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle bundle=new Bundle();
        bundle.putString("url",Variables.privacy_policy);
        bundle.putString("title","Privacy Policy");
        webview_f.setArguments(bundle);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, webview_f).commit();
    }

    // this will erase all the user info store in locally and logout the user
    public void Logout(){
        SharedPreferences.Editor editor= Variables.sharedPreferences.edit();
        editor.putString(Variables.u_id,"").clear();
        editor.putString(Variables.u_name,"").clear();
        editor.putString(Variables.u_pic,"").clear();
        editor.putBoolean(Variables.islogin,false).clear();
        editor.commit();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainMenuActivity.class));
    }

    public void open_Aboutus_Fragment(){
        AboutUsFragment aboutUsFragment = new AboutUsFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, aboutUsFragment).commit();
    }

    public void open_meet_Richie_Fragment(){
        MeetRichieFragmant meetRichieFragmant = new MeetRichieFragmant();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, meetRichieFragmant).commit();
    }

    public void open_Mail_for_feedback(){

        String toMail="developer.kalakaar@gmail.com";
        String subMail="Help and Feedback";
        String bodyMail="Hi Team,";
        String mailto = "mailto:"+toMail +"?cc=" + "" +
                "&subject=" + Uri.encode(subMail) +"&body=" + Uri.encode(bodyMail);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));
        startActivity(emailIntent);


    }


}
