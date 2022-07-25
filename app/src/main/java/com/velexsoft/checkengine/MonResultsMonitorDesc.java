package com.velexsoft.checkengine;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.velexsoft.checkengine.modess.Mode6GetMIDTID;
import com.velexsoft.checkengine.servicess.BluetoothService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonResultsMonitorDesc extends ConnActivityParent implements
        LoaderManager.LoaderCallbacks<String> {

    LinearLayoutManager linearLayoutManager;
    private List<InfoItem> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ParamsAdapter mAdapter;
    BluetoothService mService;
    ArrayList<ArrayList<String>> mode06Array = new ArrayList();
    private ProgressBar mLoadingIndicator;
    String monTitle = null;
    TextView  tx;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.test_result_mon_desc;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context,"MONIT1TEST: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        monTitle = bundle.getString("monTitle");

        loadTitle(monTitle);
        l.fileName = ConnActivityParent.logFileName;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        tx = (TextView)findViewById(R.id.titleDesc);
        tx.setVisibility(View.VISIBLE);
        getDescription ( monTitle );

        lw("PROGRAM   : ConnectionScreen.onPostCreate ");

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mAdapter = new ParamsAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        Button button1 = (Button) findViewById(R.id.buttonRunTest);
        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        onButtonCLick();
                    }
                }
        );
    }

    public void onButtonCLick(){

        tx.setVisibility(View.INVISIBLE);

        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(5, queryBundle, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {

                mLoadingIndicator.setVisibility(View.VISIBLE);
                if (args == null) {
                    return;
                }

                forceLoad();
            }

            @Override
            public String loadInBackground() {
                    try {
                    if (mBound) {

                        System.out.println ( "Monitor Title = "+monTitle);
                        if (currProtocolGroup.contains("ISO") || currProtocolGroup.contains("1850")) {
                            lw  (   "INFO      : About to execute ISO "+currProtocol);
                             boolean okToRun = mService.okToRunMode06IS01850();


                            mService.testRunEachResultService(monTitle, ConnActivityParent.currProtocolGroup );
                        } else {
                            lw  (   "INFO      : About to execute CAN :"+currProtocol+", Protocol Group:" +currProtocolGroup);
                             boolean okToRun = mService.okToRunMode06CAN();

                            mService.testRunEachResultService(monTitle, ConnActivityParent.currProtocolGroup);
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
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (null == data) {

        } else {

        }
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
        System.out.println ( "Method is onStop");
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private BroadcastReceiver mReceiver;

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN.checkEngine");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                lw( "BroadCast onReceiver MonResultsMonitorDesc...");

                ArrayList<String> newRow = intent.getStringArrayListExtra( "testResultRow" );
                if (newRow==null) {
                    lw (  "INFO      : Array is NULL");
                } else {
                    System.out.println( "Calling loadVehicleParams" );
                    loadVehicleParams( newRow );
                }


            }
        };
        this.registerReceiver(mReceiver, intentFilter);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }

    public void loadVehicleParams ( ArrayList<String> p_row_mode01) {
        lw (  "-------------------------------------------------------------------------------");
        lw (  "INFO      : TestResults.loadVehicleParams");
        lw (  "-------------------------------------------------------------------------------");
        InfoItem sched = new InfoItem();
        Mode6GetMIDTID inte = new Mode6GetMIDTID(context);
        ArrayList<String> labelArray = inte.getMIDDescription(  p_row_mode01.get(0) );
        sched.setvCategory( labelArray.get(0) );
        sched.setvTitle( labelArray.get(1) );
        sched.setvScaleDesc(  inte.getTIDDescription ( p_row_mode01.get(2) )   );
        sched.setvMID  ( p_row_mode01.get(3) );
        sched.setvTID  ( p_row_mode01.get(4) );
        sched.setvMIN ( p_row_mode01.get(6) );
        sched.setvMAX ( p_row_mode01.get(7) );
        sched.setvResult ( p_row_mode01.get(9) );
        sched.setvPassFail ( p_row_mode01.get(8) );
        lw("loadVehicleParams      : "+p_row_mode01.get(0)+","+p_row_mode01.get(1)+","+p_row_mode01.get(2)+","+p_row_mode01.get(3)+","
                +p_row_mode01.get(4)+","+p_row_mode01.get(5)+","+p_row_mode01.get(6)+","+p_row_mode01.get(7)+","+p_row_mode01.get(8) );
        lw (  "INFO      : About to add info into MovieList");
        movieList.add(sched);
        lw (  "INFO      : About to notify Adapter");
        mAdapter.notifyDataSetChanged();
    }

    public class InfoItem {
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
        mService.setRollDashService ();
        finish();
    }


    class ParamsAdapter extends RecyclerView.Adapter<ParamsAdapter.MyViewHolder> {

        private List<InfoItem> infoList;

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

        public ParamsAdapter(List<InfoItem> moviesList) {
            this.infoList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tabitemtestresult, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ParamsAdapter.MyViewHolder holder, int position) {
            InfoItem infoitem = infoList.get(position);
            holder.vCategory.setText(infoitem.getvCategory()+" - "+infoitem.getvTitle());
            int currNum = position + 1;
            holder.vMIDTID.setText("("+currNum+")   ["+infoitem.getvMID()+" "+infoitem.getvTID()+"]" );
            holder.vScalDesc.setText( infoitem.getvScaleDesc() );

            lw ( "INSIDE ADAPTOR : MIN = "+infoitem.getvMIN()+", MAX = "+infoitem.getvMAX()+", RESULT = "+infoitem.getvResult() );
            holder.vResult.setText( infoitem.getvResult() );
            holder.min.setText(infoitem.getvMIN());
            holder.max.setText(infoitem.getvMAX());


            if (infoitem.getvPassFail().equals("1")) {
                holder.image.setImageResource( R.drawable.goodd1 );
            } else if ( infoitem.getvPassFail().equals("0") ) {
                holder.image.setImageResource( R.drawable.badd1 );
            } else if ( infoitem.getvPassFail().equals("2") ) {
                holder.image.setImageResource( R.drawable.warningg1 );
            } else if ( infoitem.getvPassFail().equals("-") ) {
                holder.image.setVisibility(View.INVISIBLE);
            }

            setAnimation(holder.itemView, position);
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


    public void getDescription ( String mCode ) {

        TextView t = (TextView) findViewById(R.id.titleDesc );
        if (mCode.contains("Misfire"))
           t.setText(   getString( R.string.getMisFireMonitorInfo )   );
        else if (mCode.equals("Fuel System"))
            t.setText(   getString( R.string.getFuelSystemMonitorInfo )   );
        else if (mCode.equals("Components"))
            t.setText(   getString( R.string.getComprehensiveComponentMonitor )   );
        else if (mCode.equals("Catalyst"))
            t.setText(   getString( R.string.getCatalystConverterMonitorInfo )   );
        else if (mCode.equals("Heated Catalyst"))
            t.setText(   getString( R.string.getHeatedCatalystMonitorInfo )   );
        else if (mCode.equals("Evaporative System"))
            t.setText(   getString( R.string.getEvapMonitorInfo )   );
        else if (mCode.equals("Secondary Air System"))
            t.setText(   getString( R.string.getSecondaryAirSystemMonitorInfo )   );
        else if (mCode.equals("A/C Refrigerant"))
            t.setText(   getString( R.string.getACRefrigerantMonitorInfo )   );
        else if (mCode.equals("Oxygen Sensor"))
            t.setText(   getString( R.string.getOxygenSensorMonitorInfo )   );
        else if (mCode.equals("Oxygen Sensor Heater"))
            t.setText(   getString( R.string.getOxygenSensorHeaterMonitorInfo )   );
        else if (mCode.equals("EGR System"))
            t.setText(   getString( R.string.getEGRMonitorInfo )   );
        else if (mCode.equals("NMHC Catalyst"))
            t.setText(   getString( R.string.getNMHCMonitorInfo )   );
        else if (mCode.equals("N0x/SCR Monitor"))
            t.setText(   getString( R.string.getNOxSCRMonitorInfo )   );
        else if (mCode.equals("Boost Pressure"))
            t.setText(   getString( R.string.getBoostPressureMonitorInfo )   );
        else if (mCode.equals("Exhaust Gas Sensor"))
            t.setText(   getString( R.string.getExhaustGasSensorMonitorInfo )   );
        else if (mCode.equals("PM Filter Monitoring"))
            t.setText(   getString( R.string.getPMFilterMonitorInfo )   );
        else if (mCode.equals("EGR and/or VVT System"))
            t.setText(   getString( R.string.getEGRMonitorInfo )   );
    }


}
