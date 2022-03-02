package com.epsilonpros.ipsolution.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.epsilonpros.ipsolution.services.ServiceBackground;
import com.epsilonpros.ipsolution.utils.Plus;
import com.epsilonpros.ipsolution.utils.Utils;

public class ReceiverBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       if (Plus.getBooleanPref(context, ServiceBackground.class.getSimpleName(),true) && !Utils.isServiceRunning(context, ServiceBackground.class))
           context.startService(new Intent(context, ServiceBackground.class));
    }
}
