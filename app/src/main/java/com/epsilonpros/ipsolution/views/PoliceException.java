package com.epsilonpros.ipsolution.views;

import android.util.Log;

/**
 * Created by KADI on 18/03/2018.
 */

public class PoliceException extends Exception{

    public PoliceException() {
        Log.i(PoliceException.class.getSimpleName(), "Ne peut pas construire cette police");
    }

    @Override
    public void printStackTrace() {
        Log.i(PoliceException.class.getSimpleName(),"Verifier le nom ou le chemin qui pointe vers la police");
        super.printStackTrace();
    }
}
