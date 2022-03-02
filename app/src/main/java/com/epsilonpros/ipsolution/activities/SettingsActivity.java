package com.epsilonpros.ipsolution.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.services.ServiceBackground;
import com.epsilonpros.ipsolution.utils.Keys;
import com.epsilonpros.ipsolution.utils.Log;

/**
 * Settings activity that allows the user to start or stop the service and also change the polling
 * interval
 */
public class SettingsActivity extends PreferenceActivity {

    private boolean isBounded;
    private ServiceBackground backgroundService;

    /**
     * Connection class between the activity and the service to be able to invoke service methods
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            isBounded = false;
            backgroundService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            isBounded = true;
            ServiceBackground.LocalBinder mLocalBinder = (ServiceBackground.LocalBinder) service;
            backgroundService = mLocalBinder.getServerInstance();
        }
    };

    /**
     * OnStart method of the activity that establishes a connection with the service by binding to it
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(getApplicationContext(), ServiceBackground.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    /**
     * OnStop method of the activity that destroys the connection with the service by unbinding from
     * it
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (isBounded) {
            unbindService(mConnection);
            isBounded = false;
        }
    }

    /**
     * Post create method that bind each of the preferences to a listener so that updating the
     * preferences will fire the listeners to trigger updates to the notification
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference(Keys.notificationEnabled).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            /**
             * Preference change listener that handles the starting and the stopping of the service
             * depending on whether the the switch is toggled or not.
             */
            @Override
            public boolean onPreferenceChange(Preference enabledPreference, Object newValue) {
                if ((Boolean) newValue) {
                    backgroundService.showNotification();
                } else {
                    backgroundService.hideNotification();
                }
                return true;
            }
        });

        findPreference(Keys.notificationVisibleInLockScreen).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            /**
             * Preference change listener that handles whether the notification should be displayed
             * on the lockscreen depending on whether the the switch is toggled or not.
             */
            @Override
            public boolean onPreferenceChange(Preference enabledPreference, Object newValue) {
                backgroundService.visibilityPublic((Boolean) newValue);
                return true;
            }
        });
        findPreference(Keys.notificationLowPower).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference enabledPreference, Object newValue) {
                Log.i("Power "+(boolean)newValue);
                return true;
            }
        });

    }
}
