package com.velexsoft.checkengine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.velexsoft.checkengine.utilities.RegScreen;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.velexsoft.checkengine.ConnActivityParent.PREFS_NAME;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by WoleProject on 10/15/2016.
 */

public class EnterSerialFragments extends Fragment {

    SharedPreferences settings;
    ProgressBar mLoadingIndicator;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText editText;
    static String serialValue;
    boolean clickFailed;
    String secViolation = "SecurityViolation";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_serial_fragment, container, false);
        {
            editText = (EditText) view.findViewById( R.id.vSerialNumber );
            mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
            Button btn = (Button) view.findViewById(R.id.activate );
            settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
            btn.setOnClickListener(

                new Button.OnClickListener() {
                    public void onClick(View v) {

                        Editable enteredSerial = editText.getText();
                        serialValue = enteredSerial.toString();
                        if ( serialValue.length()!=8 ) {
                            Toast.makeText( getContext() ,"Invalid Serial Number, 8 digits required", Toast.LENGTH_SHORT).show();
                        } else {
                            new executeRegAsyncTask().execute("");

                        }
                    }
                }
            );

            TextView textView =(TextView)view.findViewById(R.id.linkText);
            textView.setClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            String htmlString="<u>www.checkengine.me</u>";
            textView.setText(Html.fromHtml(htmlString));
            textView.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            goCheckdotcom();
                        }
                    }
            );

            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    checkSecurity();
                }
            });
            myThread.start();

        }
        return view;
    }

    public void goCheckdotcom () {
        try {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(getContext(), WebViewEx.class);
                    startActivityForResult(intentMain, 1);
                }
            });
            inMyThread.start();
        } catch ( Exception e ) {
        }
    }

    public boolean isConecctedToInternet() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            System.out.println(  "Yes, It is connected to internet...");
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public void registerSecurityViolation99User() {

        System.out.println(   "registerSecurityViolation99User");

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

        CollectionReference UserInfo = db.collection(secViolation);
        Map<String, Object> user = new HashMap<>();

        user.put("serialnum", "" );
        user.put("violationType", "3SerialNumTries99Violation" );
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

    public void checkSecurity() {

        try {
            System.out.println ( "EnterSerialFragments.checkSecurity... "  );
            String deviceID = android.os.Build.DEVICE;
            final String androidId = android.provider.Settings.Secure.getString(getActivity().getContentResolver(),
                         android.provider.Settings.Secure.ANDROID_ID);
            DocumentReference docRef = db.collection(secViolation).document(deviceID);
            final int failedAttemptCount = settings.getInt("failedAttemptCount", 0);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists() && document.getString("androidID").equals(androidId)) {
                                System.out.println( "This device Marked. Check engine will never work on it again." );
                                System.exit(0);
                            }
                        } else {

                        }
                    } else {

                    }
                }
            });
        } catch ( Exception e ){

        }
    }

    private class executeRegAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            clickFailed = false;
        }

        @Override
        protected String doInBackground(String... params) {
            int failedAttemptCount = settings.getInt("failedAttemptCount", 0);
            System.out.println ( "Enter Serial failedAttemptCount = "+failedAttemptCount );

            connectLogic();
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void connectLogic() {

            checkIfExists();
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (clickFailed) {
                Toast.makeText(getActivity(), "Failed Attempt... Your phone will be barreded after 3 attempts", Toast.LENGTH_LONG).show();
                //incrementFailedAttemptCount();
                int failedAttemptCount = settings.getInt("failedAttemptCount", 0);
                if (failedAttemptCount>=4) {
                    try {
                        Thread.sleep(1000);
                        System.exit(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void incrementFailedAttemptCount() {
        int failedAttemptCount = settings.getInt("failedAttemptCount", 0);
        failedAttemptCount ++;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("failedAttemptCount", failedAttemptCount );
        editor.commit();
    }


    public void setThingsRight( boolean value ) {

        if (value) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.myLinearLayout);
            linearLayout.setVisibility(View.INVISIBLE);

            Fragment fragment = new OverviewMonFragments();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
            transaction.replace(R.id.fragment_output, fragment);
            transaction.commit();
        } else {
            Toast.makeText(getActivity() ,"Invalid Code", Toast.LENGTH_SHORT).show();
        }
    }

    static boolean vDataExist = false;

    public boolean checkIfExists() {
        DocumentReference docRef = db.collection("DeviceInfo").document(serialValue);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists() && document.getString("Status").equals("0")) {
                            String statusSerial = document.getString("Status");
                            System.out.println ("This is status now = "+statusSerial );
                            updateStatus();
                            writeSerialNumber();
                            writeRegisterProcess();
                            vDataExist = true;
                            setThingsRight(vDataExist);
                        } else {
                            clickFailed = true;

                            incrementFailedAttemptCount();
                            int failedAttemptCount = settings.getInt("failedAttemptCount", 0);
                            if (failedAttemptCount>=4) {
                                registerSecurityViolation99User();
                            }
                        }
                    } else {

                    }
                } else {

                }
            }
        });
        return vDataExist;
    }

    public void updateStatus() {
        RegScreen Rs = new RegScreen();
        String Deviceid = android.os.Build.DEVICE;
        String FingerPrint = Rs.getFingerPrint();
        Date currentTime = Calendar.getInstance().getTime();

        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("Status", "1");
        deviceInfo.put("Deviceid1", Deviceid );
        deviceInfo.put("FingerPrint1", FingerPrint );
        deviceInfo.put("RegisteredDate1", currentTime.toString() );

        db.collection("DeviceInfo").document(serialValue)
                .set(deviceInfo, SetOptions.merge());
        System.out.println ("This is the completion updateStatus ");
    }

    public void writeSerialNumber() {
        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("vSerialNumber", serialValue  );
        editor.commit();
    }

    public void writeRegisterProcess() {
        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("registerProcess", 1  );
        editor.commit();
    }
}
