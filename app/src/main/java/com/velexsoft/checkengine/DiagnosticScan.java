package com.velexsoft.checkengine;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.velexsoft.checkengine.modess.InterpreteMode01;
import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.utilities.IntepretDashValues;
import com.velexsoft.checkengine.utilities.VehicleDiagnosticReport;

import java.io.File;
import java.util.ArrayList;

import static com.velexsoft.checkengine.DriveCycleTripScreen.allTrips;

public class DiagnosticScan extends ConnActivityParent {

    CustomAdapter2Images adapter;
    ListView list;
    Window window ;
    ArrayList<ArrayList<String>> troubleCodeArray ;
    ArrayList<ArrayList<String>> dashBoardArray ;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mLoadingIndicator;
    final Context context = this;
    int noOfDTC;
    boolean mBound = false;
    BluetoothService mService;
    String Stored_Pending = "Stored";
    public  DiagnosticScan  CustomListView = null;
    String originalTitle ;
    boolean isItFirstTime = true;
    ImageView MilImg;
    ImageView engineOilTempImg;
    ImageView CoolTempImg;
    ImageView BatteryPowerImg;
    ImageView FuelTankImg;
    String[] items;
    String code;
    String offline = "offline";
    static boolean reportIconShow = true;
    ArrayList<ArrayList<String>> mode01_PID_01Array = new ArrayList<ArrayList<String>>();
   // ArrayList<ArrayList<String>> troubleCodeArray = new ArrayList<ArrayList<String>>();
    static ArrayList<ArrayList<String>> vehParamArray = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> freezeFrameArray = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> TopList  = new ArrayList();
   // static ArrayList<ArrayList<String>> dashBoardArray  = new ArrayList<>();
    ArrayList<ArrayList<String>>  finalFreezeFrameArray = new  ArrayList();
    ArrayList<ArrayList<String>> resultArrayList  = new ArrayList();

    public  ArrayList<InfoItem2Images> CustomListViewValuesArr = new ArrayList<InfoItem2Images>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_diagnostic_scan;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "DIAGNOSTIC: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstTime = true;
        reportIconShow = true;
        l.fileName = ConnActivityParent.logFileName;
        originalTitle = getString( R.string.diagnostic_scan);
        loadTitle(originalTitle);
        code = "03";
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Diagnostic Scan Started");
        lw (  "-------------------------------------------------------------------------------");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        window = getWindow();
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String stored = getString(R.string.stored);
        if ( !currProtocolGroup.contains("CAN")  ) {
            items = new String[]{stored, getString(R.string.pending) };
        } else {
            items = new String[]{stored, getString(R.string.pending), getString(R.string.permanent)};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if ( isItFirstTime )  {
                    isItFirstTime = false;
                }else {
                    if (position == 0) {
                        Stored_Pending = "Stored";
                    } else if (position == 1) {
                        Stored_Pending = "Pending";
                    } else if (position == 2) {
                        Stored_Pending = "Permanent";
                    }
                    newExecute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        Stored_Pending = "Stored";
        list = (ListView) this.findViewById(R.id.listView);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                TextView  txt = (TextView)view.findViewById(R.id.text);
                TextView  txtDesc = (TextView)view.findViewById(R.id.textwide);
                checkMessageNow( context , txt.getText()+" - "+txtDesc.getText() );
                return true;
            }
        });
        CustomListView = this;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newExecute();
            }
        });

        MilImg = (ImageView)findViewById(R.id.image1);
        engineOilTempImg = (ImageView)findViewById(R.id.image2);
        CoolTempImg = (ImageView)findViewById(R.id.image3);
        BatteryPowerImg = (ImageView)findViewById(R.id.image4);
        FuelTankImg = (ImageView)findViewById(R.id.image5);
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

    public void checkMessageNow( Context pcontext, String exitMsg){
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
            lw (  "ERROR ERROR : DiagnosticScan.checkMessageNow. Error = "+e );
        }
    }

    public void newExecute() {
        if (lineMode.equals(offline)) {
            mSwipeRefreshLayout.setRefreshing(false);
        }else {
            new executeConnAsyncTask().execute("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lineMode.equals(offline)) {

        }else {
            if (!mBound) {
                Intent intent = new Intent(this, BluetoothService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lineMode.contains(offline)) {
        } else {
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }
    }

    private class executeConnAsyncTask extends AsyncTask<String, Void, String> {

        boolean didItFailed = false;
        String num_of_dtc = getString( R.string.no_of_dtc );

        @Override
        protected void onPreExecute() {
            clearDashBoard();
            troubleCodeArray = new ArrayList();
            CustomListViewValuesArr.clear();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            if (mService.validateMode0100(threadSleepSendCmd*2)) {
                try {
                    if (Stored_Pending.equals("Stored")) {
                        lw("------------- DTC RETRIEVE : EXECUTE STORED DTC -------------");
                        code = "03";
                        noOfDTC = Integer.parseInt(mService.CheckNoOfDTC("01"));
                        lw("STORED [" + code + "] - noOfDTC =" + noOfDTC);
                        String num_of_dtc = getString(R.string.no_of_dtc);

                    } else if (Stored_Pending.equals("Pending")) {
                        lw("------------- DTC RETRIEVE : EXECUTE PENDING DTC -------------");
                        code = "07";
                        noOfDTC = Integer.parseInt(mService.CheckNoOfDTC("41"));
                        lw("STORED [" + code + "] - noOfDTC =" + noOfDTC);
                    } else if (Stored_Pending.equals("Permanent")) {
                        lw("------------- DTC RETRIEVE : EXECUTE PERMANENT DTC -------------");
                        code = "0A";
                    }
                    dashBoardArray = new ArrayList();
                    dashBoardArray = mService.CheckDashBoard();

                    troubleCodeArray = mService.readTroubleCodes(code, noOfDTC);

                } catch (Exception e) {
                    didItFailed= true;
                    lw("ERROR ERROR : DiagnosticScan.executeConnAsyncTask.doInBackground = " + e);
                }
            } else {
                didItFailed= true;
                mBound = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            setDashBoard();
            setListData();
            if (Stored_Pending.equals("Stored")) toastIt(num_of_dtc+" : "+noOfDTC);
            if (didItFailed)  toastIt(  getString( R.string.connProblem )  );
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    static boolean isFirstTime;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            lw ( "Method is onServiceConnected Diagnostic Scan");
            try {
                final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;

                lw("About to bind Service");

                mService = binder.getService();
                mBound = true;

                if (isFirstTime) {
                    isFirstTime = false;
                    newExecute();
                }

            } catch ( Exception e ){
                lw ("ERROR ERROR : DiagnosticScan.onServiceConnected = "+e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            toastIt( "Connection interrupted" );
        }
    };

    public void clearDashBoard() {
        MilImg.setImageResource( R.drawable.checkengineok );
        FuelTankImg.setImageResource( R.drawable.gastankok );
        CoolTempImg.setImageResource( R.drawable.coolantempok );
        engineOilTempImg.setImageResource( R.drawable.engineoiltempok );
        BatteryPowerImg.setImageResource( R.drawable.batteryok );
    }

    String MILon="0";
    ArrayList<String> FuelTank = new ArrayList<>();
    ArrayList<String> coolTemp = new ArrayList<>();
    ArrayList<String> engineOilTemp = new ArrayList<>();
    ArrayList<String> inputBattery = new ArrayList<>();

    public void setDashBoard() {

        IntepretDashValues intp = new IntepretDashValues( context, dashBoardArray );

        try {
            MILon = dashBoardArray.get(0).get(1);
            lw  ( "This is MILon = "+MILon);
            if (MILon.equals("1")) {
                MilImg.setImageResource( R.drawable.checkenginebad );
            } else {
                MilImg.setImageResource( R.drawable.checkengineok );
            }
        } catch ( Exception e ) {
            lw("WARNING   : DiagnosticScan.setDashBoard.MILon = "+e);
        }

        try {
            FuelTank = dashBoardArray.get(1);
            if (intp.getFuelTankGoodBad().contains("BAD")) {
                FuelTankImg.setImageResource(R.drawable.gastankbad);
            } else {
                FuelTankImg.setImageResource(R.drawable.gastankok);
            }
        } catch ( Exception e ) {
            lw("WARNING   : DiagnosticScan.setDashBoard.FuelTank = "+e);
        }

        try {
            coolTemp = dashBoardArray.get(2);
            lw("This is CoolTemp = " + coolTemp.get(1));
            if (intp.getCoolTempGoodBad().contains("BAD")) {
                lw("This is CoolTemp is bad " );
                CoolTempImg.setImageResource(R.drawable.coolantempbad);
            } else {
                CoolTempImg.setImageResource(R.drawable.coolantempok);
            }
        } catch ( Exception e ) {
            lw("WARNING   : DiagnosticScan.setDashBoard.coolTemp = "+e);
        }

        try {
            engineOilTemp = dashBoardArray.get(3);
            lw("This is engineOilTemp= " + engineOilTemp.get(1));
            if (intp.getEngineOilGoodBad().contains("BAD")) {
                engineOilTempImg.setImageResource(R.drawable.engineoiltempbad);
            } else {
                engineOilTempImg.setImageResource(R.drawable.engineoiltempok);
            }
        } catch ( Exception e ) {
            lw("WARNING   : DiagnosticScan.setDashBoard.engineOilTemp = "+e);
        }

        try {
            inputBattery = dashBoardArray.get(4);
            lw("This is inputBattery= " + inputBattery.get(1));
            if ( intp.getInputBatteryBad().contains("BAD") ) {
                BatteryPowerImg.setImageResource(R.drawable.batterybad);
            } else {
                BatteryPowerImg.setImageResource(R.drawable.batteryok);
            }
        } catch ( Exception e ) {
            lw("WARNING   : DiagnosticScan.setDashBoard.inputBattery = "+e);
        }
    }

    InterpreteMode01 intMode1 = new InterpreteMode01();

    public void onClickEngLight(View view) {

       lw( "This is Check Engine = "+ MILon);
        if ( MILon.contains("0")  ) {
            ArrayList<String>  distTrav = mService.distanceTravelledCodeCleared();
            if ( ( distTrav.size()> 1 )  )
                toastIt( distTrav.get(1)+" : "+distTrav.get(2)  );
        } else {
            ArrayList<String>  distTrav = mService.distanceTravelled();
            if ( ( distTrav.size()> 1 )  )
            toastIt( distTrav.get(1)+" : "+distTrav.get(2)  );
        }
    }

    public void onClickFuelTank(View view) {
        try {
            String fuelFinalVlaue = intMode1.P2F ( FuelTank.get(1) );
            lw ("This is onClickFuelTank Fuel Tank = " + fuelFinalVlaue);
            toastIt("Fuel Tank Volume : " + fuelFinalVlaue   );
        } catch( Exception e ) {
           lw ( "Error onClickFuelTank ERROR : "+ e  );
        }
    }

    public void onClickCoolTemp(View view) {
        try {
            String coolFinalTemp = intMode1.P05 ( coolTemp.get(1) );
            lw("This is onClickCoolTemp Coolant Temp = " + coolFinalTemp);
            toastIt("Engine Coolant Temperature : " + coolFinalTemp );
        } catch ( Exception e ) {
            lw ( "Error onClickCoolTemp ERROR : "+ e  );
        }
    }

    public void onClickEngineOilTemp(View view) {
        try {
            String engineFinalTemp = intMode1.P5C ( engineOilTemp.get(1) );
            lw( "This is onClickEngineOilTemp engine Oil Temp= "+engineFinalTemp );
            toastIt( "Engine Oil Temperature : "+engineFinalTemp  );
        } catch ( Exception e ) {
            lw ( "Error onClickEngineOilTemp ERROR : "+ e  );
        }
    }

    public void onClickBattery(View view) {
        try {
            lw ( "This is onClickBattery input Battery= "+inputBattery );
            toastIt( "Battery Input Voltage : "+inputBattery.get(1)  );
        } catch ( Exception e ) {
            lw ( "Error onClickBattery ERROR : "+ e  );
        }
    }

    public void setListData ()
    {
        ArrayList<String>  p_row;
        if (Stored_Pending.equals("Stored")) {
            try {
                CustomListViewValuesArr.clear();
                for (int i = 0; i < (troubleCodeArray.size() ); i++) {

                    p_row = troubleCodeArray.get(i);
                    InfoItem2Images sched = new InfoItem2Images();
                    sched.setmTitle(p_row.get(0));
                    sched.setmImage(p_row.get(1));
                    sched.setmImage1(p_row.get(2));
                    sched.setmDescription(p_row.get(3));
                    CustomListViewValuesArr.add(sched);
                }
            } catch (Exception e) {
                lw ("ERROR ERROR : DiagnosticScan.setListData.Stored = "+e);
            }
        } else if (Stored_Pending.equals("Pending")) {
            try {
                CustomListViewValuesArr.clear();
                for (int i = 0; i < troubleCodeArray.size(); i++) {
                    p_row = troubleCodeArray.get(i);
                    InfoItem2Images sched = new InfoItem2Images();
                    sched.setmTitle( p_row.get(0) );
                    sched.setmImage( p_row.get(1) );
                    sched.setmImage1( p_row.get(2) );
                    sched.setmDescription( p_row.get(3) );
                    CustomListViewValuesArr.add( sched );
                }
            } catch (Exception e) {
                lw ("ERROR ERROR : DiagnosticScan.setListData.Pending = "+e);
            }

        } else if (Stored_Pending.equals("Permanent")) {
            try {
                CustomListViewValuesArr.clear();
                for (int i = 0; i < troubleCodeArray.size(); i++) {
                    p_row = troubleCodeArray.get(i);
                    InfoItem2Images sched = new InfoItem2Images();
                    sched.setmTitle( p_row.get(0) );
                    sched.setmImage( p_row.get(1) );
                    sched.setmImage1( p_row.get(2) );
                    sched.setmDescription( p_row.get(3) );
                    CustomListViewValuesArr.add( sched );
                }
            } catch (Exception e) {
                lw ("ERROR ERROR : DiagnosticScan.setListData.Permanent = "+e);
            }
        }

        lw (  "INFO      : *************************************************");
        lw (  "Results for Diagnostic Trouble Codes [ "+ Stored_Pending  +" ]" );
        lw (  "INFO      : *************************************************");
        lw (  "INFO      : Total num = "+CustomListViewValuesArr.size());

        Resources res = getResources();
        list = (ListView) this.findViewById(R.id.listView);
        adapter = new CustomAdapter2Images(this, CustomListViewValuesArr, res);
        list.setLongClickable(true);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diagnostic_scan_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.freezeFrame) {
            displayFreezeFrameMode(context);
            return true;
        }  else if ( id == R.id.action_clearTroubCode) {
            displayClearToubleCode();
            return true;
        }  else if ( id == R.id.vehicle_info) {
            goVehicleParameters();
            return true;
        } else if ( id == R.id.run_pdf) {
            System.out.println("XX - About to run PDF...");
            new executeReport1AsyncTask().execute("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goVehicleParameters() {
        lw (  "PROGRAM   : DiagnosticScan.goVehicleParameters");
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

                                    try {
                                        mService.vehicleParamService1();
                                    } catch (Exception e ){
                                        lw ( "WARNING : mService.vehicleParamService1 call failed"+ e );
                                    }
                                }
                            });
                            inMyThread.start();

                        Intent intent = new Intent(locContext, VehicleParameters.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);

                        lw ( "INFO      : DiagnosticScan.goVehicleParameters completed [Success]");
                    } catch (Exception e) {
                        lw("ERROR ERROR : DiagnosticScan.goVehicleParameters. Error1 = " + e);
                    }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : DiagnosticScan.goVehicleParameters. Error2 = " + e);
        }
    }

    public void displayClearToubleCode () {
        lw (  "PROGRAM   : DiagnosticScan.displayClearToubleCode" );
        try {
            velexMessageNow(context, "");
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : DiagnosticScan.displayClearToubleCode Error = "+e );
        }
    }

    public void goGoogle (String PIDCode) {
        lw (  "PROGRAM   : DiagnosticScan.goGoogle" );
        final String pidcode = PIDCode;
        try {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(context, WebViewExGoogle.class);
                    String preppendedDTC = "OBD Code "+pidcode;
                    intentMain.putExtra("PIDCode", preppendedDTC );
                    startActivityForResult(intentMain, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            inMyThread.start();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : DiagnosticScan.goGoogle. Error = "+e );
        }
    }

    public void displayFreezeFrameMode(Context context) {
        final Context locContext = context;
        Thread myThread;
        final ProgressDialog myPd_ring;
        myPd_ring = new ProgressDialog(context, R.style.MyTheme);
        myPd_ring.setCancelable(false);
        myPd_ring.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        myThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(locContext, FreezeFrameActivity.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });
        myThread.start();
    }

    public boolean clearDTCCodes() {
        CustomListView = this;
        boolean itCompleted = mService.clearDiagnosticCodes();
        clearDashBoard();
        if (itCompleted) {
            Resources res = getResources();
            CustomListViewValuesArr.clear();
            adapter = new CustomAdapter2Images(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);
            MilImg.setImageResource( R.drawable.checkengineok );
            FuelTankImg.setImageResource( R.drawable.gastankok );
            return true;
        }   else {
             toastIt("ERROR clearing DTCs" );
            return false;
        }
    }

    public void onUserSelectValue(String selectedValue) {
        lw ( "PROGRAM : DiagnosticScan.onUserSelectValue" );
        if (selectedValue.equals("ok") &&  (lineMode.equals( "online"))  ) {
            clearDTCCodes();
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

    class InfoItem2Images {

        private String mImage;
        private String mImage1;
        private String mTitle;
        private String mDescription;

        public String getmTitle() {
            return mTitle;
        }

        public void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public String getmDescription() {
            return mDescription;
        }

        public void setmDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        public String getmImage() {
            return mImage;
        }

        public void setmImage(String mImage) {
            this.mImage = mImage;
        }

        public void setmImage1(String mImage1) {
            this.mImage1 = mImage1;
        }
    }

    public class CustomAdapter2Images  extends BaseAdapter  {

        private Activity activity;
        private ArrayList<InfoItem2Images> data;
        private LayoutInflater inflater=null;
        public Resources res;
        InfoItem2Images tempValues=null;
        int i=0;

        public CustomAdapter2Images(Activity a,  ArrayList<InfoItem2Images> d,Resources resLocal) {

            activity = a;
            data=d;
            res = resLocal;
            inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if(data.size()<=0)
                return 1;
            return data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder{

            public TextView text;
            public TextView text1;
            public TextView textWide;
            public ImageView image;
            public ImageView image1;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;

            if(convertView==null){
                vi = inflater.inflate(R.layout.tabitemdiagnosticsnew, null);
                holder = new ViewHolder();
                holder.text = (TextView) vi.findViewById(R.id.text);
                holder.image=(ImageView)vi.findViewById(R.id.image);
                holder.textWide=(TextView)vi.findViewById(R.id.textwide);
                vi.setTag( holder );
            }
            else
                holder=(ViewHolder)vi.getTag();

            if(data.size()<=0) {
                holder.text.setText( "No Error Codes" );
                holder.image.setImageResource(R.drawable.goodd1);
            } else {
                tempValues=null;
                tempValues = ( InfoItem2Images ) data.get( position );
                String textCode = "  "+tempValues.getmTitle()+ "  " + tempValues.getmImage();
                holder.text.setText( textCode );
                holder.textWide.setText(  tempValues.getmDescription() );
                holder.image.setVisibility( View.VISIBLE );
                if ( code.equals("07") ) {
                    holder.image.setImageResource(R.drawable.dtcwarning);
                } else {
                    holder.image.setImageResource(R.drawable.dtcstored );
                }
                lw ( "textCode = "+tempValues.getmImage()+", Description = "+tempValues.getmDescription() );
                vi.setOnClickListener(new OnItemClickListener( position , tempValues.getmImage()   ));
            }

            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
            animation.setStartOffset( position * 50 );
            vi.startAnimation(animation);
            return vi;
        }

        private class OnItemClickListener  implements View.OnClickListener {
            private int mPosition;
            private String mDtc;

            OnItemClickListener(int position, String dtc ){
                mPosition = position;
                mDtc = dtc;
            }

            @Override
            public void onClick(View arg0) {

                ListView lv = (ListView)findViewById(R.id.listView);
                View view =  (lv.getChildAt (mPosition));
                String pCode = mDtc;

                if ( isNetworkAvailable() ) {
                    goGoogle(pCode);
                } else {
                   toastIt("Internet access is NOT available..." );
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        mService.releaseSocket();
        super.onBackPressed();
        finish();
    }

    public static int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
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
}
