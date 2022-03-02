package com.epsilonpros.ipsolution.fragments.welcome;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.utils.Keys;
import com.epsilonpros.ipsolution.utils.Plus;
import com.stephentuso.welcome.WelcomeFinisher;
import com.stephentuso.welcome.WelcomePage;
import com.stephentuso.welcome.WelcomeUtils;

import net.qiujuer.genius.ui.widget.CheckBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoneFragment extends Fragment implements WelcomePage.OnChangeListener {

    private ViewGroup rootLayout;
    private CheckBox checkBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_done, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootLayout =  view.findViewById(R.id.viewRoot);
        checkBox =  view.findViewById(R.id.checkbox);
        checkBox.setChecked(Plus.getBooleanPref(getContext(),Keys.welcomeScreen, true));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                Plus.sharedPreferencePut(getContext(), Keys.welcomeScreen,isChecked,Keys.TYPE_BOOLEAN));

        view.findViewById(R.id.button).setOnClickListener(v -> new WelcomeFinisher(DoneFragment.this).finish());
    }

    @Override
    public void onWelcomeScreenPageScrolled(int pageIndex, float offset, int offsetPixels) {
        if (rootLayout != null)
            WelcomeUtils.applyParallaxEffect(rootLayout, true, offsetPixels, 0.3f, 0.2f);
    }

    @Override
    public void onWelcomeScreenPageSelected(int pageIndex, int selectedPageIndex) {
        //Not used
    }

    @Override
    public void onWelcomeScreenPageScrollStateChanged(int pageIndex, int state) {
        //Not used
    }
}
