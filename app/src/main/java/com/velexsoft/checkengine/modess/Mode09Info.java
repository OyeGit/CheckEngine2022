package com.velexsoft.checkengine.modess;

import android.bluetooth.BluetoothSocket;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.LogFileNew;

import static com.velexsoft.checkengine.ConnActivityParent.removeCarriage;

/**
 * Created by ooyeg on 5/9/2018.
 */

public class Mode09Info {

    BluetoothSocket sSocket;
    LogFileNew l = new LogFileNew() ;
    Boolean logErrors = ConnActivityParent.logErrors;
    String currProtocol = ConnActivityParent.currProtocol;
    String currProtocolGroup =  ConnActivityParent.currProtocolGroup;
    public String vResponse;

    public void lw( String msg ) {
        if ( logErrors ) {
            lw("MODE09INFO: " + msg);
            System.out.println( "MODE09INFO: " + msg);
        }
        lw( "This is current currProtocolGroup = "+ currProtocolGroup );
    }

    public Mode09Info( BluetoothSocket mSocket ){
        this.sSocket=mSocket;
        //get01CalibrationCount();
        //get02DataCalibrationCount();
    }

    public void runAll() {

    }

    public void get01CalibrationCount(BluetoothSocket mmSocket) {
        try {
            OBDCommand1Byte payData = new OBDCommand1Byte("09 01");
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 01 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 01. Error = "+e);
        }
    }

    public void get02DataCalibrationCount(BluetoothSocket mmSocket) {
        try {
            OBDInitializeDelay payData = new OBDInitializeDelay("09 02", 3);
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 02 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 02. Error = "+e);
        }
    }

    public void get03CalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDCommand1Byte payData = new OBDCommand1Byte("09 03");
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 03 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 03. Error = "+e);
        }
    }

    public void get04DataCalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDInitializeDelay payData = new OBDInitializeDelay("09 04", 3);
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 04 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 04. Error = "+e);
        }
    }

    public void get05CalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDCommand1Byte payData = new OBDCommand1Byte("09 05");
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 05 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 05. Error = "+e);
        }
    }

    public void get06DataCalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDInitializeDelay payData = new OBDInitializeDelay("09 06", 3);
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 06 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 06. Error = "+e);
        }
    }


    public void get07CalibrationCount( BluetoothSocket mmSocket) {
        try {
            OBDCommand1Byte payData = new OBDCommand1Byte("09 07");
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 07 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 07. Error = "+e);
        }
    }

    public void get08DataCalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDInitializeDelay payData = new OBDInitializeDelay("09 08", 3);
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 08 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 08. Error = "+e);
        }
    }

    public void get09CalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDCommand1Byte payData = new OBDCommand1Byte("09 09");
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 09 PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 09. Error = "+e);
        }
    }

    public void get0ADataCalibrationCount( BluetoothSocket mmSocket ) {
        try {
            OBDInitializeDelay payData = new OBDInitializeDelay("09 0A", 3);
            payData.talkToDevice(mmSocket.getInputStream(), mmSocket.getOutputStream());
            String payLoad = removeCarriage(payData.getResponse());
            lw ( "09 0A PayLoad = "+payLoad);
        } catch ( Exception e ) {
            lw ("Error getting 09 0A. Error = "+e);
        }
    }


}
