package com.velexsoft.checkengine.modess;

import android.content.Context;
import android.content.res.Resources;

import com.velexsoft.checkengine.R;

import java.util.ArrayList;

public class Mode6GetMIDTID {
    Context context;
    
    public Mode6GetMIDTID ( Context vcontext) {
        this.context = vcontext;
        
    }


    public  String getTIDDescription ( String sTID ) {


        if (sTID.equals("01")) {
            return context.getString ( R.string.T01);
        } else if (sTID.equals("02")) {
            return context.getString ( R.string.T02);
        } else if (sTID.equals("03")) {
            return context.getString ( R.string.T03);
        } else if (sTID.equals("04")) {
            return context.getString ( R.string.T04);
        } else if (sTID.equals("05")) {
            return context.getString ( R.string.T05);
        } else if (sTID.equals("06")) {
            return context.getString ( R.string.T06);
        } else if (sTID.equals("07")) {
            return context.getString ( R.string.T07);
        } else if (sTID.equals("08")) {
            return context.getString ( R.string.T08);
        } else if (sTID.equals("09")) {
            return context.getString ( R.string.T09);
        } else if (sTID.equals("0A")) {
            return context.getString ( R.string.T0A);
        } else {
            return context.getString ( R.string.MAN);
        }
    }

    public ArrayList<String> getMIDDescription (String sMID ) {
        ArrayList<String> resultValue = new ArrayList();



        if (sMID.equals("01")) {  resultValue.add( context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("02") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) );  resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("03") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("04") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor4)) ;             }
        else if ( sMID.equals("05") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("06") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("07") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("08") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("09") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("0A") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("0B") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("0C") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("0D") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("0E") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("0F") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("10") )  {    resultValue.add(  context.getString ( R.string.oxygen_sensor_monitor) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("21") )  {    resultValue.add( context.getString( R.string.catalystic_monitor ) ); resultValue.add( context.getString(R.string.catalytic_bank1 ) );             }
        else if ( sMID.equals("22") )  {    resultValue.add( context.getString( R.string.catalystic_monitor )  ); resultValue.add( context.getString(R.string.catalytic_bank2 ) ) ;             }
        else if ( sMID.equals("23") )  {    resultValue.add( context.getString( R.string.catalystic_monitor ) ); resultValue.add( context.getString(R.string.catalytic_bank3 ) ) ;             }
        else if ( sMID.equals("24") )  {    resultValue.add( context.getString( R.string.catalystic_monitor )  ); resultValue.add( context.getString(R.string.catalytic_bank4 ) ) ;             }
        else if ( sMID.equals("31") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank1 ) ) ;             }
        else if ( sMID.equals("32") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank2 ) ) ;             }
        else if ( sMID.equals("33") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank3 ) ) ;             }
        else if ( sMID.equals("34") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank4 ) ) ;             }
        else if ( sMID.equals("35") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank5 ) ) ;             }
        else if ( sMID.equals("36") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank6 ) ) ;             }
        else if ( sMID.equals("37") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank7 ) ) ;             }
        else if ( sMID.equals("38") )  {    resultValue.add( context.getString( R.string.egr_monitor)  ); resultValue.add( context.getString(R.string.egr_bank8 ) ) ;             }
        else if ( sMID.equals("39") )  {    resultValue.add( context.getString( R.string.evap_monitor )  ); resultValue.add( context.getString(R.string.evap_capoff ) ) ;             }
        else if ( sMID.equals("3A") )  {    resultValue.add( context.getString( R.string.evap_monitor ) ); resultValue.add( context.getString(R.string.evap_90) ) ;             }
        else if ( sMID.equals("3B") )  {    resultValue.add( context.getString( R.string.evap_monitor ) ); resultValue.add( context.getString(R.string.evap_40) ) ;             }
        else if ( sMID.equals("3C") )  {    resultValue.add( context.getString( R.string.evap_monitor ) ); resultValue.add( context.getString(R.string.evap_20) ) ;             }
        else if ( sMID.equals("3D") )  {    resultValue.add( context.getString( R.string.flux_de_purge ) ); resultValue.add( context.getString(R.string.flux_de_purge) ) ;             }
        else if ( sMID.equals("41") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor1)) ;             }
        else if ( sMID.equals("42") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("43") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("44") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("45") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("46") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("47") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("48") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("49") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("4A") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("4B") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("4C") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("4D") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor1) ) ;             }
        else if ( sMID.equals("4E") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor2) ) ;             }
        else if ( sMID.equals("4F") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor3) ) ;             }
        else if ( sMID.equals("50") )  {    resultValue.add( context.getString( R.string.heated_exhaust_gas ) ); resultValue.add( context.getString(R.string.bank1_sensor4) ) ;             }
        else if ( sMID.equals("61") )  {    resultValue.add( context.getString( R.string.heated_catalyst_sensor ) ); resultValue.add( context.getString(R.string.heated_catalyst_bank1) ) ;             }
        else if ( sMID.equals("62") )  {    resultValue.add( context.getString( R.string.heated_catalyst_sensor ) ); resultValue.add( context.getString(R.string.heated_catalyst_bank2) ) ;             }
        else if ( sMID.equals("63") )  {    resultValue.add( context.getString( R.string.heated_catalyst_sensor ) ); resultValue.add( context.getString(R.string.heated_catalyst_bank3) ) ;             }
        else if ( sMID.equals("64") )  {    resultValue.add( context.getString( R.string.heated_catalyst_sensor ) ); resultValue.add( context.getString(R.string.heated_catalyst_bank4) ) ;             }
        else if ( sMID.equals("71") )  {    resultValue.add( context.getString( R.string.secondary_air_sensor) ); resultValue.add( context.getString(R.string.sec_air1) ) ;             }
        else if ( sMID.equals("72") )  {    resultValue.add( context.getString( R.string.secondary_air_sensor) ); resultValue.add( context.getString(R.string.sec_air2) ) ;             }
        else if ( sMID.equals("73") )  {    resultValue.add( context.getString( R.string.secondary_air_sensor) ); resultValue.add( context.getString(R.string.sec_air3) ) ;             }
        else if ( sMID.equals("74") )  {    resultValue.add( context.getString( R.string.secondary_air_sensor) ); resultValue.add( context.getString(R.string.sec_air4) ) ;             }
        else if ( sMID.equals("81") )  {    resultValue.add( context.getString( R.string.fuel_system_sensor) ); resultValue.add( context.getString(R.string.sec_air1) ) ;             }
        else if ( sMID.equals("82") )  {    resultValue.add( context.getString( R.string.fuel_system_sensor) ); resultValue.add( context.getString(R.string.sec_air2) ) ;             }
        else if ( sMID.equals("83") )  {    resultValue.add( context.getString( R.string.fuel_system_sensor) ); resultValue.add( context.getString(R.string.sec_air3) ) ;             }
        else if ( sMID.equals("84") )  {    resultValue.add( context.getString( R.string.fuel_system_sensor) ); resultValue.add( context.getString(R.string.sec_air4) ) ;             }
        else if ( sMID.equals("85") )  {    resultValue.add( context.getString( R.string.turbo_pressure_sensor) ); resultValue.add( "Turbo pressure bank 1" ) ;             }
        else if ( sMID.equals("86") )  {    resultValue.add( context.getString( R.string.turbo_pressure_sensor) ); resultValue.add( "Turbo pressure bank 2" ) ;             }
        else if ( sMID.equals("90") )  {    resultValue.add( context.getString( R.string.nox_absorber_bank1) ); resultValue.add( context.getString(R.string.nox_absorb_bank1)) ;             }
        else if ( sMID.equals("91") )  {    resultValue.add( context.getString( R.string.nox_absorber_bank2) ); resultValue.add(  context.getString(R.string.nox_absorb_bank2) ) ;             }
        else if ( sMID.equals("98") )  {    resultValue.add( context.getString( R.string.catalystic_nox1) ); resultValue.add( context.getString(R.string.cat_nox_bank1) ) ;             }
        else if ( sMID.equals("99") )  {    resultValue.add( context.getString( R.string.catalystic_nox2) ); resultValue.add( context.getString(R.string.cat_nox_bank2) ) ;             }
        else if ( sMID.equals("A1") )  {    resultValue.add( context.getString( R.string.general_misfire_monitoring) ); resultValue.add( context.getString(R.string.cat_nox_bank2) ) ;             }
        else if ( sMID.equals("A2") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder1) ) ;             }
        else if ( sMID.equals("A3") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder2) ) ;             }
        else if ( sMID.equals("A4") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder3) ) ;             }
        else if ( sMID.equals("A5") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder4) ) ;             }
        else if ( sMID.equals("A6") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder5) ) ;             }
        else if ( sMID.equals("A7") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder6) ) ;             }
        else if ( sMID.equals("A8") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder7) ) ;             }
        else if ( sMID.equals("A9") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder8) ) ;             }
        else if ( sMID.equals("AA") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder9) ) ;             }
        else if ( sMID.equals("AB") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder10) ) ;             }
        else if ( sMID.equals("AC") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder11) ) ;             }
        else if ( sMID.equals("AD") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder12) ) ;             }
        else if ( sMID.equals("AE") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder13) ) ;             }
        else if ( sMID.equals("AF") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder14) ) ;             }
        else if ( sMID.equals("B0") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder15) ) ;             }
        else if ( sMID.equals("B1") )  {    resultValue.add( context.getString( R.string.misfire_monitoring) ); resultValue.add( context.getString(R.string.misfire_cylinder16) ) ;             }
        else if ( sMID.equals("B2") )  {    resultValue.add( context.getString( R.string.pm_filter) ); resultValue.add(  context.getString(R.string.pm_filter1) ) ;             }
        else if ( sMID.equals("B3") )  {    resultValue.add( context.getString( R.string.pm_filter) ); resultValue.add( context.getString(R.string.pm_filter2) ) ;             }
        return resultValue;
    }
}
