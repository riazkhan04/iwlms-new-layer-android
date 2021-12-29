package com.orsac.android.pccfwildlife.MyUtils;

import static com.orsac.android.pccfwildlife.Activities.SplashScreen.signal_strength;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

public class PhoneCustomSignalStateListener extends PhoneStateListener {

    public int signalSupport = 0;

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        try {
            signalSupport = signalStrength.getGsmSignalStrength();
            signal_strength = signalStrength.getGsmSignalStrength();
            Log.d(getClass().getCanonicalName(), "------ gsm signal --> " + signalSupport);

            if (signalSupport > 30) {
                Log.d(getClass().getCanonicalName(), "Signal GSM : Good");
                signal_strength=signalSupport;

            } else if (signalSupport > 20 && signalSupport < 30) {
                Log.d(getClass().getCanonicalName(), "Signal GSM : Avarage");
                signal_strength=signalSupport;

            } else if (signalSupport < 20 && signalSupport > 3) {
                Log.d(getClass().getCanonicalName(), "Signal GSM : Weak");
                signal_strength=signalSupport;

            } else if (signalSupport < 3) {
                Log.d(getClass().getCanonicalName(), "Signal GSM : Very weak");
                signal_strength=signalSupport;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
