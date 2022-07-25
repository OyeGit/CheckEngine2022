package com.velexsoft.checkengine.modess;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogFileNew;

import static com.velexsoft.checkengine.ConnActivityParent.logErrors;

/**
 * Created by ooyeg on 11/5/2017.
 */
public class Mode06CANNewParams {

    BluetoothSocket mmSocket;

    String mode0100;
    String mode0120;
    String mode0140;
    String mode0160;
    String mode0180;
    String mode01A0;
    String mode01C0;
    String mode01E0;
    String mMode ;
    Context context;

    String mode0100Binary = "0000000000000000000000000000000";
    String mode0120Binary = "0000000000000000000000000000000";
    String mode0140Binary = "0000000000000000000000000000000";
    String mode0160Binary = "0000000000000000000000000000000";
    String mode0180Binary = "0000000000000000000000000000000";
    String mode01A0Binary = "0000000000000000000000000000000";
    String mode01C0Binary = "0000000000000000000000000000000";
    String mode01E0Binary = "0000000000000000000000000000000";

    public static String completeBinaries ;

   // LogFileNew l = ConnActivityParent.l;

    public Mode06CANNewParams(BluetoothSocket pSocket, String pMode, Context vcontext  ){
       // l.fileName = ConnActivityParent.logFileName;
        lw("PROGRAM      : Mode01NewParams.Mode01NewParams Execution");
        mmSocket = pSocket;
        mMode = pMode;
        this.context = vcontext;
        getBinaries();
    }

    public void lw( String msg ) {
        if ( logErrors )  {
            System.out.println (  "MOD"+mMode+"PARAM: "+msg );
        }
    }

    public void getBinaries () {
        lw("PROGRAM      : Mode06CANNewParams.getBinaries Execution");
        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "00");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0100 = obdCommand.getResponse().substring( 4, 12 );
            mode0100Binary = ConvertValues.hexToBin( mode0100 );
            lw("mode"+mMode+"00Binary = " + "["+mode0100+"] - "+ mode0100Binary );
            //System.out.println("mode"+mMode+"00Binary = " + mode0100Binary  );
            mode0100Binary = mode0100Binary.substring( 0,31 );
            //System.out.println("mode"+mMode+"00Binary = " + mode0100Binary  );
        } catch (Exception e) {
            mode0100Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " 00 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " 00 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "20");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0120 = obdCommand.getResponse().substring( 4, 12 );
            mode0120Binary = ConvertValues.hexToBin( mode0120 );
            lw("mode"+mMode+"20Binary = " + "["+mode0120+"] - "+ mode0120Binary );
            //System.out.println( "mode"+mMode+"20Binary = " + mode0120Binary  );
            mode0120Binary = mode0120Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"20Binary = " + mode0120Binary  );
        } catch (Exception e) {
            mode0120Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " 20 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " 20 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "40");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0140 = obdCommand.getResponse().substring( 4, 12 );
            mode0140Binary = ConvertValues.hexToBin( mode0140 );
            lw("mode"+mMode+"40Binary = " + "["+mode0140+"] - "+ mode0140Binary );
            //System.out.println( "mode"+mMode+"40Binary = " + mode0140Binary  );
            mode0140Binary = mode0140Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"40Binary = " + mode0140Binary  );
        } catch (Exception e) {
            mode0140Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " 40 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " 40 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "60");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0160 = obdCommand.getResponse().substring( 4, 12 );
            mode0160Binary = ConvertValues.hexToBin( mode0160 );
            lw("mode"+mMode+"60Binary = " + "["+mode0160+"] - "+ mode0160Binary );
            //System.out.println( "mode"+mMode+"60Binary = " + mode0160Binary  );
            mode0160Binary = mode0160Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"60Binary = " + mode0160Binary  );
        } catch (Exception e) {
            mode0160Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " 60 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " 60 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "80");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0180 = obdCommand.getResponse().substring( 4, 12 );
            mode0180Binary = ConvertValues.hexToBin( mode0180 );
            lw("mode"+mMode+"80Binary = " + "["+mode0180+"] - "+ mode0180Binary );
            //System.out.println( "mode"+mMode+"80Binary = " + mode0180Binary  );
            mode0180Binary = mode0180Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"80Binary = " + mode0180Binary  );
        } catch (Exception e) {
            mode0180Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " 08 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " 80 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "A0");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01A0 = obdCommand.getResponse().substring( 4, 12 );
            mode01A0Binary = ConvertValues.hexToBin( mode01A0 );
            lw("mode"+mMode+"A0Binary = " + "["+mode01A0+"] - "+ mode01A0Binary );
            //System.out.println( "mode"+mMode+"A0Binary = " + mode01A0Binary  );
            mode01A0Binary = mode01A0Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"A0Binary = " + mode01A0Binary  );
        } catch (Exception e) {
            mode01A0Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " A0 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " A0 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "C0");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01C0 = obdCommand.getResponse().substring( 4, 12 );
            mode01C0Binary = ConvertValues.hexToBin( mode01C0 );
            lw("mode"+mMode+"C0Binary = " + "["+mode01C0+"] - "+ mode01C0Binary );
            //System.out.println( "mode"+mMode+"C0Binary = " + mode01C0Binary  );
            mode01C0Binary = mode01C0Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"C0Binary = " + mode01C0Binary  );
        } catch (Exception e) {
            mode01C0Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " C0 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " C0 Data..." + e);
        }
        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "E0");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01E0 = obdCommand.getResponse().substring( 6, 14 );
            mode01E0Binary = ConvertValues.hexToBin( mode01E0 );
            lw("mode"+mMode+"E0Binary = " + "["+mode01E0+"] - "+ mode01E0Binary );
            //System.out.println( "mode"+mMode+"E0Binary = " + mode01E0Binary  );
            mode01E0Binary = mode01E0Binary.substring( 0,31 );
            //System.out.println( "mode"+mMode+"E0Binary = " + mode01E0Binary  );
        } catch (Exception e) {
            mode01E0Binary = "0000000000000000000000000000000";
            lw("ERROR ERROR : Error Getting " + mMode + " E0 Data..." + e);
            //System.out.println( "ERROR ERROR : Error Getting " + mMode + " E0 Data..." + e);
        }

        /*
        //System.out.println( "mode0100Binary = "+mode0100Binary );
        mode0100Binary = mode0100Binary.substring(0, 30);
        //System.out.println( "mode0100Binary.substring(0, 31) = "+mode0100Binary );
        mode0120Binary = mode0120Binary.substring(0, 30);
        mode0140Binary = mode0140Binary.substring(0, 30);
        mode0160Binary = mode0160Binary.substring(0, 30);
        mode0180Binary = mode0180Binary.substring(0, 30);
        mode01A0Binary = mode01A0Binary.substring(0, 30);
        mode01C0Binary = mode01C0Binary.substring(0, 30);
*/

        completeBinaries = mode0100Binary + mode0120Binary +mode0140Binary + mode0160Binary + mode0180Binary + mode01A0Binary +mode01C0Binary + mode01E0Binary ;
        lw("completeBinaries mode"+mMode+" = "+ completeBinaries  );
        lw("Completed  Mode06NewParams.getBinaries [Success]"  );
    }


}
