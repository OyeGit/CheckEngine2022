package com.velexsoft.checkengine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.velexsoft.checkengine.modess.InterpreteMode01;
import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.speedviewlib.SpeedView;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.TripClass;
import com.velexsoft.checkengine.utilities.VehicleDiagnosticReport;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DriveCycleTripScreen extends ConnActivityParent  {

    final Context context = this;
    CustomFragAdapter adapter;
    ListView list;
    ListView listTrip;
    ArrayList<ArrayList<String>> mode01_PID_01Array = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> troubleCodeArray = new ArrayList<ArrayList<String>>();
    static ArrayList<ArrayList<String>> vehParamArray = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> freezeFrameArray = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> TopList  = new ArrayList();
    static ArrayList<ArrayList<String>> dashBoardArray  = new ArrayList<>();
    ArrayList<ArrayList<String>> resultArrayList  = new ArrayList();
    static ArrayList<ArrayList<String>>  freezeDropdown = new ArrayList();
    ArrayList<ArrayList<Double>> trip1 = new ArrayList<ArrayList<Double>>();
    ArrayList<ArrayList<Double>> trip2 = new ArrayList<ArrayList<Double>>();
    private Chronometer chronometer;

    public static final String PREFS_NAME = ConnActivityParent.PREFS_NAME;
    BluetoothService mService;
    boolean mBound = false;
    Window window ;
    String ignitionType;
    Integer noOfDTC = 0;
    String lineSep = System.getProperty("line.separator");
    SpeedView awesomeSpeedometer1;
    SpeedView awesomeSpeedometer2;
    SpeedView awesomeSpeedometer3;
    InterpreteMode01 interP = new InterpreteMode01();
    final String DEGREE  = "\u00b0";
    TextView distTrav;
    TextView timeTaken;
    Button btnStartTrip;
    private ProgressBar mLoadingIndicator;
    static String[] frameItems;
    static boolean reportIconShow = true;

    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    double longitudeGPS, latitudeGPS;
    double longitudeNetwork, latitudeNetwork;
    TextView longitudeValueBest, latitudeValueBest;
    TextView longitudeValueGPS, latitudeValueGPS;
    TextView longitudeValueNetwork, latitudeValueNetwork;

    Paint p = new Paint();
    private BroadcastReceiver mReceiver;
    String vSpeedHex;
    String unitMeasure;
    static double lastlongitudeGPS = 0.0;
    static double lastlatitudeGPS = 0.0;
    static ArrayList<ArrayList<Double>> currTripLongLat ;
    DecimalFormat df = new DecimalFormat("#.##");
    Button buttonOther;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.drivecycle_trip_screen;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "DRIVECYCLE: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportIconShow = true;
        l.fileName = ConnActivityParent.logFileName;
        loadTitle(getString(R.string.drivecycle));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Drive Cycle Trip Screen Started");
        lw (  "-------------------------------------------------------------------------------");
        window = getWindow();
        list = (ListView) this.findViewById(R.id.listView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        awesomeSpeedometer3 = (SpeedView) findViewById(R.id.speedView3);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        unitMeasure =  sharedPreferences.getString("unit_measure", "Metric");
        //list = (ListView) this.findViewById(R.id.listView);
        if (unitMeasure.contains("Imperial")) {
            awesomeSpeedometer3.setMinMaxSpeed(0,75);
            awesomeSpeedometer3.setStartEndDegree( 135 , 405);
            awesomeSpeedometer3.setUnit("mi/h");
        } else {
            awesomeSpeedometer3.setMinMaxSpeed(0,100);
            awesomeSpeedometer3.setStartEndDegree( 135 , 405);
            awesomeSpeedometer3.setUnit("km/h");
        }

        awesomeSpeedometer3.setUnitTextColor(Color.DKGRAY);
        awesomeSpeedometer3.setTextColor(Color.DKGRAY);
        awesomeSpeedometer3.setClickable(true);
        // awesomeSpeedometer.setSpeedTextPadding(20f);
        awesomeSpeedometer3.setMarkColor(Color.DKGRAY);
        awesomeSpeedometer3.setTickRotation(true);
        // awesomeSpeedometer3.setDecelerate(1.6f);
        // awesomeSpeedometer3.setAccelerate(1.0f);
        // awesomeSpeedometer3.setTrembleDuration(0);
        // awesomeSpeedometer3.setWithTremble(true);

        btnStartTrip = (Button) findViewById(R.id.buttonStartTrip );
        btnStartTrip.setEnabled(false);
        buttonOther = (Button) findViewById(R.id.buttonRunTest);
        distTrav = (TextView)findViewById(R.id.distTravelled);
        // timeTaken = (TextView)findViewById(R.id.timeTaken);

        checkPermission();
    }

    public void Precheck(){
        lw (  "PROGRAM   : DriveCycleTripScreen.runAll" );
        dashBoardArray = mService.CheckDashBoard();
        lw ("MSG       : dashBoardArray count = "+ dashBoardArray.size()  );
        lw ( "MSG       : About to pull Vehicle Param Array..." );
        vehParamArray = mService.getReportVehicleParams();
        lw ( "MSG       : vehParamArray count = "+ vehParamArray.size()  );
    }

    public void runAllReport(){
        lw (  "PROGRAM   : DriveCycleTripScreen.runAllReport" );
        System.out.println ( "DriveCycleTripScreen.runAllReport 1" );
        System.out.println( "VVVVVVVV onPreExecute allTrips.size() = "+allTrips.size() );
        lw ( "MSG       : freeze freeze count = "+ freezeFrameArray.size()  );

        dashBoardArray = new ArrayList();
        dashBoardArray = mService.CheckDashBoard();
        lw ("MSG       : dashBoardArray count = "+ dashBoardArray.size()  );

        System.out.println ( "DriveCycleTripScreen.runAllReport 2" );

        vehParamArray = new ArrayList();
        vehParamArray = mService.getReportVehicleParams();
        lw ( "MSG       : vehParamArray count = "+ vehParamArray.size()  );

        if ( allTrips.size()>0 ) {
            System.out.println(  "DriveCycleTripScreen.setTopList = "+ allTrips.size() );

            for (int i = 0; i < allTrips.size(); i++) {
                System.out.println( allTrips.get(i).tripNo+", TimeSpent = "
                        +allTrips.get(i).timeSpentStg+", Distance = "+allTrips.get(i).sitanceTravelled);
                String label = "Distance & Duration for Trip "+allTrips.get(i).tripNo;
                String labelValue = "TimeSpent = "+allTrips.get(i).timeSpentStg
                        +", Distance = "+allTrips.get(i).sitanceTravelled ;
                ArrayList<String> row = new ArrayList();
                row.add( "Trip" ); row.add( label );  row.add( labelValue );
                vehParamArray.add(row);
            }
        }

        System.out.println ( "DriveCycleTripScreen.runAllReport 3" );

        mode01_PID_01Array = new ArrayList();
        mode01_PID_01Array = mService.getBadMonitorsArray("01");
        lw ( "MSG       : Monitor Array count = "+ mode01_PID_01Array.size()  );

        noOfDTC =  Integer.parseInt( mService.CheckNoOfDTC("01") );
        lw ( "MSG       : This is Number of DTCs = "+noOfDTC );

        System.out.println ( "DriveCycleTripScreen.runAllReport 4" );

        troubleCodeArray = new ArrayList();
        troubleCodeArray = mService.readTroubleCodes("03", noOfDTC);
        lw ( "MSG       : This is Number of DTCs in Array = "+troubleCodeArray.size() );

        /*
        System.out.println ( "DriveCycleTripScreen.runAllReport 5" );
        freezeDropdown = mService.freezeFrameCount();
        frameItems =  validateFrames(freezeDropdown);
        freezeFrameArray = new ArrayList();
        freezeFrameArray = mService.freezeFrameServiceReport( frameItems);
        System.out.println ( "DriveCycleTripScreen.runAllReport 6" );
        */

        // loadAllVehFrame();


        boolean includeCompTest =  sharedPreferences.getBoolean("include_comp_tests", false);
        lw ( "includeCompTest = "+includeCompTest );
        if (includeCompTest) {
            ArrayList<ArrayList<String>> resultArrayListTemp = new ArrayList();
            if (currProtocolGroup.contains("ISO") || currProtocolGroup.contains("1850")) {
                lw  (   "INFO      : About to execute ISO "+currProtocol);
                boolean okToRun = mService.okToRunMode06IS01850();
                mService.testResultServiceFastISO1850(true);
                resultArrayListTemp = mService.resultFASTISO1850Mode06Array;
            } else {
                lw  (   "INFO      : About to execute CAN :"+currProtocol+", Protocol Group:" +currProtocolGroup);
                boolean okToRun = mService.okToRunMode06CAN();
                mService.testResultFastService(true);
                resultArrayListTemp = mService.resultAllMode06FastArray;
            }

            resultArrayList = new ArrayList();

            for (int i = 0; i < resultArrayListTemp.size(); i++) {
                ArrayList<String>  child = new ArrayList();
                child = resultArrayListTemp.get(i);
                if (    child.get(8).contains("0") ) {
                    resultArrayList.add( child );
                }
            }
        }

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

    public void setTopList() {
        lw (  "PROGRAM   : DriveCycleTripScreen.setTopList " );
        TopList = new ArrayList();

        for (int i = 0; i < vehParamArray.size(); i++) {
            ArrayList<String> vehP = vehParamArray.get(i);
            ArrayList<String> row1 = new ArrayList();
            row1.add(vehP.get(0));
            row1.add(vehP.get(1));
            TopList.add(row1);
        }
    }

    public class executeConnAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            lw ( "MSG       : Stopping service in case it is running" );
            Precheck();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            reloadList();
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    public class executeReport1AsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            lw ( "MSG       : Stopping service in case it is running" );
            runAllReport();
            File file = new File(context.getFilesDir() + "/checkEngine.PDF");
            System.out.println ("CONTENT :1"+  file.toString() );
            generateVehDiagnosticReport( file );
            lw ( "Checking if file exist???....");
            if (!file.exists() || !file.canRead()) {
                lw ( "File does not exist???....Proceed");
            } else {
                lw ( "File is found!!!!");
                Uri pdfUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
                lw ( "pdfUri = "+pdfUri.toString());
                lw ("About to call Intent");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(pdfUri, "application/pdf");
                startActivity(intent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    public int generateVehDiagnosticReport(File vfile) {
        l.w( context, "PROGRAM : DriveCycleTripScreen.generateVehMonitorsReport" );
        final File file = vfile;

        try {
            VehicleDiagnosticReport vehin = new VehicleDiagnosticReport( vfile,  dashBoardArray, vehParamArray, finalFreezeFrameArray, troubleCodeArray,
                    mode01_PID_01Array, resultArrayList, context);
            l.w( context, "MSG : generateVehMonitorsReport Completed Report generation");
            vehin.GenerateNow();

        } catch ( Exception ee ) {
            l.w( context, "ERROR : Error generateVehMonitorsReport generating report");
        }
        return 1;
    }

    public void displayRunReportData ( View view) {
        lw (  "PROGRAM   : DriveCycleTripScreen.displayRunReportData" );
        // Start Precheck

        LinearLayout myCheckLight = (LinearLayout) findViewById(R.id.myCheckLight) ;
        myCheckLight.setVisibility(View.VISIBLE);

        allTrips =  new ArrayList();
        new executeConnAsyncTask().execute("");
    }

    public void displayClearToubleCode (  ) {
        lw (  "PROGRAM   : DiagnosticScan.displayClearToubleCode" );
        try {
            velexMessageNow(context, "");
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : DiagnosticScan.displayClearToubleCode Error = "+e );
        }
    }

    public void velexMessageNow( Context pcontext, String exitMsg){
        lw (  "PROGRAM   : ConnectionScreen.velexMessageNow MSG = "+exitMsg );
        String lineSep = System.getProperty("line.separator");
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pcontext);
            String txt =    getString ( R.string.wanna_clear_dtc ) + lineSep + getString(R.string.docontinue) ;
            alertDialogBuilder.setMessage(  txt  );

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    onUserSelectValue("ok");
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_fade;
            alertDialog.show();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.velexMessageNow Error = "+e );
        }
    }

    public void onUserSelectValue(String selectedValue) {
        lw ( "PROGRAM : DiagnosticScan.onUserSelectValue" );
        if (selectedValue.equals("ok")) {
            clearDTCCodes();
        }
    }

    public boolean clearDTCCodes() {
        boolean itCompleted = mService.clearDiagnosticCodes();
        if (itCompleted) {
            Resources res = getResources();
            return true;
        }   else {
            toastIt( "ERROR clearing DTCs" );
            return false;
        }
    }

    public void reloadList() {
        lw (  "PROGRAM   : DriveCycleTripScreen.reloadList " );
        lw (  "INFO      : Total num = "+dashBoardArray.size());
/*
        LinearLayout lnchkEngine = (LinearLayout) findViewById(R.id.myCheckLight) ;
        LinearLayout mygascoolant = (LinearLayout) findViewById(R.id.mygascoolant) ;
        mygascoolant.setVisibility(View.VISIBLE);
        */

        setTopList();
        ignitionType = ConnActivityParent.ignitionType;
        ImageView imagechkLight = (ImageView) findViewById(R.id.imagechklight);

        //ImageView imagegasLight = (ImageView) findViewById(R.id.gaslightimage);

        //ImageView imagecooltempLight = (ImageView) findViewById(R.id.cooltempimage);

        InterpreteMode01 intMode1 = new InterpreteMode01();

        if (lineMode.equals("offline")) {

        } else {

            try {
                lw (  "INFO      : DriveCycleTripScreen.reloadList");
                lw (  "INFO      : This is the Monitor array size " + TopList.size());


                try {
                    if (vehParamArray.get(0).get(0).equals("01")) {
                        System.out.println("    ");
                        //txchklight.setText("Check Engine light on?  (" + vehParamArray.get(0).get(2) + ")");
                        if (vehParamArray.get(0).get(2).equals("Yes")) {
                            imagechkLight.setImageResource(R.drawable.checkenginebad);
                        } else {
                            imagechkLight.setImageResource(R.drawable.checkengineok);
                        }
                    }
                } catch ( Exception e) {
                    lw ( "ERROR ERROR: reloadList 1 = "+e);
                }
                float fuelS = 0f;
                try {
                    ArrayList<String> gasTankArray = dashBoardArray.get(1);
                    fuelS = (float) intMode1.P2FINT ( gasTankArray.get(1)  );
                    if (  gasTankArray.size()==2 ) {
                        if ( (fuelS >= 25) && (fuelS <= 75)  ) {
                            lw("This is Activity FuelTank SetResource " + gasTankArray.get(1));
                            //        imagegasLight.setImageResource(R.drawable.goodd1);
                        } else {
                            //        imagegasLight.setImageResource(R.drawable.badd1);
                        }
                    }
                } catch ( Exception e) {
                    lw ( "ERROR ERROR: reloadList 2 = "+e);
                }
                float coolantTemp = 0;
                try {
                    ArrayList<String> coolantTempArray = dashBoardArray.get(2);
                    //coolantTemp = Integer.parseInt(coolantTempArray.get(1));
                    coolantTemp = (float) intMode1.P05INT (coolantTempArray.get(1));
                    System.out.println("this coolant temp value = " + coolantTemp);
                    if (coolantTempArray.size() == 2) {
                        if ((coolantTemp >= 71) && (coolantTemp <= 100)) {
                            lw("This is Activity coolantTempArray = " + coolantTempArray.get(1));
                            //       imagecooltempLight.setImageResource(R.drawable.goodd1);
                        } else {
                            //       imagecooltempLight.setImageResource(R.drawable.badd1);
                        }
                    }
                } catch ( Exception e) {
                    lw ( "ERROR ERROR: reloadList 3 = "+e);
                }

                //lnchkEngine.setVisibility(View.VISIBLE);

                awesomeSpeedometer1 = (SpeedView) findViewById(R.id.speedView1);
                awesomeSpeedometer2 = (SpeedView) findViewById(R.id.speedView2);
                awesomeSpeedometer1.setBackgroundCircleColor(Color.WHITE);
                awesomeSpeedometer1.setSpeedTextColor(Color.BLACK);
                awesomeSpeedometer1.setMinMaxSpeed(0,120);            // works
                awesomeSpeedometer1.setStartEndDegree( 135 , 405);    // works
                awesomeSpeedometer1.setUnitTextColor(Color.DKGRAY);
                awesomeSpeedometer1.setTextColor(Color.DKGRAY);
                awesomeSpeedometer1.setClickable(true);
                awesomeSpeedometer1.setMarkColor(Color.DKGRAY);
                String tempUnitLabel = DEGREE+"C";
                awesomeSpeedometer1.setUnit(tempUnitLabel);
                awesomeSpeedometer1.setTickRotation(true);
                awesomeSpeedometer1.setWithTremble(true);

                awesomeSpeedometer2.setBackgroundCircleColor(Color.WHITE);
                awesomeSpeedometer2.setSpeedTextColor(Color.BLACK);
                awesomeSpeedometer2.setMinMaxSpeed(0,100);            // works
                awesomeSpeedometer2.setStartEndDegree( 135 , 405);    // works
                awesomeSpeedometer2.setUnitTextColor(Color.DKGRAY);
                awesomeSpeedometer2.setTextColor(Color.DKGRAY);
                awesomeSpeedometer2.setClickable(true);
                awesomeSpeedometer2.setUnit( "%" );
                awesomeSpeedometer2.setMarkColor(Color.DKGRAY);
                awesomeSpeedometer2.setTickRotation(true);
                awesomeSpeedometer2.setScrollBarSize(2);

                lw ( "fuelS = "+fuelS);

                awesomeSpeedometer1.speedTo( coolantTemp );
                awesomeSpeedometer2.speedTo( fuelS );

                btnStartTrip.setEnabled(true);
                btnStartTrip.setTextColor(Color.BLACK);

            } catch ( Exception e ) {
                e.printStackTrace();
                lw("ERROR ERROR : DriveCycleTripScreen.reloadList = " + e);
            }
        }
    }

    public double  P2Fd (String A ) {
        try {
            double resultA = ConvertValues.HexToInteger(A.substring(0, 2));
            double result = (0.39215686) * resultA;
            return result;
        } catch ( Exception e ) {
            return 0.0;
        }
    }

    public void goTripManual (String vIgnitionType) {
        lw (  "PROGRAM   : DriveCycleTripScreen.goMonitorManual" );
        final String ignitionType = vIgnitionType;
        try {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(context, TripManual.class);
                    intentMain.putExtra("ignitionType", ignitionType);
                    startActivityForResult(intentMain, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            inMyThread.start();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : DriveCycleTripScreen.goMonitorManual Error = "+e );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        lw (  "PROGRAM   : DriveCycleTripScreen.onCreateOptionsMenu");

        boolean includeCompTest =  sharedPreferences.getBoolean("include_comp_tests", false);
        lw ( "includeCompTest = "+includeCompTest );

        try {
            getMenuInflater().inflate(R.menu.menu_trip_screen, menu );
            MenuItem trip_manual = menu.findItem(R.id.trip_manual);
            MenuItem runReportPDF = menu.findItem(R.id.run_pdf);
            MenuItem clear_enginelight = menu.findItem(R.id.clear_enginelight);

            System.out.println (  "reportIconShow = "+reportIconShow );

            if ( reportIconShow ) {
                runReportPDF.setVisible(true);
                trip_manual.setEnabled(true);
                clear_enginelight.setEnabled(true);
            } else {
                runReportPDF.setVisible(false);
                trip_manual.setEnabled(false);
                clear_enginelight.setEnabled(false);
            }
            //includeCompMenu.setChecked(includeCompTest);

        } catch ( Exception e ) {
            lw("ERROR ERROR : DriveCycleTripScreen.onCreateOptionsMenu. Error = " + e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lw (  "PROGRAM   : DriveCycleTripScreen.onOptionsItemSelected");

        try {
            int id = item.getItemId();
            if (id == R.id.trip_manual) {
                goTripManual("");
                return true;
            } else if (id == R.id.vehicle_info) {
                goVehicleParameters(context);
                return true;
            } else if (id == R.id.action_settings) {
                return true;
            } else if (id == R.id.clear_enginelight) {
                displayClearToubleCode();
                return true;
            } else if (id == R.id.run_pdf) {
                new executeReport1AsyncTask().execute("");
                return true;
            }
        } catch ( Exception e ) {
            lw("ERROR ERROR : DriveCycleTripScreen.onOptionsItemSelected. Error = " + e);
        }
        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            lw (  "PROGRAM   : DriveCycleTripScreen.onServiceConnected" );
            final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            lw ("INFO      : Method is onServiceDisconnected");
            mBound = false;
        }
    };

    public void goVehicleParameters(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.goVehicleParameters");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread inMyThread;
                        inMyThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mService.vehicleParamService1();
                            }
                        });
                        inMyThread.start();
                        Intent intent = new Intent(locContext, VehicleParameters.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);

                        lw ( "INFO      : MainMenuActivity.goVehicleParameters completed [Success]");
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.goVehicleParameters. Error1 = " + e);
                    }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.goVehicleParameters. Error2 = " + e);
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN.checkEngine");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (null != intent) { //Null Checking
                        Bundle bundleExtras = new Bundle();
                        bundleExtras = intent.getBundleExtra("statsBundle");
                        vSpeedHex = bundleExtras.getString("0");
                        updateSpeed( vSpeedHex );
                        //redraw();
                    }
                } catch ( Exception e ) {
                    //System.out.println( "ERROR ERROR On Receive");
                }
            }
        };
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    float speed = 0;
    private static final int HISTORY_SIZE = 20;

    static float currentSpeed ;

    public void updateSpeed(String hexResponse0) {
        try {
            Integer decResponse010D = ConvertValues.HexToInteger ( hexResponse0 );
            if (unitMeasure.contains("Imperial")) {
                awesomeSpeedometer3.speedTo(decResponse010D*(float)0.621371, 2000);
            }else {
                if (decResponse010D < 10 ) {
                    awesomeSpeedometer3.speedTo(decResponse010D, 3000);
                } else if (  decResponse010D <= 20 ) {
                    awesomeSpeedometer3.speedTo(decResponse010D + 1, 3000);
                } else if (  decResponse010D > 20 ) {
                    awesomeSpeedometer3.speedTo(decResponse010D + 2 , 3000);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        if ( mBound ) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!mBound) {
            Intent intent = new Intent(  context, BluetoothService.class);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
    }

    public class CustomFragAdapter extends BaseAdapter{
        Context context;
        List<TripClass> rowItem;

        CustomFragAdapter(Context context, List<TripClass> rowItem) {
            this.context = context;
            this.rowItem = rowItem;
        }

        @Override
        public int getCount() {
            return rowItem.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rowItem.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.tabitemdrivecycletripnew, null);
            }

            TextView txtTitle1 = (TextView) convertView.findViewById(R.id.text);
            TextView txtTitle2 = (TextView) convertView.findViewById(R.id.text1);
            Button  btn = (Button) convertView.findViewById(R.id.bbtn);

            final int pos = position;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    System.out.println("Clicking click. Position = "+pos );
                    callMap(pos);
                }
            });

            TripClass row_pos = rowItem.get(position);

            txtTitle1.setText(  "TRIP "+row_pos.tripNo+" - "+ row_pos.formatedDate   );

            if ( row_pos.sitanceTravelled.equals("X") || row_pos.timeSpentStg.equals("X") ){

            } else {
                txtTitle2.setText("Distance: "+row_pos.sitanceTravelled + ", Time: " + row_pos.timeSpentStg);
                // btn.setText( position );
            }
/*
            System.out.println( "txtTitle1 = "+ row_pos.getTitle1()  );

            System.out.println( "txtcode = "+ row_pos.getCode()  );

            System.out.println( "txtTitle2 = "+ row_pos.getTitle2()  );

            if (row_pos.getTitle1().equals("01")) {
                if ( row_pos.getTitle2().equals("Yes") ) {
                    image2.setImageResource(R.drawable.badd1);
                } else {
                    image2.setImageResource(R.drawable.goodd1);
                }
            } else {
                image2.setImageResource(R.drawable.warningg1);
                if (row_pos.getCode().equals("Stored DTC(s)")) image2.setImageResource(R.drawable.badd1);
                if (row_pos.getCode().equals("Incomplete Monitors")) image2.setImageResource(R.drawable.badd1);
            }
            */

            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
            animation.setStartOffset(position * 100);
            convertView.startAnimation(animation);
            return convertView;
        }
    }

    ArrayList<ArrayList<String>>  finalFreezeFrameArray = new  ArrayList();

    public void loadAllVehFrame ( ) {
        finalFreezeFrameArray = new ArrayList();

        if (freezeFrameArray.size()>1) {

            for (int i = 0; i < freezeFrameArray.size(); i++) {
                ArrayList<String> row = new ArrayList<String>();
                ArrayList<String> newrow = new ArrayList<String>();
                row = freezeFrameArray.get(i);
                newrow = ConvertValues.getTitleResult( row.get(0), row.get(1), context, "02" );
                finalFreezeFrameArray.add(newrow);
            }
        }
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    static int positionClicked = 0;


    public void callMap(int val) {

        System.out.println ( "This is allTriips count = "+allTrips.size()  );

/*
        Intent intent = new Intent (this,  MapsActivity.class);

        int cnt = allTrips.get(val).tripGPS.size();
        if ( reportIconShow ) {
            if (cnt > 4) {
                // System.out.println ( "This is pos vp = "+vp  );

                intent.putExtra("routeClass", allTrips.get(val));

                overridePendingTransition(R.anim.fade_in_dialog_v, R.anim.fade_out_dialog_v);
                startActivity(intent);
            } else {
                toastIt("Not a valid route/trip");
            }
        }
        */
    }

    @Override
    public void onBackPressed() {
        velexMessageNowDataRates(context, "Do you want to exit drive cycle?");
    }

    public void velexMessageNowDataRates( Context pcontext, String exitMsg){
        lw (  "PROGRAM   : ConnectionScreen.velexMessageNow MSG = "+exitMsg );
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pcontext);
            alertDialogBuilder.setMessage(exitMsg);
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    locationManager.removeUpdates(locationListenerGPS);
                    mService.releaseSocket ();
                    finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.velexMessageNow Error = "+e );
        }
    }

/*
    public void toggleBestUpdates(View view) {
        if(!checkLocation())
            return;
        Button button = (Button) view;
        if(button.getText().equals(getResources().getString(R.string.pause))) {
            locationManager.removeUpdates(locationListenerBest);
            button.setText(R.string.resume);
        }
        else {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if(provider != null) {

                if (checkPermission()) {
                    locationManager.requestLocationUpdates(provider,  1000, 10, locationListenerBest);
                    button.setText("pause");
                    Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void toggleNetworkUpdates(View view) {
        if(!checkLocation())
            return;
        Button button = (Button) view;
        if(button.getText().equals("pause")) {
            locationManager.removeUpdates(locationListenerNetwork);
            button.setText("resume");
        }
        else {

            if (checkPermission()) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,  1000, 10, locationListenerNetwork);
                Toast.makeText(this, "Network provider started running", Toast.LENGTH_LONG).show();
                button.setText("pause");
            }
        }
    }

/*
    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueBest.setText(longitudeBest + "");
                    latitudeValueBest.setText(latitudeBest + "");
                    Toast.makeText(DriveCycleTripScreen.this, "Best Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueNetwork.setText(longitudeNetwork + "");
                    latitudeValueNetwork.setText(latitudeNetwork + "");
                    Toast.makeText(DriveCycleTripScreen.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
*/


    public boolean checkPermission() {

        int LOCATION = 0;
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(this, "Does not have location priviledges", Toast.LENGTH_LONG).show();
            System.out.println (  " Does not have location priviledges  ");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Toast.makeText(this, "Need your permission for locational services", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Need your permission for locational services", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION);
            }
        } else {

        }
        return true;
    }

    public void updateTrip(TripClass tmp) {
        System.out.println( "********************************************************");
        System.out.println( " updateTrip ");
        System.out.println( "Trip start TIme : "+tmp.startTime );
        System.out.println( "Trip end TIme : "+tmp.endTime );
        System.out.println( "Trip number : "+tmp.tripNo );
        System.out.println( "beginlong : "+tmp.beginlong );
        System.out.println( "beginlat : "+tmp.beginlat );
        System.out.println( "endlong : "+tmp.endlong );
        System.out.println( "endlat : "+tmp.endlat );
        String distanceTravelled = tmp.sitanceTravelled;
        String timeTaken = tmp.timeSpentStg;

        System.out.println ( "distanceTravelled = "+distanceTravelled );

        if ( distanceTravelled.equals("X")   ) {
            System.out.println( "distanceTravelled is null");
            distanceTravelled = "0.0 meters";
        } else {
            //distanceTravelled = tmp.sitanceTravelled;
        }

        if ( timeTaken.equals("X") ) {
            System.out.println( "timeTaken is null");
            timeTaken = "0.0 minutes";
        } else {
            //timeTaken = tmp.timeSpentStg;
        }

        System.out.println( "sitanceTravelled : "+distanceTravelled);
        System.out.println( "timeTaken : "+timeTaken);

        System.out.println( "********************************************************");

        adapter = new CustomFragAdapter(context, allTrips);
        list.setAdapter(adapter);

    }

    static ArrayList<TripClass>  allTrips =  new ArrayList();

    TripClass tclass;

    public void toggleGPSUpdates(View view) {
        lw (  "PROGRAM   : DriveCycleTripScreen.toggleGPSUpdates" );

        buttonOther.setEnabled(false);
        buttonOther.setTextColor(Color.LTGRAY);

        int tripPosition = allTrips.size();

        Button button = (Button) view;
        if(button.getText().equals("pause")) {
            lw ( "INFO      : Ending Trip... TripPosition = "+ tripPosition );
            FrameLayout flay = (FrameLayout) findViewById(R.id.mygascoolant1);
            flay.setVisibility(View.INVISIBLE);

            reportIconShow = true;
            invalidateOptionsMenu();

            locationManager.removeUpdates(locationListenerGPS);
            button.setText(R.string.resume);
            int longlatcount = currTripLongLat.size();
            System.out.println( "This is Trip Position count = "+ tripPosition );

            System.out.println( "Stoping existing trip now ...longlatcount = "+longlatcount );
            allTrips.get(tripPosition-1).tripStop();
            allTrips.get(tripPosition-1).storeLonLatList(currTripLongLat);
            allTrips.get(tripPosition-1).beginlat = lastlatitudeGPS;
            allTrips.get(tripPosition-1).beginlong = lastlongitudeGPS;

            System.out.println("XXXXX lastlatitudeGPS = "+lastlatitudeGPS+" , lastlongitudeGPS = "+lastlongitudeGPS);

            updateTrip( allTrips.get(tripPosition-1) );

            lastlongitudeGPS = 0.0;
            lastlongitudeGPS = 0.0;

            mService.releaseSocket ();

        }
        else {
            lw ( "INFO      : Begining Trip... TripPosition = "+ tripPosition );
            //  lastlongitudeGPS = 0.0;
            //  lastlongitudeGPS = 0.0;

            tclass = new TripClass( tripPosition+1  );
            startTimeTrip = new Date();

            FrameLayout flay = (FrameLayout) findViewById(R.id.mygascoolant1);
            flay.setVisibility(View.VISIBLE);

            reportIconShow = false;
            invalidateOptionsMenu();

            allTrips.add( tclass );

            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    lw ( "INFO      : Kick off vehicle speed");
                    mService.vehicleStatsService();
                }
            });
            inMyThread.start();

            adapter = new CustomFragAdapter(context, allTrips);
            list.setAdapter(adapter);

            if (checkPermission()) {
                currTripLongLat = new ArrayList();
                lw ( "INFO      : Kick off locational services");
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,   500, 20, locationListenerGPS);
                button.setText(R.string.pause);
            }
        }
    }

    static Date startTimeTrip;

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // if (currentSpeed == 0.0) {

                    //  } else {

                    ArrayList<Double> trip = new ArrayList<Double>();
                    trip.add(longitudeGPS);
                    trip.add(latitudeGPS);

                    if ((lastlongitudeGPS == 0.0) && (lastlatitudeGPS == 0.0)) {
                        System.out.println("This is just begining 0.0");
                        lastlongitudeGPS = longitudeGPS;
                        lastlatitudeGPS = latitudeGPS;

                    } else {

                        int tripCounter = allTrips.size();
                        System.out.println("This is tripCounter = " + tripCounter);

                        currTripLongLat.add(trip);

                        float [] results = new float[5];
                        Location.distanceBetween(lastlatitudeGPS, lastlongitudeGPS,
                                latitudeGPS,  longitudeGPS, results);

                        int timeSpent = minutesDiff(startTimeTrip,new Date() );

                        String tmpp = results[0]+ "" ;

                        if ( tmpp.length()>5 ) {

                        }else {
                            String display = "Dist. : " +
                                    roundFloat (results[0], 1)  + " m";
                            distTrav.setText( display );
                        }

                    }
                    // }
                }
            });
        }

        public float roundFloat (float number, int decimalPlace) {
            BigDecimal bd = new BigDecimal(number);
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd.floatValue();
        }

        public int minutesDiff( Date earlierDate, Date laterDate ) {
            long SECOND_MILLIS = 1000;
            long MINUTE_MILLIS = SECOND_MILLIS*60;
            if( earlierDate == null || laterDate == null ) return 0;
            return (int)((laterDate.getTime()/MINUTE_MILLIS) - (earlierDate.getTime()/MINUTE_MILLIS));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

}



