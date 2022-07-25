package com.velexsoft.checkengine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.velexsoft.checkengine.modess.OBDCommand1Byte;
import com.velexsoft.checkengine.modess.OBDInitialize;
import com.velexsoft.checkengine.modess.OBDInitializeDelay;
import com.velexsoft.checkengine.modess.OBDInitializeDelayForProt;
import com.velexsoft.checkengine.modess.OBDMODE06UnitScaling;
import com.velexsoft.checkengine.modess.ProtocolInit;
import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;
import com.velexsoft.checkengine.utilities.RegScreen;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class ConnectionScreen extends ConnActivityParent {

    final Context context = this;
    final int REQUEST_ENABLE_BT = 2;
    boolean velexSensorPaired = false;
    int backButtonCount;
    Dialog d;
    String TempMacAddr = "";
    DisplayMetrics displayMetrics;
    ProgressBar mLoadingIndicator;
    Window window ;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences settings;
    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    static String globalMACAddress;
    LogCloudFileNew c;
    CheckBox checkBox;
    String versionName;
    ArrayList<String> connaudit = new ArrayList();
    boolean  cloudLoging;
    String secViolation = "SecurityViolation";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_connection_screen;
    }

    public void lw( String msg ) {

        if ( logErrors ) {
            l.w (  context, "CONNSCREEN: "+msg );
            //System.out.println ( "CONNSCREEN: "+msg );
        }

        if (cloudLoging ) {
            connaudit.add(  "CONNSCREEN: "+msg );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        checkBox = (CheckBox) findViewById(R.id.offline);
        l = new com.velexsoft.checkengine.utilities.LogFileNew();
        l.fileName = ConnActivityParent.logFileName;
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        window = getWindow();
        settings = getSharedPreferences(PREFS_NAME, 0);
        c = new LogCloudFileNew( "ConnectionDetails3", context );

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //toastIt( "user:"+currentUser );
        toastIt( "user:"+currentUser.getUid() );
        System.out.println( "Current User = "+currentUser.getUid() );
        lw ( "currentUser = " +currentUser  );
        //mAuth.addAuthStateListener( currentUser  );
        if ( (currentUser.equals(null) || (currentUser.toString().isEmpty()) || (currentUser.toString().equals(null))  ) )  {
            lw ( "Your account is disabled. Contact Check Engine support") ;
            System.exit(0);
        }

        requestScanningPermission(ConnectionScreen.this, 887 );

        /*
        view1 = findViewById(R.id.loginButton);
        view1.setVisibility(View.VISIBLE);
        view1.setAlpha(0);
        view1.animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(null);
                */
    }

    public static void requestScanningPermission(
            @NonNull final Activity requestingActivity,
            final int requestCode) {
        System.out.println( "requestScanningPermission()" );
        final String permission = Manifest.permission.BLUETOOTH_CONNECT;
        if (ContextCompat.checkSelfPermission(requestingActivity,
                permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requestingActivity, permission)) {
                ActivityCompat.requestPermissions(requestingActivity,
                        new String[] { permission }, requestCode);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean allowOflineMode =  sharedPreferences.getBoolean("allow_offline_mode", false);
        if (allowOflineMode ) {
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cloudLoging =  sharedPreferences.getBoolean("enable_logging", false);
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch( Exception e ){
            e.printStackTrace();
        }

        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Connection Screen Started ["+versionName+"]");
        lw (  "-------------------------------------------------------------------------------");
        lw (  "PROGRAM   : ConnectionScreen.onPostCreate " );

        TextView tx = (TextView) findViewById(R.id.title2) ;
        tx.setText ( "Vehicle Diagnostics "+versionName);

        displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = Math.round(displayMetrics.heightPixels / displayMetrics.density);
        screenWidth = Math.round(displayMetrics.widthPixels / displayMetrics.density);

        lw (  "INFO      : ----------------------------------");
        lw (  "INFO      : screenHeight = "+screenHeight+" dp");
        lw (  "INFO      : screenWidth  = "+screenWidth+" dp");
        lw (  "INFO      : ----------------------------------");

        try {
            lw("INFO      : Completed ConnectionScreen.onPostCreate[Success]");
            vSerialNumber = settings.getString("vSerialNumber", "");
            lw("This is vSerialNumber = " + vSerialNumber);
            globalMACAddress = settings.getString("globalMACAddress", "");
            lw("This is globalMACAddress = " + globalMACAddress);
            String checkEngineUser = settings.getString("checkEngineUser", "");
            lw("This is checkEngineUser = " + checkEngineUser);
            String fireBaseUserString = settings.getString("fireBaseUserString", "");
            lw("This is fireBaseUserString = " + fireBaseUserString);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            lw("This is currentUser = " + currentUser);
            lw("This is currentUser.getUid = " + currentUser.getUid());
        }catch (Exception e) {
            e.printStackTrace();
        }

        Thread myThread;
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {

                checkSecurity();
            }
        });
        myThread.start();
    }

    public void callMap() {
       // startActivity(new Intent(this, MapsActivity.class));
    }

    public void checkSecurity() {

        try {
            Thread.sleep(3000);

            System.out.println ( "EnterSerialFragments.checkSecurity... "  );
            String deviceID = android.os.Build.DEVICE;
            final String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
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

    public void ConnButton( View view) {
        boolean checked = checkBox.isChecked();
        lw  ( "INFO      : This is value of offline checked = "+checked);
        if (checked) {
            lw  ( "INFO      : This is Offline Mode");
            connectOfflineMode();
        } else {
            lw  (  "INFO      : This is Online Mode");
            connectOnlineMode();
        }
    }

    public void goCheckCom( View view) {
        lw("INFO      : Moving to Check Engine website .... ");
        goCheckdotcom();
    }

    private static final int WRITE_REQUEST_CODE = 43;

    public void checkEngineMAC( View view) {
        checkDevicePaired();
        lw (  "INFO      : MAC Addr : "+TempMacAddr);
        String msg =  "MAC:"+TempMacAddr;
        toastIt( msg ) ;
    }

    public void checkCheckBox( View view) {

    }

    public void goToMainMenu() {
        lw (  "PROGRAM   : ConnectionScreen.goToMainMenu");
        try {
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(context, MainMenuActivity.class);
                    intentMain.setFlags(intentMain.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : ConnectionScreen.goToMainMenu = " + e);
        }
    }

    public void goCheckdotcom () {
        lw (  "PROGRAM   : ConnectionScreen.goCheckdotcom" );
        try {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(context, WebViewEx.class);
                    startActivityForResult(intentMain, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            inMyThread.start();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.goCheckdotcom Error = "+e );
        }
    }

    public void connectOfflineMode() {
        lw (  "PROGRAM   : ConnectionScreen.connectOfflineMode");
        lineMode = "offline";
        goToMainMenu();
    }

    public int CheckBluetoothHardware () {
        lw (  "PROGRAM   : ConnectionScreen.CheckBluetoothHardware");
        int     enabled = 0;
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                velexMessageNow(context, "Device does not support Bluetooth...");
                enabled = 0;
            } else if (!mBluetoothAdapter.isEnabled()) {
                lw("WARNING   : BlueTooth is not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    lw("INFO      : BlueTooth has been enabled {Success}");
                    enabled = 1;
                }
            }
        } catch (Exception e ){
            lw (  "ERROR ERROR : ConnectionScreen.CheckBluetoothHardware Error = "+e );
        }
        return enabled;
    }

    public void velexMessageNow( Context pcontext, String exitMsg){
        lw (  "PROGRAM   : ConnectionScreen.velexMessageNow MSG = "+exitMsg );
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pcontext);
            alertDialogBuilder.setMessage(exitMsg);
            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.velexMessageNow Error = "+e );
        }
    }

    public int checkDevicePaired() {
        lw (  "PROGRAM   : ConnectionScreen.checkDevicePaired");
        int flagStatus = 0;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lw (  "MSG       : Bluetooth already enabled");
        lw (  "MSG       : About to get a current list of paired devices?.....");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        lw (  "MSG       : There are "+pairedDevices.size()+" paired devices.....");
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                lw (  "INFO      :     "+device.getName() + " , " + device.getAddress());
                if ( device.getName().equals("CheckEngine") ) {
                    lw (  "MSG       : Device is paired and found.....");
                    lw (  "INFO      : Choosen Device: "+device.getName() + " , " + device.getAddress());
                    TempMacAddr = device.getAddress();
                    velexSensorPaired = true;
                    lw (  "MSG       : velexSensorPaired :" + velexSensorPaired);
                    flagStatus = 1;
                    lw (  "MSG       : Success! Verified Paired Device :" + device.getAddress() );
                }
            }
        } else {
            lw (  "WARNING  : There are "+pairedDevices.size()+" paired devices.....");
            velexMessageNow( context, "Check Engine Device has not been paired...");
        }
        return flagStatus;
    }

    public void stopServiceConnScreen () {
        lw ( "PROGRAM   : ConnectionScreen.stopServiceConnScreen");
        try {
            View v = new View(context);
            stopService(v);
            int vState = BluetoothService.mState;
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.stopServiceConnScreen Error = "+e );
        }
    }

    public void startService(View view) {
        lw ( "PROGRAM   : ConnectionScreen.startService");
        try {
            startService(new Intent(getBaseContext(), BluetoothService.class));
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.startService Error = "+e );
        }
    }

    public void stopService(View view) {
        lw ( "PROGRAM   : ConnectionScreen.stopService");
        stopService(new Intent(getBaseContext(), BluetoothService.class));
    }

    public int startServiceConnScreen () {
        lw (  "PROGRAM   : ConnectionScreen.startServiceConnScreen");
        CheckBox checkBox = (CheckBox) findViewById(R.id.offline);
        boolean checked = checkBox.isChecked();
        if (checked) {
            return 0;
        } else {
            View v = new View(context);
            startService(v);
            int vState = BluetoothService.mState;
            return vState;
        }
    }

    public void connectOnlineMode() {
        lw (  "PROGRAM   : ConnectionScreen.connectOnlineMode" );
        lineMode = "online";
        int bluehardware = CheckBluetoothHardware();
        if ( bluehardware == 1  )  {
            checkDevicePaired();
            if (velexSensorPaired) {
                new executeConnAsyncTask().execute("");
            } else {
                velexMessageNow(context, "Check Engine device has not been paired... Please go to Android Settings > Bluetooth Connections > Scan for the device (CheckEngine) and pair to phone with code:4444");
            }
        }
    }

    private class executeConnAsyncTask extends AsyncTask<String, Void, String> {

        Boolean ConnectedSuccess;

        @Override
        protected String doInBackground(String... params) {
            lw ( "MSG       : Stopping service in case it is running" );
            stopServiceConnScreen();
            connectLogic();
            readDeviceInfo();
            return null;
        }

        public void connectLogic() {

            ConnectNow firstAttempt = new ConnectNow(1);
            lw ( "MSG       : Connecting First Attempt..." );
            ConnectedSuccess = firstAttempt.ConnectWithSocketFirst();
            lw ( "MSG       : Connected on First Attempt = "+ConnectedSuccess );

            if (!ConnectedSuccess)  {
                lw ( "MSG       : Connecting Second Attempt..." );
                ConnectNow SecondAttempt = new ConnectNow(2);
                ConnectedSuccess = SecondAttempt.ConnectWithSocketSecond();
                lw ( "MSG       : Connected on Second Attempt = "+ConnectedSuccess );
            }

            if (!ConnectedSuccess)  {
                lw ( "MSG       : Connecting Third Attempt..." );
                ConnectNow ThirdAttempt = new ConnectNow(3);
                ConnectedSuccess = ThirdAttempt.ConnectWithSocketDiscover();
                lw ( "MSG       : Connected on Third Attempt = "+ConnectedSuccess );
            }

            if (ConnectedSuccess) {
                startServiceConnScreen();
                lw ( "MSG       : Successfully Connected..." );
                c.wConn( "protocol" , "Success" , connaudit  );
                goToMainMenu();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            lw (  "Header Information = "+ header1 + " - "+ header2 + " - "+ header3 );
            //toastIt( header1 + " - "+ header2 + " - "+ header3  );
            if (!ConnectedSuccess) {
                c.wConn( "protocol" , "failed" , connaudit  );
                toastIt("Failed to connect...");
            }
        }

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class ConnectNow{

        BluetoothSocket bbSocket;
        BluetoothAdapter mBluetoothAdapter;
        BluetoothDevice device;
        int numTimes;

        public boolean verifySerial( BluetoothSocket pSocket ) {
            boolean verified = false;
            try {
                lw("verifySerial Phone Serial = "+vSerialNumber);
                OBDInitialize setReset = new OBDInitialize("AT @2");
                setReset.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
                String vResponse = removeCarriage( setReset.getResponse() );
                if (vResponse.contains(vSerialNumber)) {
                    verified = true;
                } else {
                    verified = false;
                }
            } catch (Exception e) {
                lw("ERROR ERROR: Validating Serial"+e);
            }
            return verified;
        }

        public ConnectNow(int vNumTimes) {
            numTimes = vNumTimes;
            lw (  "PROGRAM   : ConnectionScreen.ConnectNow.Constructor" + "["+numTimes+"]");
            try {
                lw (  "MSG       : Creating Bluetooth Adapter" + "["+numTimes+"]");
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                lw (  "MSG       : Obtaining remote device..."+ "["+numTimes+"]" );
                lw (  "MSG       : globalMACAddress = "+globalMACAddress+ " ["+numTimes+"]" );
                BluetoothService.globalMacAddress=globalMACAddress;
                device = mBluetoothAdapter.getRemoteDevice( globalMACAddress  );
                lw (  "MSG       : Completed ConnectNow Contructor"+ "["+numTimes+"]" );
            } catch ( Exception e ) {
                lw (  "ERROR ERROR : Failed to complete ConnectNow.Constructor. Error "+ "["+numTimes+"]" +e);
            }
        }

        public Boolean ConnectWithSocketDiscover() {
            lw (  "PROGRAM   : ConnectionScreen.ConnectWithSocketDiscover" + "["+numTimes+"]" );
            ProtocolInit protocolInit = new ProtocolInit( numTimes, context );
            Boolean connected = false;
            try { // Creating Bluetooth Adapter
                lw (  "INFO      : Cancel Discovery..." + "["+numTimes+"]" );
                mBluetoothAdapter.cancelDiscovery();
                lw (  "INFO      : About to create Socket..." + "["+numTimes+"]");
                bbSocket = device.createRfcommSocketToServiceRecord(myUUID);
                lw (  "INFO      : Connecting now.........." + "["+numTimes+"]" );
                System.out.println( "INFO      : Connecting now.........." + "["+numTimes+"]" ) ;
                bbSocket.connect();
                lw (  "INFO      : Successfully Connected Bluetooth [Success]  *********" + "["+numTimes+"]" );
                System.out.println( "INFO      : Successfully Connected Bluetooth [Success] *********" + "["+numTimes+"]") ;
                boolean verified =  verifySerial(bbSocket);
                if (verified) {
                    //int initialized =
                    protocolInit.initializeGeneralCommand(bbSocket, numTimes);

                    String ECUProtocol = checkECUProtocol(bbSocket);
                    lw("INFO      : This is the retrieved protocol " + ECUProtocol + "[" + numTimes + "]");
                    protocolInit.initializewithDiscovery(bbSocket, numTimes);

                    int initialized = testBaudRate(bbSocket, numTimes);
                    lw("This is testBaudRate = " + initialized + "[" + numTimes + "]");
                    if (initialized == 1) {
                        resetH();
                        connected = true;
                    } else {

                        String protocolToTry = "A0";
                        tryThisProtocol(bbSocket, protocolToTry);

                    }
                }
            } catch ( Exception e ) {
                System.out.println( "ERROR ERROR : Failed to complete ConnectionScreen.ConnectNow.ConnectWithSocketDiscover. Error "+e ) ;
                connected = false;
                lw (  "ERROR ERROR : Failed to complete ConnectionScreen.ConnectNow.ConnectWithSocketDiscover. Error "+e);
                closeSocket();
            }
            closeSocket();
            return connected;
        }

        public void resetH() {

            try {
                lw("predashSettings.resetH[AT H0]");
                OBDInitialize setHeaderOff = new OBDInitialize("AT H0");
                setHeaderOff.talkToDevice(bbSocket.getInputStream(), bbSocket.getOutputStream());
                String vResponses = removeCarriage(setHeaderOff.getResponse());
                lw("setHeaderOn.getResponse() = " + vResponses);
                if (setHeaderOff.getResponse().contains("OK")) {
                    lw("setHeaderOn.getResponse() = is GOOD " + vResponses);
                }
            } catch (Exception e) {
                lw("ERROR ERROR: initializeGeneralCommand.setHeaderOn" + e);
            }
            //testPhysicalAddress();
        }

        public void testPhysicalAddress() {
            try {
                lw("predashSettings.setHeaderOn[68 10 F0 01 0D]");
                OBDInitialize setHeaderOff = new OBDInitialize("68 10 F0 01 0D");
                setHeaderOff.talkToDevice(bbSocket.getInputStream(), bbSocket.getOutputStream());
                String vResponses = removeCarriage(setHeaderOff.getResponse());
                lw("setHeaderOn.getResponse() = " + vResponses);
                if (setHeaderOff.getResponse().contains("OK")) {
                    lw("setHeaderOn.getResponse() = is GOOD " + vResponses);
                }
            } catch (Exception e) {
                lw("ERROR ERROR: initializeGeneralCommand.setHeaderOn" + e);
            }
        }

        public Boolean ConnectWithSocketSecond() {
            lw (  "PROGRAM   : ConnectionScreen.ConnectWithSocketSecond" + "["+numTimes+"]" );
            ProtocolInit protocolInit = new ProtocolInit( numTimes, context );
            Boolean connected = false;
            try { // Creating Bluetooth Adapter
                lw (  "INFO      : Cancel Discovery..." + "["+numTimes+"]" );
                mBluetoothAdapter.cancelDiscovery();
                lw (  "INFO      : About to create Socket..." + "["+numTimes+"]");
                bbSocket = device.createRfcommSocketToServiceRecord(myUUID);
                lw (  "INFO      : Connecting now.........." + "["+numTimes+"]" );
                bbSocket.connect();
                boolean verified =  verifySerial(bbSocket);
                if (verified) {
                    lw("INFO      : Successfully Connected Bluetooth [Success]  *********" + "[" + numTimes + "]");
                    int initialized = protocolInit.initializeGeneralCommand(bbSocket, numTimes);
                    String ECUProtocol = checkECUProtocol(bbSocket);
                    lw("INFO      : This is the retrieved protocol " + ECUProtocol + "[" + numTimes + "]");
                    lw("This is initializeGeneralCommand = " + initialized + "[" + numTimes + "]");
                    initialized = testBaudRate1(bbSocket, numTimes);
                    lw("This is testBaudRate = " + initialized + "[" + numTimes + "]");
                    if (initialized == 1) {
                        resetH();
                        connected = true;
                    }

                }
            } catch ( Exception e ) {
                connected = false;
                lw (  "ERROR ERROR : Failed to complete ConnectionScreen.ConnectNow.ConnectWithSocketFirst. Error "+e);
                closeSocket();
            }
            closeSocket();
            return connected;
        }


        public Boolean ConnectWithSocketFirst() {
            lw (  "PROGRAM   : ConnectionScreen.ConnectWithSocketFirst" + "["+numTimes+"]" );
            ProtocolInit protocolInit = new ProtocolInit( numTimes, context );
            Boolean connected = false;
            try { // Creating Bluetooth Adapter
                lw (  "INFO      : Cancel Discovery..." + "["+numTimes+"]" );
                mBluetoothAdapter.cancelDiscovery();
                lw (  "INFO      : About to create Socket..." + "["+numTimes+"]");
                bbSocket = device.createRfcommSocketToServiceRecord(myUUID);
                lw (  "INFO      : Connecting now.........." + "["+numTimes+"]" );
                bbSocket.connect();
                boolean verified =  verifySerial(bbSocket);
                if (verified) {
                    lw("INFO      : Successfully Connected Bluetooth [Success]  *********" + "[" + numTimes + "]");
                    int initialized = protocolInit.initializeGeneralCommand(bbSocket, numTimes);
                    String ECUProtocol = checkECUProtocol(bbSocket);
                    lw("INFO      : This is the retrieved protocol " + ECUProtocol + "[" + numTimes + "]");
                    lw("This is initializeGeneralCommand = " + initialized + "[" + numTimes + "]");
                    initialized = testBaudRate1(bbSocket, numTimes);
                    lw("This is testBaudRate = " + initialized + "[" + numTimes + "]");
                    if (initialized == 1) {
                        resetH();
                        connected = true;
                    }
                }
            } catch ( Exception e ) {
                connected = false;
                lw (  "ERROR ERROR : Failed to complete ConnectionScreen.ConnectNow.ConnectWithSocketFirst. Error "+e);
                closeSocket();
            }
            closeSocket();
            return connected;
        }

        private void closeSocket() {
            try {
                bbSocket.close();
                lw (  "INFO      : Closed Socket" );
            } catch (Exception a) {
                lw (  "ERROR ERROR : Failed to closeSocket. Error "+a);
            }
        }
    }

    public int testBaudRate1( BluetoothSocket  pSocket , int vNum ) {
        lw("PROGRAM   : ConnectionScreen.testBaudRate");
        int vstatus = 1;

        boolean gotProtocol = validateUsingSpeed(pSocket, vNum, 5, 1);
        getInfo ( pSocket );

        if (!gotProtocol){
            gotProtocol = validateUsingSpeed(pSocket, vNum, 100, 2 );
        }

        if (!gotProtocol){
            gotProtocol = validateUsingSpeed(pSocket, vNum, 3000 , 3 );
        }

        if (!gotProtocol){
            gotProtocol = validateUsingSpeed(pSocket, vNum, 5000 , 4 );
        }

        if (gotProtocol) {
            vstatus = 1;
        } else {
            vstatus = 0;
        }
        return vstatus;
    }

    public int testBaudRate( BluetoothSocket  pSocket , int vNum ) {
        lw("PROGRAM   : ConnectionScreen.testBaudRate");
        int vstatus = 1;

        boolean gotProtocol = validateUsingSpeed(pSocket, vNum, 1000, 1);
        getInfo ( pSocket );

        if (!gotProtocol){
            gotProtocol = validateUsingSpeed(pSocket, vNum, 3000, 2 );
        }

        if (!gotProtocol){
            gotProtocol = validateUsingSpeed(pSocket, vNum, 5000 , 3 );
        }

        if (gotProtocol) {
            vstatus = 1;
        } else {
            vstatus = 0;
        }
        return vstatus;
    }



    public void addressGet( String  payLoad) {
        lw("PROGRAM   : ConnectionScreen.addressGet. Payload = "+payLoad);
        int payLoadLenght = payLoad.length();
        lw ( "This is length of payload = "+ payLoadLenght+", currProtocolGroup = "+currProtocolDesc );


        if (currProtocolDesc.contains("CAN" )){
            lw("PROGRAM   : ConnectionScreen.addressGet. THis is CAN");
            try {
                header1 = payLoad.substring(0, 3);
                lw ( "header1 = "+header1 );
            } catch ( Exception e ){
                lw ( "WARNING   : header1 Extract error. "+e );
            }

            try {
                header2 = payLoad.substring(17, 20);
                lw ( "header2 = "+header2 );
            } catch ( Exception e ){
                lw ( "WARNING   : header2 Extract error. "+e );
            }

            try {
                header3 = payLoad.substring(34, 37);
                lw ( "header3 = "+header3 );
            } catch ( Exception e ){
                lw ( "WARNING   : header3 Extract error. "+e );
            }
        } else {
            if ( (payLoadLenght==60) ||  (payLoadLenght==20) || (payLoadLenght==40)  )  {
                try {
                    header1 = payLoad.substring(0, 6);
                    lw ( "header1 = "+header1 );
                } catch ( Exception e ){
                    lw ( "WARNING   : header1 Extract error. "+e );
                }

                try {
                    header2 = payLoad.substring(20, 26);
                    lw ( "header2 = "+header2 );
                } catch ( Exception e ){
                    lw ( "WARNING   : header2 Extract error. "+e );
                }

                try {
                    header3 = payLoad.substring(40, 46);
                    lw ( "header3 = "+header3 );
                } catch ( Exception e ){
                    lw ( "WARNING   : header3 Extract error. "+e );
                }
            }
        }
        lw (  "addressGet - Header Information = "+ header1 + " - "+ header2 + " - "+ header3 );
    }

    public boolean validateUsingSpeed( BluetoothSocket pSocket, int num, Integer waitTime, int cnt ){
        lw("PROGRAM   : ConnectionScreen.initializeGeneralCommand.ValidateUsingSpeed = "+cnt);
        System.out.println( "PROGRAM   : ConnectionScreen.initializeGeneralCommand.ValidateUsingSpeed = "+cnt);
        boolean vstatus = false;

        try {
            lw ( "INITIALIZE: obdmode0101[01 00]" );
            lw ( "INITIALIZE: WAIT-TIME = "+ waitTime  );
            OBDInitializeDelayForProt obdmode0100 = new OBDInitializeDelayForProt("01 00", num, waitTime);
            obdmode0100.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            response0101Raw = obdmode0100.getResponse();

            String vResponse = removeCarriage( obdmode0100.getResponse() );

            lw ( "INITIALIZE: obdmode0100.getResponse() = ["+cnt+"] - "+ vResponse );
            if ( obdmode0100.getResponse().contains("4100") && !obdmode0100.getResponse().contains("Searching")  ) {

                lw ( "INITIALIZE: Yes it contains 4100. OK Check Protocol - "+ vResponse );
                lw ( "response0100Raw = "+response0101Raw  );

                String finalValidatedProtocol  = checkECUProtocol(pSocket);
                String finalValidatedProtocolDesc  = checkECUProtocolDesc(pSocket);
                currProtocol = finalValidatedProtocol;
                currProtocolDesc = finalValidatedProtocolDesc;

                lw ( "ValidatedProtocol: ****************************************************************");
                lw ( "ValidatedProtocol: finalValidatedProtocol : "+finalValidatedProtocol.trim()  );
                lw ( "ValidatedProtocol: finalValidatedProtocolDesc : "+finalValidatedProtocolDesc.trim()  );
                lw ( "ValidatedProtocol: ****************************************************************");

                initializeThresholdNow();
                try {
                    addressGet(vResponse);
                } catch ( Exception e ){
                    lw (  "ERROR ERROR : Error calling addressGet "+ e   );
                }

                if (currProtocolGroup.contains("CAN")) {
                    try {
                        int header1Cnt = header1.length();
                        lw("This is CAN header1Cnt Setting Headers = " + header1Cnt);
                        if ( (header1Cnt == 3) && (ConvertValues.isStringHex( header1 ) )) {
                            try {
                                lw ( "XXXXX Setting Header to only 1....");
                                {
                                    lw("validateUsingSpeed.setHeaderOn[AT CRA] " + header1);
                                    OBDInitialize setHeaderOff = new OBDInitialize("AT CRA " + header1);
                                    setHeaderOff.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
                                    String vResponses = removeCarriage(setHeaderOff.getResponse());
                                    lw("setHeaderOn.getResponse() = " + vResponses);
                                    if (setHeaderOff.getResponse().contains("OK")) {
                                        lw("setHeaderOn.getResponse() = is GOOD " + vResponses);
                                    }
                                }

                            } catch (Exception e) {
                                lw("ERROR ERROR: initializeGeneralCommand.setHeaderOn" + e);
                            }
                        }
                    } catch ( Exception e ) {
                        lw ( "ERROR ERROR : Setting header header"+e  );
                    }
                } else  { //if (currProtocolGroup.contains("ISO")) {
                    try {
                        int header1Cnt = header1.length();
                        lw("This is ISO header1Cnt Setting Headers= " + header1Cnt);
                        if (header1Cnt == 3) {

                            try {
                                lw("predashSettings.setHeaderOn[AT SH E4 10 F1] " + header1);
                                OBDInitialize setHeaderOff = new OBDInitialize("AT SH E4 10 F1" );
                                setHeaderOff.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
                                String vResponses = removeCarriage(setHeaderOff.getResponse());
                                lw("setHeaderOn.getResponse() = " + vResponses);
                                if (setHeaderOff.getResponse().contains("OK")) {
                                    lw("setHeaderOn.getResponse() = is GOOD " + vResponses);
                                }
                            } catch (Exception e) {
                                lw("ERROR ERROR: initializeGeneralCommand.setHeaderOn" + e);
                            }

                        }
                    } catch ( Exception e ) {
                        lw ( "ERROR ERROR : Setting header header"+e  );
                    }
                }

                vstatus = true;
            } else {
                vstatus = false;
            }

        } catch ( Exception e ) {
            vstatus = false;
            lw (  "ERROR ERROR: initializeConnection.ValidateUsingSpeed"+e);
        }

        return vstatus;
    }


    public void getInfo ( BluetoothSocket pSocket ) {
        lw ( "INITIALIZE: getinfo RV" );
        try {
            lw("INITIALIZE:  AT RV");
            OBDInitialize doSomethin= new OBDInitialize("AT RV");
            doSomethin.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(doSomethin.getResponse());
            lw("INITIALIZE: AT RV = " + vResponse);
        } catch (Exception e) {
            lw("ERROR ERROR: INITIALIZE: AT RV " + e);
            e.printStackTrace();
        }

        try {
            lw("INITIALIZE:  AT SN");
            OBDInitialize doSomethin= new OBDInitialize("AT SN");
            doSomethin.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(doSomethin.getResponse());
            lw("INITIALIZE: AT SN = " + vResponse);
        } catch (Exception e) {
            lw("ERROR ERROR: INITIALIZE: AT RV " + e);
            e.printStackTrace();
        }
    }

    public boolean tryThisProtocol ( BluetoothSocket pSocket, String prot ) {
        lw ( "INITIALIZE: tryThisProtocol "+prot );

        try {
            OBDInitialize doSomethin= new OBDInitialize("AT SP "+prot);
            doSomethin.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(doSomethin.getResponse());
            lw("INITIALIZE: tryThisProtocol = " + vResponse);
        } catch (Exception e) {
            lw("ERROR ERROR: INITIALIZE: tryThisProtocol " + e);
        }
/*
        try {
            lw ( "INITIALIZE: [AT PB C0 0F]" );
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("AT PB C0 06" , 1,100 );
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            lw ( "INITIALIZE:AT IB 48 .getResponse() = "+ vResponse );
        } catch ( Exception e ) {
            lw (  "ERROR ERROR: ConnectionScreen.testBaudRate");
        }
        */

        String ECUProtocol = checkECUProtocol(pSocket);
        lw (  "INFO      : ThisXXXX is the retrieved protocol "+ ECUProtocol   );
        return true;
    }

    public String checkECUProtocol(BluetoothSocket pSocket) {
        lw ( "CLASS     : ConnectionScreen.ConnBlueConnScreen.checkECUProtocol" );
        String response = "";
        try {
            OBDInitialize getCurrProtocolN = new OBDInitialize("AT DPN");
            getCurrProtocolN.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            response = getCurrProtocolN.getResponse();
        } catch (Exception h) {
            lw ("ERROR ERROR : ConnectionScreen.checkECUProtocol = "+h);
        }
        return response;
    }

    public String checkECUProtocolDesc( BluetoothSocket pSocket ) {
        lw ( "CLASS     : ConnectionScreen.ConnBlueConnScreen.checkECUProtocolDesc" );
        String response = "";
        try {
            OBDInitialize getCurrProtocolN = new OBDInitialize("AT DP");
            getCurrProtocolN.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            response = getCurrProtocolN.getResponse();
        } catch (Exception h) {
            lw ("ERROR ERROR : ConnectionScreen.checkECUProtocolDesc = "+h);
        }
        return response;
    }

    @Override
    public void onBackPressed()
    {
        lw (  "PROGRAM   : ConnectionScreen.onBackPressed");
        if( backButtonCount >= 1 )
        {
            lw (  "WARNING   : About to terminate application");
            stopServiceConnScreen(); //Remember to Stop Service
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            overridePendingTransition(R.anim.fade_out_dialog_v, R.anim.fade_in_dialog_v);
            startActivity(intent);
            lw (  "INFO      : Exiting Application Completely");
            System.exit(0);
        }
        else
        {
            toastIt(  getString( R.string.close_application ) ) ;
            backButtonCount++;
        }
    }

    public void initializeThresholdNow () {

        if (currProtocol.contains("1")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "J1850";
        } else if (currProtocol.contains("2")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "J1850";
        } else if (currProtocol.contains("3")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "ISO";
        } else if (currProtocol.contains("4")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "ISO";
        } else if (currProtocol.contains("5")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "ISO";
        } else if (currProtocol.contains("6")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        } else if (currProtocol.contains("7")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        } else if (currProtocol.contains("8")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        } else if (currProtocol.contains("9")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        }  else if (currProtocol.contains("A")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        } else if (currProtocol.contains("B")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        } else if (currProtocol.contains("C")) {
            threadSleepSendCmd = 10;
            currProtocolGroup = "CAN";
        } else {
            threadSleepSendCmd = 10;
            currProtocolGroup = "Unidentified";
        }
    }

    public int readDeviceInfo() {
        lw ("PROGRAM   : ConnectionScreen.readDeviceInfo");
        try {
            String deviceid = android.os.Build.DEVICE;
            RegScreen Rs = new RegScreen();
            String timezone = Rs.getTimeZone();
            String phoneos = Rs.getAndroidVersion();
            String phoneversion = Rs.getReleaseVersion();
            String timestamp = Rs.getTime();
            String dummy = Rs.getFingerPrint();

            lw(  "MSG       : deviceid     = " + deviceid);
            lw(  "MSG       : timezone     = " + timezone);
            lw(  "MSG       : phoneos      = " + phoneos);
            lw(  "MSG       : phoneversion = " + phoneversion);
            lw(  "MSG       : timestamp    = " + timestamp);
            lw(  "MSG       : dummy        = " + dummy);
            lw(  "MSG       : Successfully readDeviceInfo Reg Screen...[Success]");
            return 1;
        } catch (Exception e) {
            lw("ERROR ERROR : ConnectionScreen.readDeviceInfo = " + e);
            return 0;
        }
    }
}


