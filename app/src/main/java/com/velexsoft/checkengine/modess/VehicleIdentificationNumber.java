package com.velexsoft.checkengine.modess;

import android.content.Context;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;
import com.velexsoft.checkengine.utilities.LogFileNew;

import java.util.ArrayList;

/**
 * Created by WoleProject on 9/28/2016.
 */

public class VehicleIdentificationNumber {
    public Context context ;
    ArrayList<ArrayList<String>> VehInfoList;
    ArrayList<ArrayList<String>> VehInfoListReport;
    String ECUProtocol = ConnActivityParent.currProtocol;
    String protocolGrp = ConnActivityParent.currProtocolGroup;
    LogFileNew l = new LogFileNew() ;
    Boolean logErrors = ConnActivityParent.logErrors;
    String mCommand = "09 02";
    LogCloudFileNew c;
    String vResponse;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "VINNUMSGET: " + msg);
        }
    }

    public VehicleIdentificationNumber( String Vin ) {
        vResponse =  ConvertValues.removeFrameHeader("Vin");
        c = new LogCloudFileNew( "VinNumber2", context );
        l.fileName = ConnActivityParent.logFileName;
        lw(  "MSG       : Completed VehicleIdentificationNumber Constructor.....");
    }

    public String getResponse() {
        lw(  "MSG       : PROGRAM - VehicleIdentificationNumber.getResponse().....");
        String vinHexadecimal = "";
        String vinNumber = "";
        //String vResponse = res.toString();
        //vResponse = "49020131464D5A49020255373445490203333355434902043435343849020538000000";
        //vResponse =   "4902010000003149020247314A43490203353434344902045237323549020532333637";
        lw ( " This is the VIN response = "+ vResponse);
        lw ( " This is the protocolGrp = "+ protocolGrp);
        String oBDProtocolN = ECUProtocol;

        if (protocolGrp.contains("J1850")||protocolGrp.contains("ISO")) {
            System.out.println("length of vResponse protocolGrp = " + vResponse.length() );

            if ( vResponse.length()==70   ) {
                vResponse = vResponse.substring(5);
                System.out.println( " Vcut Response = "+ vResponse );
            }

            if (vResponse.length() >= 65) {
                try {
                    String Frm1 = vResponse.substring(7, 9);
                    lw("MSG : vResponse.substring(7 , 9) = " + Frm1);
                    String Frm2 = vResponse.substring(15, 23);
                    lw("MSG : vResponse.substring(15, 23) = " + Frm2);
                    String Frm3 = vResponse.substring(29, 37);
                    lw("MSG : vResponse.substring(29, 37) = " + Frm3);
                    String Frm4 = vResponse.substring(43, 51);
                    lw("MSG : vResponse.substring(43, 51) = " + Frm4);
                    String Frm5 = vResponse.substring(57, 65);
                    lw("MSG : vResponse.substring(57, 65) = " + Frm5);
                    vinHexadecimal = Frm1 + Frm2 + Frm3 + Frm4 + Frm5;
                    lw( "MSG       : This is the vinHexadecimal ISO/J1850 = "+ vinHexadecimal);
                    //vinHexadecimal = vinHexadecimal.substring(6,40);
                    lw( "MSG       : This is the vinHexadecimal ISO/J1850= "+ vinHexadecimal);
                } catch ( Exception  e ) {
                    lw("ERROR ERROR : This is the vinHexadecimal = " + vinHexadecimal);
                }
            }

        }  else if (protocolGrp.contains("CAN"))  {
            lw  ("length of vResponse CAN = " + vResponse );
            lw  ("length of vResponse.count = " + vResponse.length() );
            if (vResponse.length() >= 43) {
                try {
                    String Frm1 = vResponse.substring(9, 43);
                    lw("MSG : vResponse.substring(9, 43) = " + Frm1);
                    //String Frm2 = vResponse.substring(21, 35);
                    //lw("MSG : vResponse.substring(21, 35) = " + Frm2);
                    //String Frm3 = vResponse.substring(38, 52);
                    //lw("MSG : vResponse.substring(38, 52) = " + Frm3);
                    vinHexadecimal = Frm1 ; //+ Frm2 + Frm3;
                    lw("MSG       : This is the vinHexadecimal CAN = " + vinHexadecimal);
                } catch ( Exception  e ) {
                    lw("ERROR ERROR : This is the vinHexadecimal CAN = " + vinHexadecimal);
                }
            }
        }

        System.out.println("Vin Hex Count = " + vinHexadecimal.length()  );
        if ( (vinHexadecimal.length() == 34) || (vinHexadecimal.length()== 32) ) {
            for (int i = 0; i < 34; i++) {
                int begin     = i;
                int beginplus = i+2;
                String lowerAAsString = vinHexadecimal.substring(begin,beginplus);
                // System.out.println("This is aChar = " + lowerAAsString);
                vinNumber = vinNumber + hexToAscii(lowerAAsString);
                i = i+1;
            }
        } else
        {
            vinNumber = "No Data";
        }
        // System.out.println("This is VIN = "+vinNumber );
        ArrayList<String> dtcArray = new ArrayList();
        dtcArray.add( vinNumber );
        c.w( mCommand , vResponse , dtcArray  );
        return vinNumber;
    }

    public void checkVinExist() {

    }

    String hexToAscii(String s) {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n / 2);
        for (int i = 0; i < n; i += 2) {
            char a = s.charAt(i);
            char b = s.charAt(i + 1);
            sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
        }
        return sb.toString();
    }

    private static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
        if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
        if ('0' <= ch && ch <= '9') { return ch - '0'; }
        throw new IllegalArgumentException(String.valueOf(ch));
    }
}
