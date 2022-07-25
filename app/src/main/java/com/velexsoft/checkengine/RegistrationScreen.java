package com.velexsoft.checkengine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;

public class RegistrationScreen extends ConnActivityParent {

    final Context context = this;
    ProgressBar mLoadingIndicator;
    EditText editText;
    SharedPreferences settings;
    boolean registeredAlready = false;
    String versionName = "";
    int registerProcess = 0;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_registration_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("XX - Begin RegistrationScreen onCreate...");
        super.onCreate(savedInstanceState);
        //setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        l.fileName = ConnActivityParent.logFileName;
        editText = (EditText) findViewById( R.id.vSerialNumber );
        registeredAlreadyKey = "registeredAlready";
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        System.out.println("XX - Begin RegistrationScreen onPostCreate...");
        super.onPostCreate(savedInstanceState);

        settings = getSharedPreferences(PREFS_NAME, 0);
        TextView tx = (TextView) findViewById(R.id.title2) ;
        /*
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("registerProcess", 0  );
        editor.commit();

         */

        registeredAlready = settings.getBoolean(registeredAlreadyKey, false);
        registerProcess = settings.getInt("registerProcess", 0);
        System.out.println ( "XX - RegistrationScreen   registerProcess = "+registerProcess   );
        System.out.println ( "XX - RegistrationScreen   registeredAlready = "+registeredAlready   );

        if (registerProcess==1) {
            setThingsRight();
        } else if ( registerProcess==2 ) {
            goConnectionScreen(context);
        }

        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch( Exception e ){
           e.printStackTrace();
        }
        tx.setText( getString(R.string.title2) + " " + versionName );

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        settings = getSharedPreferences(PREFS_NAME, 0);

        if ( isNetworkAvailable() ) {

            toastIt("Internet access is available...");

        } else {
           velexMessageNow(context, "Internet access is NOT available..." );
        }
        writeGlobalMACAddress2();

        registerProcess = settings.getInt("registerProcess", 0);
    }

    @Override
    public void onRestart() {
        System.out.println( "on Restart");
        super.onRestart();
    }

    @Override
    public void onResume() {
        System.out.println( "on Resume");
        super.onResume();
    }


    public void setThingsRight(  ) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
        linearLayout.setVisibility(View.INVISIBLE);
        Fragment fragment = new OverviewMonFragments();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.fragment_output, fragment);
        transaction.commit();
    }

    public boolean isConecctedToInternet() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public void writeGlobalMACAddress2() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("globalMACAddress", "" );
        editor.commit();
    }

    public void velexMessageNow(Context pcontext, String exitMsg){
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pcontext);
            alertDialogBuilder.setMessage(exitMsg);
            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch ( Exception e ) {

        }
    }

    public void goConnectionScreen (Context vcontext) {
        try {
            final Context locContext = vcontext;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(locContext, ConnectionScreen.class);
                    intentMain.setFlags(intentMain.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

