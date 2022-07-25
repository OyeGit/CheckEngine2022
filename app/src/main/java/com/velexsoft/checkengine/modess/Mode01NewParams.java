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
public class Mode01NewParams {

    BluetoothSocket mmSocket;
    Context context;

    String mode0100;
    String mode0120;
    String mode0140;
    String mode0160;
    String mode0180;
    String mode01A0;
    String mode01C0;
    String mMode ;

    String mode0100Binary = "00000000";
    String mode0120Binary = "00000000";
    String mode0140Binary = "00000000";
    String mode0160Binary = "00000000";
    String mode0180Binary = "00000000";
    String mode01A0Binary = "00000000";
    String mode01C0Binary = "00000000";

    public static String completeBinaries ;

    LogFileNew l = ConnActivityParent.l;

    public Mode01NewParams(BluetoothSocket pSocket, String pMode, Context vcontext  ){
        mmSocket = pSocket;
        mMode = pMode;
        this.context = vcontext;
        l.fileName = ConnActivityParent.logFileName;
        //getBinaries();
    }

    public void lw( String msg ) {
        if ( logErrors ) {
             l.w ( context,"MOD"+mMode+"PARAM: "+msg );
        }
    }

    public void getBinaries () {
        lw("PROGRAM      : Mode01NewParams.getBinaries Execution");
        try {
            OBDCommand1Byte obdCommand = new OBDCommand1Byte(mMode + " " + "00");
            obdCommand.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0100 = obdCommand.getResponse().substring(4, 12);

            OBDCommand1Byte obdCommand1 = new OBDCommand1Byte(mMode + " " + "20");
            obdCommand1.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0120 = obdCommand1.getResponse().substring(4, 12);

            OBDCommand1Byte obdCommand2 = new OBDCommand1Byte(mMode + " " + "40");
            obdCommand2.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0140 = obdCommand2.getResponse().substring(4, 12);

            OBDCommand1Byte obdCommand3 = new OBDCommand1Byte(mMode + " " + "60");
            obdCommand3.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0160 = obdCommand3.getResponse().substring(4, 12);

            OBDCommand1Byte obdCommand4 = new OBDCommand1Byte(mMode + " " + "80");
            obdCommand4.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode0180 = obdCommand4.getResponse().substring(4, 12);

            OBDCommand1Byte obdCommand5 = new OBDCommand1Byte(mMode + " " + "A0");
            obdCommand5.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01A0 = obdCommand5.getResponse().substring(4, 12);

            OBDCommand1Byte obdCommand6 = new OBDCommand1Byte(mMode + " " + "C0");
            obdCommand6.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            mode01C0 = obdCommand6.getResponse().substring(4, 12);

        } catch (Exception e) {
            lw("ERROR ERROR : Error Getting " + mMode + " 00 Data..." + e);
        }
    }

    public void setResults() {

        try {
            mode0100Binary = ConvertValues.hexToBin( mode0100 );
            lw("mode"+mMode+"00Binary = " + "["+mode0100+"] - "+ mode0100Binary );
            mode0100Binary = mode0100Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING    : Error Getting " + mMode + " 20 Data..." + e);
        }

        try {
            mode0120Binary = ConvertValues.hexToBin( mode0120 );
            lw("mode"+mMode+"20Binary = " + "["+mode0120+"] - "+ mode0120Binary );
            mode0120Binary = mode0120Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING   : Error Getting " + mMode + " 20 Data..." + e);
        }

        try {
            mode0140Binary = ConvertValues.hexToBin( mode0140 );
            lw("mode"+mMode+"40Binary = " + "["+mode0140+"] - "+ mode0140Binary );
            mode0140Binary = mode0140Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING   : Error Getting " + mMode + " 40 Data..." + e);
        }

        try {
            mode0160Binary = ConvertValues.hexToBin( mode0160 );
            lw("mode"+mMode+"60Binary = " + "["+mode0160+"] - "+ mode0160Binary );
            mode0160Binary = mode0160Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING   : Error Getting " + mMode + " 60 Data..." + e);
        }

        try {
            mode0180Binary = ConvertValues.hexToBin( mode0180 );
            lw("mode"+mMode+"80Binary = " + "["+mode0180+"] - "+ mode0180Binary );
            mode0180Binary = mode0180Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING   : Error Getting " + mMode + " 08 Data..." + e);
        }

        try {
            mode01A0Binary = ConvertValues.hexToBin( mode01A0 );
            lw("mode"+mMode+"A0Binary = " + "["+mode01A0+"] - "+ mode01A0Binary );
            mode01A0Binary = mode01A0Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING   : Error Getting " + mMode + " A0 Data..." + e);
        }

        try {
            mode01C0Binary = ConvertValues.hexToBin( mode01C0 );
            lw("mode"+mMode+"C0Binary = " + "["+mode01C0+"] - "+ mode01C0Binary );
            mode01C0Binary = mode01C0Binary.substring( 0,31 );
        } catch (Exception e) {
            lw("WARNING   : Error Getting " + mMode + " C0 Data..." + e);
        }

        completeBinaries = mode0100Binary + mode0120Binary +mode0140Binary + mode0160Binary + mode0180Binary + mode01A0Binary +mode01C0Binary ;
        lw("completeBinaries mode"+mMode+" = "+ completeBinaries  );
        lw("Completed  Mode01NewParams.getBinaries [Success]"  );
    }


}
