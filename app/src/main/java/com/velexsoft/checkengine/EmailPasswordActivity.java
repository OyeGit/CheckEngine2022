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
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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


public class EmailPasswordActivity extends ConnActivityParent implements
        View.OnClickListener {

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    SharedPreferences settings;
    static boolean vRegisteredAlready  = false ;
    final Context context = this;
    private FirebaseAuth mAuth;
    String vGlobalMACAddress;
    String checkEngineUser;
    String fireBaseUserString;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_emailpassword;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        l.fileName = ConnActivityParent.logFileName;
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        settings = getSharedPreferences(PREFS_NAME, 0);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            String result = task.getException().toString();
                            String find = ":";
                            result = result.substring(result.indexOf(find)+find.length());
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            toastIt( "Authentication Successful."  );
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isUserVerified = user.isEmailVerified();
                            if ( isUserVerified ) {
                                updateUI(user);
                                TextView tx = (TextView)findViewById(R.id.detail);
                                checkEngineUser = tx.getText().toString();
                                fireBaseUserString = user.toString();
                                checkIfExists();
                            }

                        } else {
                            toastIt( "Authentication Failed." );
                            String result = task.getException().toString();
                            String find = ":";
                            result = result.substring(result.indexOf(find)+find.length());
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        findViewById(R.id.verify_email_button).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                findViewById(R.id.verify_email_button).setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(EmailPasswordActivity.this,
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                    signOut();
                } else {
                    Toast.makeText(EmailPasswordActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
                }
            });
    }

    private boolean validateForm() {
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
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            boolean isUserVerified = user.isEmailVerified();

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);

            if (isUserVerified) {
                //doFinal(user);
                findViewById(R.id.goToCheckEngine).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.goToCheckEngine).setVisibility(View.GONE);
            }
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
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
