package com.velexsoft.checkengine.utilities;

import android.os.Build;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by WoleProject on 10/27/2015.
 */
public class RegScreen {

    public RegScreen () {

    }

    public String getTimeZone () {
        TimeZone tZ = TimeZone.getDefault();
        return String.valueOf(tZ);
    }

    public String getFingerPrint() {
        String vFingerPrint = Build.FINGERPRINT;
        return vFingerPrint;
    }

    public String getReleaseVersion() {
       String vReleaseVersion = Build.VERSION.RELEASE;
        return vReleaseVersion;
    }

    public String getTime (   ) {
        Calendar c = Calendar.getInstance();
        int ss = c.get(Calendar.SECOND);
        int mm = c.get(Calendar.MINUTE);
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int dy = c.get(Calendar.DAY_OF_MONTH);
        int mth = c.get(Calendar.MONTH);
        mth++;
        int yr = c.get(Calendar.YEAR);
        return yr+"/"+mth+"/"+dy+" "+hh+":"+mm+":"+ss;
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK:"+sdkVersion+"("+release+")";
    }
}
