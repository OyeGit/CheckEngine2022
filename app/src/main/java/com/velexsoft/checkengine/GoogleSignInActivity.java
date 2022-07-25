package com.velexsoft.checkengine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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


public class GoogleSignInActivity extends ConnActivityParent implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    SharedPreferences settings;
    static boolean vRegisteredAlready  = false ;
    final Context context = this;
    String vGlobalMACAddress;
    String checkEngineUser;
    String fireBaseUserString;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_google;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        l.fileName = ConnActivityParent.logFileName;

        settings = getSharedPreferences(PREFS_NAME, 0);

        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        TextView tx = (TextView)findViewById(R.id.detail);
                        checkEngineUser = tx.getText().toString();
                        fireBaseUserString = user.toString();
                        checkIfExists();

                    } else {
                        toastIt( "Authentication Failed." );
                        updateUI(null);
                    }
                    hideProgressDialog();
                }
            });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateUI(null);
                }
            });
    }

    private void revokeAccess() {
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateUI(null);
                }
            });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            //findViewById(R.id.goConnectionScreen).setVisibility(View.VISIBLE);

        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.goConnectionScreen).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.disconnect_button) {
           revokeAccess();
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
