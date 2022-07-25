package com.velexsoft.checkengine.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.R;
import com.velexsoft.checkengine.modess.InterpreteMode01;

import java.util.ArrayList;

import static com.velexsoft.checkengine.ConnActivityParent.l;
import static com.velexsoft.checkengine.ConnActivityParent.logErrors;

public class IntepretDashValues {

    Context context;
    ArrayList<ArrayList<String>> dashBoardArray;
    SharedPreferences sharedPreferences;

    ArrayList<String> FuelTank = new ArrayList<>();
    ArrayList<String> coolTemp = new ArrayList<>();
    ArrayList<String> engineOilTemp = new ArrayList<>();
    ArrayList<String> inputBattery = new ArrayList<>();

    public static int FuelTankCap;
    public static int CoolTempCap;
    public static int EngineOilTempCap;
    public static double BatteryTempCap ;

    public static String FuelTankCapStg;
    public static String CoolTempCapStg;
    public static String EngineOilTempCapStg;
    public static String BatteryTempCapStg ;
    InterpreteMode01  intMode1;


    public IntepretDashValues ( Context context, ArrayList<ArrayList<String>> dashBoardArray ) {
        this.context = context;
        this.dashBoardArray = dashBoardArray;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        FuelTankCapStg =  sharedPreferences.getString ("fuel_tank_min", context.getString(R.string.fueltankmin)  );
        CoolTempCapStg =  sharedPreferences.getString ("coolant_temp_max",context.getString(R.string.coolanttempmax)  );
        EngineOilTempCapStg =  sharedPreferences.getString ("engine_oil_temp_max", context.getString(R.string.engineoiltempmax)  );
        BatteryTempCapStg =  sharedPreferences.getString("input_battery", context.getString(R.string.inputbattery)  );
        l.fileName = ConnActivityParent.logFileName;
        intMode1 = new InterpreteMode01();
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "INTERDASHV: " + msg);
        }
    }

    public String intepMIL() {

        if (dashBoardArray.get(0).get(1).equals("1")) {
            return "BAD";
        } else {
            return "OK";
        }
    }

    public String getFuelTankGoodBad () {
        FuelTankCap = Integer.parseInt( FuelTankCapStg );
        lw  ( "FuelTankCap = "+FuelTankCap );
        try {
            FuelTank = dashBoardArray.get(1);
            lw ( "FuelTankCap = "+FuelTankCap+" -  FuelTank (HEX) = "+FuelTank );
            String fuelTankUnit = intMode1.P2F( FuelTank.get(1)  );
            double fuelTankdouble = intMode1.P2FINT( FuelTank.get(1)  );
            lw ( "fuelTankUnit = "+fuelTankUnit+" -  fuelTankdouble = "+fuelTankdouble );
            if (   ( FuelTank.get(0).contains("GasTank") ) && ( fuelTankdouble <= FuelTankCap )  ) {
                if (!FuelTank.get(1).equals("0")) {
                    lw ( "fuelTank is Bad");
                    return "BAD";
                } else {
                    lw ( "fuelTank is Good");
                    return "OK";
                }
            } else {
                return "OK";
            }
        } catch ( Exception e ) {
            lw ("WARNING   : DiagnosticScan.setDashBoard.FuelTank = "+e);
            return "";
        }
    }

    public String getCoolTempGoodBad () {

        CoolTempCap = Integer.parseInt( CoolTempCapStg );
        lw ( "CoolTempCap = "+CoolTempCap );

        try{
            coolTemp = dashBoardArray.get(2);
            lw ( "CoolTempCap = "+CoolTempCap+" -  coolTemp (HEX) = "+coolTemp );
            String coolTempUnit = intMode1.P05( coolTemp.get(1)  );
            double coolTempdouble = intMode1.P05INT( coolTemp.get(1)  );
            lw ( "coolTempUnit = "+coolTempUnit+" -  coolTempdouble = "+coolTempdouble );
            if (coolTemp.get(0).contains("CoolTemp") && (coolTempdouble > CoolTempCap )) {
                return "BAD";
            } else {
                return "OK";
            }
        } catch ( Exception e ) {
            lw ("WARNING   : DiagnosticScan.setDashBoard.FuelTank = "+e);
            return "";
        }
    }

    public String getEngineOilGoodBad () {
        EngineOilTempCap = Integer.parseInt( EngineOilTempCapStg );
        lw( "EngineOilTempCap = "+EngineOilTempCap );
        try{
            engineOilTemp = dashBoardArray.get(3);
            lw ( "EngineOilTempCap = "+EngineOilTempCap+" -  engineOilTemp (HEX) = "+engineOilTemp );
            String engineTempUnit = intMode1.P5C( engineOilTemp.get(1)  );
            double engineTempdouble = intMode1.P5CINT( engineOilTemp.get(1)  );
            lw ( "engineTempUnit = "+engineTempUnit+" -  engineTempdouble  = "+engineTempdouble );
            if (engineOilTemp.get(0).contains("engineOilTemp") && (engineTempdouble > EngineOilTempCap)) {
                return "BAD";
            } else {
                return "OK";
            }
        } catch ( Exception e ) {
           lw ("WARNING   : DiagnosticScan.setDashBoard.FuelTank = "+e);
            return "";
        }
    }

    public String getInputBatteryBad () {
        BatteryTempCap = Double.parseDouble( BatteryTempCapStg ) ;
        lw ( "BatteryTempCap = "+BatteryTempCap );
        inputBattery = dashBoardArray.get(4);
        if ( inputBattery.get(1).contains("V") ) {
            try {
                String vString = inputBattery.get(1).replace("V", "") ;
                double inputVoltint = Double.parseDouble( vString );

                lw ( "BatteryTempCap = "+BatteryTempCap+" -  inputBattery (HEX) = "+inputBattery );
                lw(" inputVolt Bouble Value = "+inputVoltint);
                if ( inputVoltint < BatteryTempCap ) {
                    return "BAD";
                }
            } catch (Exception e ) {
                lw ( "Error Converting input Voltage..."+e);
            }

        } else {
            return "OK";
        }
        return "";
    }
}
