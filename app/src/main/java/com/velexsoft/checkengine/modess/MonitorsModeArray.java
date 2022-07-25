package com.velexsoft.checkengine.modess;

import android.bluetooth.BluetoothSocket;
import android.content.Context;


import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogFileNew;

import java.util.ArrayList;

/**
 * Created by WoleProject on 11/23/2016.
 */


public class MonitorsModeArray {

    ArrayList<ArrayList<String>> mode01_PID_01Array = new ArrayList<ArrayList<String>>();
    String oBDProtocolN = ConnActivityParent.currProtocol;
    LogFileNew l = new LogFileNew() ;
    Boolean logErrors = ConnActivityParent.logErrors;
    public String vResponse;
    Context context;

    public void lw( String msg ) {

        if ( logErrors ) {
            l.w(context,"MONMODEARR: " + msg);
        }
    }

    public ArrayList<ArrayList<String>> MonitorsModeArrayRun (BluetoothSocket mmSocket, boolean showUnsupportedMon,
                                                              Context vcontext, String vMonitorMode ) {
        l.fileName = ConnActivityParent.logFileName;
        this.context = vcontext;
        lw ( "This is the current protocol = "+ oBDProtocolN);


        OBDCommand2Byte obdCommand = new OBDCommand2Byte("01 "+vMonitorMode);
        try { obdCommand.talkToDevice  (mmSocket.getInputStream(), mmSocket.getOutputStream());
        } catch (Exception e) {
            lw  ( "ERROR ERROR : MonitorsModeArrayRun.OBDCommand2Byte. Error = "+e );
        }

        vResponse = obdCommand.getResponse();
        lw ("vResponse = "+   vResponse  );

        vResponse =  ConvertValues.removeFrameHeader(  vResponse );
        lw ("vResponse  removeFrameHeader= "+   vResponse  );

        vResponse = ConvertValues.removeErrorString(  vResponse );
        lw ("vResponse  removeErrorString = "+   vResponse  );

        if (oBDProtocolN.substring(0,2).contains("1")) {
             vResponse = vResponse.substring(0,12);
        } else if (oBDProtocolN.substring(0,2).contains("2")) {
            vResponse = vResponse.substring(0,12);
        } else if (oBDProtocolN.substring(0,2).contains("3")) {
            vResponse = vResponse.substring(0,12);
        } else if (oBDProtocolN.substring(0,2).contains("4")) {
            vResponse = vResponse.substring(0,12);
        } else if (oBDProtocolN.substring(0,2).contains("5")) {
            vResponse = vResponse.substring(0,12);
        } else if (oBDProtocolN.substring(0,2).contains("6")) {
            vResponse = vResponse.substring(0,12);
        } else if (oBDProtocolN.substring(0,2).contains("8")) {
            vResponse = vResponse.substring(0,12);
        } else {
            vResponse = vResponse.substring(0,12);
        }

        String codeResponse01  = vResponse;

        String first00_2Bytes  = codeResponse01.substring(4,6);
        String second2Bytes = codeResponse01.substring(6,8);
        String third2Bytes = codeResponse01.substring(8,10);
        String fourth2Bytes = codeResponse01.substring(10,12);

        String first8Binary  = ConvertValues.hexToBin( first00_2Bytes );
        String second8Binary = ConvertValues.hexToBin( second2Bytes );
        String third8Binary  = ConvertValues.hexToBin( third2Bytes );
        String fourth8Binary = ConvertValues.hexToBin( fourth2Bytes );

        String response0101SecondBytebit3 =  second8Binary.substring( 4,5  );

        String B0 =  second8Binary.substring( 7,8  );
        String B4 =  second8Binary.substring( 3,4  );

        String B1 =  second8Binary.substring( 6,7  );
        String B5 =  second8Binary.substring( 2,3  );

        String B2 =  second8Binary.substring( 5,6  );
        String B6 =  second8Binary.substring( 1,2  );

        String B3 =  second8Binary.substring( 4,5  );

        {
            ArrayList<String> mode0101_Misfire = new ArrayList<String>();
            mode0101_Misfire.add(0, "Misfire");
            mode0101_Misfire.add(1, B0);
            mode0101_Misfire.add(2, B4);
            if (showUnsupportedMon) {
                if ( B0.equals("1")) {
                    mode01_PID_01Array.add(mode0101_Misfire);
                }
            } else {
                mode01_PID_01Array.add(mode0101_Misfire);
            }
        }
        {
            ArrayList<String> mode0101_FuelSystem = new ArrayList<String>();
            mode0101_FuelSystem.add(0, "Fuel System");
            mode0101_FuelSystem.add(1, B1);
            mode0101_FuelSystem.add(2, B5);
            if (showUnsupportedMon) {
                if ( B1.equals("1")) {
                    mode01_PID_01Array.add(mode0101_FuelSystem);
                }
            }else {
                mode01_PID_01Array.add(mode0101_FuelSystem);
            }
        }
        {
            ArrayList<String> mode0101_CompComponent = new ArrayList<String>();
            mode0101_CompComponent.add(0, "Components");
            mode0101_CompComponent.add(1, B2);
            mode0101_CompComponent.add(2, B6);
            if (showUnsupportedMon) {
                if ( B2.equals("1")) {
                    mode01_PID_01Array.add(mode0101_CompComponent);
                }
            } else {
                mode01_PID_01Array.add(mode0101_CompComponent);
            }
        }

        String C0 = third8Binary.substring( 7,8 );
        String C1 = third8Binary.substring( 6,7 );
        String C2 = third8Binary.substring( 5,6 );
        String C3 = third8Binary.substring( 4,5 );
        String C4 = third8Binary.substring( 3,4 );
        String C5 = third8Binary.substring( 2,3 );
        String C6 = third8Binary.substring( 1,2 );
        String C7 = third8Binary.substring( 0,1 );

        String D0 = fourth8Binary.substring( 7,8 );
        String D1 = fourth8Binary.substring( 6,7 );
        String D2 = fourth8Binary.substring( 5,6 );
        String D3 = fourth8Binary.substring( 4,5 );
        String D4 = fourth8Binary.substring( 3,4 );
        String D5 = fourth8Binary.substring( 2,3 );
        String D6 = fourth8Binary.substring( 1,2 );
        String D7 = fourth8Binary.substring( 0,1 );

        if ( response0101SecondBytebit3.equals("1") ) {
            // Compression Ignition
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                if (B3.equals("0"))
                    mode0101_SparkIgn.add(0, "Catalyst");
                else
                    mode0101_SparkIgn.add(0, "NMHC Catalyst");
                mode0101_SparkIgn.add(1, C0);
                mode0101_SparkIgn.add(2, D0);
                if (showUnsupportedMon) {
                    if ( C0.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                } else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }


            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                if (B3.equals("0"))
                    mode0101_SparkIgn.add(0, "Heated Catalyst");
                else
                    mode0101_SparkIgn.add(0, "NOx/SCR Monitor");
                mode0101_SparkIgn.add(1, C1);
                mode0101_SparkIgn.add(2, D1);
                if (showUnsupportedMon) {
                    if ( C1.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }

            if (B3.equals("0"))
            {
                ArrayList<String> mode0101_EvapSys = new ArrayList<String>();
                mode0101_EvapSys.add(0, "Evaporative System");
                mode0101_EvapSys.add(1, C2);
                mode0101_EvapSys.add(2, D2);
                if (showUnsupportedMon) {
                    if ( C2.equals("1")) {
                        mode01_PID_01Array.add(mode0101_EvapSys);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_EvapSys);
                }
            }


            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                if (B3.equals("0"))
                    mode0101_SparkIgn.add(0, "Secondary Air System");
                else
                    mode0101_SparkIgn.add(0, "Boost Pressure");
                mode0101_SparkIgn.add(1, C3);
                mode0101_SparkIgn.add(2, D3);
                if (showUnsupportedMon) {
                    if ( C3.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }


            if (B3.equals("0"))
            {
                ArrayList<String> mode0101_EvapSys = new ArrayList<String>();
                mode0101_EvapSys.add(0, "A/C Refrigerant");
                mode0101_EvapSys.add(1, C4);
                mode0101_EvapSys.add(2, D4);
                if (showUnsupportedMon) {
                    if ( C4.equals("1")) {
                        mode01_PID_01Array.add(mode0101_EvapSys);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_EvapSys);
                }
            }

            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                if (B3.equals("0"))
                    mode0101_SparkIgn.add(0, "Oxygen Sensor");
                else
                    mode0101_SparkIgn.add(0, "Exhaust Gas Sensor");
                mode0101_SparkIgn.add(1, C5);
                mode0101_SparkIgn.add(2, D5);
                if (showUnsupportedMon) {
                    if ( C5.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                if (B3.equals("0"))
                    mode0101_SparkIgn.add(0, "Oxygen Sensor Heater");
                else
                    mode0101_SparkIgn.add(0, "PM Filter Monitoring");
                mode0101_SparkIgn.add(1, C6);
                mode0101_SparkIgn.add(2, D6);
                if (showUnsupportedMon) {
                    if ( C6.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                if (B3.equals("0"))
                    mode0101_SparkIgn.add(0, "EGR System");
                else
                    mode0101_SparkIgn.add(0, "EGR and/or VVT System");
                mode0101_SparkIgn.add(1, C5);
                mode0101_SparkIgn.add(2, D5);
                if (showUnsupportedMon) {
                    if ( C5.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }

        }else if ( response0101SecondBytebit3.equals("0") ){
            //Spark Ignition
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Catalyst");
                mode0101_SparkIgn.add(1, C0);
                mode0101_SparkIgn.add(2, D0);
                if (showUnsupportedMon) {
                    if ( C0.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Heated Catalyst");
                mode0101_SparkIgn.add(1, C1);
                mode0101_SparkIgn.add(2, D1);
                if (showUnsupportedMon) {
                    if ( C1.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Evaporative System");
                mode0101_SparkIgn.add(1, C2);
                mode0101_SparkIgn.add(2, D2);
                if (showUnsupportedMon) {
                    if ( C2.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Secondary Air System");
                mode0101_SparkIgn.add(1, C3);
                mode0101_SparkIgn.add(2, D3);
                if (showUnsupportedMon) {
                    if ( C3.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "A/C Refrigerant");
                mode0101_SparkIgn.add(1, C4);
                mode0101_SparkIgn.add(2, D4);
                if (showUnsupportedMon) {
                    if ( C4.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Oxygen Sensor");
                mode0101_SparkIgn.add(1, C5);
                mode0101_SparkIgn.add(2, D5);
                if (showUnsupportedMon) {
                    if ( C5.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Oxygen Sensor Heater");
                mode0101_SparkIgn.add(1, C6);
                mode0101_SparkIgn.add(2, D6);
                if (showUnsupportedMon) {
                    if ( C6.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "EGR System");
                mode0101_SparkIgn.add(1, C7);
                mode0101_SparkIgn.add(2, D7);
                if (showUnsupportedMon) {
                    if ( C7.equals("1")) {
                        mode01_PID_01Array.add(mode0101_SparkIgn);
                    }
                }else {
                    mode01_PID_01Array.add(mode0101_SparkIgn);
                }
            }
        }else
        {
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Catalyst");
                mode0101_SparkIgn.add(1, "No Data");
                mode0101_SparkIgn.add(2, "No Data");
                mode01_PID_01Array.add(mode0101_SparkIgn);

            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Heated Catalyst");
                mode0101_SparkIgn.add(1, "No Data");
                mode0101_SparkIgn.add(2, "No Data");
                mode01_PID_01Array.add(mode0101_SparkIgn);
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Evaporative System");
                mode0101_SparkIgn.add(1, "No Data");
                mode0101_SparkIgn.add(2, "No Data");
                mode01_PID_01Array.add(mode0101_SparkIgn);
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Secondary Air System");
                mode0101_SparkIgn.add(1, "No Data");
                mode0101_SparkIgn.add(2, "No Data");
                mode01_PID_01Array.add(mode0101_SparkIgn);
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "A/C Refrigerant");
                mode0101_SparkIgn.add(1, "No Data");
                mode0101_SparkIgn.add(2, "No Data");
                mode01_PID_01Array.add(mode0101_SparkIgn);
            }
            {
                ArrayList<String> mode0101_SparkIgn = new ArrayList<String>();
                mode0101_SparkIgn.add(0, "Oxygen Sensor");
                mode0101_SparkIgn.add(1, "No Data");
                mode0101_SparkIgn.add(2, "No Data");
                mode01_PID_01Array.add(mode0101_SparkIgn);
            }
        }

        ArrayList<String> p_row;
        for (int i = 0; i < mode01_PID_01Array.size(); i++) {
            p_row = mode01_PID_01Array.get(i);

         lw  ( p_row.get(0)+", "+p_row.get(1)+ " = "+p_row.get(2) );
        }
        return mode01_PID_01Array;
    }


}
