package com.velexsoft.checkengine;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.velexsoft.checkengine.modess.InterpreteMode01;
import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;

import java.util.ArrayList;
import java.util.List;

public class FreezeFrameActivity extends ConnActivityParent {

    private List<ParamItem> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ParamsAdapter mAdapter;
    BluetoothService mService;
    InterpreteMode01 inteprete;
    final Context context = this;
    static String[] items;
    ArrayList<String>  vehParam = new ArrayList();
    private ProgressBar mLoadingIndicator;
    Window window ;
    LinearLayout spinner1Layout;
    String currFrame = "00";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_freeze_frame;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "FREEZEFRMS: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String freezeTitle = getString(R.string.freeze_frame);
        loadTitle(freezeTitle);
        l.fileName = ConnActivityParent.logFileName;
        inteprete = new InterpreteMode01();
        window = getWindow();
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        spinner1Layout = (LinearLayout) findViewById(R.id.spinner1Layout);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : FreezeFrameActivity Started");
        lw (  "-------------------------------------------------------------------------------");

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new executeConnAsyncTask().execute("");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public class pullFrame extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            lw ( "MSG       : pullFrame.doInBackground..." );
            if (mBound) {

                mService.freezeFrameService(currFrame, true);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            new executeConnAsyncTask().execute("");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    static ArrayList<ArrayList<String>>  freezeDropdown = new ArrayList();

    public class executeConnAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            lw("MSG       : Stopping service in case it is running");
            if (mBound) {
                lw("INFO      : This is current Protocol = " + currProtocol);
                freezeDropdown = mService.freezeFrameCount();
                items =  mService.validateFrames(freezeDropdown);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if ( items.length==0 ) {
                toastIt( "Freeze frame data not available");
            } else {
                Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items);
                dropdown.setAdapter(adapter);
                spinner1Layout.setVisibility(View.VISIBLE);

                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        currFrame = items[position];
                        lw ( "onItemSelected position= "+ selectedItemView  );
                        loadFrameonView();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            }
        }

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    public void loadFrameonView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        //mSwipeRefreshLayout.setRefreshing(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        movieList = new ArrayList();
        mAdapter = new ParamsAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        new pullFrame().execute("");
    }


    private BroadcastReceiver mReceiver;

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN.checkEngine");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                lw ( "OnRecieve Broadcast... ");
                ArrayList<String> newRow = intent.getStringArrayListExtra( "freezeRow" );
                lw ( newRow.toString() );
                System.out.println( newRow.toString() );
                loadVehicleParams( newRow );
            }
        };
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mService.setRollDashService ();
        mService.releaseSocket();

        sendCloud();
        finish();
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

    public void sendCloud() {
        LogCloudFileNew c = new LogCloudFileNew( "FreezeFrame3", context );
        c.wMon( "01" , "Response Included" , vehParam  );
    }

    public void loadVehicleParams ( ArrayList<String> p_row_mode01) {
        try {
            ArrayList<String> row =  ConvertValues.getTitleResult(p_row_mode01.get(0), p_row_mode01.get(1), context, "02");
            String vehParamVal = "[" + p_row_mode01.get(0) + " - " + p_row_mode01.get(1) + "]      [" + row.get(0) + " = " + row.get(1) + "]";
            vehParam.add(vehParamVal);
            ParamItem movie = new ParamItem(row.get(0), row.get(1), "");
            this.movieList.add(movie);
            this.mAdapter.notifyDataSetChanged();
        } catch (Exception e ) {
            System.out.println ( "Error Error" );
        }
    }


    class ParamItem {

        private String title;

        public String getResultValue() {
            return resultValue;
        }

        public void setResultValue(String resultValue) {
            this.resultValue = resultValue;
        }

        private String resultValue;

        public ParamItem(String title, String resultValue, String vun ) {
            this.title = title ;
            this.resultValue = resultValue ;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String name) {
            this.title = name;
        }
    }

    class ParamsAdapter extends RecyclerView.Adapter<ParamsAdapter.MyViewHolder> {

        private List<ParamItem> paramList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, year, genre;

            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                genre = (TextView) view.findViewById(R.id.resultValue);
            }
        }

        public ParamsAdapter(List<ParamItem> moviesList) {
            this.paramList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tabitemparameters, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ParamItem paramitem = paramList.get(position);
            holder.title.setText(paramitem.getTitle());
            holder.genre.setText(paramitem.getResultValue());
            lw ( "FreezeFrame Name = "+ paramitem.getTitle()+" - "+ paramitem.getResultValue()  );
        }

        @Override
        public int getItemCount() {
            return paramList.size();
        }
    }


}
