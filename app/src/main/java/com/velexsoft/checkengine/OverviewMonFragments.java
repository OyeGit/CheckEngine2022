package com.velexsoft.checkengine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.velexsoft.checkengine.utilities.RegScreen;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.velexsoft.checkengine.ConnActivityParent.PREFS_NAME;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Created by WoleProject on 10/15/2016.
 */

public class OverviewMonFragments extends Fragment {
    SharedPreferences settings;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String serialValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agreement_accept_fragment, container, false);
        {
            TextView textViewInfo = (TextView) view.findViewById(R.id.textMonInfo);
            textViewInfo.setText( returnTerms() );
            settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
            serialValue = settings.getString("vSerialNumber", "");
            Button button1 = (Button) view.findViewById(R.id.activate);
            button1.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            checkIfExists();
                        }
                    }
            );
        }
        return view;
    }


    public void checkIfExists() {

        final String deviceId = android.os.Build.DEVICE;
        // String androidId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
        //  android.provider.Settings.Secure.ANDROID_ID);

        DocumentReference docRef = db.collection("DeviceInfo").document(serialValue);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists() && document.getString("Status").equals("1") &&
                                document.getString( "Deviceid1").equals(deviceId)) {
                            String statusSerial = document.getString("Status");
                            String devID =  document.getString("Deviceid1");
                            System.out.println ( "XX - statusSerial = "+statusSerial+" - "+"devID = "+devID);

                            writeRegisterProcess();
                            goConnectionScreen(getActivity());
                            updateStatus();

                        } else {

                            registerSecurityViolation99User();

                        }
                    } else {

                    }
                } else {

                }
            }
        });
    }

    public void registerSecurityViolation99User() {

        System.out.println(   "XX - registerSecurityViolation99User");

        RegScreen Rs = new RegScreen();
        String deviceid = android.os.Build.DEVICE;
        String timezone = Rs.getTimeZone();
        String phoneos = Rs.getAndroidVersion();
        String phoneversion = Rs.getReleaseVersion();
        String fingerPrint = Rs.getFingerPrint();
        String timestamp = Rs.getTime();
        Date currentTime = Calendar.getInstance().getTime();
        String androidId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        String secViolation = "SecurityViolation";

        CollectionReference UserInfo = db.collection(secViolation);
        Map<String, Object> user = new HashMap<>();

        user.put("serialnum", "" );
        user.put("violationType", "multipledevicesSameSerialNum" );
        user.put("currentTime", currentTime.toString() );
        user.put("timezone", timezone);
        user.put("deviceid", deviceid );
        user.put("androidID", androidId);
        user.put("phoneos", phoneos );
        user.put("phoneversion", phoneversion);
        user.put("fingerPrint", fingerPrint);
        user.put("timestamp", timestamp);

        UserInfo.document( deviceid  ).set( user );

        System.out.println(   "registerSecurityViolation99User Completed!");
    }
    /*
    public void writeAllAuthInfo( ) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(registeredAlreadyKey, true );
        editor.putString("globalMACAddress"  , vGlobalMACAddress );
        editor.putString("checkEngineUser"   , checkEngineUser );
        editor.putString("fireBaseUserString", fireBaseUserString );
        editor.putInt("registerProcess", 3  );
        editor.commit();
        toastIt( "Authentication Successful."  );
        registerAuditUser();
        updateStatus();
        goToConnectionScreen();
    }

     */

    public void updateStatus() {
        RegScreen Rs = new RegScreen();
        String Deviceid = android.os.Build.DEVICE;
        String FingerPrint = Rs.getFingerPrint();
        Date currentTime = Calendar.getInstance().getTime();

        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("Status", "2");
        deviceInfo.put("Deviceid2", Deviceid );
        deviceInfo.put("FingerPrint2", FingerPrint );
        deviceInfo.put("RegisteredDate2", currentTime.toString() );

        db.collection("DeviceInfo").document(serialValue)
                .set(deviceInfo, SetOptions.merge());
    }

    public void goConnectionScreen (Context vcontext) {
        try {
            final Context locContext = vcontext;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(locContext, ChooserActivity.class);
                    intentMain.setFlags(intentMain.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    startActivity(intentMain);
                }
            });
            myThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeRegisterProcess() {
        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("registerProcess", 2  );
        editor.commit();
    }

    public String returnTerms() {
        String lineSep = System.getProperty("line.separator");
        String textString =         "By downloading the check engine app, purchasing the check engine device, and/or clicking on “I Accept” on the button below, these check engine terms and conditions automatically applies to you – you should make sure therefore, that you read them carefully before using the app. Bada Barawu is offering this app for your personal use at no cost, however, you should be aware that you cannot send it on to anyone else, and you are not allowed to copy or modify any part of the app, or our trademarks in any way. You are not allowed to attempt to extract the source code of the app, and you are also not allowed to translate the app into other languages, or make derivative versions. The app itself, and all the trademarks, copyright, database rights and other intellectual property rights related to it, belongs to Bada Barawu. " +
                lineSep + lineSep + "You should be aware that Bada Barawu will not take responsibility for all/or any functions and features of the app, the network connections, or the check engine device. In addition, Check Engine will not work at full capacity if you do not have access to the internet, nor are outside the Bluetooth coverage area." +
                lineSep + lineSep + "While using the app, you should remember that your terms of agreement with your mobile network provider still applies. As a result, you may be charged by your mobile provider for the cost of data transmission, or other additional data related charges. In using the app, you are accepting full responsibility for any such charges. If you are not the bill payer for the data plan on the device on which you are using the app, please be aware that we assume that you have received permission from the bill payer for using the app on the device." +
                lineSep + lineSep + "At some point Bada Barawu may wish to update the app on Android Google Play – the requirements for the app and for any additional systems we decide to extend may change, and you will need to download the updates if you want to keep using the app. Bada Barawu does not promise that it will always update the app so that it is relevant to you and/or works with your Android version. However, you agree to always accept updates to the application when offered to you."+
                lineSep + lineSep + "Bada Barawu may also wish to stop providing the app, and may terminate the use of it at any some point without giving notice of termination to you. Unless we tell you otherwise, upon any termination, (a) the rights and licenses granted to you in these terms will end; (b) you must stop using the app, and (if required) delete it from your device.";
        return  textString;
    }
}
