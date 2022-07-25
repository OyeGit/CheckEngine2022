package com.velexsoft.checkengine.modess;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.LogFileNew;

import static com.velexsoft.checkengine.ConnActivityParent.logErrors;
import static com.velexsoft.checkengine.ConnActivityParent.removeCarriage;

/**
 * Created by ooyeg on 2/5/2018.
 */

public class ProtocolInit {

    LogFileNew l = ConnActivityParent.l;
    int  vNumValue ;
    Context context;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "INITGENCMD:[" + vNumValue + "] : " + msg);
        }
    }

    public ProtocolInit( int vNum , Context vcontext) {

        vNumValue = vNum;
        this.context = vcontext;
    }


    public int initializeGeneralCommand(BluetoothSocket pSocket, int vNum  ) {
        lw("PROGRAM   : initializeGeneralCommand");
        int vstatus = 1;

        try {
            lw("setReset[AT Z]");
            OBDInitialize setReset = new OBDInitialize("AT Z");
            setReset.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( setReset.getResponse() );
            if (setReset.getResponse().contains("OK")) {
                lw("setReset.getResponse() is GOOD =  " + vResponse);
            } else {  vstatus = 0; }
        } catch (Exception e) { vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.setReset"+e);
        }

        try {
            lw("setResetDefault[AT D]");
            OBDInitialize setResetDefault = new OBDInitialize("AT D");
            setResetDefault.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(setResetDefault.getResponse());
            if (setResetDefault.getResponse().contains("OK")) {
                lw("setResetDefault.getResponse() is GOOD = " + vResponse);
            } else {   vstatus = 0;  }
        } catch (Exception e) { vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.setResetDefault" + e);
        }

        try {
            lw ( "setMemoryOn[AT M1]" );
            OBDInitialize setMemoryOn = new OBDInitialize("AT M1");
            setMemoryOn.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( setMemoryOn.getResponse() );
            if ( setMemoryOn.getResponse().contains("OK") ) {
                lw ( "setMemoryOn.getResponse() is GOOD = "+ vResponse );
                vstatus = 1;
            } else {   vstatus = 0;  }
        } catch ( Exception e ) { vstatus = 0;
            lw (  "ERROR ERROR: initializeConnection.setMemoryOn" + e);
        }

        try {
            lw("INITIALIZE: setEchoOff[AT E0]");
            OBDInitialize setEchoOff = new OBDInitialize("AT E0");
            setEchoOff.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(setEchoOff.getResponse());
            lw("setEchoOff.getResponse() = " + vResponse);
            if (setEchoOff.getResponse().contains("OK")) {
                lw("setEchoOff.getResponse() = is GOOD " + vResponse);
                vstatus = 1;
            } else {  vstatus = 0;  }
        } catch (Exception e) {  vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.setEchoOff");
        }

        try {
            lw("setHeaderOn[AT H1]");
            OBDInitialize setHeaderOff = new OBDInitialize("AT H1");
            setHeaderOff.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(setHeaderOff.getResponse());
            lw("setHeaderOn.getResponse() = " + vResponse);
            if (setHeaderOff.getResponse().contains("OK")) {
                lw("setHeaderOn.getResponse() = is GOOD " + vResponse);
                vstatus = 1;
            } else {  vstatus = 0; }
        } catch (Exception e) {  vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.setHeaderOn" + e);
        }

        try {
            lw("setSpacesOff[AT S0]");
            OBDInitialize setSpacesOff = new OBDInitialize("AT S0");
            setSpacesOff.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(setSpacesOff.getResponse());
            lw("setSpacesOff.getResponse() = " + vResponse);
            if (setSpacesOff.getResponse().contains("OK")) {
                lw("setSpacesOff.setSpacesOff() = is GOOD " + vResponse);
                vstatus = 1;
            } else {  vstatus = 0; }
        } catch (Exception e) {  vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.setSpacesOff" + e);
        }
        try {
            lw("adaptiveTiming[AT1]");
            OBDInitialize adaptiveTiming = new OBDInitialize("AT AT1");
            adaptiveTiming.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(adaptiveTiming.getResponse());
            lw("IadaptiveTiming.getResponse() = " + vResponse);
            if (adaptiveTiming.getResponse().contains("OK")) {
                lw("adaptiveTiming.getResponse() = is GOOD " + vResponse);
                vstatus = 1;
            } else {  vstatus = 0; }
        } catch (Exception e) { vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.adaptiveTiming" + e);
        }
        return vstatus;
    }

    public void initializewithDiscovery( BluetoothSocket pSocket,  int vNum  ) {
        int vstatus = 0;
        try {
            lw ( "eraseCurrentProtocol[SP 00]" );
            OBDInitialize eraseCurrentProtocol = new OBDInitialize("AT SP 00");
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            // lw ( "INITIALIZE: eraseCurrentProtocol.getResponse() = "+ vResponse );
            if ( eraseCurrentProtocol.getResponse().contains("OK") ) {
                lw ( "eraseCurrentProtocol.getResponse() is GOOD = "+ vResponse );
                vstatus = 1;
            } else {
                vstatus = 0;
            }
        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }
    }

    public void initK_LINEProtocols( BluetoothSocket pSocket,  int vNum  ) {
        int vstatus = 0;

        try {
            lw("init91412Protocols[80 80]");
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("80 80", 1, 100);
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(eraseCurrentProtocol.getResponse());
            lw("AT SI .getResponse() = " + vResponse);

        } catch (Exception e) {
            vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }

        try {
            lw("init91412Protocols[94 94]");
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("94 94", 1, 5000);
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage(eraseCurrentProtocol.getResponse());
            lw(" 94 94 .getResponse() = " + vResponse);

        } catch (Exception e) {
            vstatus = 0;
            lw("ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }
    }

    public void init91412Protocols( BluetoothSocket pSocket,  int vNum  ) {
        int vstatus = 0;

        try {
            lw ( " init91412Protocols[AT SI]" );
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("AT SI" , 1,100 );
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            lw ( "AT SI .getResponse() = "+ vResponse );

        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }

        try {
            lw ( " init91412Protocols[33]" );
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("33" , 1,600 );
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            lw ( " 33 .getResponse() = "+ vResponse );

        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }

        try {
            lw ( " init91412Protocols[F7]" );
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("F7" , 1,6000 );
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            lw ( "  .getResponse() = "+ vResponse );
        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }

        try {
            lw ( " init91412Protocols[69 6A F1 01 00 C5]" );
            OBDInitializeDelayForProt eraseCurrentProtocol = new OBDInitializeDelayForProt("69 6A F1 01 00 C5" , 1,500 );
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            lw ( "  .getResponse() = "+ vResponse );
        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }
    }

    public void TryOutProtocols( BluetoothSocket pSocket,  int vNum  ) {
        int vstatus = 0;

        try {
            lw ( " eraseCurrentProtocol[SP 00]" );
            OBDInitialize eraseCurrentProtocol = new OBDInitialize("AT SP 00");
            eraseCurrentProtocol.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( eraseCurrentProtocol.getResponse() );
            if ( eraseCurrentProtocol.getResponse().contains("OK") ) {
                lw ( " eraseCurrentProtocol.getResponse() is GOOD = "+ vResponse );
                vstatus = 1;
            } else {
                vstatus = 0;
            }
        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.testBaudRate");
        }

        try {
            lw ( " bufferDump[AT BD]" );
            OBDInitialize bufferDump = new OBDInitialize("AT BD");
            bufferDump.talkToDevice(pSocket.getInputStream(), pSocket.getOutputStream());
            String vResponse = removeCarriage( bufferDump.getResponse() );
            lw ( " bufferDump.getResponse() = "+ vResponse );
            if ( bufferDump.getResponse().contains("OK") ) {
                lw ( " bufferDump.getResponse() is GOOD = "+ vResponse );
                vstatus = 1;
            } else {
                vstatus = 0;
            }
        } catch ( Exception e ) {
            vstatus = 0;
            lw (  "ERROR ERROR: initializeGeneralCommand.bufferDump");
        }
    }

}
