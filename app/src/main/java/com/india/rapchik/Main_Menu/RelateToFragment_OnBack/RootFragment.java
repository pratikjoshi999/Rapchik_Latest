package com.india.rapchik.Main_Menu.RelateToFragment_OnBack;

import androidx.fragment.app.Fragment;

/**
 * Created by Pratik on 6/30/2020.
 */

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImplimentation(this).onBackPressed();
    }
}