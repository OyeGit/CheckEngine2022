package com.velexsoft.checkengine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.core.content.FileProvider;

import com.velexsoft.checkengine.utilities.RegScreen;

import java.io.File;


public class ContactUsActivity extends ConnActivityParent {

    final Context context = this;
    Spinner dropdown;
    String[] item;
    SharedPreferences settings;
    RegScreen Ut;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_contact_us;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context,"CONTACTUSS: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        l.fileName = ConnActivityParent.logFileName;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Contact Us Screen Started");
        lw (  "-------------------------------------------------------------------------------");
        loadTitle("Contact Support");
        initializeActivity();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, item);
        dropdown.setAdapter(adapter);
        settings = getSharedPreferences(PREFS_NAME, 0);
        Ut = new RegScreen();
    }

    public void saveSend(View view) {
        Thread myThread;
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SendCheckEmail ();
            }
        });
        myThread.start();
    }

    public void initializeActivity() {
        dropdown =(Spinner)findViewById(R.id.spinner);
        item = new String[]{ "Errors/Anomalies Report","Suggestions/Comments","Questions/Inquiry"};
    }

    public String returnDataText() {
        String vSerialNumber = settings.getString("vSerialNumber", "");
        String dropDownValue = dropdown.getSelectedItem().toString();
        String androidVersion = Ut.getAndroidVersion();
        String versionName = "";
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch( Exception e ){
            lw ("ERROR ERROR : Unable to retrieve package info. Error = "+e);
        }
        String emailSubject = "Check Engine "+versionName+" - "+dropDownValue+" - "+androidVersion+" ["+vSerialNumber+"]";
        return emailSubject;
    }


    public void SendCheckEmail (  ) {
        lw("PROGRAM   : ContactUsActivity.SendCheckEmail");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@checkengine.me"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, returnDataText() );

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxAttFile);
        boolean checkBoxChecked = checkBox.isChecked();

        try {

            if (checkBoxChecked) {
                File file = new File(context.getCacheDir() + "/" + logFileName);
                if (!file.exists() || !file.canRead()) {
                    lw("WARNING   : File does not exist???....Proceed");
                    toastIt("File does not exist");
                } else {

                    try {
                        Uri txtUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
                        lw("File is found!!!!");
                        lw("txtUri = " + txtUri.toString());
                        emailIntent.putExtra(Intent.EXTRA_STREAM, txtUri);
                    } catch (Exception e) {
                        toastIt("Unable to attach file");
                        lw("ERROR ERROR : Unable to attach file. Error = " + e);
                    }
                }
            }

            try {
                lw ( "About to start activity for Sending....");
                startActivity(Intent.createChooser(emailIntent, ""));
            } catch (android.content.ActivityNotFoundException ex) {
                toastIt("There are no email clients installed." );
            } catch (Exception e) {
                toastIt("Unable to send E-mail" );
                lw ( "ERROR ERROR : Unable to send E-mail. Error = "+e);
            }

        } catch ( Exception e) {
            System.out.println("ERROR : "+e);
            lw ( "ERROR ERROR : Unable to send E-mail. Error = "+e);
        }
    }

}
