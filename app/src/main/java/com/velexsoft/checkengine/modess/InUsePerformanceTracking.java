package com.velexsoft.checkengine.modess;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;


import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogFileNew;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ooyeg on 9/22/2017.
 */

public class InUsePerformanceTracking {
    BluetoothSocket mmSocket;
    LogFileNew l = new LogFileNew() ;
    String oBDProtocolN;
    private static DecimalFormat df2;
    public ArrayList<ArrayList<String>> spkIgnitionArray = new ArrayList();
    Resources res;
    Boolean logErrors = ConnActivityParent.logErrors;
    ArrayList<String> cloudPerfTrack =  new ArrayList();
    Context context;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w  ( context, "INUSETRKPE: "+msg );
        }
    }
    
    public InUsePerformanceTracking(BluetoothSocket bbSocket, Context vcontext ) {
        this.context = vcontext;
        l.fileName = ConnActivityParent.logFileName;
        lw  ( "PROGRAM   : InUsePerformanceTracking Constructor" );
        mmSocket = bbSocket;
        oBDProtocolN = ConnActivityParent.currProtocol;
        df2 = new DecimalFormat("#.####");
        res = Resources.getSystem();
    }

    public ArrayList<String> sparkIgnition (   ) {
        lw  ( "PROGRAM   : InUsePerformanceTracking.sparkIgnition" );
        ArrayList<String>  sparkIgn = new ArrayList<String>();

        lw  ( "Protocol = "+ oBDProtocolN );

        OBDCommand2Byte obd0907Command = new OBDCommand2Byte( "09 07");
        try {
            obd0907Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            lw  ( "INFO      : obd0907Command progress" );
        } catch (Exception e) {
            lw  ( "ERROR ERROR : obd0907Command error retrieving data "+e );
        }
        String codeResponse0907 = ConnActivityParent.removeCarriage(obd0907Command.getResponse());
        lw  ( "This is codeResponse0907 = "+codeResponse0907 );
        sparkIgn.add( codeResponse0907 );

        OBDCommand2Byte obd0908Command = new OBDCommand2Byte( "09 08");
        try {
            obd0908Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            lw  ( "INFO      : obd0908Command progress" );
        } catch (Exception e) {
            lw  ( "ERROR ERROR : obd0907Command error retrieving data "+e );
        }
        String codeResponse0908 = ConnActivityParent.removeCarriage(  obd0908Command.getResponse() );
        lw  ( "This is codeResponse0908 = "+ ConnActivityParent.removeCarriage(codeResponse0908) );
        sparkIgn.add( codeResponse0908 );
        lw  ( "INFO      : InUsePerformanceTracking.sparkIgnition completed [Success]" );


        if (oBDProtocolN.substring(0,2).contains("1")) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        } else if ( oBDProtocolN.substring(0,2).contains("2")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        } else if ( oBDProtocolN.substring(0,2).contains("3")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            //vinHexadecimal = vinHexadecimal.substring(6,40);
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        }  else if ( oBDProtocolN.substring(0,2).contains("4")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            //vinHexadecimal = vinHexadecimal.substring(6,40);
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        }  else if ( oBDProtocolN.substring(0,2).contains("5")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            //vinHexadecimal = vinHexadecimal.substring(6,40);
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        }  else if ( oBDProtocolN.substring(0,2).contains("6")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        }  else if ( oBDProtocolN.substring(0,2).contains("7")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            //vinHexadecimal = vinHexadecimal.substring(6,40);
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        }  else if ( oBDProtocolN.substring(0,2).contains("8")  ) {

            String Frm1 = codeResponse0908.substring(1,  3);
            lw  ( "This is Frm1 = "+Frm1 );
            String Frm2 = codeResponse0908.substring(10, 18);
            lw  ("This is Frm2 = "+Frm2 );
            String Frm3 = codeResponse0908.substring(21, 35);
            lw  ("This is Frm3 = "+Frm3 );
            String Frm4 = codeResponse0908.substring(38, 52);
            lw  ( "This is Frm4 = "+Frm4 );
            String Frm5 = codeResponse0908.substring(55, 69);
            lw  ( "This is Frm5 = "+Frm5 );
            String Frm6 = codeResponse0908.substring(72, 86);
            lw  ( "This is Frm6 = "+Frm6 );
            String Frm7 = codeResponse0908.substring(89, 103);
            lw  ( "This is Frm7 = "+Frm7 );
            String vResponseTogether = Frm1 + Frm2 + Frm3 + Frm4 + Frm5 + Frm6 + Frm7;
            //vinHexadecimal = vinHexadecimal.substring(6,40);
            lw  ( "INFO      : vResponseTogether ["+oBDProtocolN+"]= "+vResponseTogether );
            breakInto2Bytes ( vResponseTogether );
        }
        return sparkIgn;
    }

    public ArrayList<String> compressionIgnition (   ) {
        lw  ( "PROGRAM   : compressionIgnition.sparkIgnition" );
        ArrayList<String>  compIgn = new ArrayList<String>();

        OBDCommand2Byte obd0907Command = new OBDCommand2Byte( "09 07");
        try {
            obd0907Command.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            lw  ( "INFO      : obd0907Command progress" );
        } catch (Exception e) {
            lw  ( "ERROR ERROR : obd0907Command error retrieving data "+e );
        }
        String codeResponse0907 = ConnActivityParent.removeCarriage(obd0907Command.getResponse() );

        lw  ( "This is codeResponse0907 = "+codeResponse0907 );
        compIgn.add( codeResponse0907 );

        OBDCommand2Byte obd090BCommand = new OBDCommand2Byte( "09 0B");
        try {
            obd090BCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            lw  ( "INFO      : obd090BCommand progress" );
        } catch (Exception e) {
            lw  ( "ERROR ERROR : obd090BCommand error retrieving data "+e );
        }
        String codeResponse090B = obd090BCommand.getResponse();
            lw  ( "This is codeResponse090B = "+codeResponse090B );
        compIgn.add( codeResponse090B );

        lw  ( "INFO      : InUsePerformanceTracking.compressionIgnition completed [Success]" );
        return compIgn;
    }

    public ArrayList<ArrayList<String>>  breakInto2Bytes ( String vResponse ) {
        lw  ( "PROGRAM   : InUsePerformanceTracking.breakInto2Bytes" );
        try {
            ArrayList<String> row1 = new ArrayList();
            double ratio = 0.0;
            row1.add(0, "OBD Monitoring Counts ");
            row1.add(1, "OBD Monitoring Conditions Encountered Counts ");
            row1.add(2, vResponse.substring(0, 2));
            row1.add(3, ConvertValues.HexToInteger(vResponse.substring(0, 2)) + "");
            row1.add(4, "OBDCOND");
            row1.add(5, "Ignition Counter ");
            row1.add(6, vResponse.substring( 2,4 )  );
            row1.add(7, ConvertValues.HexToInteger ( vResponse.substring( 2,4 ) )+"" );
            row1.add(8, "IGNCNTR" ) ;
            row1.add(9, df2.format ( ratio ) ) ;
            spkIgnitionArray.add(row1);
            //String  perfResult = ( row1.get(4)+"/"+row1.get(8)
            lw  ( "INFO      : OBDCOND  = "+vResponse.substring(0, 2) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes OBDCOND error  "+e );
        }

        try {
            ArrayList<String>  row3 = new ArrayList();
            Integer CATCOMP1 = 0;
            Integer CATCOND1 = 0;
            String ratio = "0.0";
            row3.add( 0, "Catalyst Monitor Counts (Bank 1)" );
            row3.add( 1, "Catalyst Monitor Completion Counts Bank 1 ");
            row3.add( 2, vResponse.substring( 4,6 )  );
            CATCOMP1 = ConvertValues.HexToInteger ( vResponse.substring( 4,6 ) );
            lw  ( "INFO      : CATCOMP1 = "+CATCOMP1 );
            row3.add( 3, CATCOMP1+"" );
            row3.add( 4, "CATCOMP1" ) ;
            row3.add( 5, "Catalyst Monitor Conditions Encountered Counts Bank 1");
            row3.add( 6, vResponse.substring( 6, 8 )  );
            CATCOND1 = ConvertValues.HexToInteger ( vResponse.substring( 6,8 ) );
            lw  ( "INFO      : CATCOND1 = "+CATCOND1 );
            row3.add( 7, CATCOND1+"" );
            row3.add( 8, "CATCOND1" ) ;
            try {
                ratio = getRatio( CATCOMP1 , CATCOND1  );
                lw  ( "INFO      : ratio = "+ratio );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP1 / CATCOND1 "+e );
            }
            String dfForm =  ratio ;
            lw  ( "INFO      : dfForm = "+dfForm );
            row3.add( 9, dfForm ) ;
            spkIgnitionArray.add(row3);
            lw  ( "INFO      : CATCOMP1 = "+vResponse.substring(4, 6) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes CATCOMP1 error  "+e );
        }

        try {
            ArrayList<String>  row5 = new ArrayList();
            Integer CATCOMP2 = 0;
            Integer CATCOND2 = 0;
            String ratio = "0.0";
            row5.add( 0, "Catalyst Monitor Counts (Bank 2)" );
            row5.add( 1, "Catalyst Monitor Completion Counts Bank 2 ");
            row5.add( 2, vResponse.substring( 8,10 )  );
            CATCOMP2 = ConvertValues.HexToInteger ( vResponse.substring( 8,10 ) );
            row5.add( 3, CATCOMP2+"" );
            row5.add( 4, "CATCOMP2" ) ;
            row5.add( 5, "Catalyst Monitor Conditions Encountered Counts Bank 2 ");
            row5.add( 6, vResponse.substring( 10,12 )  );
            CATCOND2 = ConvertValues.HexToInteger ( vResponse.substring( 10, 12 ) );
            row5.add( 7, CATCOND2+"" );
            row5.add( 8, "CATCOND2" ) ;
            try {
                ratio = getRatio( CATCOMP2 , CATCOND2  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row5.add( 9, ratio ) ;
            spkIgnitionArray.add(row5);
            lw  ( "INFO      : CATCOMP2 = "+vResponse.substring(8, 10) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes CATCOMP2 error  "+e );
        }

        try {
            ArrayList<String>  row7 = new ArrayList();
            String ratio = "0.0";
            Integer  O2SCOMP1 = 0;
            Integer  O2SCOND1 = 0;
            row7.add( 0, "O2 Sensor Monitor Counts (Bank 1) ");
            row7.add( 1, "O2 Sensor Monitor Completion Counts Bank 1 ");
            row7.add( 2, vResponse.substring( 12,14 )  );
            O2SCOMP1 = ConvertValues.HexToInteger ( vResponse.substring( 12,14  ) );
            row7.add( 3,  O2SCOMP1+"" );
            row7.add( 4, "O2SCOMP1" ) ;
            row7.add( 5, "O2 Sensor Monitor Conditions Encountered Counts Bank 1 ");
            row7.add( 6, vResponse.substring( 14,16 )  );
            O2SCOND1 =  ConvertValues.HexToInteger ( vResponse.substring( 14,16 ) );
            row7.add( 7, O2SCOND1+"" );
            row7.add( 8, "O2SCOND1" ) ;
            try {
                ratio = getRatio( O2SCOMP1 , O2SCOND1  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row7.add( 9, ratio  ) ;
            spkIgnitionArray.add(row7);
            lw  ( "INFO      : O2SCOMP1 = "+vResponse.substring(12,14 ) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes O2SCOMP1 error  "+e );
        }

        try {
            ArrayList<String>  row9 = new ArrayList();
            String ratio = "0.0";
            Integer O2SCOMP2 = 0;
            Integer O2SCOND2 = 0;
            row9.add( 0, "O2 Sensor Monitor Counts (Bank 2) ");
            row9.add( 1, "O2 Sensor Monitor Completion Counts Bank 2 ");
            row9.add( 2, vResponse.substring( 16,18 )  );
            O2SCOMP2 = ConvertValues.HexToInteger ( vResponse.substring( 16,18 ) );
            row9.add( 3, O2SCOMP2+"" );
            row9.add( 4, "O2SCOMP2" ) ;
            row9.add( 5, "O2 Sensor Monitor Conditions Encountered Counts Bank 2");
            row9.add( 6, vResponse.substring( 18,20 )  );
            O2SCOND2 =  ConvertValues.HexToInteger ( vResponse.substring( 18,20 ) );
            row9.add( 7, O2SCOND2+"" );
            row9.add( 8, "O2SCOND2" ) ;
            try {
                ratio = getRatio( O2SCOMP2 , O2SCOND2  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row9.add( 9, ratio ) ;
            spkIgnitionArray.add(row9);
            lw  ( "INFO      : O2SCOMP2 = "+vResponse.substring(16,18) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes O2SCOMP2 error  "+e );
        }

        try {
            ArrayList<String>  row11 = new ArrayList();
            String ratio = "0.0";
            Integer EGRCOMP = 0;
            Integer EGRCOND = 0;
            row11.add( 0, "EGR Monitor Completion Counts");
            row11.add( 1, "EGR Monitor Completion Condition Counts");
            row11.add( 2, vResponse.substring( 20,22 )  );
            EGRCOMP =  ConvertValues.HexToInteger ( vResponse.substring( 20,22 ) );
            row11.add( 3, EGRCOMP+"" );
            row11.add( 4, "EGRCOMP" ) ;
            row11.add( 5, "EGR Monitor Conditions Encountered Counts ");
            row11.add( 6, vResponse.substring( 22,24 )  );
            EGRCOND =  ConvertValues.HexToInteger ( vResponse.substring( 22,24 ) );
            row11.add( 7, EGRCOND+"" );
            row11.add( 8, "EGRCOND" ) ;
            try {
                ratio = getRatio( EGRCOMP , EGRCOND  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row11.add( 9, ratio ) ;
            spkIgnitionArray.add(row11);
            lw  ( "INFO      : EGRCOMP  = "+vResponse.substring(20,22) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes EGRCOMP error  "+e );
        }

        try {
            ArrayList<String>  row13 = new ArrayList();
            String ratio = "0.0";
            Integer AIRCOMP = 0;
            Integer AIRCOND = 0;
            row13.add( 0, "AIR Monitor Counts (Secondary Air) ");
            row13.add( 1, "AIR Monitor Completion Condition Counts (Secondary Air) ");
            row13.add( 2, vResponse.substring( 24,26 )  );
            AIRCOMP = ConvertValues.HexToInteger ( vResponse.substring( 24,26 ) );
            row13.add( 3, AIRCOMP+"" );
            row13.add( 4, "AIRCOMP" ) ;
            row13.add( 5, "AIR Monitor Conditions Encountered Counts (Secondary Air) ");
            row13.add( 6, vResponse.substring( 26,28 )  );
            AIRCOND = ConvertValues.HexToInteger ( vResponse.substring( 26,28 ) );
            row13.add( 7, AIRCOND+"" );
            row13.add( 8, "AIRCOND" ) ;
            try {
                ratio = getRatio( AIRCOMP , AIRCOND  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row13.add( 9,  ratio ) ;
            spkIgnitionArray.add(row13);
            lw  ( "INFO      : AIRCOMP  = "+vResponse.substring(24,26) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes AIRCOMP error  "+e );
        }

        try {
            ArrayList<String>  row15 = new ArrayList();
            String ratio = "0.0";
            Integer EVAPCOMP = 0;
            Integer EVAPCOND = 0;
            row15.add( 0, "EVAP Monitor Counts");
            row15.add( 1, "EVAP Monitor Completion Condition Counts");
            row15.add( 2, vResponse.substring( 28,30 )  );
            EVAPCOMP =  ConvertValues.HexToInteger ( vResponse.substring( 28,30 ) );
            row15.add( 3, EVAPCOMP+"" );
            row15.add( 4, "EVAPCOMP" ) ;
            row15.add( 5, "EVAP Monitor Conditions Encountered Counts");
            row15.add( 6, vResponse.substring( 30,32 )  );
            EVAPCOND =  ConvertValues.HexToInteger ( vResponse.substring( 30,32 ) );
            row15.add( 7, EVAPCOND+"" );
            row15.add( 8, "EVAPCOND" ) ;
            try {
                ratio = getRatio( EVAPCOMP , EVAPCOND  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row15.add( 9, ratio  ) ;
            spkIgnitionArray.add(row15);
            lw  ( "INFO      : EVAPCOMP = "+vResponse.substring( 28,30 ) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes EVAPCOMP error  "+e );
        }

        try {
            ArrayList<String>  row17 = new ArrayList();
            String ratio = "0.0";
            Integer SO2SCOMP1 = 0;
            Integer SO2SCOND1 = 0;
            row17.add( 0, "Secondary O2 Sensor Monitor (Bank 1) ");
            row17.add( 1, "Secondary O2 Sensor Monitor Completion Counts Bank 1 ");
            row17.add( 2, vResponse.substring( 30,32 )  );
            SO2SCOMP1 = ConvertValues.HexToInteger ( vResponse.substring( 30,32 ) );
            row17.add( 3, SO2SCOMP1+"" );
            row17.add( 4, "SO2SCOMP1" ) ;
            row17.add( 5, "Secondary O2 Sensor Monitor Conditions Encountered Counts Bank 1 ");
            row17.add( 6, vResponse.substring( 34,36 )  );
            SO2SCOND1 =  ConvertValues.HexToInteger ( vResponse.substring( 34,36  ) );
            row17.add( 7, SO2SCOND1+"" );
            row17.add( 8, "SO2SCOND1" ) ;
            try {
                ratio = getRatio( SO2SCOMP1 , SO2SCOND1  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row17.add( 9,  ratio  ) ;
            spkIgnitionArray.add(row17);
            lw  ( "INFO      : SO2SCOMP1 = "+vResponse.substring(30,32) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes SO2SCOMP1 error  "+e );
        }

        try {
            ArrayList<String>  row19 = new ArrayList();
            String ratio = "0.0";
            Integer SO2SCOMP2 = 0;
            Integer SO2SCOND2 = 0;
            row19.add( 0, "Secondary O2 Sensor Monitor (Bank 2) ");
            row19.add( 1, "Secondary O2 Sensor Monitor Completion Counts Bank 2 ");
            row19.add( 2, vResponse.substring( 36,38 )  );
            SO2SCOMP2 =  ConvertValues.HexToInteger ( vResponse.substring( 36,38 ) );
            row19.add( 3, SO2SCOMP2+"" );
            row19.add( 4, "SO2SCOMP2");
            row19.add( 5, "Secondary O2 Sensor Monitor Conditions Encountered Counts Bank 2");
            row19.add( 6, vResponse.substring( 38,40 )  );
            SO2SCOND2 =  ConvertValues.HexToInteger ( vResponse.substring( 38,40 ) );
            row19.add( 7, SO2SCOND2+"" );
            row19.add( 8, "SO2SCOND2" ) ;
            try {
                ratio = getRatio( SO2SCOMP2 , SO2SCOND2  );
            } catch ( Exception e ) {
                lw  ( "ERROR ERROR : Error dividing CATCOMP2 / CATCOND2 "+e );
            }
            row19.add( 9,  ratio ) ;
            spkIgnitionArray.add(row19);
            lw  ( "INFO      : SO2SCOMP2 = "+vResponse.substring(36,38) );
        } catch ( Exception e ) {
            lw  ( "ERROR ERROR : breakInto2Bytes SO2SCOMP2 error  "+e );
        }
        lw  ( "INFO      : Break2Bytes Completed [Success]" );
        return spkIgnitionArray;
    }

    public static String getRatio(double d1, double d2)//1.5, 3.25
    {
        while(Math.max(d1,d2) < Long.MAX_VALUE && d1 != (long)d1 && d2 != (long)d2)
        {
            d1 *= 10;//15 -> 150
            d2 *= 10;//32.5 -> 325
        }
        //d1 == 150.0
        //d2 == 325.0
        try
        {
            double gcd = getGCD(d1,d2);//gcd == 25
            return ((long)(d1 / gcd)) + ":" + ((long)(d2 / gcd));//"6:13"
        }
        catch (StackOverflowError er)//in case getGDC (a recursively looping method) repeats too many times
        {
            throw new ArithmeticException("Irrational ratio: " + d1 + " to " + d2);
        }
    }

    public static double getGCD(double i1, double i2)//(150,325) -> (150,175) -> (150,25) -> (125,25) -> (100,25) -> (75,25) -> (50,25) -> (25,25)
    {
        if (i1 == i2)
            return i1;//25
        if (i1 > i2)
            return getGCD(i1 - i2, i2);//(125,25) -> (100,25) -> (75,25) -> (50,25) -> (25,25)
        return getGCD(i1, i2 - i1);//(150,175) -> (150,25)
    }




}
