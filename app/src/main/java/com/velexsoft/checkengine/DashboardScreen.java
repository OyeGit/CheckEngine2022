package com.velexsoft.checkengine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.velexsoft.checkengine.servicess.BluetoothService;
import com.velexsoft.checkengine.speedviewlib.AwesomeSpeedometer;
import com.velexsoft.checkengine.speedviewlib.AwesomeSpeedometerSmall;
import com.velexsoft.checkengine.speedviewlib.RaySpeedometer;
import com.velexsoft.checkengine.speedviewlib.SpeedView;
import com.velexsoft.checkengine.speedviewlib.components.note.Note;
import com.velexsoft.checkengine.speedviewlib.components.note.TextNote;
import com.velexsoft.checkengine.utilities.ConvertValues;

import java.text.DecimalFormat;

public class DashboardScreen extends ConnActivityParent {
    final Context context = this;

    private BroadcastReceiver mReceiver;
    AwesomeSpeedometer awesomeCover;
    AwesomeSpeedometerSmall awesomeSpeedometer1;
    AwesomeSpeedometerSmall awesomeSpeedometer2;
    AwesomeSpeedometerSmall awesomeSpeedometer3;
    AwesomeSpeedometerSmall awesomeSpeedometer4;
    SpeedView awesomeSpeedometer;
    RaySpeedometer raySpeedometer1;
    RaySpeedometer raySpeedometer2;
    BluetoothService mService;
    int logCounter = 0;
    int maxLogCounter = 10;
    TextNote note;
    TextNote noteGas;
    TextNote noteTemp;
    TextNote noteSpeed;
    TextNote noteMaf;
    TextNote noteRPM;
    PopupWindow popUp;
    FrameLayout layout;
    double d256 ;
    double d4 ;
    static boolean labelSet;

    String fuelUnitLabel = "Fuel ";
    final String DEGREE  = "\u00b0";
    final String MDot = "\u1E41";
    DecimalFormat df = new DecimalFormat("#.##");
    String unitMeasure ;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "DASHBOARDS: " + msg);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_dashboard_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        unitMeasure =  sharedPreferences.getString("unit_measure", "Metric");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        l.fileName = ConnActivityParent.logFileName;
        labelSet = false;
    }

    public void updateFuelLevel(String fuelLevelResponse0) {
        try {
            Integer decResponseFuelLevel = ConvertValues.HexToInteger ( fuelLevelResponse0 );
            double fuelLevel = v100_255 * decResponseFuelLevel ;
            awesomeSpeedometer4.speedTo( (float)fuelLevel );
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void updateCoolantTemp(String airTempResponse0) {
        try {
            Integer decResponseAirTemp = ConvertValues.HexToInteger ( airTempResponse0 );
            float airTemp = decResponseAirTemp - 40 ;
            if (unitMeasure.contains("Imperial")) {
                float answer = (float) (( (float)airTemp * 1.8 ) + 32 );
                awesomeSpeedometer3.speedTo( answer );
            }else {
                awesomeSpeedometer3.speedTo( (float)airTemp );
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }

    public void updateSpeed(String hexResponse0) {
        try {
            Integer decResponse010D = ConvertValues.HexToInteger ( hexResponse0 );
            if (unitMeasure.contains("Imperial")) {
                awesomeSpeedometer.speedTo(decResponse010D*(float)0.621371, 2000);
            }else {
                if (decResponse010D < 10 ) {
                    awesomeSpeedometer.speedTo(decResponse010D, 3000);
                } else if (  decResponse010D <= 20 ) {
                    awesomeSpeedometer.speedTo(decResponse010D + 1, 3000);
                } else if (  decResponse010D > 20 ) {
                    awesomeSpeedometer.speedTo(decResponse010D + 2 , 3000);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void updateSpeed00() {
        try {
            awesomeSpeedometer.speedTo(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        popUp = new PopupWindow(this);
        layout = new FrameLayout(this);

        String mafUnitLabel = getString( R.string.maf_label );
        String tempUnitLabel = getString( R.string.temp_label );
        d256 = 256.0;
        d4 = 4.0;

        lw ( "header1 = "+header1 );
        lw ( "header2 = "+header2 );
        lw ( "header3 = "+header3 );

        awesomeCover = (AwesomeSpeedometer) findViewById(R.id.awesomeSpeedometer);
        raySpeedometer1= (RaySpeedometer) findViewById(R.id.raySpeedometer1);
        raySpeedometer2= (RaySpeedometer) findViewById(R.id.raySpeedometer2);

        awesomeCover.setMarkColor(Color.DKGRAY);
        awesomeCover.setBackgroundCircleColor(Color.WHITE);
        awesomeCover.setStartEndDegree( 135 , 405);


        raySpeedometer1.setMinMaxSpeed(0,2383);
        raySpeedometer1.setStartEndDegree( 135 , 405);
        raySpeedometer1.setSpeedTextColor(Color.WHITE);
        raySpeedometer1.setUnit("");
        raySpeedometer1.setIndicatorWidth(0.5f);
        raySpeedometer1.setMarkColor(Color.WHITE);
        raySpeedometer1.setRayColor(Color.TRANSPARENT);


        raySpeedometer2.setMinMaxSpeed(0,180);
        raySpeedometer2.setStartEndDegree( 135 , 405);
        raySpeedometer2.setSpeedTextColor(Color.WHITE);
        raySpeedometer2.setUnit("");
        raySpeedometer2.setIndicatorWidth(0.01f);
        raySpeedometer2.setMarkColor(Color.WHITE);
        raySpeedometer2.setRayColor(Color.TRANSPARENT);


        awesomeSpeedometer = (SpeedView) findViewById(R.id.speedView);
        awesomeSpeedometer.setBackgroundCircleColor(Color.WHITE);
        //awesomeSpeedometer.setSpeedTextSize(180f);           // Works
        awesomeSpeedometer.setSpeedTextColor(Color.BLACK);
        //awesomeSpeedometer.setUnitTextSize(80f);             // Works
        awesomeSpeedometer.setWithTremble(false);




        if (unitMeasure.contains("Imperial")) {
            awesomeSpeedometer.setMinMaxSpeed(0,186);
            awesomeCover.setMinMaxSpeed(0,186);
            awesomeCover.setStartEndDegree( 135 , 405);
            awesomeSpeedometer.setStartEndDegree( 135 , 405);
            awesomeSpeedometer.setUnit("mi/h");
        } else {
            awesomeSpeedometer.setMinMaxSpeed(0,300);
            awesomeCover.setMinMaxSpeed(0,300);
            awesomeCover.setStartEndDegree( 135 , 405);
            awesomeSpeedometer.setStartEndDegree( 135 , 405);
            awesomeSpeedometer.setUnit("km/h");
        }

        awesomeSpeedometer.setUnitTextColor(Color.DKGRAY);
        awesomeSpeedometer.setTextColor(Color.DKGRAY);
        awesomeSpeedometer.setClickable(true);
        //awesomeSpeedometer.setSpeedTextPadding(20f);
        awesomeSpeedometer.setMarkColor(Color.DKGRAY);
        awesomeSpeedometer.setTickRotation(true);
        awesomeSpeedometer.setDecelerate(1.6f);
        awesomeSpeedometer.setAccelerate(1.0f);
        //awesomeSpeedometer.setTrembleDuration(0);
        //awesomeSpeedometer.setWithTremble(true);

        /*
        Thread inMyThread;
        inMyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                noteSpeed = new TextNote(getApplicationContext(), "Speed to high")
                        .setPosition(Note.Position.CenterIndicator)// position of Note.
                        .setAlign(Note.Align.Top)// Note Align.
                        .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))// style, or font.
                        .setBackgroundColor(Color.LTGRAY)// change dialog color.
                        .setCornersRound(16f)// dialog's rectangle Corners Round.
                        .setPadding(2f,10f,2f,40f)
                        .setTextColor(Color.RED)
                        .setTextSize(awesomeSpeedometer.dpTOpx(22f));

                note = new TextNote(getApplicationContext(), "Speed")
                        .setPosition(Note.Position.CenterSpeedometer)// position of Note.
                        .setAlign(Note.Align.Top)// Note Align.
                        .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))// style, or font.
                        .setBackgroundColor(Color.TRANSPARENT)// change dialog color.
                        .setCornersRound(16f)// dialog's rectangle Corners Round.
                        .setPadding(2f,10f,2f,40f)
                        .setTextSize(awesomeSpeedometer.dpTOpx(32f));

                noteGas = new TextNote(getApplicationContext(), "Fuel Tank")
                        .setPosition(Note.Position.CenterSpeedometer)// position of Note.
                        .setAlign(Note.Align.Top)// Note Align.
                        .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))// style, or font.
                        .setBackgroundColor( Color.TRANSPARENT )// change dialog color.
                        .setCornersRound(10f)// dialog's rectangle Corners Round.
                        .setPadding(2f,20f,2f,4f)
                        .setTextSize(awesomeSpeedometer.dpTOpx(16f));

                noteTemp = new TextNote(getApplicationContext(), "Temperature")
                        .setPosition(Note.Position.CenterSpeedometer)// position of Note.
                        .setAlign(Note.Align.Top)// Note Align.
                        .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))// style, or font.
                        .setBackgroundColor( Color.TRANSPARENT )// change dialog color.
                        .setCornersRound(10f)// dialog's rectangle Corners Round.
                        .setPadding(2f,40f,2f,4f)
                        .setTextSize(awesomeSpeedometer.dpTOpx(16f));

                noteMaf = new TextNote(getApplicationContext(), "MAF")
                        .setPosition(Note.Position.CenterSpeedometer)// position of Note.
                        .setAlign(Note.Align.Top)// Note Align.
                        .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))// style, or font.
                        .setBackgroundColor( Color.TRANSPARENT )// change dialog color.
                        .setCornersRound(10f)// dialog's rectangle Corners Round.
                        .setPadding(2f,20f,2f,4f)
                        .setTextSize(awesomeSpeedometer.dpTOpx(16f));

                noteRPM = new TextNote(getApplicationContext(), "RPM")
                        .setPosition(Note.Position.CenterSpeedometer)// position of Note.
                        .setAlign(Note.Align.Top)// Note Align.
                        .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))// style, or font.
                        .setBackgroundColor( Color.TRANSPARENT )// change dialog color.
                        .setCornersRound(10f)// dialog's rectangle Corners Round.
                        .setPadding(2f,40f,2f,4f)
                        .setTextSize(awesomeSpeedometer.dpTOpx(16f));

            }
        });
        inMyThread.start();
        */

        //------------------------------------------------------------------------------------------
        awesomeSpeedometer1 = (AwesomeSpeedometerSmall) findViewById(R.id.awesomeSpeedometer1);
        awesomeSpeedometer1.setUnit("rpm");  //655
        awesomeSpeedometer1.setMinMaxSpeed(0,2383);
        awesomeSpeedometer1.setBackgroundCircleColor(Color.WHITE);
        awesomeSpeedometer1.setBackgroundColor(Color.TRANSPARENT);
        awesomeSpeedometer1.setSpeedTextColor(Color.BLACK);
        awesomeSpeedometer1.setUnitTextColor(Color.DKGRAY);
        awesomeSpeedometer1.setStartEndDegree( 135 , 405);
        //------------------------------------------------------------------------------------------
        awesomeSpeedometer2 = (AwesomeSpeedometerSmall) findViewById(R.id.awesomeSpeedometer2);

        awesomeSpeedometer2.setSpeedTextColor(Color.BLACK);
        awesomeSpeedometer2.setBackgroundCircleColor(Color.WHITE);

        if (unitMeasure.contains("Imperial")) {
            awesomeSpeedometer2.setUnit( MDot );
            awesomeSpeedometer2.setMinMaxSpeed(0,10);
            awesomeSpeedometer2.setStartEndDegree( 135 , 405);
        }else {
            awesomeSpeedometer2.setUnit( mafUnitLabel );
            awesomeSpeedometer2.setMinMaxSpeed(0,180);
            awesomeSpeedometer2.setStartEndDegree( 135 , 405);
        }

        awesomeSpeedometer2.setUnitTextColor(Color.DKGRAY);
        awesomeSpeedometer3 = (AwesomeSpeedometerSmall) findViewById(R.id.awesomeSpeedometer3);
        awesomeSpeedometer3.setBackgroundCircleColor(Color.WHITE);
        tempUnitLabel = "("+DEGREE+"C"+")";
        String tempUnitLabel1 = "("+DEGREE+"F"+")";
        if (unitMeasure.contains("Imperial")) {
            awesomeSpeedometer3.setUnit( tempUnitLabel1);
            awesomeSpeedometer3.setMinMaxSpeed(-40, 419);
            awesomeSpeedometer3.setStartEndDegree( 135 , 405);
        }else {
            awesomeSpeedometer3.setUnit( tempUnitLabel );
            awesomeSpeedometer3.setMinMaxSpeed(-40, 215);
            awesomeSpeedometer3.setStartEndDegree( 135 , 405);

        }
        awesomeSpeedometer3.setUnitUnderSpeedText(false);
        awesomeSpeedometer3.setSpeedTextColor(Color.BLACK);
        awesomeSpeedometer3.setUnitTextColor(Color.DKGRAY);
        awesomeSpeedometer4 = (AwesomeSpeedometerSmall) findViewById(R.id.awesomeSpeedometer4);
        awesomeSpeedometer4.setBackgroundCircleColor(Color.WHITE);
        awesomeSpeedometer4.setSpeedTextColor(Color.DKGRAY);
        awesomeSpeedometer4.setUnitTextColor(Color.DKGRAY);
        awesomeSpeedometer4.setStartEndDegree( 135 , 405 );
        awesomeSpeedometer4.setUnitUnderSpeedText(false);
        awesomeSpeedometer4.setUnit( fuelUnitLabel );
        awesomeSpeedometer4.setAccelerate(0.01f);
        awesomeSpeedometer4.setWithTremble(false);
        awesomeSpeedometer4.setUnitTextSize(awesomeSpeedometer.dpTOpx(16f));
        awesomeSpeedometer4.setUnit("(%)");
    }

    @Override
    public void onResume() {
        super.onResume();

        if ( lineMode.equals("offline") ) {

        } else {

            IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN.checkEngine");
            mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                if (null != intent) { //Null Checking
                    try {
                        Bundle bundleExtras = new Bundle();
                        bundleExtras = intent.getBundleExtra("bundle");
                        String vSpeedHex = bundleExtras.getString("0");
                        String vMAFHex = bundleExtras.getString("1");
                        String vRPM = bundleExtras.getString("2");
                        String vCoolTemp = bundleExtras.getString("4");
                        String vFuelTank = bundleExtras.getString("5");

                        lw ( "vSpeedHex:"+vSpeedHex+", vMAFHex:"+vMAFHex+", vRPM:"+vRPM+", vCoolTemp:"+vCoolTemp+", vFuelTank:"+vFuelTank  );

                        if (vSpeedHex.equals("00")) {
                            updateSpeed00();
                        } else {
                            updateSpeed( vSpeedHex  );
                        }

                        updateMAF(vMAFHex);
                        updateRPM(vRPM);
                        updateCoolantTemp(vCoolTemp);
                        updateFuelLevel(vFuelTank);

                        logCounter++;
                    } catch ( Exception e) {
                        System.out.println("ERROR : Retrieving value in updateRPM 1"+e);
                        e.printStackTrace();
                    }
                }
                }
            };
            this.registerReceiver(mReceiver, intentFilter);
        }
    }

    Integer decFirstByteResponse011F;
    Integer decSecondByteResponse011F;
    double v100_255 =  0.392156 ;
    double decResponse010C_A = 0.0;
    double decResponse010C_B = 0.0;

    public void updateRPM(String ptextViewRPMValue) {

        try {
            decResponse010C_A = ConvertValues.HexToInteger(ptextViewRPMValue.substring(0, 2));
            decResponse010C_B = ConvertValues.HexToInteger(ptextViewRPMValue.substring(2, 4));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double decResponse010C = ( (decResponse010C_A *d256) + decResponse010C_B )/d4 ;
            awesomeSpeedometer1.speedTo( (float)decResponse010C );
            raySpeedometer1.speedTo( (float)decResponse010C );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMAF(String mafResponse0) {
        try {

            decFirstByteResponse011F = ConvertValues.HexToInteger(mafResponse0.substring(0, 2));
            decSecondByteResponse011F = ConvertValues.HexToInteger(mafResponse0.substring(2, 4));
            double tempint = ((decFirstByteResponse011F*256)+decSecondByteResponse011F)/100;

            if (unitMeasure.contains("Imperial")) {
                float finValue =  ((float)tempint * (float) 0.00220462);
                awesomeSpeedometer2.speedTo( finValue );
                raySpeedometer2.speedTo( finValue );
            }else {
                awesomeSpeedometer2.speedTo( (float)tempint );
                raySpeedometer2.speedTo( (float)tempint );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( lineMode.equals("offline") ) {
        } else {
            this.unregisterReceiver(this.mReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        mService.releaseSocket();
        //mService.postdashSettings();
        finish();
    }

}
