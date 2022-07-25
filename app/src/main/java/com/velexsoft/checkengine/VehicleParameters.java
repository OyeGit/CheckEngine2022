package com.velexsoft.checkengine;

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
import android.widget.TextView;

import com.velexsoft.checkengine.modess.InterpreteMode01;
import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;

import java.util.ArrayList;
import java.util.List;

import static com.velexsoft.checkengine.R.id.recyclerView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class VehicleParameters extends ConnActivityParent {

    LinearLayoutManager linearLayoutManager;
    private List<ParamItem> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ParamsAdapter mAdapter;
    BluetoothService mService;
    ArrayList<ArrayList<String>> mode01Array = new ArrayList();
    String originalTitle ;
    ArrayList<String>  vehParam = new ArrayList();
    Context context = this;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_vehicle_parameters;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "VEHIPARAMS: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        originalTitle = getString( R.string.veh_param  );
        loadTitle(originalTitle);
        l.fileName = ConnActivityParent.logFileName;
        lw("-------------------------------------------------------------------------------");
        lw("ACTIVITY  : Vehicle Parameters Started");
        lw("-------------------------------------------------------------------------------");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        mAdapter = new ParamsAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            mSwipeRefreshLayout.setRefreshing(false);
            //System.out.println("This is refresh...");
            }
        });
    }

    private BroadcastReceiver mReceiver;

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN.checkEngine");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<String> newRow = intent.getStringArrayListExtra( "paramRow" );

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
        mService.releaseSocket();

        sendCloud();

        finish();
    }

    public void sendCloud() {
        LogCloudFileNew c = new LogCloudFileNew( "VehicleParams3", context );
        c.wMon( "01" , "Response Included" , vehParam  );
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
           // System.out.println ( "Method is onServiceDisconnected");
            mBound = false;
        }
    };


    public void loadVehicleParams ( ArrayList<String> p_row_mode01) {

        try {

            ArrayList<String> newArray = ConvertValues.getTitleResult(p_row_mode01.get(0), p_row_mode01.get(1), context, "01");

            String decodedParams = newArray.get(0) + " = " + newArray.get(1);
           // System.out.println( decodedParams);
            String vehParamVal = "["+p_row_mode01.get(0)+" - "+p_row_mode01.get(1)+"]      ["+newArray.get(0)+" = "+ newArray.get(1)+"]" ;
            System.out.println( "vehParamVal = "+ vehParamVal );
            vehParam.add( vehParamVal );
            ParamItem movie = new ParamItem(newArray.get(0), newArray.get(1), "");
            movieList.add(movie);

            mAdapter.notifyDataSetChanged();
            //mSwipeRefreshLayout.setRefreshing(false);
        } catch ( Exception e ) {
           // System.out.println( "This is the loadVehicleParams" );
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

        public ParamItem() {
        }

        public ParamItem(String title, String resultValue, String vun ) {
            this.title =  title ;
            this.resultValue = resultValue;
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
                    .inflate(R.layout.tabitemparametersred, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ParamItem paramitem = paramList.get(position);
            holder.title.setText(paramitem.getTitle());
            holder.genre.setText(paramitem.getResultValue());
        }

        @Override
        public int getItemCount() {
            return paramList.size();
        }
    }


}
