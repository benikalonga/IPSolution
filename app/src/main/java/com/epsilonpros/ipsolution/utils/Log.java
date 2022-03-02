package com.epsilonpros.ipsolution.utils;

import android.support.annotation.NonNull;

/**
 * Created by KADI on 15/04/2018.
 */

public class Log {

    public static void i(@NonNull Object message){
        android.util.Log.i("AppTendance", String.valueOf(message));
    }

}
