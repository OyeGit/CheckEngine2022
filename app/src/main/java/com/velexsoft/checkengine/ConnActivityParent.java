package com.velexsoft.checkengine;


import android.app.ProgressDialog;

import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.velexsoft.checkengine.utilities.LogFileNew;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ooyeg on 9/15/2017.
 */

public abstract class ConnActivityParent extends AppCompatActivity {

    static public String currVinNumber = "";
    static public String currProtocol = "";
    static public String currProtocolGroup = "";
    static public String currProtocolDesc = "";
    static public String sessionCode = "SessionCode";
    static public String logFileName = "checkEngineLog.txt";
    static public String ignitionType = "Monitors";

    static public String registeredAlreadyKey = "";
    static protected String lineMode = "online";
    static protected String vSerialNumber = "";
    static public boolean logErrors;
    private Toast mToast;
    static String header1;
    static String header2;
    static String header3;
    static String response0101Raw;

    public Context getContext() {
        return context;
    }

    public Context context = this;

    static public int  threadSleepSendCmd = 2;
    static boolean mBound = false;
    public static LogFileNew l;

    public static Integer screenWidth = 0;
    public static Integer screenHeight = 0;
    SharedPreferences settings;

    public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final String PREFS_NAME = "CheckEnginePrefsFile";
    SharedPreferences sharedPreferences;
    boolean showtips;

    static ArrayList<ArrayList<String>> arrayListVehInfo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        l = new LogFileNew();
        settings = getSharedPreferences(PREFS_NAME, 0);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showtips =  sharedPreferences.getBoolean("show_tips_messages", true);
        logErrors =  sharedPreferences.getBoolean("enable_file_logging", false);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected abstract int getLayoutResourceId();

    public static String removeCarriage (String text) {
        return text.replace("\n", "").replace("\r", "");
    }

    public void loadTitle( String title ) {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View vv = inflator.inflate(R.layout.titleview, null);
        ((TextView) vv.findViewById(R.id.title)).setTextColor(Color.DKGRAY);
        ((TextView) vv.findViewById(R.id.title)).setText(title);
        getSupportActionBar().setCustomView(vv);
    }

    public void toastIt(String msg) {
        if (showtips) {
            if (mToast != null) {
                mToast.cancel();
            }
            String toastMessage = msg;
            mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    public ProgressDialog mProgressDialog;

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }
}
