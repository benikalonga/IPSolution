package com.epsilonpros.ipsolution.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.epsilonpros.ipsolution.services.ServiceBackground;
import com.epsilonpros.ipsolution.utils.Keys;
import com.epsilonpros.ipsolution.utils.Plus;

/**
 * Broadcast receiver class to help start or stop the traffic monitoring service when the phone's
 * battery level is low or okay.
 */
public class BatteryReceiver extends BroadcastReceiver {

    /**
     * Receiver method for the phone boot that starts the traffic monitoring service
     */
    @Override
    public void onReceive(Context appContext, Intent ittIntent) {

        if (Plus.getBooleanPref(appContext, Keys.notificationEnabled, true) && Plus.getBooleanPref(appContext,Keys.notificationLowPower, false)) {
            if (ittIntent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                Log.i("BatteryReceiver", "Battery low. Stopping service");
                appContext.stopService(new Intent(appContext, ServiceBackground.class));
            } else {
                Log.i("BatteryReceiver", "Battery okay. Starting service");
                appContext.startService(new Intent(appContext, ServiceBackground.class));
            }
        }
    }
}