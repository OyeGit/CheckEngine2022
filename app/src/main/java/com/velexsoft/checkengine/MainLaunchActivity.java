package com.velexsoft.checkengine;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.velexsoft.checkengine.utilities.LogFileNew;
import java.util.Calendar;

public class MainLaunchActivity extends Activity {

    final Context context = this;
    boolean registeredAlready = false;
    int registerProcess = 0;
    public static final String PREFS_NAME = ConnActivityParent.PREFS_NAME;
    String sessionID;
    String logFileName = "checkEngineLog.txt";
    LogFileNew l ;
    SharedPreferences settings;
    String globalMACAddress;
    String registeredAlreadyKey;
    SharedPreferences sharedPreferences;
    boolean logErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainLaunchActivity");
        setContentView(R.layout.activity_main_launch);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_reports_info, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_communication, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        registeredAlreadyKey = "registeredAlready";
        Thread myThread;

        settings = getSharedPreferences(PREFS_NAME, 0);

        System.out.println ( "registeredAlready = "+registeredAlready  );
        registerProcess = settings.getInt("registerProcess", 0);
        System.out.println ( "registerProcess = "+registerProcess  );
        logErrors =  sharedPreferences.getBoolean("enable_file_logging", false);
        // registeredAlready = false;
        // registerProcess = 1;

        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                l = new LogFileNew();
                sessionID = getDateTimeSessionID();
                ConnActivityParent.sessionCode = sessionID;
                ConnActivityParent.logFileName = logFileName;
            }
        });
        myThread.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String state = "X";
        registeredAlready = settings.getBoolean(registeredAlreadyKey, false);
        registerProcess = settings.getInt("registerProcess", 0);
        System.out.println( "XX - registeredAlready = "+registeredAlready );
        System.out.println( "XX - registerProcess = "+registerProcess );

        if (!registeredAlready) { // if not registered
            System.out.println( "XX - goRegistrationScreen" );
            goRegistrationScreen(context);
        } else {
            System.out.println( "XX - goConnectionScreen" );
            goConnectionScreen(context);
        }

        if (!registeredAlready) {
            if ( registerProcess<2  ) {
                state = "goReg";
            } else if ( registerProcess==2  ) {
                state = "goCho";
            }
        } else {
            globalMACAddress = settings.getString("globalMACAddress", "");
            System.out.println ( "INFO      : registeredAlready - globalMACAddress = "+globalMACAddress );
            state = "goCon";
        }

        registeredAlready = settings.getBoolean(registeredAlreadyKey, false);
        registerProcess = settings.getInt("registerProcess", 0);
        if (!registeredAlready) {
            if ( registerProcess<2  ) {
                if ( state.equals("goReg"))   goRegistrationScreen(context);
            } else if ( registerProcess==2  ) {
                if ( state.equals("goCho"))  goChooseActivityScreen(context);
            }
        } else {
            globalMACAddress = settings.getString("globalMACAddress", "");
            System.out.println ( "INFO      : registeredAlready - globalMACAddress = "+globalMACAddress );
            if ( state.equals("goCon"))  goConnectionScreen(context);
        }

        finish();
    }

    public String getDateTimeSessionID()  {
        Calendar c = Calendar.getInstance();
        int ss = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);
        int mm  = c.get(Calendar.MINUTE);
        int hh  = c.get(Calendar.HOUR_OF_DAY);
        int dy = c.get(Calendar.DAY_OF_MONTH);
        int mth = c.get(Calendar.MONTH);
        int yr  = c.get(Calendar.YEAR);
        return  ""+yr+(mth+1)+dy+hh+mm+ms;
    }

    public void goRegistrationScreen (Context vcontext) {
        System.out.println( "XX - About to call RegistrationScreen " );
        try {
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                Intent intentMain = new Intent(context, RegistrationScreen.class);
                overridePendingTransition(R.anim.fade_in_dialog_v, R.anim.fade_out_dialog_v);
                startActivity(intentMain);
                }
            });
            myThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goConnectionScreen (Context vcontext) {
        System.out.println( "XX - About to call ConnectionScreen " );
        try {

            final Context locContext = vcontext;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(locContext , ConnectionScreen.class);
                    startActivity(intentMain);
                    overridePendingTransition(R.anim.fade_in_dialog, R.anim.fade_out_dialog);

                }
            });
            myThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void goChooseActivityScreen (Context vcontext) {
        try {

            final Context locContext = vcontext;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(locContext, ChooserActivity.class);
                    overridePendingTransition(R.anim.fade_in_dialog_v, R.anim.fade_out_dialog_v);
                    startActivity(intentMain);

                }
            });
            myThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
