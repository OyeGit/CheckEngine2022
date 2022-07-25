package com.velexsoft.checkengine;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;

import java.util.ArrayList;
import java.util.List;

public class MonitorsScreen extends ConnActivityParent  {

    final Context context = this;
    CustomFragAdapter adapter;
    ListView list;
    private List<RowItem> rowItems;
    static RowItem choosenRow = null;
    ArrayList<ArrayList<String>> mode01_PID_01Array = new ArrayList<ArrayList<String>>();
    public static final String PREFS_NAME = ConnActivityParent.PREFS_NAME;
    BluetoothService mService;
    boolean mBound = false;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView  tx ;
    String mMonitorType = "01";
    boolean isItFirstTime= true;
    String ignitionType;
    boolean fisrtTimein = true;
    LogCloudFileNew c;
    ArrayList<String> monListCloud =  new ArrayList();
    String monRawResponse;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_monitors_screen;
    }

    public void lw( String msg )
    {
        if ( logErrors ) {
            l.w(context, "MONITORSCR: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        l.fileName = ConnActivityParent.logFileName;
        fisrtTimein = true;
        loadTitle(getString( R.string.monitors ));

        ignitionType = ConnActivityParent.ignitionType;
        c = new LogCloudFileNew( "Monitors3", context );

        if ( ignitionType.equals("0") ) {
            lw (  "INFO      : MonitorsScreen.onServiceConnected : "+ R.string.spark_ignition );
            loadTitle(getString( R.string.spark_ignition ));
        } else   if ( ignitionType.equals("1") ){
            lw (  "INFO      : MonitorsScreen.onServiceConnected : "+ R.string.comp_ignition );
            loadTitle(getString( R.string.comp_ignition ));
        } else {
            lw (  "INFO      : MonitorsScreen.onServiceConnected : "+ R.string.monitors );
            loadTitle(getString( R.string.monitors ));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Monitor Screen Started");
        lw (  "-------------------------------------------------------------------------------");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String statusCleared = getString ( R.string.status_since_cleared);
        String statusThisCycle = getString ( R.string.status_this_cycle);
        String[] items = new String[]{ statusCleared, statusThisCycle };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                lw (  "INFO      : onPostCreate.onItemSelected. This is position = "+position );

                if ( isItFirstTime )  {
                    isItFirstTime = false;
                } else {
                    if (position == 0) {
                        mMonitorType = "01";
                        new executeConnAsyncTask().execute("");
                    } else if (position == 1) {
                        mMonitorType = "41";
                        new executeConnAsyncTask().execute("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        list = (ListView) this.findViewById(R.id.listView);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new executeConnAsyncTask().execute("");
            }
        });
    }

    private class executeConnAsyncTask extends AsyncTask<String, Void, String> {

        boolean didItFailed = false;
        String num_of_dtc = getString( R.string.no_of_dtc );
        String monTypeDesc = "";
        int countMonFail = 0;
        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            if (mService.validateMode0100(threadSleepSendCmd*2)) {
                try {
                    monRawResponse = "";
                    monListCloud = new ArrayList();
                    mode01_PID_01Array = mService.getMonitorsArray(mMonitorType);
                    monRawResponse = mService.monRawResponse;
                    lw (  "INFO      : This is the Monitor array size " + mode01_PID_01Array.size());
                    lw (  "INFO      : This is the Raw response " + monRawResponse );

                    rowItems = new ArrayList<RowItem>();
                    ArrayList<String> p_row_mode01;

                    lw (  "INFO      : *************************************************");
                    lw (  "INFO      : Results for ["+mMonitorType+"] - "+monTypeDesc);
                    lw (  "INFO      : *************************************************");
                    lw (  "INFO      : Total num = "+mode01_PID_01Array.size());

                    for (int i = 0; i < mode01_PID_01Array.size(); i++) {
                        p_row_mode01 = mode01_PID_01Array.get(i);
                        int cnt = i + 1;
                        String dataInfo = "("+cnt+") " + p_row_mode01.get(2)+" - "+ p_row_mode01.get(1)+" - "+p_row_mode01.get(0) ;
                        lw (  "INFO      : "+dataInfo  ) ;
                        monListCloud.add( dataInfo );
                        if (  p_row_mode01.get(2).equals("1")  ) {
                            countMonFail++;
                        }
                        RowItem items = new RowItem(p_row_mode01.get(2), p_row_mode01.get(1), p_row_mode01.get(0), "A");
                        rowItems.add(items);
                    }
                    c.wMon( mMonitorType , monRawResponse , monListCloud  );
                } catch (Exception e) {
                    didItFailed= true;
                    lw("ERROR ERROR : DiagnosticScan.executeConnAsyncTask.doInBackground = " + e);
                }
            } else {
                didItFailed= true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (didItFailed) {
                toastIt(  getString( R.string.connProblem )  );
            } else {
                toastIt(getString(R.string.incomp_monitor) + " : " + countMonFail);
            }
            adapter = new CustomFragAdapter(context, rowItems);
            try {
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        choosenRow = rowItems.get(position);
                        goToDescMonitors(choosenRow.getCode());
                    }
                });
            }catch(Exception  e) {
                lw (  "ERROR ERROR : MonitorScreen.onPostExecute Error = "+e );
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void goToDescMonitors (String vMonTitle) {
        lw (  "PROGRAM   : MonitorsScreen.goToDescMonitors" );
        final String monTitle = vMonTitle;
        try {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                Intent intentMain = new Intent(context, MonResultsMonitorDesc.class);
                intentMain.putExtra("monTitle", monTitle);
                startActivityForResult(intentMain, 1);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            inMyThread.start();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : MonitorScreen.goToDescMonitors Error = "+e );
        }
    }

    public void goMonitorManual (String vIgnitionType) {
        lw (  "PROGRAM   : MonitorsScreen.goMonitorManual" );
        final String ignitionType = vIgnitionType;
        try {
            Thread inMyThread;
            inMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(context, MonitorManual.class);
                    intentMain.putExtra("ignitionType", ignitionType);
                    startActivityForResult(intentMain, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            inMyThread.start();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : MonitorsScreen.goMonitorManual Error = "+e );
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            lw (  "PROGRAM   : MonitorsScreen.onServiceConnected" );
            final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;

            mService = binder.getService();
            mBound = true;

            if (fisrtTimein) {
                fisrtTimein = false;
                new executeConnAsyncTask().execute("");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            lw ("INFO      : Method is onServiceDisconnected");
            mBound = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    public class RowItem {

        private String vflag;
        private String title1;
        private String title2;
        private String code;

        public RowItem(String title1, String title2, String code, String vflag) {
            this.title1 = title1;
            this.title2 = title2;
            this.code = code;
            this.vflag = vflag;
        }

        public String getTitle1() {
            return title1;
        }
        public String getTitle2() {
            return title2;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }

    public class CustomFragAdapter extends BaseAdapter{
        Context context;
        List<RowItem> rowItem;

        CustomFragAdapter(Context context, List<RowItem> rowItem) {
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
                convertView = mInflater.inflate(R.layout.tabitemmonitorsnew, null);
            }

            TextView txtTitle1 = (TextView) convertView.findViewById(R.id.text);
            TextView txtTitle2 = (TextView) convertView.findViewById(R.id.text1);
            ImageView image2 = (ImageView) convertView.findViewById(R.id.image2);
            ImageView contmon = (ImageView) convertView.findViewById(R.id.contmon);

            RowItem row_pos = rowItem.get(position);
            txtTitle1.setText(   getMonitorName ( row_pos.getCode() )  );

            if (row_pos.getTitle2().equals("1")) {
                txtTitle2.setText( getString( R.string.supported )  );
            } else {
                txtTitle2.setText( getString( R.string.unsupported )  );
            }

            if (row_pos.getCode().equals( "Misfire" ))  {
                contmon.setVisibility(View.VISIBLE);
            } else if (row_pos.getCode().equals( "Fuel System" ))  {
                contmon.setVisibility(View.VISIBLE);
            } else if (row_pos.getCode().equals( "Components" ))  {
                contmon.setVisibility(View.VISIBLE);
            }

            if (row_pos.getTitle1().equals("1")) {
                image2.setImageResource(R.drawable.badd1);
                if (row_pos.getTitle2().equals("0")) image2.setImageResource(R.drawable.warningg1);
            } else {
                image2.setImageResource(R.drawable.goodd1);
                if (row_pos.getTitle2().equals("0")) image2.setImageResource(R.drawable.warningg1);
            }

            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
            animation.setStartOffset(position * 70);
            convertView.startAnimation(animation);
            return convertView;
        }
    }

    public String getMonitorName( String vName ) {
        if ( vName.equals( "Misfire"  ) ) {
            return getString( R.string.misfire );
        } else if ( vName.equals( "Fuel System"  ) ) {
            return getString( R.string.fuel_system );
        } else if ( vName.equals( "Components"  ) ) {
            return getString( R.string.components );
        } else if ( vName.equals( "Catalyst"  ) ) {
            return getString( R.string.catalyst );
        } else if ( vName.equals( "NMHC Catalyst"  ) ) {
            return getString( R.string.nmhc_catalyst );
        } else if ( vName.equals( "Heated Catalyst"  ) ) {
            return getString( R.string.heated_catalyst );
        } else if ( vName.equals( "NOx/SCR Monitor"  ) ) {
            return getString( R.string.nox_scr_monitors );
        } else if ( vName.equals( "Evaporative System"  ) ) {
            return getString( R.string.evaporative_system );
        } else if ( vName.equals( "Secondary Air System"  ) ) {
            return getString( R.string.sec_air_system );
        } else if ( vName.equals( "Boost Pressure"  ) ) {
            return getString( R.string.boost_pressure );
        } else if ( vName.equals( "A/C Refrigerant"  ) ) {
            return getString( R.string.ac_refigerant );
        } else if ( vName.equals( "Oxygen Sensor"  ) ) {
            return getString( R.string.oxy_sensor );
        } else if ( vName.equals( "Exhaust Gas Sensor"  ) ) {
            return getString( R.string.exhaust_gas );
        } else if ( vName.equals( "Components"  ) ) {
            return getString( R.string.components );
        } else if ( vName.equals( "Oxygen Sensor Heater"  ) ) {
            return getString( R.string.oxy_sensor_heater );
        } else if ( vName.equals( "PM Filter Monitoring"  ) ) {
            return getString( R.string.pm_filter_monitoring );
        } else if ( vName.equals( "EGR System"  ) ) {
            return getString( R.string.egr_system );
        } else if ( vName.equals( "EGR and/or VVT System"  ) ) {
            return getString( R.string.egr_vvt );
        } else {
            return "No Data";
        }
    }

}
