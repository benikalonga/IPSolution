package com.epsilonpros.ipsolution.activities;

import android.support.v4.app.Fragment;

import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.fragments.welcome.DoneFragment;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.FullscreenParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by stephentuso on 10/5/16.
 */

public class WelcomeScreenActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.welcomeDefault)
                .swipeToDismiss(false)
                .exitAnimation(android.R.anim.fade_out)
                .page(new FullscreenParallaxPage(R.layout.fragment_welcome_first))
                .page(new FullscreenParallaxPage(R.layout.fragment_welcome_second))
                .page(new FullscreenParallaxPage(R.layout.fragment_welcome_third))
                .page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new DoneFragment();
                    }
                })
                .useCustomDoneButton(true)
                .build();
    }

}
