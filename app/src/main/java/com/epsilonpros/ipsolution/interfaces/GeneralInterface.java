package com.epsilonpros.ipsolution.interfaces;

import java.io.Serializable;

/**
 * Created by KADI on 10/08/2018.
 */

public interface GeneralInterface<T> extends Serializable {
    public void onDone(T... t);
}
