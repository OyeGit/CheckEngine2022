package com.velexsoft.checkengine;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.velexsoft.checkengine.modess.VehicleIdentificationNumber;
import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.vin.VIN;

import java.util.ArrayList;

import static com.velexsoft.checkengine.R.string.scan_dtc;

public class MainMenuActivity extends ConnActivityParent {

    final Context context = this;
    BluetoothService mService;
    boolean mBound = false;
    static boolean isitfirstTime = true;
    SharedPreferences sharedPreferences;

    public static String[] prgmNameList = {
            "scan_dtc", "monitors","dashboard" ,
            "vehicleparam" , "testresults" ,  "vehparam"
    };

    private int[] prgmImages = {
            R.drawable.scandtc,
            R.drawable.monitors, R.drawable.dashboard,
            R.drawable.vehicleparam   , R.drawable.statistics  ,  R.drawable.testresult ,
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_menu;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "MAINMENUSC: " + msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadTitle ( getString( R.string.main_menu)  );
        l.fileName = ConnActivityParent.logFileName;
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Main Menu Screen Started");
        lw (  "-------------------------------------------------------------------------------");
        loadVehInfo();
    }

    public void loadVehInfo() {

        if (isitfirstTime) {
            isitfirstTime=false;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(400);
                        displayVehInfo();
                    } catch (Exception e) {
                        System.out.println(" ");
                        lw("ERROR ERROR : MainMenuActivity.display0. Error1 = " + e);
                    }
                }
            });
            myThread.start();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Thread inMyThread;
        inMyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setGridListMainMenu();
                } catch( Exception e ) {
                    lw (  "ERROR     : MainMenuActivity.onPostCreate.setGridListMainMenu => "+e );
                }
            }
        });
        inMyThread.start();
    }

    @Override
    protected void onStart() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            super.onStart();

            if (!mBound) {
                System.out.println("About to BIND Service");
                Intent intent = new Intent(this, BluetoothService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        } catch ( Exception e ) {
            lw (  "ERROR     : Error in onStart program = "+ e );
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
            System.out.println("Completed onStop");
        } catch ( Exception e ) {
            lw (  "ERROR     : Error in onStop program = "+ e );
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            lw (  "PROGRAM   : MainMenuActivity.onServiceConnected");
            final BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            System.out.println ("Method is Connected!!!");
            lw (  "PROGRAM   : MainMenuActivity.onServiceConnected Connected...[Success]");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            lw (  "PROGRAM   : MainMenuActivity.onServiceDisconnected");
            mBound = false;
        }
    };

    static int connMessage = 0;

    public void ConnectedToast() {

        if (connMessage == 0) {
            String message = getString(R.string.service_connected) + " [" + currProtocol.trim() + "] - " + currProtocolGroup.trim() + " " + currVinNumber;
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            connMessage++;
        }
    }

    public void setGridListMainMenu () {
        lw ( "PROGRAM   : MainMenuActivity.setGridListMainMenu" );
        try {
            GridView gridview = (GridView) findViewById(R.id.gridView);

            gridview.setAdapter(new ImageAdapter(this, prgmNameList, prgmImages));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (position == 0) {
                    System.out.println("Diagnostics");
                    display0(context);
                } else if (position == 1) {
                    System.out.println("Monitors");
                    display1(context);
                } else if (position == 2) {
                    System.out.println("Parameters");
                    display2(context);
                } else if (position == 3) {
                    System.out.println("Test Results");
                    display3(context);
                } else if (position == 4) {
                    System.out.println("Dashboard");
                    display4(context);
                } else if (position == 5) {
                    System.out.println("blog");
                    display5(context);
                }
                }
            });
            int size = gridview.getChildCount();
        }catch (Exception e) {
            lw (  "ERROR     : MainMenuActivity.setGridListMainMenu failed with error => "+e );
        }
        lw(  "INFO      : MainMenuActivity.setGridListMainMenu completed [Success]" );
    }

    public class ImageAdapter extends BaseAdapter {
        String[] result;
        Context context;
        int[] imageId;
        private LayoutInflater inflater = null;

        public ImageAdapter(MainMenuActivity mainActivity, String[] prgmNameList1, int[] prgmImages1) {

            result = prgmNameList1;
            context = mainActivity;
            imageId = prgmImages1;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return result.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public class Holder {
            TextView tv;
            ImageView img;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.program_list, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
            String menuLabel = result[position];
            menuLabel = interpretLabels ( menuLabel );
            holder.tv.setText( menuLabel );
            holder.img.setImageResource(imageId[position]);
            return rowView;
        }
    }

    public String interpretLabels ( String inLabel ) {
        if  ( inLabel.equals("scan_dtc" ) ) {
            return getString(R.string.scan_dtc);
        } else if ( inLabel.equals("monitors" ) ) {
            return getString(R.string.monitors);
        }  else if ( inLabel.equals("dashboard" ) ) {
            return getString(R.string.dashboard);
        }  else if ( inLabel.equals("parameters" ) ) {
            return getString(R.string.parameters);
        }  else if ( inLabel.equals("testresults" ) ) {
            return getString(R.string.testresults);
        }  else if ( inLabel.equals("blog" ) ) {
            return getString(R.string.blog);
        }  else if ( inLabel.equals("inuseperf" ) ) {
            return getString(R.string.inuseperf);
        }  else if ( inLabel.equals("freezeframe" ) ) {
            return getString(R.string.freeze_frame);
        } else if ( inLabel.equals("vehparam" ) ) {
            return getString(R.string.vehparam);
        } else if ( inLabel.equals("freezeframe" ) ) {
            return getString(R.string.freeze_frame);
        } else if ( inLabel.equals("vehicleparam" ) ) {
            return getString(R.string.settingslabel);
        } else return null;
    }

    public void display0(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display0");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent(locContext, DiagnosticScan.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);

                    } catch (Exception e) {
                        System.out.println(" ");
                        lw("ERROR ERROR : MainMenuActivity.display0. Error1 = " + e);
                    }
                }
            });
            myThread.start();
            lw ( "INFO      : MainMenuActivity.display0 completed [Success]");
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display0. Error2 = " + e);
        }
    }

    public void display1(Context context) {
        lw ( "PROGRAM   : MainMenuActivity.display1");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent(locContext, MonitorsScreen.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.display1. Error1 = " + e);
                    }
                }
            });
            myThread.start();
            lw ( "INFO      : MainMenuActivity.display1 completed [Success]");
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display1. Error2 = " + e);
        }
    }

    public void display2(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display2");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //mService.predashSettings();
                    Intent intent = new Intent(locContext, DashboardScreen.class);
                    try {
                        if (lineMode.equals("offline")) {
                            // dispOfflineMode();
                        } else {
                            Thread inMyThread;
                            inMyThread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    mService.vehicleDashboardService();
                                    /*
                                    if (currProtocolGroup.contains("ISO") || currProtocolGroup.contains("1850")) {
                                        mService.vehicleDashboardService();
                                    } else {
                                        //mService.predashSettings();
                                        mService.vehicleDashboardServiceCAN();
                                    }
                                    */
                                }
                            });
                            inMyThread.start();
                        }
                            startActivityForResult(intent, 1);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);

                        lw ( "INFO      : MainMenuActivity.display2 completed [Success]");
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.display2. Error1 = " + e);
                    }
                }
            });
            myThread.start();

        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display2. Error2 = " + e);
        }
    }

    public void display3 (Context context) {
        lw (  "PROGRAM   : MainMenuActivity.displaySettings");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent(locContext, SettingsActivity.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        lw ( "INFO      : MainMenuActivity.displaySettings completed [Success]");
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.displaySettings. Error1 = " + e);
                    }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.displaySettings. Error1 = " + e);
        }
    }
/*
    public void display3(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display3");y
        try {

            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    Intent intent = new Intent(locContext, DriveCycleTripScreen.class);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);

                    lw ( "INFO      : MainMenuActivity.display3 completed [Success]");
                } catch (Exception e) {
                    lw("ERROR ERROR : MainMenuActivity.display3. Error1 = " + e);
                }
                }
            });
            myThread.start();

        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display3. Error2 = " + e);
        }
    }
*/
    public void display4(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display3");

        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    Intent intent = new Intent(locContext, CompResults.class);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    lw ( "INFO      : MainMenuActivity.display4 completed [Success]");
                } catch (Exception e) {
                    lw("ERROR ERROR : MainMenuActivity.display4. Error1 = " + e);
                }
                }
            });
            myThread.start();

        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display4. Error2 = " + e);
        }
    }

    public void display5(Context context) {

        lw (  "PROGRAM   : MainMenuActivity.display7");
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
                        //System.out.println("Before vehicle parameters");
                        Intent intent = new Intent(locContext, VehicleParameters.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);

                        lw ( "INFO      : MainMenuActivity.display5 completed [Success]");
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.display5. Error1 = " + e);
                    }

                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display5. Error2 = " + e);
        }
    }
/*
    public void display6(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display6");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(locContext, FreezeFrameActivity.class);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);

                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display6. Error2 = " + e);
        }
    }

    public void display7(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display7");
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
                        //System.out.println("Before vehicle parameters");
                        Intent intent = new Intent(locContext, VehicleParameters.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);

                        lw ( "INFO      : MainMenuActivity.display7 completed [Success]");
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.display7. Error1 = " + e);
                    }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display7. Error2 = " + e);
        }
    }

    public void display8(Context context) {
        lw (  "PROGRAM   : MainMenuActivity.display7");
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
                            mService.readPerfTrack();
                        }
                    });
                    inMyThread.start();

                    //System.out.println("Before vehicle parameters");
                    Intent intent = new Intent(locContext, InusePerformance.class);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);

                    lw ( "INFO      : MainMenuActivity.display7 completed [Success]");
                } catch (Exception e) {
                    lw("ERROR ERROR : MainMenuActivity.display7. Error1 = " + e);
                }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.display7. Error2 = " + e);
        }
    }
    */

    public void displaySettings (Context context) {
        lw (  "PROGRAM   : MainMenuActivity.displaySettings");
        try {
            final Context locContext = context;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    Intent intent = new Intent(locContext, SettingsActivity.class);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    lw ( "INFO      : MainMenuActivity.displaySettings completed [Success]");
                } catch (Exception e) {
                    lw("ERROR ERROR : MainMenuActivity.displaySettings. Error1 = " + e);
                }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.displaySettings. Error1 = " + e);
        }
    }

    @Override
    public void onBackPressed() {

        boolean confirmExit =  sharedPreferences.getBoolean("confirm_exit", true);

        if (confirmExit) {
            disconnFromSensor();
        } else {
            toastIt("Disconnecting..." );
            gobackNow();
        }
    }

    @Override
    public void onResume() {
        lw (  "PROGRAM   : MainMenuActivity.onResume");
        super.onResume();

        invalidateOptionsMenu();

    }

    public void disconnFromSensor() {
        lw (  "PROGRAM   : MainMenuActivity.disconnFromSensor");
        try {
            /*
            MessageDialogs myDiag = new MessageDialogs();
            myDiag.setCancelable(false);
            FragmentManager fm = getFragmentManager();
            myDiag.show(fm, "");
            */
            velexMessageNow(context, "Do you want to disconnect from Sensor?");
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.disconnFromSensor. Error = " + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        lw (  "PROGRAM   : MainMenuActivity.onCreateOptionsMenu");
        try {
            getMenuInflater().inflate(R.menu.menu_main_menu_screen, menu);

            boolean allowSupportEmail =  sharedPreferences.getBoolean("allow_support_email", false);
            boolean showSocialMedia =  sharedPreferences.getBoolean("allow_social_media", true);

            if (allowSupportEmail) {
                MenuItem sendEmail = menu.findItem(R.id.sendMail);
                sendEmail.setVisible(true);
            }

            if (showSocialMedia) {
                MenuItem bblog = menu.findItem(R.id.check_engine_blog);
                bblog.setVisible(true);
            }


        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.onCreateOptionsMenu. Error = " + e);
        }
        return true;
    }

    public void displayVehInfo () {

        lw (  "PROGRAM   : MainMenuActivity.displayVehInfo");

        try {
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String value = getMode09Number();
                    System.out.println ("getMode09Number value = "+value +", count = "+value.length() );

                    if (getMode09Number().equals("X")) {

                    } else if ( value.length()==17 ) {

                        MessageDialogsVehInfo myDiag = new MessageDialogsVehInfo();
                        myDiag.setCancelable(false);
                        FragmentManager fm = getFragmentManager();
                        myDiag.show(fm, "");
                    } else {
                        // toastIt("vehicle information not available");
                    }
                }
            });
            myThread.start();

        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.onOptionsItemSelected. Error = " + e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lw (  "PROGRAM   : MainMenuActivity.stopService");
        try {
            int id = item.getItemId();
            if (id == R.id.sendMail) {
                ContactUs();
                return true;
            } else if (id == R.id.actionReport) {
                item.setChecked(!item.isChecked());
                SharedPreferences settings = getSharedPreferences(ConnActivityParent.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("actionReport", item.isChecked());
                editor.commit();
                return true;
            } else if (id == R.id.action_settings) {
                displaySettings(context);
                return true;
            } else if (id == R.id.check_engine_blog) {

                if ( isNetworkAvailable() ) {

                    velexMessageNowDataRates(context, "Data charges may apply... Do you want to continue?");
                } else {
                    toastIt("Internet access is NOT available..." );
                }

                return true;
            } else if (id == R.id.action_exit) {
                try {
                    disconnFromSensor();
                } catch ( Exception e ) {
                    lw("ERROR ERROR : MainMenuActivity.onOptionsItemSelected. Error = " + e);
                }
                return true;
            }
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.onOptionsItemSelected. Error = " + e);
        }
        return super.onOptionsItemSelected(item);
    }


    public void ContactUs( ) {
        lw (  "PROGRAM   : MainMenuActivity.ContactUs");
        try {
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                            Intent intent = new Intent(context, ContactUsActivity.class);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    } catch (Exception e) {
                        lw("ERROR ERROR : MainMenuActivity.ContactUs. Error1 = " + e);
                    }
                }
            });
            myThread.start();
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.ContactUs. Error2 = " + e);
        }
    }

    public void onUserSelectValueData(String selectedValue) {
        lw (  "PROGRAM   : MainMenuActivity.onUserSelectValue");
        if (selectedValue.equals("ok")) {
            Intent intent = new Intent(context, WebViewBlogEx.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in_dialog, R.anim.fade_out_dialog);
        }
    }

    public void velexMessageNow( Context pcontext, String exitMsg){
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
                    onUserSelectValue("ok");
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.velexMessageNow Error = "+e );
        }
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
                    onUserSelectValueData("ok");
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.velexMessageNow Error = "+e );
        }
    }

    public static class MessageDialogsDataRate extends DialogFragment {

        int resultCode = 1;
        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate( R.layout.messagedialog, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            //getDialog().getWindow().setWindowAnimations(
                //    R.style.dialog_animation_fade);
            //Typeface bwFonts = Typeface.createFromAsset(getActivity().getAssets(), "fonts/broadway.ttf");
            TextView tv = (TextView) v.findViewById(R.id.textTitle);
            tv.setText( getString(R.string.caution) );
            tv.setTextColor( Color.BLACK );

            TextView txView = (TextView) v.findViewById(R.id.textView);
            //String msg =  getString( R.string.do_you_disconnect );
            String msg =  "Data rates may apply. Do you want to continue?";

            txView.setText( msg );
            Button Cancel = (Button) v.findViewById(R.id.Cancel);
            Cancel.setText( getString( R.string.cancel  ) );
            Cancel.setFocusableInTouchMode(true);
            Cancel.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            dismiss();
                        }
                    }
            );
            Button ok = (Button) v.findViewById(R.id.ok);
            ok.setText( getString( R.string.ok  ) );
            ok.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            MainMenuActivity callingActivity = (MainMenuActivity) getActivity();
                            callingActivity.onUserSelectValueData("ok");
                            dismiss();
                        }
                    }
            );
            return v;
        }
    }



    public static class MessageDialogs extends DialogFragment {

        int resultCode = 1;
        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate( R.layout.messagedialog, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setWindowAnimations(
                    R.style.dialog_animation_fade);
            TextView tv = (TextView) v.findViewById(R.id.textTitle);
            tv.setText( getString(R.string.caution) );
            tv.setTextColor( Color.BLACK );

            TextView txView = (TextView) v.findViewById(R.id.textView);
            String msg =  getString( R.string.do_you_disconnect );

            txView.setText( msg );
            Button Cancel = (Button) v.findViewById(R.id.Cancel);
            Cancel.setText( getString( R.string.cancel  ) );
            Cancel.setFocusableInTouchMode(true);
            Cancel.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            dismiss();
                        }
                    }
            );
            Button ok = (Button) v.findViewById(R.id.ok);
            ok.setText( getString( R.string.ok  ) );
            ok.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            MainMenuActivity callingActivity = (MainMenuActivity) getActivity();
                            callingActivity.onUserSelectValue("ok");
                        }
                    }
            );
            return v;
        }
    }

    public static class MessageDialogsVehInfo extends DialogFragment {

        int resultCode = 1;
        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate( R.layout.messagevehinfo, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setWindowAnimations(
                    R.style.dialog_animation_fade_vehinfo);

            Typeface bwFonts = Typeface.createFromAsset(getActivity().getAssets(), "fonts/broadway.ttf");
            TextView tv = (TextView) v.findViewById(R.id.textTitle);
            tv.setText( "Vehicle Information" );
            tv.setTextColor( Color.BLACK );

            TextView txView = (TextView) v.findViewById(R.id.textView);
            String msg = "Vehicle Information";

            String vinNum = getMode09Number();

            VIN viDecode = new VIN( vinNum );

            String [] vinArray = new String[]{
                    vinNum, viDecode.getWMIData(), viDecode.getYear()+""
            };

            ListView listView = v.findViewById(R.id.list_view);
            MyArrayAdapter adapter = new MyArrayAdapter(v.getContext(), R.layout.tabitemvehinfo, vinArray);

            listView.setAdapter(adapter);

            Button ok = (Button) v.findViewById(R.id.ok);
            ok.setText( getString( R.string.ok  ) );
            ok.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                           dismiss();
                        }
                    }
            );
            return v;
        }
    }

    public static class MyArrayAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private String[] mClasses;
        private int[] mDescriptionIds;

        public MyArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate( R.layout.tabitemvehinfo, null);
            }
            ((TextView) view.findViewById(R.id.text2)).setText(mClasses[position]);
            ((TextView) view.findViewById(R.id.text1)).setText("Vehicle Identification Number"  );

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            mDescriptionIds = descriptionIds;
        }
    }


    public static String getMode09Number () {
/*
        try {
            ArrayList<ArrayList<String>> mode09Array = new ArrayList();
            ArrayList<String> vinData = arrayListVehInfo.get(0);
            VehicleIdentificationNumber vehicleIdentificationNumber = new VehicleIdentificationNumber( vinData.get(1) );
            String vVin = vehicleIdentificationNumber.getResponse();
            System.out.println("MSG : Vehicle Identification HEX = " + vVin + "  ***********");
            if ( vVin.length() == 17 ) {
                currVinNumber = vVin;
                System.out.println("MSG       : Vehicle Identification HEX = " + vVin + " ************");
                ArrayList<String> row = new ArrayList();
                row.add("Vehicle Identification");
                row.add(vVin);
                mode09Array.add(row);
                return vVin;
            } else {
                l.w("WARNING - VIN Failed verification");
                System.out.println("ERROR - VIN Failed verification");
                return "X";
            }
        } catch (Exception e) {
            return "X";
        }
        */
        return "X";
    }



    public void onUserSelectValue(String selectedValue) {
        lw (  "PROGRAM   : MainMenuActivity.onUserSelectValue");
        if (selectedValue.equals("ok")) {

            gobackNow();
        }
    }

    public void stopService() {
        lw (  "PROGRAM   : MainMenuActivity.stopService");
        stopService(new Intent(getBaseContext(), BluetoothService.class));
    }


    public void gobackNow() {
        lw (  "PROGRAM   : MainMenuActivity.gobackNow");
        stopService();
        goToConnectActivity(context);
    }

    public void goToConnectActivity(Context vcontext) {
        lw (  "PROGRAM   : MainMenuActivity.goToConnectActivity");
        try {
            final Context locContext = vcontext;
            Thread myThread;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intentMain = new Intent(locContext, ConnectionScreen.class);
                    intentMain.setFlags(intentMain.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    finish();
                }
            });
            myThread.start();
            lw (  "INFO      : Completed MainMenuActivity.goToConnectActivity [Success]");
        } catch ( Exception e ) {
            lw("ERROR ERROR : MainMenuActivity.goToConnectActivity. Error = " + e);
        }
    }
}
