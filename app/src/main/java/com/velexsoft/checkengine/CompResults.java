package com.velexsoft.checkengine;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;
import com.velexsoft.checkengine.utilities.VehicleComponentTestReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CompResults extends ConnActivityParent implements
        LoaderManager.LoaderCallbacks<String> {

    LinearLayoutManager linearLayoutManager;
    private List<InfoItem> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ParamsAdapter mAdapter;
    BluetoothService mService;
    Window window ;
    ArrayList<ArrayList<String>> mode06Array = new ArrayList();

    private ProgressBar mLoadingIndicator;
    String  originalTitle;
    private ArrayList<Integer> imagesState = new ArrayList<>();
    LogCloudFileNew c;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.comp_result_parameters;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "COMPRESULT: " + msg);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : CompResults Started");
        lw (  "-------------------------------------------------------------------------------");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
        originalTitle = getString ( R.string.test_results );
        loadTitle(originalTitle);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        l.fileName = ConnActivityParent.logFileName;

        mAdapter = new ParamsAdapter(movieList, imagesState );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        getSupportLoaderManager().initLoader(5, null, this);

        Button button1 = (Button) findViewById(R.id.buttonRunTest);
        button1.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                   onImageCLick();
                }
            }
        );
        c = new LogCloudFileNew( "TestResults3", context );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comp_result_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.setResults) {
            return true;
        } else  if (id == R.id.run_pdf) {
            toastIt( "Running Report..." );
            new executeConnAsyncTask().execute("");
            return true;
        }
        return true;
    }


    public void onImageCLick(){

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.buttonView1);

        linearLayout.setVisibility(View.INVISIBLE);

        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> githubSearchLoader = loaderManager.getLoader(5);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(5, queryBundle, this);
        } else {
            loaderManager.restartLoader(5, queryBundle, this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        // COMPLETED (4) Return a new AsyncTaskLoader<String> as an anonymous inner class with this as the constructor's parameter
        return new AsyncTaskLoader<String>(this) {

            // COMPLETED (5) Override onStartLoading

            boolean didItFailed = false;
            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                try {

                    if (mBound) {

                        if (mService.validateMode0100(threadSleepSendCmd*2)) {
                            lw("INFO      : This is current Protocol = " + currProtocol);

                            if (currProtocolGroup.contains("ISO") || currProtocolGroup.contains("1850")) {
                                lw("INFO      : About to execute ISO " + currProtocol);

                                boolean okToRun = mService.okToRunMode06IS01850();
                                mService.testResultServiceISO1850();
                                //mService.loadFakeResult();

                            } else {
                                lw("INFO      : About to execute CAN :" + currProtocol + ", Protocol Group:" + currProtocolGroup);
                                boolean okToRun = mService.okToRunMode06CAN();
                                mService.testResultFastService(false);
                            }
                        } else {
                            didItFailed= true;
                            mBound = false;
                        }
                    }
                    return "hello";
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onStopLoading() {
                if  (lineMode.equals( "online")) {
                    if (didItFailed) toastIt(getString(R.string.connProblem));
                }
            }
        };
    }


    public class executeConnAsyncTask extends AsyncTask<String, Void, String> {
        boolean didItFailed = false;
        @Override
        protected String doInBackground(String... params) {
            lw ( "MSG       : Stopping service in case it is running" );
            if (mBound) {

                if (mService.validateMode0100(threadSleepSendCmd*2)) {
                    lw("INFO      : This is current Protocol = " + currProtocol);
                    resultArrayList = new ArrayList();
                    if (currProtocolGroup.contains("ISO") || currProtocolGroup.contains("1850")) {
                        lw("INFO      : About to execute ISO " + currProtocol);
                        boolean okToRun = mService.okToRunMode06IS01850();
                        mService.testResultServiceFastISO1850(true);

                        resultArrayList = mService.resultFASTISO1850Mode06Array;
                    } else {
                        lw("INFO      : About to execute CAN :" + currProtocol + ", Protocol Group:" + currProtocolGroup);
                        boolean okToRun = mService.okToRunMode06CAN();
                        mService.testResultFastService(true);
                        resultArrayList = mService.resultAllMode06FastArray;
                    }

                    System.out.println("resultArrayListresultArrayList.size() = " + resultArrayList.size());

                    File file = new File(context.getFilesDir() + "/checkEngineComp.PDF");
                    System.out.println("CONTENT :1" + file.toString());
                    generateCompResultReport(file);
                    lw("Checking if file exist???....");
                    if (!file.exists() || !file.canRead()) {
                        lw("File does not exist???....Proceed");
                    } else {
                        lw("File is found!!!!");

                        Uri pdfUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
                        lw("pdfUri = " + pdfUri.toString());
                        lw("About to call Intent");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(pdfUri, "application/pdf");
                        startActivity(intent);
                    }
                } else {
                    didItFailed= true;
                    mBound = false;
                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (lineMode.equals( "online")) {
                if (didItFailed) toastIt(getString(R.string.connProblem));
            }
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

    static ArrayList<ArrayList<String>> dashBoardArray = new ArrayList();

    static ArrayList<ArrayList<String>> vehParamArray = new ArrayList();

    static ArrayList<ArrayList<String>> mode01_PID_01Array = new ArrayList();

    static ArrayList<ArrayList<String>> resultArrayList = new ArrayList();

    static ArrayList<ArrayList<String>> resultArrayListDesc = new ArrayList();

    public void runAllReport(){

        lw (  "PROGRAM   : DriveCycleTripScreen.runAll" );

        dashBoardArray = mService.CheckDashBoard();
        lw ("MSG       : dashBoardArray count = "+ dashBoardArray.size()  );

        lw ( "MSG       : About to pull Vehicle Param Array..." );
        vehParamArray = mService.getReportVehicleParams();
        lw ( "MSG       : vehParamArray count = "+ vehParamArray.size()  );

        lw ( "MSG       : About to pull Monitor Array..." );
        mode01_PID_01Array = mService.getBadMonitorsArray("01");
        lw ( "MSG       : Monitor Array count = "+ mode01_PID_01Array.size()  );
    }


    public int generateCompResultReport(File vfile) {
        l.w( context, "PROGRAM : DriveCycleTripScreen.generateVehMonitorsReport" );
        final File file = vfile;
        runAllReport();

        resultArrayListDesc = new ArrayList();

        for (int i = 0; i < resultArrayList.size(); i++) {
            ArrayList<String> n = new ArrayList<String>();
            Integer vNo = i + 1;
            n = resultArrayList.get(i);
            lw ( "Result= "+  n.get(0)+","+n.get(1)+","+n.get(2)+","+n.get(3)+","+n.get(4)+","
                    +n.get(5 )+","+n.get(6) +","+n.get(7)+","+n.get(8) +","+n.get(9)+","+n.get(10)  ) ;
            ArrayList tempArray = new ArrayList();

            tempArray.add(  0, n.get(0) );
            tempArray.add(  1,  n.get(1)   );
            tempArray.add(  2,  n.get(2)    );
            tempArray.add(  3,  n.get(3)    );
            tempArray.add(  4,  n.get(4)    );
            tempArray.add(  5, n.get(1)   );
            tempArray.add(  6,  n.get(6)    );
            tempArray.add(  7,  n.get(7)    );
            tempArray.add(  8,  n.get(8)    );
            tempArray.add(  9,  n.get(9)    );
            tempArray.add(  10,  n.get(10)    );
            tempArray.add(  11,  n.get(11)    );
            tempArray.add(  12,  n.get(12)    );
            tempArray.add(  13,  n.get(13)    );

            resultArrayListDesc.add(tempArray);
        }

        try {
            VehicleComponentTestReport vehin = new VehicleComponentTestReport( vfile,  dashBoardArray, vehParamArray,   resultArrayListDesc, context);
            lw (  "MSG : generateVehMonitorsReport Completed Report generation");
            vehin.GenerateNow();

        } catch ( Exception ee ) {
            lw (  "ERROR : Error generateVehMonitorsReport generating report");
        }
        return 1;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    @Override
    protected void onStart() {

        super.onStart();

        if ( !mBound ) {
            Intent intent = new Intent(this, BluetoothService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    @Override
    public void onRestart() {
        System.out.println( "on Restart");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        super.onRestart();
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            lw("CompResults.onServiceConnected [Success]" );
            final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            lw("CompResults.onServiceDisconnected [Success]" );
            mBound = false;
        }
    };

    private BroadcastReceiver mReceiver;

    @Override
    public void onResume() {
        super.onResume();

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN.checkEngine");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
            try {
                int Progress = intent.getIntExtra("progress", 0);
                int usrInput = Progress;
                    if (Progress== -2){
                        toastIt("Condition NOT OK to run test");
                        lw( "Condition NOT OK to run test");
                    } else if (Progress== -1){
                        toastIt("Condition OK to run test");
                        lw( "Condition OK to run test");
                    } else if (Progress!=0) {
                        loadTitle(originalTitle + "(" + Progress + "%)");
                        if (  Progress == 100 )     c.w( "06" , "Test Result" , arrayTestResult  );
                    } else {
                        lw( "BroadCast onReceiver Vehicle parameters firing...");
                        try {
                            ArrayList<String> newRow = intent.getStringArrayListExtra("testResultRow");
                            loadTestResult(newRow);
                        } catch ( Exception e ) {
                            lw ("ERROR ERROR : CompResults.BroadcastReceiver "+e );
                        }
                    }
                } catch( Exception e ){
               lw ("ERROR ERROR : Error trying to get Progress = "+e);
            }
            }
        };
        this.registerReceiver(mReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }

    ArrayList<String>  arrayTestResult = new ArrayList();

    public void loadTestResult ( ArrayList<String> p_row_mode01) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "INFO      : TestResults.loadTestResult");
        lw (  "-------------------------------------------------------------------------------");
        InfoItem sched = new InfoItem();

        sched.setvCategory( p_row_mode01.get(11 )  );
        sched.setvTitle( p_row_mode01.get(12 )  );
        sched.setvScaleDesc(   p_row_mode01.get(13 )   );
        sched.setvMID  ( p_row_mode01.get(3) );
        sched.setvTID  ( p_row_mode01.get(4) );
        sched.setvMIN  ( p_row_mode01.get(6) );
        sched.setvMAX  ( p_row_mode01.get(7) );
        sched.setvResult   ( p_row_mode01.get(9) );
        sched.setvPassFail ( p_row_mode01.get(8) );

        String resultArrayString = p_row_mode01.get(10)+" ["+sched.getvMID()+"-"+sched.getvTID()+"] [min:"+sched.getvMIN()+"] [max:"+sched.getvResult()+"] [Res:"+sched.getvResult()+"] [PASS/FAIL:"+sched.getvPassFail()+"]";
        arrayTestResult.add( resultArrayString );
        lw (  "INFO      : About to add info into MovieList" +resultArrayString);
        movieList.add(sched);
        lw (  "INFO      : About to notify Adapter");
        mAdapter.notifyDataSetChanged();
    }

    public class InfoItem {
        private int num;
        private String vCategory;
        private String vTitle;
        private String vScaleDesc;
        private String vResult;
        private String vMID;
        private String vTID;
        private String vMIN;
        private String vMAX;
        private String vPassFail;

        public String getvPassFail() {
            return vPassFail;
        }

        public void setvPassFail(String vPassFail) {
            this.vPassFail = vPassFail;
        }

        public String getvCategory() {
            return vCategory;
        }

        public void setvCategory(String vCategory) {
            this.vCategory = vCategory;
        }

        public String getvTitle() {
            return vTitle;
        }

        public void setvTitle(String vTitle) {
            this.vTitle = vTitle;
        }

        public String getvScaleDesc() {
            return vScaleDesc;
        }

        public void setvScaleDesc(String vScaleDesc) {
            this.vScaleDesc = vScaleDesc;
        }

        public String getvResult() {
            return vResult;
        }

        public void setvResult(String vResult) {
            this.vResult = vResult;
        }

        public String getvMID() {
            return vMID;
        }

        public void setvMID(String vMID) {
            this.vMID = vMID;
        }

        public String getvTID() {
            return vTID;
        }

        public void setvTID(String vTID) {
            this.vTID = vTID;
        }

        public String getvMIN() {
            return vMIN;
        }

        public void setvMIN(String vMIN) {
            this.vMIN = vMIN;
        }

        public String getvMAX() {
            return vMAX;
        }

        public void setvMAX(String vMAX) {
            this.vMAX = vMAX;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mService.releaseSocket();
        finish();
    }

    class ParamsAdapter extends RecyclerView.Adapter<ParamsAdapter.MyViewHolder> {

        private List<InfoItem> infoList;
        private ArrayList<Integer> imagesState;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView vCategory;
            public TextView vMIDTID;
            public TextView vTitle;
            public TextView vScalDesc;
            public TextView vResult;
            public ImageView image;
            public TextView min;
            public TextView max;

            public MyViewHolder(View view) {
                super(view);
                vCategory = (TextView) view.findViewById(R.id.vCategory);
                vMIDTID = (TextView) view.findViewById(R.id.vMIDTID);
                vTitle = (TextView) view.findViewById(R.id.vTitle);
                vScalDesc = (TextView) view.findViewById(R.id.textScaleDesc);
                vResult =(TextView)view.findViewById(R.id.result);
                image = (ImageView) view.findViewById(R.id.image);
                min = (TextView) view.findViewById(R.id.min);
                max = (TextView) view.findViewById(R.id.max);
            }
        }

        public ParamsAdapter(List<InfoItem> moviesList, ArrayList<Integer> imagesState ) {
            this.infoList = moviesList;
            this.imagesState = imagesState;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tabitemtestresult, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            InfoItem infoitem = infoList.get(position);
            String label1 = infoitem.getvCategory()+" - "+infoitem.getvTitle();
            holder.vCategory.setText(label1);
            lw( label1 );
            int currNum = position + 1;
            String label2 = "("+currNum+") "+infoitem.getvMID()+"  "+infoitem.getvTID();
            holder.vMIDTID.setText( label2 );
            String label3  = infoitem.getvScaleDesc();
            holder.vScalDesc.setText( label3 );

            if (currProtocolGroup.equals("CAN")) {
                lw( "This is CAN " );
                lw("INSIDE ADAPTOR : MIN = " + infoitem.getvMIN() + ", MAX = " + infoitem.getvMAX() + ", RESULT = " + infoitem.getvResult());
                holder.vResult.setText(infoitem.getvResult());
                holder.min.setText(infoitem.getvMIN());
                holder.max.setText(infoitem.getvMAX() );

                if (infoitem.getvPassFail().equals("1")) {
                    holder.image.setImageResource( R.drawable.goodd1 );
                } else if ( infoitem.getvPassFail().equals("0") ) {
                    holder.image.setImageResource( R.drawable.badd1 );
                } else if ( infoitem.getvPassFail().equals("2") ) {
                    holder.image.setImageResource( R.drawable.warningg1 );
                } else if ( infoitem.getvPassFail().equals("-") ) {
                    holder.image.setVisibility(View.INVISIBLE);
                }
            } else {
                lw( "This is ISO/J1850 " );
                String testLimitType = infoitem.getvMIN();
                String minValue = "";
                String maxValue = "";
                String resultValue = infoitem.getvResult();
                if ( testLimitType.equals("0") ) {
                    minValue = "0";
                    maxValue = infoitem.getvMAX();
                } else if ( testLimitType.equals("1") ) {
                    minValue = infoitem.getvMAX();
                    maxValue = "...";
                }
                holder.max.setText(maxValue);
                holder.min.setText(minValue);
                holder.vResult.setText(resultValue);

                lw("INSIDE ADAPTOR : MIN = " + minValue+ ", MAX = " + maxValue + ", RESULT = " + resultValue);

                if (infoitem.getvPassFail().equals("1")) {
                    holder.image.setImageResource( R.drawable.goodd1 );
                } else if ( infoitem.getvPassFail().equals("0") ) {
                    holder.image.setImageResource( R.drawable.badd1 );
                } else if ( infoitem.getvPassFail().equals("2") ) {
                    holder.image.setImageResource( R.drawable.warningg1 );
                } else if ( infoitem.getvPassFail().equals("-") ) {
                    holder.image.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return infoList.size();
        }
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

}
