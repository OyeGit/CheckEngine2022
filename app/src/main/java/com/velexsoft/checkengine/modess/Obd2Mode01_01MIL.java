package com.velexsoft.checkengine.modess;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogFileNew;

import static com.velexsoft.checkengine.ConnActivityParent.logErrors;


public class Obd2Mode01_01MIL {

    Context context;
    BluetoothSocket mmSocket = null;
    LogFileNew l = new LogFileNew();
    public String MILValue;
    public String MILCount;
    String mCommand;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "GETMILCODE: " + msg);
        }
    }

    public Obd2Mode01_01MIL (BluetoothSocket ppSocket, Context pContext   ) {
        context = pContext;
        mmSocket = ppSocket;
    }

    public String Obd2Mode01_01MILNow( String code ) {
        mCommand = "01 "+code;
        OBDCommand2Byte obdCommand = new OBDCommand2Byte( mCommand );
        try {
            obdCommand.talkToDevice  (mmSocket.getInputStream(), mmSocket.getOutputStream());
        } catch (Exception e) {
          lw ( "ERROR ERROR : Error Getting OBDProtocol..."+e  );
        }

        String codeResponse00  = obdCommand.getResponse();
        lw ("MIL "+mCommand+" codeResponse00 = "+   codeResponse00  );

        codeResponse00 = ConvertValues.removeErrorString(  codeResponse00 );
        lw ("MIL "+mCommand+" removeErrorString = "+   codeResponse00  );
        String first00_2Bytes  = codeResponse00.substring(4,6);
        lw ("MIL "+mCommand+" first00_2Bytes = "+   first00_2Bytes  );
        String first8Binary  = ConvertValues.hexToBin(first00_2Bytes);
        {
            String FirstByteFirstBit = first8Binary.substring(0, 1);
            String dTCnoBinary = first8Binary.substring(1,8 );
            MILCount = ConvertValues.BinaryStringToDECString( dTCnoBinary  );
            lw( "final MILCount = "+MILCount);

            MILValue = FirstByteFirstBit;
            lw ( "final MILValue = "+MILValue);
        }
        return MILValue;
    }



}
