/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.velexsoft.checkengine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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



public class AnonymousAuthActivity extends ConnActivityParent implements
        View.OnClickListener {

    private static final String TAG = "AnonymousAuth";

    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;

    SharedPreferences settings;
    static boolean vRegisteredAlready  = false ;
    final Context context = this;
    String vGlobalMACAddress;
    String checkEngineUser;
    String fireBaseUserString;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_anonymous_auth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        settings = getSharedPreferences(PREFS_NAME, 0);
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        findViewById(R.id.button_anonymous_sign_in).setOnClickListener(this);
        findViewById(R.id.button_anonymous_sign_out).setOnClickListener(this);
        findViewById(R.id.button_link_account).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            toastIt( "Authentication Successful."  );
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            TextView tx = (TextView)findViewById(R.id.anonymous_status_id);
                            checkEngineUser = tx.getText().toString();
                            fireBaseUserString = user.toString();

                            checkIfExists();

                        } else {
                            toastIt( "Authentication Failed." );
                            updateUI(null);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void linkAccount() {
        if (!validateLinkForm()) {
            return;
        }
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateLinkForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    private void updateUI(FirebaseUser user) {

        TextView idView = findViewById(R.id.anonymous_status_id);
        TextView emailView = findViewById(R.id.anonymous_status_email);
        boolean isSignedIn = (user != null);

        if (isSignedIn) {
            idView.setText(getString(R.string.id_fmt, user.getUid()));
            emailView.setText(getString(R.string.email_fmt, user.getEmail()));
        } else {
            idView.setText(R.string.signed_out);
            emailView.setText(null);
        }
        findViewById(R.id.button_anonymous_sign_in).setEnabled(!isSignedIn);
        findViewById(R.id.button_anonymous_sign_out).setEnabled(isSignedIn);
        findViewById(R.id.proceedToCheckEngine).setEnabled(isSignedIn);
        findViewById(R.id.button_link_account).setEnabled(isSignedIn);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_anonymous_sign_in) {
            signInAnonymously();
        } else if (i == R.id.button_anonymous_sign_out) {
            signOut();
        } else if (i == R.id.button_link_account) {
            linkAccount();
        }
    }

    public void goToConnectionScreen() {
        try {

            final Context locContext = this;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(locContext, ConnectionScreen.class);
                    intentMain.setFlags(intentMain.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                    finish();
                }
            });
            myThread.start();
        } catch (Exception e) {
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    public void registerAuditUser() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RegScreen Rs = new RegScreen();
        String deviceid = android.os.Build.DEVICE;
        final String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        String timezone = Rs.getTimeZone();
        String phoneos = Rs.getAndroidVersion();
        String phoneversion = Rs.getReleaseVersion();
        String fingerPrint = Rs.getFingerPrint();
        String timestamp = Rs.getTime();
        Date currentTime = Calendar.getInstance().getTime();

        CollectionReference UserInfo = db.collection("AuthUsers");
        Map<String, Object> user = new HashMap<>();

        user.put("serialnum", vSerialNumber );
        user.put("checkEngineUser", checkEngineUser );
        user.put("fireBaseUserString", fireBaseUserString );
        user.put("globMACAddress", vGlobalMACAddress );
        user.put("currentTime", currentTime.toString() );
        user.put("timezone", timezone);
        user.put("deviceid", deviceid );
        user.put("androidId", androidId );
        user.put("phoneos", phoneos );
        user.put("phoneversion", phoneversion);
        user.put("fingerPrint", fingerPrint);
        user.put("timestamp", timestamp);

        UserInfo.document( checkEngineUser  ).set( user );

    }

    public void checkIfExists() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        settings = getSharedPreferences(PREFS_NAME, 0);
        vSerialNumber = settings.getString("vSerialNumber", "");
        vRegisteredAlready = settings.getBoolean("registeredAlready", false);
        if ( !vRegisteredAlready ) {

            System.out.println("checkIfExists serialValue = "+vSerialNumber );
            DocumentReference docRef = db.collection("DeviceInfo").document(vSerialNumber);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            //if (document.exists() && document.getString("Status").equals("1") ) {
                            if (document.exists() && document.getString("Status").equals("2") ) {
                                System.out.println("Serial Number Exists = " + document.getId().toString());
                                vGlobalMACAddress = document.get("MACAddress").toString();
                                System.out.println ("Serial GlobalMACAddress Exists = " + vGlobalMACAddress );
                                writeAllAuthInfo();
                            }
                        } else {
                            System.out.println("No Such Document?????");
                        }
                    } else {
                        System.out.println("Serial Number does not exists?????");
                    }
                }
            });
            System.out.println("Completed checkIfExists");
        }
    }

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

    public void updateStatus() {
        RegScreen Rs = new RegScreen();
        String Deviceid = android.os.Build.DEVICE;
        String FingerPrint = Rs.getFingerPrint();
        Date currentTime = Calendar.getInstance().getTime();

        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("Status", "3");
        deviceInfo.put("Deviceid3", Deviceid );
        deviceInfo.put("FingerPrint3", FingerPrint );
        deviceInfo.put("RegisteredDate3", currentTime.toString() );
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DeviceInfo").document(vSerialNumber)
                .set(deviceInfo, SetOptions.merge());
        System.out.println ("This is the completion updateStatus ");
    }

}
