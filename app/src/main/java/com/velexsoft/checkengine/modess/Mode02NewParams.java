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
public class Mode02NewParams {

    BluetoothSocket mmSocket;

    String mode0100;
    String mode0120;
    String mode0140;
    String mode0160;
    String mode0180;
    String mode01A0;
    String mode01C0;
    String mMode ;
    Context context;

    String mode0100Binary = "00000000";
    String mode0120Binary = "00000000";
    String mode0140Binary = "00000000";
    String mode0160Binary = "00000000";
    String mode0180Binary = "00000000";
    String mode01A0Binary = "00000000";
    String mode01C0Binary = "00000000";

    public static String completeBinaries ;
    static String frameNo;
    LogFileNew l = new LogFileNew();

    public Mode02NewParams(BluetoothSocket pSocket, String pMode, String frameNo, Context context  ){
        l.fileName = ConnActivityParent.logFileName;
        lw("PROGRAM   : Mode02NewParams.Mode02NewParams Execution");
        mmSocket = pSocket;
        mMode = pMode;
        this.context= context;
        this.frameNo = frameNo;
        getBinaries();
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            //l.w(context, "MOD" + mMode + "PARAM: " + msg);
        }
    }

    public void getBinaries () {
        lw("PROGRAM      : Mode02NewParams.getBinaries Execution");
        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "00 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0100 = obdCommand.getResponse().substring( 6, 14 );
            mode0100Binary = ConvertValues.hexToBin( mode0100 );
            mode0100Binary = mode0100Binary.substring( 0,31 );
            lw("mode"+mMode+"00Binary = " + mode0100Binary );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " 00 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "20 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0120 = obdCommand.getResponse().substring( 4, 12 );
            mode0120Binary = ConvertValues.hexToBin( mode0120 );
            lw("mode"+mMode+"20Binary = " + mode0120Binary );
            mode0120Binary = mode0120Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " 20 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "40 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0140 = obdCommand.getResponse().substring( 4, 12 );
            mode0140Binary = ConvertValues.hexToBin( mode0140 );
            mode0140Binary = mode0140Binary.substring( 0,31 );
            lw("mode"+mMode+"40Binary = " + mode0140Binary );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " 40 Data..." + e);
        }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "60 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0160 = obdCommand.getResponse().substring( 4, 12 );
            mode0160Binary = ConvertValues.hexToBin( mode0160 );
            mode0160Binary = mode0160Binary.substring( 0, 31 );
            lw("mode"+mMode+"60Binary = " + mode0160Binary );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " 60 Data..." + e);
         }

        try {
            OBDCommand4Byte obdCommand = new OBDCommand4Byte(mMode + " " + "80 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0180 = obdCommand.getResponse().substring( 4, 12 );
            mode0180Binary = ConvertValues.hexToBin( mode0180 );
            mode0180Binary = mode0180Binary.substring( 0, 31 );
            lw("mode"+mMode+"80Binary = " + mode0180Binary );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " 08 Data..." + e);
        }

        try {
            OBDCommand2Byte obdCommand = new OBDCommand2Byte(mMode + " " + "A0 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01A0 = obdCommand.getResponse().substring( 4, 12 );
            mode01A0Binary = ConvertValues.hexToBin( mode01A0 );
            mode01A0Binary = mode01A0Binary.substring( 0, 31 );
            lw("mode"+mMode+"A0Binary = " + mode01A0Binary );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " A0 Data..." + e);
        }

        try {
            OBDCommand2Byte obdCommand = new OBDCommand2Byte(mMode + " " + "C0 "+frameNo);
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01C0 = obdCommand.getResponse().substring( 4, 12 );
            mode01C0Binary = ConvertValues.hexToBin( mode01A0 );
            mode01C0Binary = mode01C0Binary.substring( 0, 31 );
            lw("mode"+mMode+"C0Binary = " + mode01C0Binary );
        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " C0 Data..." + e);
        }


        completeBinaries = mode0100Binary + mode0120Binary +mode0140Binary + mode0160Binary + mode0180Binary + mode01A0Binary +mode01C0Binary ;
        lw("completeBinaries mode"+mMode+" = "+ completeBinaries  );
        lw("Completed  Mode01NewParams.getBinaries [Success]"  );
    }


}
