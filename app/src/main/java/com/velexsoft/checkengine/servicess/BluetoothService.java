package com.velexsoft.checkengine.servicess;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.modess.InUsePerformanceTracking;
import com.velexsoft.checkengine.modess.InterpreteMode01;
import com.velexsoft.checkengine.modess.Mode01NewParams;
import com.velexsoft.checkengine.modess.Mode02NewParams;
import com.velexsoft.checkengine.modess.Mode06CANNewParams;
import com.velexsoft.checkengine.modess.Mode06NewParams;
import com.velexsoft.checkengine.modess.Mode09Info;
import com.velexsoft.checkengine.modess.Mode6GetMIDTID;
import com.velexsoft.checkengine.modess.MonitorsModeArray;
import com.velexsoft.checkengine.modess.OBDClearTroubleCode;
import com.velexsoft.checkengine.modess.OBDCommand1Byte;
import com.velexsoft.checkengine.modess.OBDCommand2Byte;
import com.velexsoft.checkengine.modess.OBDCommand4Byte;
import com.velexsoft.checkengine.modess.OBDInitialize;
import com.velexsoft.checkengine.modess.OBDInitializeDelayForProt;
import com.velexsoft.checkengine.modess.OBDMODE06UnitScaling;
import com.velexsoft.checkengine.modess.OBDMode06Delay;
import com.velexsoft.checkengine.modess.OBDMode06Fast;
import com.velexsoft.checkengine.modess.OBDModeDelayPOC;
import com.velexsoft.checkengine.modess.OBDReadDTCCodesNew;
import com.velexsoft.checkengine.modess.Obd2Mode01_01MIL;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.IntepretDashValues;
import com.velexsoft.checkengine.utilities.LogFileNew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

import static com.velexsoft.checkengine.ConnActivityParent.currProtocol;
import static com.velexsoft.checkengine.ConnActivityParent.removeCarriage;


/**
 * Created by WoleProject on 11/23/2016.
 */
public class BluetoothService extends Service {

    public BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mList = new ArrayList();
    public ArrayList<ArrayList<String>>  mode06Array = new ArrayList<ArrayList<String>>();
    public static final String BT_DEVICE = "VVS1";

    SharedPreferences sharedPreferences;
    public static String globalMacAddress;
    LogFileNew l ;
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // device
    public boolean offlineMode;
    static public int rollingStatus = 0;

    // 0 means there are no services running
    // 1 means mainMenu service is running
    // 2 means vehicle paramenter Service is running
    // public mInHangler mHandler = new mInHangler(this);

    public static int mState = STATE_NONE;
    public static String deviceName;
    public Vector<Byte> packdata = new Vector<Byte>(2048);
    public static BluetoothClass.Device device1 = null;
    int mStartMode;    /** indicates how to behave if the service is killed */
    boolean  connStatus = false;
    String MILResult=null;
    String MILCount=null;
    Integer numofDTC = 0;
    String ECUProtocol = "";
    Double realTimeSpeed = 2.0;
    Context context;
    String currSSN;
    boolean logErrors = ConnActivityParent.logErrors;
    boolean playedMILlightAudio =   false;
    private static boolean rollDashService = false;
    private static String mBit01Value = "X";
    private static String mBit06Value = "X";
    String mode01completeBinaries = "X";
    String mode02completeBinaries = "X";
    String mode06completeBinaries = "X";
    String mode05completeBinaries = "X";
    String mode08completeBinaries = "X";


    Intent i;
    int speedLimitManage = 35;
    SharedPreferences settings;
    public static final String PREFS_NAME = ConnActivityParent.PREFS_NAME;

    public ArrayList<ArrayList<String>> mode05Array = new ArrayList();

    public ArrayList<ArrayList<String>> spkIgnitionArray = new ArrayList();

    public ArrayList<String> perfTrack = new ArrayList();

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w( context,"BLUETOOTHS: " + msg);
            //System.out.println ("BLUETOOTHS: " + msg);
        }
    }

    public BluetoothService() {
        System.out.println ("BLUETOOTHS: " + "BluetoothService.BluetoothService");
        //l.fileName = ConnActivityParent.logFileName;
    }

    @Override
    public void onCreate() {
        System.out.println ("BLUETOOTHS: " + "BluetoothService.onCreate");
        //lw ( "SERVICE   : BluetoothService.onCreate");
        super.onCreate();
       // lw ( "SERVICE   : Completed BluetoothService.onCreate [Success]");
        context = getBaseContext();
        l = new LogFileNew();
        l.fileName = ConnActivityParent.logFileName;
      //  l.w (  context, "-------------------------------------------------------------------------------");
      //  l.w (  context, "SERVICE   : BluetoothService.Contruction");
       // l.w (  context, "-------------------------------------------------------------------------------");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            lw ( "SERVICE   : BluetoothService.LocalBinder.getService");
            return BluetoothService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println ("BLUETOOTHS: " + "BluetoothService.onStartCommand");

        lw (  "SERVICE   : BluetoothService.onStartCommand");
        lw (  "SERVICE   : BluetoothService.onStartCommand");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (offlineMode) {
            lw("BlueToothService.onStartCommand... This is offlineMode ");
        } else {
            try {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(globalMacAddress);
                UUID myUUID = ConnActivityParent.myUUID;
                BluetoothSocket tmp = null;
                mBluetoothAdapter.cancelDiscovery();
                try {
                    tmp = device.createRfcommSocketToServiceRecord(myUUID);
                    lw (  "SERVICE   : BluetoothService.onStartCommand created Socket {Success]");
                } catch (IOException e) {
                    lw("BlueToothService - ERROR 0002 - Failed to create bluetooth socket");
                }

                mmSocket = tmp;
                lw("BlueToothService : About to connect 1");
                try {
                    lw (  "SERVICE   : BluetoothService.onStartCommand About to to connect 1....");
                    mmSocket.connect();

                    boolean verified =  verifySerial(mmSocket);
                    if (verified) {
                        lw("BlueToothService Successfully Connected 1...");
                        lw("SERVICE   : BluetoothService.onStartCommand Successfully Connected 1...[Success]");
                        initializeBluetoothService();
                    } else {
                        mmSocket.close();
                    }
                } catch (IOException connectException) {

                   lw ("BlueToothService - ERROR 0001 - Failed to connect at first attempt Error ="+connectException);
                    try {
                        lw (  "SERVICE   : BluetoothService.onStartCommand About to to connect 2....");
                        mmSocket.connect();
                        boolean verified =  verifySerial(mmSocket);
                        if (verified) {
                            lw("BlueToothService Successfully Connected 2...");
                            lw("SERVICE   : BluetoothService.onStartCommand Successfully Connected 2...[Success]");
                            initializeBluetoothService();
                        } else {
                            mmSocket.close();
                        }
                    } catch (IOException connectException1) {
                        lw ("BlueToothService ERROR 0003 - Failed to connect at second attempt");
                        lw (  "ERROR ERROR : BluetoothService.onStartCommand Failed to connect at second attempt");
                        cancel();
                    }
                }
            }catch ( Exception e ) {
               lw ( "ERROR ERROR : BluetoothService.onStartCommand Failed to connect..."+e);
                lw (  "ERROR ERROR : BluetoothService.onStartCommand Failed to connect..."+e);
            }
        }
        return START_STICKY;
    }

    public void initializeBluetoothService ( ) {
        lw (  "SERVICE   : BluetoothService.initializeBluetoothService");
        try {
            lw ( "Before Vibrate");
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            checkSparkorCompression();
            VibrateNow();
            releaseSocket();
            lw ( "Before Make sound");
        } catch ( Exception e) {
            lw (  "ERROR ERROR : BluetoothService.initializeBluetoothService Failed "+e);
        }
    }

    public boolean verifySerial( BluetoothSocket pSocket ) {
        boolean verified = false;
        try {
            settings = getSharedPreferences(PREFS_NAME, 0);
            String vSerialNumber = settings.getString("vSerialNumber", "");
            lw("verifySerial Phone Serial = "+vSerialNumber);
            OBDInitialize setReset = new OBDInitialize("AT @2");
            setReset.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( setReset.getResponse() );
            if (setReset.getResponse().contains(vSerialNumber)) {
                verified = true;
            } else {
                verified = false;
            }
        } catch (Exception e) {
            lw("ERROR ERROR: Validating Serial"+e);
        }
        return verified;
    }

    public void checkSparkorCompression () {
        lw (  "SERVICE   : BluetoothService.checkSparkorCompression");
        String vResponse = "";
        Thread inMyThread;
        inMyThread = new Thread(new Runnable() {
            @Override
            public void run() {
            String vIgnitionType;
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                try {
                    OBDCommand2Byte obdCommand = new OBDCommand2Byte("01 01");
                    obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    String second2Bytes = obdCommand.getResponse().substring(6, 8);
                    String second8Binary = ConvertValues.hexToBin(second2Bytes);
                    vIgnitionType =  second8Binary.substring(4, 5);
                    ConnActivityParent.ignitionType = vIgnitionType;

                    loadMode01binaries();

                    releaseSocket();
                } catch (Exception e) {
                    releaseSocket();
                    lw("ERROR ERROR : BluetoothService.checkSparkorCompression... Error = " + e);
                }
            }
            }
        });
        inMyThread.start();
    }

    public void testDoubleCode () {
        lw (  "SERVICE   : BluetoothService.testDoubleCode");
        String vResponse = "";
        Thread inMyThread;
        inMyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String vIgnitionType;

                try {
                    lw("setHeaderOff[AT H1]");
                    OBDInitialize setHeaderOff = new OBDInitialize("AT H0");
                    setHeaderOff.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    String vResponse = removeCarriage(setHeaderOff.getResponse());
                    lw("setHeaderOff.getResponse() = " + vResponse);

                } catch (Exception e) {
                    lw("ERROR ERROR: initializeGeneralCommand.setHeaderOff" + e);
                }

                if ( IsSocketBuzy() ) {
                    socketIsBuzy();
                }else {
                    runSocket();
                    try {
                        OBDCommand2Byte obdCommand = new OBDCommand2Byte("01 0D 04 05");
                        obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String second2Bytes = obdCommand.getResponse();
                        System.out.println ( "The Response = "+ second2Bytes );

                        releaseSocket();
                    } catch (Exception e) {
                        releaseSocket();
                        lw("ERROR ERROR : BluetoothService.testDoubleCode... Error = " + e);
                    }
                }
            }
        });
        inMyThread.start();
    }

    static String MILon = "0";

    public void makeSounds ( ArrayList<ArrayList<String>> dashArray ) {
        lw (  "SERVICE   : BluetoothService.makeSounds");

        final ArrayList<ArrayList<String>> tempProcessArray = dashArray;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean allow_voice_notify =  sharedPreferences.getBoolean("allow_voice_notification", true);

        lw (  "This is allow_voice_notify "+allow_voice_notify);

        if (allow_voice_notify) {

            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                IntepretDashValues intp = new IntepretDashValues(context, tempProcessArray);
                    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                    try {
                    if (intp.intepMIL().contains("BAD")) {
                        lw("MIL is Bad");
                        playSound("CheckEngineLightOn.mp3");
                        Thread.sleep(2500);
                    }
                } catch (Exception e) {
                    lw("ERROR ERROR : Failed to play MIL Sound");
                }

                try {
                    if (intp.getFuelTankGoodBad().contains("BAD")) {
                        lw("Gas Fuel Tank is Bad");
                        playSound("GasTankLow.mp3");
                        Thread.sleep(2500);
                    }
                } catch (Exception e) {
                    lw("ERROR ERROR : Failed to play MIL Sound");
                }

                try {
                    if (intp.getCoolTempGoodBad().contains("BAD")) {
                        lw("Cool Temperature is Bad");
                        playSound("CoolantTempHigh.mp3");
                        Thread.sleep(2500);
                    }
                } catch (Exception e) {
                    lw("ERROR ERROR : Failed to play MIL Sound");
                }

                try {
                    if (intp.getEngineOilGoodBad().contains("BAD")) {
                        lw("Engine oil is Bad");
                        playSound("EngineTemperatureHigh.mp3");
                        Thread.sleep(2500);
                    }
                } catch (Exception e) {
                    lw("ERROR ERROR : Failed to play MIL Sound");
                }

                try {
                    if (intp.getInputBatteryBad().contains("BAD")) {
                        lw("Input Battery  is Bad");
                        playSound("BatteryLow.mp3");
                        Thread.sleep(2500);
                    }
                } catch (Exception e) {
                    lw("ERROR ERROR : Failed to play MIL Sound");
                }

                }
            });
            inMyThread.start();
        }
    }

    public void VibrateNow () {

        boolean getVibrateOnConn =  sharedPreferences.getBoolean("vibrate_on_connection", true);

        if (getVibrateOnConn) {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(20);
                    } catch (Exception e) {

                    }
                }
            });
            inMyThread.start();
        }
    }

    public void loadMode01binaries() {
        lw (  "SERVICE   : BluetoothService.loadMode01binaries");
        if (mode01completeBinaries.equals("X")) {
            Mode01NewParams mode01 = new Mode01NewParams(mmSocket, "01", context);
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();

                try {
                    mode01.getBinaries();
                    releaseSocket();
                } catch( Exception e ) {
                    releaseSocket();
                    lw("ERROR ERROR : BluetoothService.loadMode01binaries... Error = "+e);
                }

                mode01.setResults();
                mode01completeBinaries = mode01.completeBinaries;
            }
        }
    }

    //boolean playedSound = false;

    public void playSound( String soundToPlay ) {
        lw(  "INFO      : About to play Vehicle maintenance sound");
/*
        String filename = "";
        if (soundToPlay.equals("MILon" )) {
            //filename = "VehicleMaintenanceRequired.wav";
            filename = "VehicleMaintenanceRequired.wav";
        } else if (soundToPlay.equals("fuelTank" )) {
            filename = "GasTankLow.wav";
        } else if (soundToPlay.equals("engineCoolant" )) {
            filename = "EngineTemperatureHigh.wav";
        }
*/
        final String vfilename = soundToPlay;

        Thread myThread;
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaPlayer player;
                    AssetFileDescriptor afd;
                    try {
                        afd = getAssets().openFd(vfilename);
                        player = new MediaPlayer();
                        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                }
            }
        });
        myThread.start();
    }

    public void loadMode06binaries() {
        lw (  "SERVICE   : BluetoothService.loadMode06binaries");
        if (mode06completeBinaries.equals("X")) {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();

                System.out.println ( "After runSocket ");
                try {
                    System.out.println ( "This is current Protocol = " + ConnActivityParent.currProtocolGroup );
                    if  (ConnActivityParent.currProtocolGroup.contains("ISO") || ConnActivityParent.currProtocolGroup.contains("1850")) {
                        Mode06NewParams mode06 = new Mode06NewParams(mmSocket, "06", context);
                        mode06completeBinaries = mode06.completeBinaries;
                    } else {
                        System.out.println( "Begining CAN" );
                        Mode06CANNewParams mode06 = new Mode06CANNewParams(mmSocket, "06", context);
                        mode06completeBinaries = mode06.completeBinaries;
                    }
                    System.out.println ( "This is after mode06completeBinaries "+mode06completeBinaries);
                    releaseSocket();
                } catch( Exception e ) {
                    releaseSocket();
                    lw("ERROR ERROR : BluetoothService.loadMode06binaries... Error = "+e);
                }
            }
        }
    }

    public void loadMode05binaries() {
        lw (  "SERVICE   : BluetoothService.loadMode05binaries");
        if (mode05completeBinaries.equals("X")) {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                try {
                    if  (ConnActivityParent.currProtocolGroup.contains("ISO") || ConnActivityParent.currProtocolGroup.contains("1850")) {
                        Mode06NewParams mode05 = new Mode06NewParams(mmSocket, "05", context);
                        mode05completeBinaries = mode05.completeBinaries;
                    } else {
                        Mode06CANNewParams mode05 = new Mode06CANNewParams(mmSocket, "05", context);
                        mode05completeBinaries = mode05.completeBinaries;
                    }
                    releaseSocket();
                } catch( Exception e ) {
                    releaseSocket();
                    lw("ERROR ERROR : BluetoothService.loadMode05binaries... Error = "+e);
                }
            }
        }
    }


    public void loadMode08binaries() {
        lw (  "SERVICE   : BluetoothService.loadMode08binaries");
        if (mode08completeBinaries.equals("X")) {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                try {
                    Mode01NewParams mode08 = new Mode01NewParams(mmSocket, "08", context);
                    mode08completeBinaries = mode08.completeBinaries;
                    releaseSocket();
                } catch( Exception e ) {
                    releaseSocket();
                    lw("ERROR ERROR : BluetoothService.loadMode08binaries... Error = "+e);
                }
            }
        }
    }

    public void loadMode02binaries(String frameCode ) {
        lw (  "SERVICE   : BluetoothService.loadMode02binaries");
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            try {
                Mode02NewParams mode02 = new Mode02NewParams(mmSocket, "02", frameCode, context );
                mode02completeBinaries = mode02.completeBinaries;
                lw ( frameCode +" - mode02completeBinaries = "+ mode02completeBinaries  );
                releaseSocket();
            } catch( Exception e ) {
                releaseSocket();
                lw("ERROR ERROR : BluetoothService.loadMode02binaries... Error = "+e);
            }
        }
    }


    public  ArrayList<ArrayList<String>> getReportVehicleParams() {
        lw (  "SERVICE   : BluetoothService.getReportVehicleParams");

        String Runtime1F;
        String Distance21;
        String Warmups30;
        String Distance31;

        InterpreteMode01 intPret = new InterpreteMode01();

        ArrayList<ArrayList<String>> tmpArray= new ArrayList();
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            try {
                runSocket();
                {
                    try {
                        Obd2Mode01_01MIL milInfo = new Obd2Mode01_01MIL(mmSocket, getApplication());
                        MILon = milInfo.Obd2Mode01_01MILNow("01");
                        ArrayList<String> row1 = new ArrayList();
                        row1.add("01");
                        row1.add("Check Engine Light");

                        String MILStringValue = "No";
                        if (MILon.equals("1")) {
                           lw("SERVICE   : ***** MIL IS ON *****  : " + MILon);
                            MILStringValue = "Yes";
                        }
                        row1.add(MILStringValue);
                        tmpArray.add( row1 );
                    } catch ( Exception e ) {
                        lw("WARNING   : BluetoothService.getReportVehicleParams... Error = "+e);
                    }
                }
                {
                    try {
                        // Run time since engine start
                        lw ( "SERVICE   : Run time since engine start");
                        OBDCommand2Byte obd011FCommand = new OBDCommand2Byte("01 1F");
                        obd011FCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String vResponse = removeCarriage(obd011FCommand.getResponse());
                        lw ( "SERVICE   : Response obd011FCommand = "+ vResponse );
                        if (obd011FCommand.getResponse().contains("411F")) {
                            Runtime1F = intPret.P1F(vResponse);
                            ArrayList<String> row2 = new ArrayList();
                            row2.add("1F");
                            row2.add("Run time since engine start");
                            row2.add(Runtime1F + "");
                            tmpArray.add(row2);
                            lw("SERVICE   : ***** Run time since engine start  *****  : " + Runtime1F);
                        }
                    }  catch ( Exception e ) {
                        lw("WARNING   : BluetoothService.getReportVehicleParams obd011FCommand... Error = "+e);
                    }
                }
                {
                    try {
                        // input Voltage
                        lw ( "SERVICE   : Input Voltage");
                        OBDInitialize doSomethin= new OBDInitialize("AT RV");
                        doSomethin.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String vResponse = removeCarriage(doSomethin.getResponse());
                        //System.out.println("AT RV = " + vResponse);
                        lw("INITIALIZE: AT RV = " + vResponse);
                        lw ( "SERVICE   : Response obd011FCommand = "+ vResponse );

                        ArrayList<String> row2 = new ArrayList();
                        row2.add("RV");
                        row2.add("Input Voltage");
                        row2.add(vResponse + "");
                        tmpArray.add(row2);
                        lw("SERVICE   : ***** Run time since engine start  *****  : " + vResponse);

                    }  catch ( Exception e ) {
                        lw("WARNING   : BluetoothService.getReportVehicleParams obd011FCommand... Error = "+e);
                    }
                }
                {
                    try {
                        //  Distance traveled with malfunction indicator lamp (MIL) on
                        lw ( "SERVICE   : Distance traveled with malfunction indicator lamp (MIL) on");
                        OBDCommand2Byte obd0121Command = new OBDCommand2Byte("01 21");
                        obd0121Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String vResponse = removeCarriage(obd0121Command.getResponse());
                        lw ( "SERVICE   : Response obd0121Command = "+ vResponse );
                        if (obd0121Command.getResponse().contains("4121")) {
                            Distance21 = intPret.P21(vResponse);
                            ArrayList<String> row3 = new ArrayList();
                            row3.add("21");
                            row3.add("Distance traveled with malfunction indicator lamp (MIL) on");
                            row3.add(Distance21 + "");
                            tmpArray.add(row3);
                            lw("SERVICE   : ***** Distance traveled with malfunction indicator lamp (MIL) on  *****  : " + Distance21);
                        }
                        }catch ( Exception e ) {
                        lw("WARNING   : BluetoothService.getReportVehicleParams obd0121Command... Error = "+e);
                    }
                }
                {
                    try {
                        // Warm-ups since codes cleared
                        lw ( "SERVICE   : Warm-ups since codes cleared");
                        OBDCommand1Byte obd0130Command = new OBDCommand1Byte("01 30");
                        obd0130Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String vResponse = removeCarriage(obd0130Command.getResponse());
                        lw ( "SERVICE   : Response obd0130Command = "+ vResponse );
                        if (obd0130Command.getResponse().contains("4130")) {
                            Warmups30 = intPret.P30(vResponse);
                            ArrayList<String> row3 = new ArrayList();
                            row3.add("30");
                            row3.add("Warm-ups since codes cleared");
                            row3.add(Warmups30 + "");
                            tmpArray.add(row3);
                            lw("SERVICE   : ***** Warm-ups since codes cleared  *****  : " + Warmups30);
                        }
                    }catch ( Exception e ) {
                        lw("WARNING   : BluetoothService.getReportVehicleParams obd0130Command... Error = "+e);
                    }
                }
                {
                    try {
                        // Distance traveled since codes cleared
                        lw ( "SERVICE   : Distance traveled since codes cleared");
                        OBDCommand2Byte obd0131Command = new OBDCommand2Byte("01 31");
                        obd0131Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        lw ( "SERVICE   : Response obd0131Command = "+ removeCarriage(obd0131Command.getResponse() ) );
                        if (obd0131Command.getResponse().contains("4131")) {
                            Distance31 = intPret.P31(obd0131Command.getResponse().substring(4, 8));
                            ArrayList<String> row3 = new ArrayList();
                            row3.add("31");
                            row3.add("Distance traveled since codes cleared");
                            row3.add(Distance31 + "");
                            tmpArray.add(row3);
                            lw("SERVICE   : ***** Distance traveled since codes cleared  *****  : " + Distance31);
                        }
                    }catch ( Exception e ) {
                        lw("WARNING   : BluetoothService.getReportVehicleParams obd0131Command... Error = "+e);
                    }
                }
                releaseSocket();
            } catch (Exception e) {
                releaseSocket();
                lw("ERROR ERROR : BluetoothService.getReportVehicleParams... Error = "+e);
            }
        }
        return tmpArray;
    }

    public ArrayList<String> distanceTravelled() {

            InterpreteMode01 intPret = new InterpreteMode01();
            String Distance21= "0";
            ArrayList<String> row3 = new ArrayList();
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                try {
                    //  Distance traveled with malfunction indicator lamp (MIL) on
                    lw("SERVICE   : Distance traveled with malfunction indicator lamp (MIL) on");
                    OBDCommand2Byte obd0121Command = new OBDCommand2Byte("01 21");
                    obd0121Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    String vResponse = removeCarriage(obd0121Command.getResponse());
                    lw("SERVICE   : Response obd0121Command = " + vResponse);
                    if (obd0121Command.getResponse().contains("4121")) {
                        Distance21 = intPret.P21(vResponse);

                        row3.add("21");
                        row3.add("Distance traveled with malfunction indicator lamp (MIL) on");
                        row3.add(Distance21 + "");

                        lw("SERVICE   : ***** Distance traveled with malfunction indicator lamp (MIL) on  *****  : " + Distance21);
                    }
                    releaseSocket();
                } catch (Exception e) {
                    releaseSocket();
                    lw("WARNING   : BluetoothService.getReportVehicleParams obd0121Command... Error = " + e);
                }
            }
        return row3;
    }

    public ArrayList<String> distanceTravelledCodeCleared() {

        InterpreteMode01 intPret = new InterpreteMode01();
        String Distance31= "0";
        ArrayList<String> row3 = new ArrayList();

        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            try {
                // Distance traveled since codes cleared
                lw("SERVICE   : Distance traveled since codes cleared");
                OBDCommand2Byte obd0131Command = new OBDCommand2Byte("01 31");
                obd0131Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                lw("SERVICE   : Response obd0131Command = " + removeCarriage(obd0131Command.getResponse()));
                if (obd0131Command.getResponse().contains("4131")) {
                    Distance31 = intPret.P31(obd0131Command.getResponse().substring(4, 8));
                    row3.add("31");
                    row3.add("Distance traveled since codes cleared");
                    row3.add(Distance31 + "");
                    lw("SERVICE   : ***** Distance traveled since codes cleared  *****  : " + Distance31);
                }
                releaseSocket();
            } catch (Exception e) {
                releaseSocket();
                lw("WARNING   : BluetoothService.getReportVehicleParams obd0131Command... Error = " + e);
            }
        }
        return row3;
    }


    public static void setRollDashService() {
        rollDashService = false;
    }

    public Boolean IsSocketBuzy() {
        return rollDashService;
    }

    public void runSocket(){
        rollDashService = true;
    }

    public static void releaseSocket() {
        rollDashService = false;
    }

    public void socketIsBuzy ( ) {
        lw("SOCKET    : SOCKET Socket is Buzy");
    }

    public  ArrayList<ArrayList<String>> CheckDashBoard() {
        lw ("*********************************************************************");
        lw (  "SERVICE   : BluetoothService.CheckDashBoard");
        lw ("*********************************************************************");

        String MILon = "0";
        String fuelTank = "0";
        String engineCoolant = "0";
        String engineOilTemp = "0";
        String inputVolt = "0";

        ArrayList<ArrayList<String>> tmpArray= new ArrayList();
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            try {
                runSocket();
                {
                    ArrayList<String> row1 = new ArrayList();
                    row1.add("MILLight");
                    try {
                        Obd2Mode01_01MIL milInfo = new Obd2Mode01_01MIL(mmSocket, getApplication());
                        MILon = milInfo.Obd2Mode01_01MILNow("01");
                        row1.add(MILon);

                    } catch ( Exception e ) {
                        row1.add(MILon);
                        lw("WARNING   : BluetoothService.CheckDashBoard1... Error = "+e);
                    }
                    lw("SERVICE   :(1) HEX ***** MIL IS *****  : " + MILon);
                    tmpArray.add(0, row1);
                }
                {
                    ArrayList<String> row2 = new ArrayList();
                    row2.add("GasTank");
                    try {
                        // Fuel Tank Level
                        OBDCommand1Byte obd012FCommand = new OBDCommand1Byte("01 2F");
                        obd012FCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        //System.out.println ("Gas Tank = "+obd012FCommand.getResponse().substring(4, 6) );
                        fuelTank = obd012FCommand.getResponse();
                        if (fuelTank.contains("412F")) {
                            lw("fuelTank.getResponse() = is GOOD " + fuelTank);
                            row2.add(fuelTank + "");
                        } else {
                            row2.add("0");
                        }
                    }  catch ( Exception e ) {
                        row2.add(fuelTank + "");
                        lw("WARNING   : BluetoothService.CheckDashBoard2.fuelTank... Error = "+e);
                    }
                    lw("SERVICE   :(2) HEX ***** FUEL TANK  *****  : " + fuelTank);
                    tmpArray.add(1, row2);
                }
                {
                    ArrayList<String> row3 = new ArrayList();
                    row3.add("CoolTemp");
                    try {
                        // Engine Coolant Temperature
                        OBDCommand1Byte obd0105Command = new OBDCommand1Byte("01 05");
                        obd0105Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        engineCoolant = obd0105Command.getResponse();

                        if (engineCoolant.contains("4105")) {
                            lw("engineCoolant.getResponse() = is GOOD " + engineCoolant);
                            row3.add(engineCoolant + "");
                        } else {
                            row3.add( "0" );
                        }

                    }catch ( Exception e ) {
                        row3.add(engineCoolant + "");
                        lw("WARNING   : BluetoothService.CheckDashBoard3.engineCoolant.... Error = "+e);
                    }
                    lw("SERVICE   :(3) HEX ***** Coolant Temp  *****  : " + engineCoolant);
                    tmpArray.add(2, row3);
                }
                {
                    ArrayList<String> row3 = new ArrayList();
                    row3.add("engineOilTemp");
                    try {
                        // Engine Oil Temperature
                        OBDCommand1Byte obd0105Command = new OBDCommand1Byte("01 5C");
                        obd0105Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        engineOilTemp = obd0105Command.getResponse();
                        if (engineOilTemp.contains("415C")) {
                            lw("engineOilTemp.getResponse() = is GOOD " + engineOilTemp);
                            row3.add(engineOilTemp + "");
                        } else {
                            row3.add("0");
                        }

                    }catch ( Exception e ) {
                        row3.add(engineOilTemp + "");
                        lw("WARNING   : BluetoothService.CheckDashBoard4... Error = "+e);
                    }
                    lw("SERVICE   :(4) HEX ***** Engine Oil Temp  *****  : " + engineOilTemp);
                    tmpArray.add(3, row3);
                }
                {
                    ArrayList<String> row4 = new ArrayList();
                    row4.add("inputVoltage");
                    try {
                        // Input Battery
                        OBDInitialize doSomethin= new OBDInitialize("AT RV");
                        doSomethin.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        inputVolt = removeCarriage(doSomethin.getResponse());
                        row4.add(inputVolt );
                        lw("AT RV = " + inputVolt);

                    }catch ( Exception e ) {
                        row4.add(inputVolt + "");
                        lw("WARNING   : BluetoothService.CheckDashBoard5... Error = "+e);
                    }
                    lw("SERVICE   :(5) HEX ***** Input Voltage  *****  : " + inputVolt);
                    tmpArray.add(4, row4);
                }
                releaseSocket();
            } catch (Exception e) {
                releaseSocket();
                lw("ERROR ERROR : BluetoothService.CheckDashBoard... Error = "+e);
            }
        }
        if (!firstTimeSounded) {
            System.out.println ( "This is the first Time Sound...." );
            makeSounds(tmpArray);
            firstTimeSounded= true;
        }
        lw ("*********************************************************************");
        lw ( "tmpArray.size() = "+tmpArray.size() );
        lw ("*********************************************************************");
        return tmpArray;
    }

    static boolean firstTimeSounded = false;


    public void getVehicleInformation() {

        Mode09Info mode9 = new Mode09Info(mmSocket);
        //mode9.runAll();
        mode9.get01CalibrationCount(mmSocket);
        mode9.get02DataCalibrationCount(mmSocket);
        mode9.get03CalibrationCount(mmSocket);
        mode9.get04DataCalibrationCount(mmSocket);
        mode9.get05CalibrationCount(mmSocket);
        mode9.get06DataCalibrationCount(mmSocket);
        mode9.get07CalibrationCount(mmSocket);
        mode9.get08DataCalibrationCount(mmSocket);
        mode9.get09CalibrationCount(mmSocket);
        mode9.get0ADataCalibrationCount(mmSocket);
    }

    public String CheckNoOfDTC(String code) {
        lw (  "SERVICE   : BluetoothService.CheckNoOfDTC. Code = "+code);
        MILCount="0";
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {

            try {
                runSocket();
                Obd2Mode01_01MIL milInfo = new Obd2Mode01_01MIL(mmSocket, getApplication());
                milInfo.Obd2Mode01_01MILNow(code);
                MILCount = milInfo.MILCount;
                lw ("CheckNoOfDTC Result ["+code+"] - MILCount = "+MILCount );
                releaseSocket();
            } catch (Exception e) {
                releaseSocket();
                if (code.equals("01")) {
                    lw("ERROR ERROR : BluetoothService.CheckNoOfDTC... Error = " + e);
                } else {
                    lw("WARNING : BluetoothService.CheckNoOfDTC... Error = " + e);
                }
            }
        }
        return MILCount;
    }

    public ArrayList<ArrayList<String>> readTroubleCodes(String code , int vNoOfDTC ) {
        lw (  "SERVICE   : BluetoothService.readTroubleCodes");
        ArrayList<ArrayList<String>> dTCArray = new ArrayList();
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            try {
                runSocket();
                OBDReadDTCCodesNew readNow = new OBDReadDTCCodesNew
                        ( code, vNoOfDTC, getBaseContext() );
                readNow.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                       String byteResponse =  readNow.getResponse() ;
                lw ("This is DTC Byte Response " + byteResponse);
                dTCArray = readNow.DTCCodeArray;
                lw ("This is DTC Byte dTCArray.count() " + dTCArray.size());
                releaseSocket();
            } catch (Exception e) {
                releaseSocket();
                lw("ERROR ERROR : BluetoothService.readTroubleCodes... Error = "+e);
            }
        }
        return dTCArray;
    }

    public boolean clearDiagnosticCodes ( ) {
        lw (  "SERVICE   : BluetoothService.clearDiagnosticCodes");
        boolean cleared = false;
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            try {
                runSocket();
                lw ("Invoking clearTroubleCodeCommand PROC" );
                OBDClearTroubleCode cLearTroubleCodeCommand = new OBDClearTroubleCode("04");
                cLearTroubleCodeCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                String codeResponse = cLearTroubleCodeCommand.getResponse();
                lw (  ": codeResponse04 Value = "+ ConvertValues.removeFrameHeader(codeResponse) );
                lw (  ": SUCCESS - cleared trouble code...");
                OBDClearTroubleCode cLearTroubleCodeCommand1 = new OBDClearTroubleCode("14");
                cLearTroubleCodeCommand1.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                String codeResponse1 = cLearTroubleCodeCommand1.getResponse();
                lw (  ": codeResponse14 Value = "+ ConvertValues.removeFrameHeader(codeResponse1) );
                lw (  ": SUCCESS - cleared trouble code...");
                releaseSocket();
                cleared =  true;
            } catch (Exception e) {
                releaseSocket();
                cleared =  false;
                lw("ERROR ERROR : BluetoothService.clearDiagnosticCodes... Error = "+e);
            }
        }
        return cleared;
    }

    public ArrayList<ArrayList<String>>  freezeFrameCount ( ) {
        lw (  "SERVICE   : BluetoothService.freezeFrameCount ");
        ArrayList<ArrayList<String>>  freezeCount = new ArrayList();
        String[] freezeFrameNo =   {"00","01","02","03","04","05","06","07","08"};
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            for (String s : freezeFrameNo) {
                if (rollDashService) {

                    String mCommand =  "02 02 " + s ;
                    System.out.println( "freezeFrameCount - mCommand = "+mCommand );
                    try {
                        OBDCommand1Byte obdCommand = new OBDCommand1Byte(mCommand);
                        obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String vResponse = obdCommand.getResponse();
                        ArrayList freezeRow = new ArrayList();
                        freezeRow.add( s );
                        freezeRow.add(vResponse);
                        System.out.println ( "Frame No/DTC: "+ s +" - " + vResponse   );
                        freezeCount.add (freezeRow);
                    } catch (Exception e) {
                        lw("ERROR ERROR : BluetoothService.freezeFrameCount... Error = "+e);
                    }
                }
            }
        }
        releaseSocket();
        return freezeCount;
    }

    static ArrayList<ArrayList<String>>  tempFreezeArray ;

    public ArrayList<ArrayList<String>> freezeFrameServiceReport(  String[] frameItems ) {
        lw (  "SERVICE   : BluetoothService.freezeFrameServiceReport freezeCode");
        int firstIndex  =  0;
        int secondIndex =  1;

        String mMode = "02";

        tempFreezeArray = new ArrayList<>();


        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();

            for ( String freezeCode: frameItems ) {
                try {
                    freezeFrameService(  freezeCode , false );

                    //releaseSocket();
                } catch (Exception e) {
                    e.printStackTrace();

                    lw("ERROR ERROR : BluetoothService.freezeFrameServiceReport freezeCode... Error = " + e);
                }

            }
            releaseSocket();
            System.out.println ( "THis is the freezeCode  tempArray.size() = "+tempFreezeArray.size()  );
        }
        return tempFreezeArray;
    }

    public String[] validateFrames(  ArrayList<ArrayList<String>>  inTmpFrame) {

        int numCount = 0;
        for (int i = 0; i < inTmpFrame.size(); i++) {
            ArrayList<String> thisFrameCheck = inTmpFrame.get(i);
            //lw ( "thisFrameCheck 1= " +thisFrameCheck.get(0)+" - "+thisFrameCheck.get(1)  );
            if (thisFrameCheck.get(1).contains("4202") ) {
                numCount++;
            }
        }

        lw ( "This is numCount = "+numCount );
        String [] tmp = new String[ numCount ];
        int cntt = 0;

        for (int i = 0; i < inTmpFrame.size(); i++) {
            ArrayList<String> thisFrameCheck = inTmpFrame.get(i);
            lw ( "thisFrameCheck 2 = " +thisFrameCheck.get(0)+" - "+thisFrameCheck.get(1)  );
            if (thisFrameCheck.get(1).contains("4202") ) {
                lw ( "thisFrameCheck.get(0) = "+thisFrameCheck.get(0) );
                tmp[cntt] = thisFrameCheck.get(0);
                cntt++;
            }
        }
        return tmp;
    }

    public void freezeFrameService(String vFreezeCode,  boolean vbroadcast) {
        lw (  "SERVICE   : BluetoothService.freezeFrameService");
        final String freezeCode =  vFreezeCode;
        final boolean broadcast = vbroadcast;
        Thread vehicleParamThread;
        vehicleParamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int firstIndex  =  0;
                int secondIndex =  1;
                loadMode02binaries(freezeCode);
                String mMode = "02";

                if ( IsSocketBuzy() ) {
                    socketIsBuzy();
                }else {
                    runSocket();

                    try {
                        OBDCommand4Byte obdenableCommand = new OBDCommand4Byte("12");
                        obdenableCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        lw("Response to FreezeFrame request - Mode $12 = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                    } catch ( Exception e ) {
                        lw("ERROR ERROR : BluetoothService.freezeFrame request - Mode $12 ... Error = "+e);
                    }
                    System.out.println ( "mode02completeBinaries = "+mode02completeBinaries );
                    try {
                        //for (String s : modeHexArray) {
                        for (int i = 0; i < mode02completeBinaries.length(); i++) {
                            mBit01Value = null;
                            if (rollDashService) {
                                String s = modeHexArray[i];
                                if (s.length()==2) {
                                    mBit01Value = mode02completeBinaries.substring(firstIndex, secondIndex);
                                    System.out.println("firstIndex = " + firstIndex + ", secondIndex = " + secondIndex + " - modeHexArray = "
                                            + s + ", mBit01Value = " + mBit01Value);

                                    String mCommand = mMode + " " + s + " " + freezeCode;
                                    String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit01Value + "]";
                                    System.out.println( "mCommamndAND = "+mCommamndAND );
                                    if (mBit01Value.contains("1")) {
                                        try {
                                            OBDCommand1Byte obdCommand = new OBDCommand1Byte(mCommand);
                                            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                            //String vResponse = obdCommand.getResponse().substring(0, 10);
                                            String vResponse = obdCommand.getResponse();
                                            ArrayList freezeRow = new ArrayList();
                                            freezeRow.add("P" + s);
                                            freezeRow.add(vResponse);
                                            if (broadcast) {
                                                Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("freezeRow", freezeRow);
                                                mCommamndAND = mCommamndAND + "Response = " + vResponse;
                                                lw("FreezeFrame = " + mCommamndAND + " = " + vResponse);
                                                System.out.println("FreezeFrame = " + mCommamndAND + " = " + vResponse);
                                                sendBroadcast(intent);
                                            } else {
                                                tempFreezeArray.add(freezeRow);
                                            }
                                        } catch (Exception e) {
                                            lw("ERROR ERROR : BluetoothService.freezeFrameService 2... Error = " + e);
                                        }
                                    }
                                    firstIndex++;
                                    secondIndex++;
                                } else {
                                    System.out.println ( "Error S is not valid Error"   );
                                }

                            }
                        }
                        //toast("Completed Freeze Frame");
                        releaseSocket();
                    } catch (Exception e) {
                        releaseSocket();
                        e.printStackTrace();
                        lw("ERROR ERROR : BluetoothService.freezeFrameService 3... Error = "+e);
                    }
                }
            }
        });
        vehicleParamThread.start();
    }

    //----------------------------Diagnostic Scan --------------------------------
    //----------------------------Diagnostic ENDS --------------------------------

    public ArrayList<ArrayList<String>> getBadMonitorsArray(String monMode ) {
        lw (  "SERVICE   : BluetoothService.getBadMonitorsArray");
        ArrayList<ArrayList<String>> tmpArray = new ArrayList<>();
        ArrayList<ArrayList<String>> newTmpArray = new ArrayList<>();
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            try {
                MonitorsModeArray mModeArrayClass = new MonitorsModeArray();
                tmpArray = mModeArrayClass.MonitorsModeArrayRun(mmSocket, true, context, monMode);
                //newTmpArray =
                lw ( "tmpArray = "+tmpArray );
                for ( int i = 0; i < tmpArray.size(); i++ ) {
                    ArrayList<String> rowArray = new ArrayList();
                    rowArray = tmpArray.get(i);

                    if ( rowArray.get(2).contains("1")  ) {
                        newTmpArray.add( rowArray );
                        lw ("getBadMonitorsArray = " + rowArray.get(0) + " - " + rowArray.get(1) + " - " + rowArray.get(2));
                    }
                }
                releaseSocket();
            } catch ( Exception e ) {
                releaseSocket();
                lw("WARNING   : BluetoothService.getBadMonitorsArray... Error = "+e);
            }
        }
        return newTmpArray;
    }

    // ---------------------------MonitorScreen START------------------------------
    // ---------------------------MonitorScreen------------------------------------
    public static String monRawResponse ;

    public ArrayList<ArrayList<String>> getMonitorsArray(String monMode ) {
        System.out.println (  "SERVICE   : BluetoothService.getMonitorsArray");
        ArrayList<ArrayList<String>> tmpArray = new ArrayList<>();
        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        } else {
            runSocket();
            try {
                monRawResponse = "";
                MonitorsModeArray mModeArrayClass = new MonitorsModeArray();
                tmpArray = mModeArrayClass.MonitorsModeArrayRun(mmSocket, true,context, monMode);
                monRawResponse = mModeArrayClass.vResponse;
                releaseSocket();
            } catch ( Exception e ) {
                releaseSocket();
                lw("WARNING   : BluetoothService.getMonitorsArray... Error = "+e);
            }
        }
        return tmpArray;
    }
// ---------------------------MonitorScreen------------------------------------
// ---------------------------MonitorScreen START------------------------------

    public void readPerfTrack() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.readPerfTrack");
        lw (  "-------------------------------------------------------------------------------");
        boolean saved = false;
        String MIL =  "0";
        lw ( "This is checklight = "+MIL);
        String ignitionType = ConnActivityParent.ignitionType;

        try {
            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("MIL", MIL);

            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                InUsePerformanceTracking perfTracking = new InUsePerformanceTracking(mmSocket, context);

                if ( ignitionType.equals("0") ) {

                    perfTrack = perfTracking.sparkIgnition();
                } else if ( ignitionType.equals("1") ){
                    perfTrack = perfTracking.compressionIgnition();
                }

                spkIgnitionArray = perfTracking.spkIgnitionArray;
            }
            sendBroadcast(intent);
            releaseSocket();
        } catch (Exception e) {
            releaseSocket();
            lw ( "WARNING     : readPerfTrack method failed = "+e);
        }
        lw (  " BluetoothService.readPerfTrack completed [Success]");
    };

    // ---------------------------MonitorScreen------------------------------------------


    // ---------------------------Vehicle Statistics------------------------------------------
    public  void vehicleStatsService () {
        lw (  "SERVICE   : BluetoothService.vehicleStatsService");

        Bundle statsBundle = new Bundle();
        OBDCommand1Byte obd010DCommand; // Vehicle Speed
        OBDCommand2Byte obd0110Command; // MAF Mass Air Flow Rate
        OBDCommand1Byte obd010CCommand; // Engine RPM
        OBDCommand1Byte obd010FCommand; // Coolant Temperature
        OBDCommand1Byte obd012FCommand; // Fuel Tank Level

        String vSpeedByte = null;
        String vRPMByte = null ;

        Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("statsBundle", statsBundle);

        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            while (rollDashService) {
                {
                    try {  // Vehicle speed   -  1 Bytes
                        obd010DCommand = new OBDCommand1Byte("01 0D");
                        obd010DCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        vSpeedByte = obd010DCommand.getResponse().substring(4, 6);
                    } catch (Exception e) {
                        releaseSocket();
                        lw("ERROR ERROR : Error BluetoothService.vehicleStatsService. Error = " + e);
                    }
                }
/*
                {
                    try {  // Engine RPM - 2 Bytes
                        obd010CCommand = new OBDCommand1Byte("01 0C");
                        obd010CCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        vRPMByte = obd010CCommand.getResponse().substring(4, 8);

                    } catch (Exception e) {
                        releaseSocket();
                        lw("ERROR ERROR : Error BluetoothService.vehicleStatsService. Error= " + e);
                    }
                }
                */
                statsBundle.putString("0", vSpeedByte);
               // statsBundle.putString("1", vRPMByte);
                sendBroadcast(intent);
            }
            releaseSocket();
        }
    }
    // ---------------------------Vehicle Statistics------------------------------------------

    // ---------------------------Vehicle Parameters------------------------------------------


    public static String[] modeHexArray =
            {   "01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F","10",
                    "11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
                    "21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F","30",
                    "31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
                    "41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F","50",
                    "51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
                    "61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F","70",
                    "71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
                    "81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F","90",
                    "91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
            };

    public void vehicleParamService1() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.vehicleParamService1");
        lw (  "-------------------------------------------------------------------------------");
        lw (" SUCCESS - cleared vehicleParamService...");
        Thread vehicleParamThread;
        vehicleParamThread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (ConnActivityParent.currProtocolGroup.contains("ISO") || ConnActivityParent.currProtocolGroup.contains("1850")) {
                    lw  (   "INFO      : About to execute ISO "+ currProtocol);
                    boolean okToRun = okToRunMode06IS01850();
                } else {
                    lw  (   "INFO      : About to execute CAN :"+ currProtocol+", Protocol Group:" +ConnActivityParent.currProtocolGroup);
                    boolean okToRun = okToRunMode06CAN();
                }

                loadMode01binaries();
                int firstIndex  =  0;
                int secondIndex =  1;
                String mMode = "01";
                lw  ( "mBitValue = "+mBit01Value +", modeHexArray = "+modeHexArray  );
                InterpreteMode01 interprete = new InterpreteMode01();
                try {
                    if ( IsSocketBuzy() ) {
                        socketIsBuzy();
                    }else {
                        runSocket();
                        lw (  "INFO      : *************************************************");
                        lw (  "INFO      : Results for Vehicle Parameters" );
                        lw (  "INFO      : *************************************************");
                        for (String s : modeHexArray) {
                            //Do your stuff here
                            if (rollDashService) {
                                mBit01Value = mode01completeBinaries.substring(firstIndex, secondIndex);
                                //System.out.println ( "firstIndex = "+firstIndex+", secondIndex = "+secondIndex +" - modeHexArray = " +s );
                                String mCommand = mMode + " " + s;
                                String mCommamndAND = mCommand+" - [" + firstIndex + "," + secondIndex + "] - [" + mBit01Value + "]";
                                if (mBit01Value.equals("1")) {
                                    try {
                                        String pulloutCode = "P" + s;
                                        OBDCommand1Byte obdCommand = new OBDCommand1Byte(mCommand);
                                        obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                        //String vResponse = obdCommand.getResponse().substring(0, 8);
                                        String vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                        ArrayList paramRow = new ArrayList();

                                        paramRow.add(pulloutCode);
                                        paramRow.add(vResponse);
                                        Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("paramRow", paramRow);
                                        mCommamndAND = mCommamndAND + "Response = " + vResponse;
                                        lw ("vehParams = " + mCommamndAND);
                                        sendBroadcast(intent);
                                    } catch (Exception e) {
                                        lw ("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                        System.out.println("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                    }
                                }
                                firstIndex++;
                                secondIndex++;
                            }
                        }
                        releaseSocket();
                    }
                } catch (Exception e ){
                    releaseSocket();
                    lw("WARNING   : Before Executing vehicleParamService1 Error = "+e);
                }
            }
        });
        vehicleParamThread.start();
    }

    public static String[] modeHex08Array =  {
            "00","20","40","60","80","A0","C0","E0"
    };

    public void mode08RequestTestService() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.mode08RequestTestService");
        lw (  "-------------------------------------------------------------------------------");
        System.out.println("Begin mode08RequestTestService...");

        Thread vehicleParamThread;
        vehicleParamThread = new Thread(new Runnable() {
            @Override
            public void run() {

                getMonitorsArray("41");

                int firstIndex  =  0;
                int secondIndex =  1;
                String mMode = "08";
                System.out.println ( "mBitValue = "+mBit01Value +", modeHexArray = "+modeHexArray  );
                String mode08completeBinaries = mode01completeBinaries.replace("0","1" );
                try {
                    for (String s : modeHex08Array) {
                        //Do your stuff here
                        if (rollDashService) {
                            mBit01Value = mode08completeBinaries.substring(firstIndex, secondIndex);
                            String mCommand = mMode + " " + s;
                            String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit01Value + "]";
                            //l.w("mCommand = " + mCommand );
                            if (mBit01Value.equals("1")) {
                                try {
                                    OBDCommand1Byte obdCommand = new OBDCommand1Byte(mCommand);
                                    obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                    String vResponse = obdCommand.getResponse().substring(0, 8);
                                    ArrayList paramRow = new ArrayList();
                                    paramRow.add("P" + s);
                                    paramRow.add(vResponse);
                                    //Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("paramRow", paramRow);
                                    mCommamndAND = mCommamndAND + "Response = " + vResponse;
                                    System.out.println("Request Comp Test = " + mCommamndAND);
                                    lw ("Request Comp Test = " + mCommamndAND);
                                    //sendBroadcast(intent);
                                } catch (Exception e) {
                                    lw ("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                    System.out.println("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                }
                            }
                            firstIndex++;
                            secondIndex++;
                        }
                    }
                   // toast( "Completed mode08RequestTestService");
                    rollDashService=false;
                } catch (Exception e ){
                    lw("ERROR ERROR : Before Executing vehicleParamService1.Obd2ModeSet20_01_34_3B");
                }
            }
        });
        vehicleParamThread.start();
    }

    public static String[] modeHex05Array =  {
            "01 01","01 02","01 03","01 04","01 05","01 06","01 07","01 08","01 09","01 0A","01 0B","01 0C","01 0D","01 0E","01 0F","01 10",
            "02 01","02 02","02 03","02 04","02 05","02 06","02 07","02 08","02 09","02 0A","02 0B","02 0C","02 0D","02 0E","02 0F","02 10",
    };

    public void mode05RequestTestService() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.mode05RequestTestService");
        lw (  "-------------------------------------------------------------------------------");
        System.out.println(" Begin mode05RequestTestService...");
        Thread vehicleParamThread;
        vehicleParamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int firstIndex  =  0;
                int secondIndex =  1;
                String mMode = "05";
                System.out.println ( "MODE 05 Request Component Test"  );
                System.out.println ( "mBitValue = "+mBit01Value +", modeHexArray = "+modeHexArray  );
                String mode05completeBinaries = mode01completeBinaries.replace("0","1" );
                try {
                    if ( IsSocketBuzy() ) {
                        socketIsBuzy();
                    }else {
                        runSocket();
                        for (String s : modeHex05Array) {
                            //Do your stuff here
                            if (rollDashService) {
                                mBit01Value = mode05completeBinaries.substring(firstIndex, secondIndex);
                                String mCommand = mMode + " " + s;
                                String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit01Value + "]";
                                //l.w("mCommand = " + mCommand );
                                if (mBit01Value.equals("1")) {
                                    try {
                                        OBDCommand1Byte obdCommand = new OBDCommand1Byte(mCommand);
                                        obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                        String vResponse = obdCommand.getResponse().substring(0, 8);
                                        ArrayList paramRow = new ArrayList();
                                        paramRow.add("P" + s);
                                        paramRow.add(vResponse);
                                        //Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("paramRow", paramRow);
                                        mCommamndAND = mCommamndAND + "Response = " + vResponse;
                                        System.out.println("mCommamndAND = " + mCommamndAND);
                                        lw ("Mode 05 Check Data = " + mCommamndAND);
                                        //sendBroadcast(intent);
                                    } catch (Exception e) {
                                        lw ("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                        System.out.println("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                    }
                                }
                                firstIndex++;
                                secondIndex++;
                            }
                        }
                    }
                    rollDashService=false;
                } catch (Exception e ){
                    lw("ERROR ERROR : BluetoothService.mode05RequestTestService");
                }
            }
        });
        vehicleParamThread.start();
    }

    public void mode01CompleteSet() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.mode01CompleteSet");
        lw (  "-------------------------------------------------------------------------------");
        System.out.println(" Begin mode01CompleteSet...");

        Thread vehicleParamThread;
        vehicleParamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int firstIndex  =  0;
                int secondIndex =  1;
                String mMode = "01";
                System.out.println ( "MODE 01 Parameters Full Run"  );

                for (String s : modeHexArray) {
                    System.out.println ( "This is s"+s );
                    String command = "01 "+s;
                    try {
                        OBDModeDelayPOC obdCommand = new OBDModeDelayPOC(command);
                        obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        String vResponse = ConvertValues.removeFrameHeader( obdCommand.getResponse() );
                        System.out.println ( "Command = "+command+", Response = "+vResponse );
                        lw  ( "Command = "+command+", Response = "+vResponse );
                    } catch (Exception e) {
                        lw ("ERROR ERROR : Error getMode01Response re " + command + " Data..." + e);
                        System.out.println("ERROR ERROR : GetMode01Response " + command + " Data..." + e);
                    }
                }

                //System.out.println ( "mBitValue = "+mBit01Value +", modeHexArray = "+modeHexArray  );
                /*
            mode01completeBinaries = mode01completeBinaries + "1111111111111111111111111111111111111111111111111111111111111111111";
            String mode0101completeBinaries = mode01completeBinaries.replace("0","1" );
            try {
                for (String s : modeHexArray) {
                    // Do your stuff here
                    if (rollDashService) {
                        mBit01Value = mode0101completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit01Value + "]";
                        //l.w("mCommand = " + mCommand );
                        if (mBit01Value.equals("1")) {
                            try {
                                OBDModeDelayPOC obdCommand = new OBDModeDelayPOC(mCommand);
                                obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                String vResponse =   obdCommand.getResponse();
                                ArrayList paramRow = new ArrayList();
                                paramRow.add("P" + s);
                                paramRow.add(vResponse);
                                //Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("paramRow", paramRow);
                                mCommamndAND = mCommamndAND + "Response = " + vResponse;
                                System.out.println("Mode 01 Complete Set = " + mCommamndAND);
                                lw ("Mode 01 Complete Set = " + mCommamndAND);
                                //sendBroadcast(intent);
                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                System.out.println("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
                rollDashService=false;
            } catch (Exception e ){
                releaseSocket();
                lw("ERROR ERROR : BluetoothService mode01CompleteSet. Error = "+e);
            }
            */
            }
        });
        vehicleParamThread.start();
    }


    public void vehicleDashboardServiceCAN () {
        lw("SERVICE   : BluetoothService.vehicleDashboardService");

        Bundle bundleExtras = new Bundle();
        int vCounter = 49;
        OBDCommand1Byte obd010DCommand; // Vehicle Speed
        OBDCommand1Byte obd0110Command; // MAF Mass Air Flow Rate
        OBDCommand1Byte obd010CCommand; // Engine RPM
        OBDCommand1Byte obd010FCommand; // Coolant Temperature
        OBDCommand1Byte obd012FCommand; // Fuel Tank Level

        Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("bundle", bundleExtras);

        bundleExtras.putString("0", "10");
        bundleExtras.putString("1", "0101");
        bundleExtras.putString("2", "0101");
        bundleExtras.putString("4", "01");
        bundleExtras.putString("5", "01");

        sendBroadcast(intent);

        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            try {
                vCounter = 0;

                try { // Engine Coolant Temperature - 1 Byte
                    obd010FCommand = new OBDCommand1Byte("01 05");
                    obd010FCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    bundleExtras.putString("4", obd010FCommand.getResponse().substring(4, 6));
                } catch (Exception e) {
                    lw("ERROR ERROR : Error BluetoothService.vehicleDashboardService.obd010FCommand = " + e);
                }

                try {  // Fuel Tank Level
                    obd012FCommand = new OBDCommand1Byte("01 2F");
                    obd012FCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    bundleExtras.putString("5", obd012FCommand.getResponse().substring(4, 6));
                } catch (Exception e) {
                    System.out.println("Error Getting Data...");
                    lw("ERROR ERROR : Fuel Tank Input Level error " + e);
                }


                while (rollDashService) {
                    obd010DCommand = new OBDCommand1Byte("01 0D 10 0C");
                    obd010DCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    String Response = obd010DCommand.getResponse();
                    Response = ConvertValues.removeFrameHeader(  Response );
                    lw ( "Response = "+ Response );
                    //System.out.println( "removeFrameHeader = "+ Response );
                    //Response = ConvertValues.removeErrorString(  Response );
                    //System.out.println( "removeFrameHeader = "+ Response );
                    String SpeedHex = Response.substring(3,9);
                   // System.out.println( "SpeedHex = "+ SpeedHex );

                    String MAFHex = Response.substring(9,15);
                   // System.out.println( "MAFHex = "+ MAFHex );

                    String RPMHex = Response.substring(15,21);
                    //System.out.println( "RPMHex = "+ RPMHex );

                    bundleExtras.putString("0", SpeedHex.substring(4,6) );
                    bundleExtras.putString("1", MAFHex.substring (2,6) );
                    bundleExtras.putString("2", RPMHex.substring (2,6) );

                    sendBroadcast(intent);
                }
            } catch (Exception e) {
                releaseSocket();
            }
        }

    }

    public void predashSettings() {
        try {
            lw("predashSettings.setHeaderOn[AT H1]");
            //OBDInitialize setHeaderOff = new OBDInitialize("AT CRA 7E8");
            OBDInitialize setHeaderOff = new OBDInitialize("AT H1");
            setHeaderOff.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String vResponse = removeCarriage(setHeaderOff.getResponse());
            lw("setHeaderOn.getResponse() = " + vResponse);
            if (setHeaderOff.getResponse().contains("OK")) {
                lw("setHeaderOn.getResponse() = is GOOD " + vResponse);
            }
        } catch (Exception e) {
            lw("ERROR ERROR: initializeGeneralCommand.setHeaderOn" + e);
        }
    }

    public void postdashSettings() {
        try {
            lw("setHeaderOff[AT H0]");
            OBDInitialize setHeaderOff = new OBDInitialize("AT CRA");
            setHeaderOff.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String vResponse = removeCarriage(setHeaderOff.getResponse());
            lw("setHeaderOff.getResponse() = " + vResponse);
            if (setHeaderOff.getResponse().contains("OK")) {
                lw("setHeaderOff.getResponse() = is GOOD " + vResponse);
            }
        } catch (Exception e) {
            lw("ERROR ERROR: initializeGeneralCommand.setHeaderOff" + e);
        }
    }

    public void vehicleDashboardService () {
        lw (  "SERVICE   : BluetoothService.vehicleDashboardService");

        Bundle bundleExtras = new Bundle();
        int vCounter = 49;
        OBDCommand1Byte obd010DCommand; // Vehicle Speed
        OBDCommand1Byte obd0110Command; // MAF Mass Air Flow Rate
        OBDCommand1Byte obd010CCommand; // Engine RPM
        OBDCommand1Byte obd010FCommand; // Coolant Temperature
        OBDCommand1Byte obd012FCommand; // Fuel Tank Level

        Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra( "bundle", bundleExtras );

        bundleExtras.putString("0", "01");
        bundleExtras.putString("1", "0101");
        bundleExtras.putString("2", "0101");
        bundleExtras.putString("4", "01");
        bundleExtras.putString("5", "01");

        sendBroadcast(intent);

        if ( IsSocketBuzy() ) {
            socketIsBuzy();
        }else {
            runSocket();
            try {
                while (rollDashService) {
                    vCounter++;
                    {
                        try {  // Vehicle speed   -  1 Bytes
                            obd010DCommand = new OBDCommand1Byte("01 0D");
                            obd010DCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                            if ( obd010DCommand.getResponse().contains("410D")   ) {
                                bundleExtras.putString("0", obd010DCommand.getResponse().substring(4, 6));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            lw("ERROR ERROR : Error BluetoothService.vehicleDashboardService.obd010DCommand1 = " + e);
                        }
                    }

                    {
                        try { //  MAF air flow rate  -  2 Bytes
                            obd0110Command = new OBDCommand1Byte("01 10");
                            obd0110Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                            if ( obd0110Command.getResponse().contains("4110")   ) {
                                bundleExtras.putString("1", obd0110Command.getResponse().substring(4, 8));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            lw("ERROR ERROR : Error BluetoothService.vehicleDashboardService.obd0110Command = " + e);
                        }
                    }

                    {
                        try {  // Vehicle speed   -  1 Bytes
                            obd010DCommand = new OBDCommand1Byte("01 0D");
                            obd010DCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                            if ( obd010DCommand.getResponse().contains("410D")   ) {
                                bundleExtras.putString("0", obd010DCommand.getResponse().substring(4, 6));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            lw("ERROR ERROR : Error BluetoothService.vehicleDashboardService.obd010DCommand2 = " + e);
                        }
                    }

                    {
                        try { // Engine RPM - 2 Bytes
                            obd010CCommand = new OBDCommand1Byte("01 0C");
                            obd010CCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                            if ( obd010CCommand.getResponse().contains("410C")   ) {
                                bundleExtras.putString("2", obd010CCommand.getResponse().substring(4, 8));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            lw("ERROR ERROR : Error BluetoothService.vehicleDashboardService.obd010CCommand = " + e);
                        }
                    }

                    if (vCounter == 50) {
                        vCounter = 0;

                        try { // Engine Coolant Temperature - 1 Byte
                            obd010FCommand = new OBDCommand1Byte("01 05");
                            obd010FCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                            if ( obd010FCommand.getResponse().contains("4105")   ) {
                                bundleExtras.putString("4", obd010FCommand.getResponse().substring(4, 6));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            lw("ERROR ERROR : Error BluetoothService.vehicleDashboardService.obd010FCommand = " + e);
                        }

                        try {  // Fuel Tank Level
                            obd012FCommand = new OBDCommand1Byte("01 2F");
                            obd012FCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                            if ( obd012FCommand.getResponse().contains("412F")   ) {
                                bundleExtras.putString("5", obd012FCommand.getResponse().substring(4, 6));
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            System.out.println("Error Getting Data...");
                            lw("ERROR ERROR : Fuel Tank Input Level error " + e);
                        }
                    }
                }
                releaseSocket();
            } catch (Exception e ){
                releaseSocket();
            }
        }
    }

    public static String[] modeHexArray1 =
            {
                    "01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F","10",
                    "11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
                    "21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F","30",
                    "31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
                    "41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F","50",
                    "51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
                    "61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F","70",
                    "71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
                    "81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F","90",
                    "91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
                    "A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF","B0",
                    "B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
                    "C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF","D0",
                    "D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
            };

    public static String[] mode06HexArray1 =
            {
                    "01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F","10",
                    "11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
                    "21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F","30",
                    "31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
                    "41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F","50",
                    "51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
                    "61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F","70",
                    "71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
                    "81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F","90",
                    "91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
                    "A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF","B0",
                    "B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
                    "C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF","D0",
                    "D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
                    "E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF","E0",
                    "F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF",
            };

    public static String[] mode05HexArray1 =
            {
                    "01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F","10",
                    "11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
                    "21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F","30",
                    "31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
                    "41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F","50",
                    "51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
                    "61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F","70",
                    "71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
                    "81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F","90",
                    "91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
                    "A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF","B0",
                    "B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
                    "C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF","D0",
                    "D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
                    "E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF","E0",
                    "F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF",
            };

    public static String[] mode08Enable =
            {       "08 01 00 00 00 00 00","08 02 00 00 00 00 00","08 03 00 00 00 00 00",
                    "08 04 00 00 00 00 00","08 05 00 00 00 00 00","08 06 00 00 00 00 00",
                    "08 07 00 00 00 00 00","08 08 00 00 00 00 00","08 09 00 00 00 00 00",
                    "08 0A 00 00 00 00 00","08 21 00 00 00 00 00","08 22 00 00 00 00 00",
                    "08 23 00 00 00 00 00","08 24 00 00 00 00 00","08 25 00 00 00 00 00",
                    "08 26 00 00 00 00 00","08 27 00 00 00 00 00","08 28 00 00 00 00 00"
            };


    public void enableTestResultMode081() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.enableTestResultMode08");
        lw (  "-------------------------------------------------------------------------------");

        lw("Load Supported TIDS for MODE 08");
        loadMode08binaries();

        try {
            if (IsSocketBuzy()) {
                socketIsBuzy();
            } else {
                runSocket();
                for (String s : mode08Enable) {
                    //  Do your stuff here
                    if (rollDashService) {
                        OBDCommand4Byte obdCommand = new OBDCommand4Byte(s);
                        obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        lw(" Response - " + s+ " = " + ConvertValues.removeFrameHeader( obdCommand.getResponse() ) );
                        System.out.println(" Response - " + s+ " = " + ConvertValues.removeFrameHeader( obdCommand.getResponse() ) );
                    }
                }
            }
            releaseSocket();
        } catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing runAllTestResultMode06");
        }
    }
/*
    public void reconnect () {
        lw("PROGRAM   : Bluetooth.reconnect ");
        try {
            mmSocket.connect();
            lw("Successfully recoonected...");
        } catch ( Exception e ) {
            lw("ERROR ERROR : Failed to reconnect...");
        }
    }
    */

    public boolean validateMode0100(  Integer waitTime ){
        lw ( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
        lw("PROGRAM   : Bluetooth.validateMode0100 ");
        boolean vstatus = false;
        try {
            lw ( "VALIDATE01: obdmode0100[01 00]" );
            lw ( "VALIDATE01: WAIT-TIME = "+ waitTime  );
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            } else {
                runSocket();
                OBDInitializeDelayForProt obdmode0100 = new OBDInitializeDelayForProt("01 00", 1, waitTime);
                obdmode0100.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                String vResponse = removeCarriage(obdmode0100.getResponse());
                lw("VALIDATE01: Response = " + vResponse);
                if (obdmode0100.getResponse().contains("4100")) {
                    vstatus = true;
                } else {
                    vstatus = false;
                }
                releaseSocket();
            }
            lw ( "VALIDATE01: WAIT-TIME = "+ vstatus );
        } catch ( Exception e ) {
            releaseSocket();
            vstatus = false;
            lw (  "ERROR ERROR: initializeConnection.validateMode0100"+e);
        }
        lw ( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
        return vstatus;
    }

    public boolean okToRunMode06IS01850() {
        lw("PROGRAM   : Bluetooth.okToRunMode06IS01850");
        boolean okToRun = false;
        String enableTestCode = "08 01 00 00 00 00 00";
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            } else {
                runSocket();
                OBDCommand4Byte obdenableCommand = new OBDCommand4Byte(enableTestCode);
                obdenableCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                lw(" Response enableTest - " + enableTestCode + " = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                System.out.println(" Response - " + enableTestCode + " = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                if (obdenableCommand.getResponse().contains("4801")) {
                    lw("Condition OK to run Test");
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", -1);
                    sendBroadcast(intent);
                    okToRun = true;
                } else {
                    lw("Condition NOT good to run test");
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", -2);
                    sendBroadcast(intent);
                }
                releaseSocket();
            }
        } catch ( Exception e ) {
            releaseSocket();
            lw("ERROR ERROR : Before Executing okToRunMode06IS01850"+ e);
        }
        return okToRun;
    }

    public void runISO1850IndividualMonitorTest(String[] HexToRun, int vfirstIndex, int vsecondIndex) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runISO1850IndividualMonitorTest");
        lw (  "-------------------------------------------------------------------------------");
        int firstIndex  =  vfirstIndex;
        int secondIndex =  vsecondIndex;
        String mMode = "06";
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        //   Oxygen Sensor Monitor
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                for (String s : HexToRun) {
                    //  Do your stuff here
                    if (rollDashService) {
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                String vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                //int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeA3O6Frames(0, vResponse);
                                addISO1850ToMode06(vResponseArray, "0", false);
                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error runISO1850IndividualMonitorTest re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing Bluetooth.runISO1850IndividualMonitorTest = "+e);
        }
    }

    public void runISO1850AllTestResultMode05() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runISO1850AllTestResultMode05");
        lw (  "-------------------------------------------------------------------------------");
        int firstIndex  =  0;
        int secondIndex =  1;
        int progressISO1850Percent = 0;
        String mMode = "05";
        //String mMode = "05 01";
        System.out.println ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        // Oxygen Sensor Monitor
        int percentage = 0;
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();

                OBDCommand4Byte obdenableCommand = new OBDCommand4Byte("10");
                obdenableCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                lw(" Response enableTest - 10 = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                System.out.println(" Response - 10 = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));

                for (String s : mode06HexArray1) {
                    //  Do your stuff here
                    if (rollDashService) {
                        int progress = ConvertValues.HexToInteger(s);
                        double perValue = progress / 2.55;
                        percentage = (int) perValue;
                        if (  progressISO1850Percent!= percentage  ) {
                            progressISO1850Percent = percentage;
                            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", percentage);
                            sendBroadcast(intent);
                        }
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                String vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                lw(" vResponse = " + vResponse);
                                //int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeA3O6Frames(0, vResponse);
                                lw(" vResponseArray = " + vResponseArray.size() );
                                System.out.println ( "runISO1850AllTestResultMode06 Commmmand = "+ mCommamndAND );
                                addISO1850ToMode06(vResponseArray, progressISO1850Percent+"", false);

                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error runISO1850AllTestResultMode06 re " + mCommamndAND + " Data..." + e);
                                System.out.println("ERROR ERROR : Error runISO1850AllTestResultMode06 re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing runISO1850AllTestResultMode06");
        }
    }

    public void runISO1850AllTestResultMode06() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runISO1850AllTestResultMode06");
        lw (  "-------------------------------------------------------------------------------");
        int firstIndex  =  0;
        int secondIndex =  1;
        int progressISO1850Percent = 0;
        String mMode = "06";
        //String mMode = "05 01";
        System.out.println ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        //   Oxygen Sensor Monitor
        int percentage = 0;
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();

                OBDCommand4Byte obdenableCommand = new OBDCommand4Byte("10");
                obdenableCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                lw(" Response enableTest - 10 = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                System.out.println(" Response - 10 = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));

                for (String s : mode06HexArray1) {
                    //  Do your stuff here
                    if (rollDashService) {
                        int progress = ConvertValues.HexToInteger(s);
                        double perValue = progress / 2.55;
                        percentage = (int) perValue;
                        if (  progressISO1850Percent!= percentage  ) {
                            progressISO1850Percent = percentage;
                            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", percentage);
                            sendBroadcast(intent);
                        }
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                String vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                lw(" vResponse = " + vResponse);
                                //int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeA3O6Frames(0, vResponse);
                                lw(" vResponseArray = " + vResponseArray.size() );
                                System.out.println ( "runISO1850AllTestResultMode06 Commmmand = "+ mCommamndAND );
                                addISO1850ToMode06(vResponseArray, progressISO1850Percent+"", false);

                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error runISO1850AllTestResultMode06 re " + mCommamndAND + " Data..." + e);
                                System.out.println("ERROR ERROR : Error runISO1850AllTestResultMode06 re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing runISO1850AllTestResultMode06");
        }
    }

    public ArrayList<String> decodeA3O6Frames ( int pNumOfBytes, String pBytes ) {
        lw("PROGRAM   : decodeA3O6Frames.decodeA3O6Frames Execution pNumOfBytes = " + pNumOfBytes + ", pBytes = " + pBytes);
        ArrayList<String> completeStringArray = new ArrayList();
        String completeString = pBytes;

        int beg  = 0;
        int end  = 14;
        int cnter = 0;
        boolean rollon = true;
        while (rollon) {
            try {
                cnter++;
                String brokenString = completeString.substring(beg, end); // ( pNumOfBytes- 2 ) * 2
                completeStringArray.add(brokenString);
                lw("LOOP      : [" + cnter + "] " + brokenString);
                beg = beg + 14;
                end = end + 14;
                lw("INFO      : beg = " + beg + ", end = " + end);
            } catch ( Exception e ) {
                lw("END END : END decodeA3O6Frames => "+e );
                rollon = false;
            }
        }
        lw("INFO      : Completed decodeA3O6Frames [Success]" );
        return completeStringArray;
    }

    public boolean okToRunMode06CAN() {
        lw("PROGRAM   : Bluetooth.okToRunMode06CAN");
        boolean okToRun = false;
        String enableTestCode = "08 01";
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                OBDCommand4Byte obdenableCommand = new OBDCommand4Byte(enableTestCode);
                obdenableCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                lw(" Response enableTest - " + enableTestCode + " = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                System.out.println(" Response - " + enableTestCode + " = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));
                if (obdenableCommand.getResponse().contains("01")) {
                    lw("Condition OK to run Test");
                    //toast("Condition OK to run test");
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", -1);
                    sendBroadcast(intent);
                    okToRun = true;
                } else if (obdenableCommand.getResponse().contains("22")) {
                    lw("Condition NOT good to run test");
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", -2);
                    sendBroadcast(intent);
                } else {
                    lw("Condition NOT good to run test");
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", -2);
                    sendBroadcast(intent);
                }
                releaseSocket();
            }
        } catch ( Exception e ) {
            releaseSocket();
            lw("ERROR ERROR : Before Executing okToRunMode06CAN"+ e);
        }
        return okToRun;
    }
/*
    public void runAllTestResultMode06() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runAllTestResultMode06");
        lw (  "-------------------------------------------------------------------------------");
        int firstIndex  =  0;
        int secondIndex =  1;
        //enableTestResultMode08();
        String mMode = "06";
        System.out.println ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        //   Oxygen Sensor Monitor
        int percentage = 0;
        int newProgressPercent = 0;
        //String progressPercent = "0%";
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                int lengtht = mode06completeBinaries.length();
                for (String s : mode06HexArray1) {
                    //  Do your stuff here
                    if (rollDashService) {
                        int progress = ConvertValues.HexToInteger(s);
                        double perValue = progress / 2.55;
                        percentage = (int) perValue;
                        //newProgressPercent = percentage ;
                        if (  newProgressPercent!=percentage  ) {
                            newProgressPercent = percentage;
                            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", percentage);
                            sendBroadcast(intent);
                        }
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                String vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeO6Frames(numBytes, vResponse);
                                addToMode06(vResponseArray, percentage+"", false);

                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                                System.out.println("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing runAllTestResultMode06");
        }
    }

    */
    public static ArrayList<ArrayList<String>> resultAllMode06FastArray = new ArrayList();

    public void  runAllTestResultFastMode06(boolean fastMode) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runAllTestResultMode06");
        lw (  "-------------------------------------------------------------------------------");

        resultAllMode06FastArray = new ArrayList();
        int firstIndex  =  0;
        int secondIndex =  1;
        //enableTestResultMode08();

        String mMode = "06";
        System.out.println ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        //   Oxygen Sensor Monitor
        int percentage = 0;
        int newProgressPercent = 0;
        //String progressPercent = "0%";
        System.out.println( "Begining 1" );
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                int lengtht = mode06completeBinaries.length();
                for (String s : mode06HexArray1) {
                    //  Do your stuff here
                    if (rollDashService) {
                        int progress = ConvertValues.HexToInteger(s);
                        System.out.println( "Begining 2" );
                        double perValue = progress / 2.55;
                        percentage = (int) perValue;
                        //newProgressPercent = percentage ;
                        if (  newProgressPercent!=percentage  ) {
                            newProgressPercent = percentage;
                            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", percentage);
                            sendBroadcast(intent);
                        }
                        System.out.println ( "After Send Broadcast");
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        System.out.println (" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                String vResponse = "";
                                System.out.println( "Begining 3" );
                                if (fastMode) {
                                    // fast Mode
                                    System.out.println( "Begining 4 fastMode" );
                                    OBDMode06Fast obdCommand = new OBDMode06Fast(mCommand);
                                    obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                    vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                } else {
                                    // Slow Mode
                                    System.out.println( "Begining 4 SlowMode" );
                                    OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                    obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());

                                    System.out.println( "Begining 5 SlowMode" );

                                    vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());

                                    System.out.println( "Begining 5 SlowMode = "+vResponse );
                                }
                                int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeO6Frames(numBytes, vResponse);
                                addToMode06(vResponseArray, percentage+"", fastMode);
                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error getMode01Response re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing runAllTestResultMode06");
        }
    }

    public static ArrayList<ArrayList<String>> resultFASTISO1850Mode06Array = new ArrayList();

    public void runISO1850AllTestResultFastMode06( boolean fastMode) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runISO1850AllTestResultMode06");
        lw (  "-------------------------------------------------------------------------------");
        resultFASTISO1850Mode06Array = new ArrayList();
        int firstIndex  =  0;
        int secondIndex =  1;
        int progressISO1850Percent = 0;
        String mMode = "06";
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        //   Oxygen Sensor Monitor
        int percentage = 0;
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();

                OBDCommand4Byte obdenableCommand = new OBDCommand4Byte("10");
                obdenableCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                lw(" Response enableTest - 10 = " + ConvertValues.removeFrameHeader(obdenableCommand.getResponse()));

                for (String s : mode06HexArray1) {
                    //  Do your stuff here
                    if (rollDashService) {
                        int progress = ConvertValues.HexToInteger(s);
                        double perValue = progress / 2.55;
                        percentage = (int) perValue;
                        if (  progressISO1850Percent!= percentage  ) {
                            progressISO1850Percent = percentage;
                            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putExtra("progress", percentage);
                            sendBroadcast(intent);
                        }
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                String vResponse;
                                if (fastMode) {
                                    OBDMode06Fast obdCommand = new OBDMode06Fast(mCommand);
                                    obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                    vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                } else {
                                    OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                    obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                    vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                }
                                lw(" vResponse = " + vResponse);
                                //int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeA3O6Frames(0, vResponse);
                                lw(" vResponseArray = " + vResponseArray.size() );
                                addISO1850ToMode06( vResponseArray, progressISO1850Percent+"", fastMode );

                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error runISO1850AllTestResultMode06 re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing runISO1850AllTestResultMode06");
        }
    }

    public static String[] mode06OxySensorHexArray =  { "01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F","10"   };
    public static String[] CatalystHexArray =   { "21","22","23","24" };
    public static String[] EGRMonHexArray =     { "31","32","33","34" };
    public static String[] VVTMonHexArray =     { "35","36","37","38" };
    public static String[] EvapMonitorHexArray ={ "39","3A","3B","3C" };
    public static String[] PurgeFlowHexArray ={ "3D" };
    public static String[] SensorHeaterMonHexArray = { "41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F","50" };
    public static String[] HeatedCatalystHexArray = { "61","62","63","64" };
    public static String[] SecondaryAirHexArray =   { "71","72","73","74" };
    public static String[] fuelSystemHexArray = { "81","82","83","84" };
    public static String[] boostPressureHexArray =  { "85","86" };
    public static String[] NoxAbsorberMonHexArray =  { "90","91" };
    public static String[] NoxCatalystMonHexArray =  { "98","99" };
    public static String[] misFireMonHexArray = { "A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD"};
    public static String[] pMFilterHexArray =   { "B0","B1" };


    public void testResultServiceISO1850() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.testResultServiceISO1850");
        lw (  "-------------------------------------------------------------------------------");

        loadMode05binaries();
        runISO1850AllTestResultMode05();

        loadMode06binaries();
        runISO1850AllTestResultMode06();

    }

    public void testResultServiceFastISO1850(  boolean fastMode) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.testResultServiceISO1850");
        lw (  "-------------------------------------------------------------------------------");

        loadMode06binaries();
        runISO1850AllTestResultFastMode06( fastMode);
    }

    public void testResultFastService(boolean fastMode) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.testResultFastService");
        lw (  "-------------------------------------------------------------------------------");
        lw( "About to load testResultService.Mode06Binaries");
        loadMode06binaries();
        runAllTestResultFastMode06(fastMode);
    }

    public void testRunEachResultService(String MonitorMode, String protocolGroup) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.testRunEachResultService, MonitorMode = "+MonitorMode+", protocolGroup ="+protocolGroup);
        lw (  "-------------------------------------------------------------------------------");

        loadMode06binaries();

        if (MonitorMode.contains("Misfire")) {
            System.out.println ( "Test EACH. This is "+MonitorMode  );
            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest(misFireMonHexArray, 155, 156);
            } else {
                runIndividualMonitorTest(misFireMonHexArray, 155, 156);
            }
        } else if ( MonitorMode.contains("Fuel System")) {
            System.out.println ( "Test EACH. This is "+MonitorMode  );
            lw( "This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( fuelSystemHexArray, 124,125 );
            } else {
                runIndividualMonitorTest( fuelSystemHexArray, 124,125 );
            }
        } else if ( MonitorMode.contains("Components")) {
            System.out.println ( "Test EACH. This is "+MonitorMode  );
            lw( "This is "+MonitorMode  );
            // runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            /*
            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( misFireMonHexArray, 155,156 );
            } else {
                runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            }
            */
        } else if ( MonitorMode.contains("Catalyst")) {
            lw( "This is "+MonitorMode  );
            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( CatalystHexArray, 31,32 );
            } else {
                runIndividualMonitorTest( CatalystHexArray, 31,32 );
            }
        } else if ( MonitorMode.contains("Heated Catalyst")) {
            lw( "This is "+MonitorMode  );
            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( HeatedCatalystHexArray, 93,94 );
            } else {
                runIndividualMonitorTest( HeatedCatalystHexArray, 93,94 );
            }
        } else if ( MonitorMode.contains("Evaporative System")) {
            lw( "This is "+MonitorMode  );
            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( EvapMonitorHexArray, 55,56 );
            } else {
                runIndividualMonitorTest( EvapMonitorHexArray, 55,56 );
            }
        } else if ( MonitorMode.contains("Secondary Air System")) {
            lw( "This is "+MonitorMode  );
            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( SecondaryAirHexArray, 109,110 );
            } else {
                runIndividualMonitorTest( SecondaryAirHexArray, 109,110 );
            }

        } else if ( MonitorMode.contains("A/C Refrigerant")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );
            // runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            /*
            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( misFireMonHexArray, 155,156 );
            } else {
                runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            }
            */

        } else if ( MonitorMode.contains("Oxygen Sensor")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( mode06OxySensorHexArray, 0,1 );
            } else {
                runIndividualMonitorTest( mode06OxySensorHexArray, 0,1 );
            }
        } else if ( MonitorMode.contains("Oxygen Sensor Heater")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( SensorHeaterMonHexArray, 62,63 );
            } else {
                runIndividualMonitorTest( SensorHeaterMonHexArray, 62,63 );
            }

        } else if ( MonitorMode.contains("EGR System")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( EGRMonHexArray, 47,48 );
            } else {
                runIndividualMonitorTest( EGRMonHexArray, 47,48 );
            }
        } else if ( MonitorMode.contains("NMHC Catalyst")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );
            //runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( misFireMonHexArray, 155,156 );
            } else {
                runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            }
        } else if ( MonitorMode.equals("N0x/SCR Monitor")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( NoxAbsorberMonHexArray, 139,140 );
            } else {
                runIndividualMonitorTest( NoxAbsorberMonHexArray, 139,140 );
            }
        } else if ( MonitorMode.contains("Boost Pressure")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( boostPressureHexArray, 128,129 );
            } else {
                runIndividualMonitorTest( boostPressureHexArray, 128,129 );
            }
        } else if ( MonitorMode.contains("Exhaust Gas Sensor")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );
            // runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            /*
            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( misFireMonHexArray, 155,156 );
            } else {
                runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            }
            */
        } else if ( MonitorMode.contains("PM Filter Monitoring")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );

            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( pMFilterHexArray, 155,156 );
            } else {
                runIndividualMonitorTest( pMFilterHexArray, 155,156 );
            }

        } else if ( MonitorMode.contains("EGR and/or VVT System")) {
            lw( "This is "+MonitorMode  );

            System.out.println ( "Test EACH. This is "+MonitorMode  );
           /*
            if (protocolGroup.contains("ISO") || protocolGroup.contains("1850")) {
                runISO1850IndividualMonitorTest( misFireMonHexArray, 155,156 );
            } else {
                runIndividualMonitorTest( misFireMonHexArray, 155,156 );
            }
            */
        }
    }

    public void runIndividualMonitorTest(String[] HexToRun, int vfirstIndex, int vsecondIndex) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.runIndividualMonitorTest");
        lw (  "-------------------------------------------------------------------------------");
        int firstIndex  =  vfirstIndex;
        int secondIndex =  vsecondIndex;
        String mMode = "06";
        lw ( "mBitValue = "+mBit06Value +", modeHexArray = "+modeHexArray +" ["+firstIndex+","+secondIndex+"]" );
        //   Oxygen Sensor Monitor
        try {
            if ( IsSocketBuzy() ) {
                socketIsBuzy();
            }else {
                runSocket();
                for (String s : HexToRun) {
                    //  Do your stuff here
                    if (rollDashService) {
                        mBit06Value = mode06completeBinaries.substring(firstIndex, secondIndex);
                        String mCommand = mMode + " " + s;
                        String mCommamndAND = "[" + firstIndex + "," + secondIndex + "] - " + mCommand + " - [" + mBit06Value + "]";
                        lw(" Command = " + mCommamndAND);
                        if (mBit06Value.equals("1")) {
                            try {
                                OBDMode06Delay obdCommand = new OBDMode06Delay(mCommand);
                                obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
                                String vResponse = ConvertValues.removeFrameHeader(obdCommand.getResponse());
                                int numBytes = ConvertValues.HexToInteger(vResponse.substring(0, 3));
                                ArrayList<String> vResponseArray = decodeO6Frames(numBytes, vResponse);
                                addToMode06( vResponseArray, "0", false );
                            } catch (Exception e) {
                                lw ("ERROR ERROR : Error runIndividualMonitorTest re " + mCommamndAND + " Data..." + e);
                                //System.out.println("ERROR ERROR : Error runIndividualMonitorTest re " + mCommamndAND + " Data..." + e);
                            }
                        }
                        firstIndex++;
                        secondIndex++;
                    }
                }
                releaseSocket();
            }
        }catch (Exception e ){
            releaseSocket();
            lw("ERROR ERROR : Before Executing testResultService.runIndividualMonitorTest = "+e);
        }
    }

    public boolean addISO1850ToMode06 ( ArrayList<String>  pResponseArray, String progress, boolean fastMode ) {
        lw("PROGRAM   : getMode06Test.addISO1850ToMode06 Execution") ;
        try {
            Mode6GetMIDTID interp = new Mode6GetMIDTID(context);

            for (int i = 0; i < pResponseArray.size(); i++) {
                String responseString = pResponseArray.get(i);
                lw("MSG       : responseString = " +responseString ) ;  // 0205100000000002
                ArrayList<String> row = new ArrayList();
                ArrayList<String> labelArray = new ArrayList<String>();
                String testLimitCompIdBin = ConvertValues.hexToBin(responseString.substring(4, 6));
                lw( "-----------------------------------------------------------------------------------------------" );
                lw( "| TID: $" + responseString.substring(2, 4)+" - TCID: $" + responseString.substring(4, 6)+" ["+testLimitCompIdBin +"] |");
                lw( "-----------------------------------------------------------------------------------------------" );

                row.add(0, responseString.substring( 0, 2) );   // titled
                row.add(1, testLimitCompIdBin );   // TestLimitComb
                row.add(2,  responseString.substring( 2, 4)  );
                row.add(3, "TID:$" + responseString.substring(2, 4));    // MID
                row.add(4, "TCID:$" + responseString.substring(4, 6)  );    // TID

                lw( "After adding TID & TCID to array responseString = "+responseString);

                Integer resultValue = ConvertValues.HexToInteger(responseString.substring( 6, 10));
                lw( "This is resultValue = "+resultValue);
                Integer testLimit = ConvertValues.HexToInteger(responseString.substring( 10, 14));
                lw( "This is testLimit = "+testLimit);

                String testLimitType = testLimitCompIdBin.substring( 0,1 );

                //lw( "Result = "+resultValue+" - resultFlag = "+resultFlagDesc  );
                String resultFlagDesc =  "1";

                if ( testLimitType.equals("0") ) {
                    // Test Limit is maximum value
                    if ( resultValue > testLimit ) {
                        // Test Fail
                        resultFlagDesc = "0";
                    }

                } else if (testLimitType.equals("1") )  {
                    // Test Limit is minimum value
                    if ( resultValue < testLimit ) {
                        // Test Fail
                        resultFlagDesc = "0";
                    }
                }
                //if (resultDefinition.equals("3"))  resultFlagDesc = "Fail";
                lw( "TestRESULT Pass/Fail = "+ resultFlagDesc );

                row.add(5, resultFlagDesc + "" );
                row.add(6, testLimitType + "" );           // MIN
                row.add(7, testLimit + ""  );              // MAX
                row.add(8, resultFlagDesc + "" );          // TLHI
                row.add(9, resultValue+""  );              // RESULT
                row.add( 10, responseString);
                ArrayList<String> tmp = interp.getMIDDescription ( responseString.substring(0, 2)  );
                row.add( 11, tmp.get(0) );
                row.add( 12, tmp.get(1) );
                row.add( 13,  interp.getTIDDescription (  responseString.substring(2, 4) ) ) ;


                lw( "Added all emements to Array");

                //lw( "HEX VALUES: min = "+resultDefinition.get(0) +", max = "+resultDefinition.get(1) +", resultValue = "+resultDefinition.get(2)+", Pass/Fail = "+ resultDefinition.get(3));
                lw( "" );
                lw( "" );

                if (fastMode) {
                    resultFASTISO1850Mode06Array.add (row);
                }else {
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("testResultRow", row);
                    sendBroadcast(intent);
                }
            }
            return true;
        } catch ( Exception e ) {
            lw ( "ERROR ERROR : addISO1850ToMode06 Error = "+e);
            return false;
        }
    }


    public void loadFakeResult() {

        OBDMODE06UnitScaling uscaling = new OBDMODE06UnitScaling(context);
        // = "0196830067005A007A";

        ArrayList<String>  resStringArray = new ArrayList<String>();

        resStringArray.add( "019111000B00000032" );
        resStringArray.add( "019211002B0000006E" );
        resStringArray.add( "019330119400001CD4" );
        resStringArray.add( "0194301B3804AB28C2" );
        resStringArray.add( "019530328E24174145" );
        resStringArray.add( "0196830067005A007A" );
        resStringArray.add( "019781000800010064" );
        resStringArray.add( "0198B0FD22F3330000" );
        resStringArray.add( "0199B0000000001999" );
        resStringArray.add( "2190B07FFD33327FFD" );
        resStringArray.add( "3490AF01AD8000154F" );
        resStringArray.add( "3491AF00F7D8F07FFF" );
        resStringArray.add( "349296030480000663" );
        resStringArray.add( "349396051E02B67FFF" );
        resStringArray.add( "81913C14600B7C19F0" );
        resStringArray.add( "81923C147C0B7C19F0" );
        resStringArray.add( "81933C13DC0B7C19F0" );
        resStringArray.add( "81943C148C0B7C19F0" );
        resStringArray.add( "82953C14A80B7C19F0" );
        resStringArray.add( "82963C14A00B7C19F0" );
        resStringArray.add( "82973C156C0B7C19F0" );
        resStringArray.add( "82983C14F40B7C19F0" );
        resStringArray.add( "85901700DA00CAFFFF" );
        resStringArray.add( "8592FC019AFA247FFF" );
        resStringArray.add( "8593AFFFAD8000096F" );
        resStringArray.add( "85941700D900C8FFFF" );
        resStringArray.add( "859696000000000000" );
        resStringArray.add( "9890B060B12CCC7FFF" );
        resStringArray.add( "A20B2400000000FFFF" );
        resStringArray.add( "A20C2400000000FFFF" );
        resStringArray.add( "A30B2400000000FFFF" );
        resStringArray.add( "A30C2400000000FFFF" );
        resStringArray.add( "A40B2400000000FFFF" );
        resStringArray.add( "A40C2400000000FFFF" );
        resStringArray.add( "A50B2400000000FFFF" );
        resStringArray.add( "A50C2400000000FFFF" );
        resStringArray.add( "A60C2400000000FFFF" );
        resStringArray.add( "A60B2400000000FFFF" );
        resStringArray.add( "A70C2400000000FFFF" );
        resStringArray.add( "A70C2400000000FFFF" );
        resStringArray.add( "A80B2400000000FFFF" );
        resStringArray.add( "A80C2400000000FFFF" );
        resStringArray.add( "A90B2400000000FFFF" );
        resStringArray.add( "A90C2400000000FFFF" );
        resStringArray.add( "B2902035740A006400" );

        Mode6GetMIDTID interp = new Mode6GetMIDTID(context);

        for (int i = 0; i < resStringArray.size(); i++) {

            String responseString = resStringArray.get(i);
            //l.w("MSG       : responseString = " +responseString ) ;  // 0205100000000002
            ArrayList<String> row = new ArrayList();
            ArrayList<String> labelArray = new ArrayList<String>();
            String testLimitCompIdBin = ConvertValues.hexToBin(responseString.substring(4, 6));
            lw( "-----------------------------------------------------------------------------------------------" );
            lw( "| MID: $" + responseString.substring(0, 2)+" - TID: $" + responseString.substring(2, 4)+" ["+testLimitCompIdBin +"] |");
            lw( "-----------------------------------------------------------------------------------------------" );
            lw("MSG       : responseString = " +responseString ) ;
            row.add(0, responseString.substring( 0, 2) );   // titled
            row.add(1, testLimitCompIdBin );   // TestLimitComb
            row.add(2,  responseString.substring( 2, 4)  );
            row.add(3, "MID:$" + responseString.substring(0, 2));    // MID
            row.add(4, "TID:$" + responseString.substring(2, 4));    // TID
            Integer resultValue = ConvertValues.HexToInteger(responseString.substring( 6, 10));
            Integer testLimit = ConvertValues.HexToInteger(responseString.substring( 10, 14));

            ArrayList<String>  resultDefinition = new ArrayList();
            resultDefinition  =  uscaling.getDefinition( responseString );

            row.add(5, resultValue+"");
            row.add(6, resultDefinition.get(0));              // MIN
            row.add(7, resultDefinition.get(1));              // MAX
            row.add(8, resultDefinition.get(3) );             // TLHI
            row.add(9, resultDefinition.get(2));              // RESULT
            row.add( 10, responseString);
            ArrayList<String> tmp = interp.getMIDDescription ( responseString.substring(0, 2)  );
            row.add( 11, tmp.get(0) );
            row.add( 12, tmp.get(1) );
            row.add( 13,  interp.getTIDDescription (  responseString.substring(2, 4) ) ) ;

            //lw( "Result = "+resultValue+" - resultFlag = "+resultFlagDesc  );
            String resultFlagDesc =  "PASS";

            if (!resultDefinition.equals("1"))  resultFlagDesc = "Fail";
            lw( "Pass/Fail = "+ resultFlagDesc );
            lw( "min = "+resultDefinition.get(0) +", max = "+resultDefinition.get(1) );
            lw(  "resultValue = "+resultDefinition.get(2)+", Pass/Fail = "+ resultDefinition.get(3));

            resultAllMode06FastArray.add(row);

            Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("testResultRow", row);
            sendBroadcast(intent);
        }
    }

    public ArrayList<String> decodeO6Frames ( int pNumOfBytes, String pBytes ) {
        lw("PROGRAM   : getMode06Test.decodeO6Frames Execution pNumOfBytes = " + pNumOfBytes + ", pBytes = " + pBytes);
        ArrayList<String> completeStringArray = new ArrayList();
        String completeString = pBytes;

        int beg  = 5;
        int end  = 23;
        int cnter = 0;
        boolean rollon = true;
        while (rollon) {
            try {
                cnter++;
                String brokenString = completeString.substring(beg, end); // ( pNumOfBytes- 2 ) * 2
                completeStringArray.add(brokenString);
                lw("LOOP      : [" + cnter + "] " + brokenString);
                beg = beg + 18;
                end = end + 18;
                lw("INFO      : beg = " + beg + ", end = " + end);
            } catch ( Exception e ) {
                lw("COMPLETED : Done decodeO6Frames => "+e );
                rollon = false;
            }
        }
        lw("INFO      : Completed decodeO6Frames [Success]" );
        return completeStringArray;
    }

    public boolean addToMode06 ( ArrayList<String>  pResponseArray, String progress, boolean fastMode ) {
        lw("PROGRAM   : getMode06Test.addToMode06 Execution") ;
        OBDMODE06UnitScaling uscaling = new OBDMODE06UnitScaling(context);
        try {
            Mode6GetMIDTID interp = new Mode6GetMIDTID(context);

            for (int i = 0; i < pResponseArray.size(); i++) {
                String responseString = pResponseArray.get(i);
                //l.w("MSG       : responseString = " +responseString ) ;  // 0205100000000002
                ArrayList<String> row = new ArrayList();
                ArrayList<String> labelArray = new ArrayList<String>();
                String testLimitCompIdBin = ConvertValues.hexToBin(responseString.substring(4, 6));
                lw( "-----------------------------------------------------------------------------------------------" );
                lw( "| MID: $" + responseString.substring(0, 2)+" - TID: $" + responseString.substring(2, 4)+" ["+testLimitCompIdBin +"] |");
                lw( "-----------------------------------------------------------------------------------------------" );
                lw("MSG       : responseString = " +responseString ) ;
                row.add(0, responseString.substring( 0, 2) );   // titled
                row.add(1, testLimitCompIdBin );   // TestLimitComb
                row.add(2,  responseString.substring( 2, 4)  );
                row.add(3, "MID:$" + responseString.substring(0, 2));    // MID
                row.add(4, "TID:$" + responseString.substring(2, 4));    // TID
                Integer resultValue = ConvertValues.HexToInteger(responseString.substring( 6, 10));
                Integer testLimit = ConvertValues.HexToInteger(responseString.substring( 10, 14));

                ArrayList<String>  resultDefinition = new ArrayList();
                resultDefinition  =  uscaling.getDefinition( responseString );

                row.add(5, resultValue+"");
                row.add(6, resultDefinition.get(0));              // MIN
                row.add(7, resultDefinition.get(1));              // MAX
                row.add(8, resultDefinition.get(3) );             // TLHI
                row.add(9, resultDefinition.get(2));              // RESULTj
                row.add( 10, responseString);
                ArrayList<String> tmp = interp.getMIDDescription ( responseString.substring(0, 2)  );
                row.add( 11, tmp.get(0) );
                row.add( 12, tmp.get(1) );
                row.add( 13,  interp.getTIDDescription (  responseString.substring(2, 4) ) ) ;

                String resultFlagDesc =  "PASS";

                if (!resultDefinition.equals("1"))  resultFlagDesc = "Fail";
                lw( "Pass/Fail = "+ resultFlagDesc );
                lw( "min = "+resultDefinition.get(0) +", max = "+resultDefinition.get(1) );
                lw(  "resultValue = "+resultDefinition.get(2)+", Pass/Fail = "+ resultDefinition.get(3));

                if (fastMode) {
                    resultAllMode06FastArray.add(row);
                } else {
                    Intent intent = new Intent("android.intent.action.MAIN.checkEngine").putStringArrayListExtra("testResultRow", row);
                    sendBroadcast(intent);
                }
            }
            return true;
        } catch ( Exception e ) {
            lw ( "ERROR ERROR : getMode06Test.addToMode06 Error = "+e);
            return false;
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
            lw ("Successfully Closed Connection" );
        } catch (Exception e) {
            lw ( "ERROR 0005 - Failed to connect at first attempt");
        }
    }

    private static Object obj = new Object();
/*
    public void trace(String msg) {
        Log.d("AbstractActivity", msg);
        toast(msg);
    }
    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
*/
    @Override
    public void onDestroy() {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "SERVICE   : BluetoothService.onDestroy");
        lw (  "-------------------------------------------------------------------------------");

        try {
            lw ("BluetoothService About to close connection");
            // lw( context, "MSG : About to close mmSocket...");
            mmSocket.close();

            lw ("BluetoothService Successfully closed connection");
        } catch (Exception e) {
            // lw( context, "Error : About to close mmSocket...");
            lw ( "BluetoothService ERROR 03455 - failed to close connection = "+e);
        }
    }
}