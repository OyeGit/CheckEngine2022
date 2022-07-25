package com.velexsoft.checkengine.modess;

import android.content.Context;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.IntepretDTCCodes.BodyTroubleCodes;
import com.velexsoft.checkengine.IntepretDTCCodes.ChasisTroubleCodes;
import com.velexsoft.checkengine.IntepretDTCCodes.NetworkTroubleCodes;
import com.velexsoft.checkengine.IntepretDTCCodes.PowerTrainCodes;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogCloudFileNew;
import com.velexsoft.checkengine.utilities.LogFileNew;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.velexsoft.checkengine.ConnActivityParent.logErrors;

/**
 * Created by WoleProject on 11/10/2016.
 */

public class OBDReadDTCCodesNew {
    protected ArrayList<Integer> vBuffer = null;
    protected String vCommand = null;
    LogFileNew l = new LogFileNew() ;
    public StringBuilder res = new StringBuilder();
    int numOfDTCCodes = 0;
    String respByte = "";
    int numOfP0000 = 0;
    int numberingdtc = 0;
    int numberingdtcFrame = 0;
    String theCommand = "";
    String theCommandResponse = "";
    Context context;
    public ArrayList<ArrayList<String>> DTCCodeArray = new ArrayList();
    public ArrayList<String> dTCArray = new ArrayList();
    int threadSleepSendCmd = ConnActivityParent.threadSleepSendCmd;
    NetworkTroubleCodes networx ;
    PowerTrainCodes   powertrain ;
    BodyTroubleCodes  bodyworx ;
    ChasisTroubleCodes  chasisworx ;
    String protocolGrp = ConnActivityParent.currProtocolGroup;
    String protocol = ConnActivityParent.currProtocol;
    String protocolDesc = ConnActivityParent.currProtocolDesc;
    LogCloudFileNew c;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context,"READDTCCOD: " + msg);
        }
    }

    public OBDReadDTCCodesNew(String pCommand, int numofDTC, Context pContext) {
        this.context = pContext;
        c = new LogCloudFileNew( "OBDReadDTCCodesNew3", pContext );
        theCommand = pCommand;
        l.fileName = ConnActivityParent.logFileName;
        lw ("PROGRAM   : OBDReadDTCCodesNew.Constructor, pCommand = "+pCommand );
        lw ("XXXX - protocolGrp = "+ protocolGrp);
        lw ("XXXX - protocol = "+ protocol);
        lw ("XXXX - protocolDesc = "+ protocolDesc);

        if ( pCommand.equals("03") ) {
            numOfDTCCodes = numofDTC;
            theCommandResponse = "43";
        } else if ( pCommand.equals("07") ) {
            theCommandResponse = "47";
        } else if ( pCommand.equals("0A") ) {
            theCommandResponse = "4A";
        }

        this.vCommand = pCommand;
        this.vBuffer = new ArrayList<Integer>();
        networx = new NetworkTroubleCodes( context );
        powertrain = new PowerTrainCodes( context );
        bodyworx = new BodyTroubleCodes( context );
        chasisworx = new ChasisTroubleCodes( context );
    }

    public void talkToDevice(InputStream in, OutputStream out) throws IOException,
            InterruptedException {
        sendInputStream(out);
        // int timeToWait = threadSleepSendCmd * 10;
        int timeToWait = 75;
        lw ("XXXX - timeToWait = "+ timeToWait);
        Thread.sleep(timeToWait);
        getResponseStream(in);
    }

    public void sendInputStream(OutputStream out) throws IOException,
            InterruptedException {
        vCommand += "\r";
        out.write(vCommand.getBytes());
        out.flush();
    }

    public void getResponseStream(InputStream in) throws IOException {
        byte bData = 0;
        while ((char) (bData = (byte) in.read()) != '>') {
            res.append((char) bData);
        }
    }

    public String getResponse() {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.getResponse" );
        respByte = res.toString();
        lw ("respByte  res.toString()= "+   respByte  );

        respByte = ConvertValues.removeFrameHeader(  respByte );
        lw ("respByte  removeFrameHeader= "+   respByte  );

        respByte = ConvertValues.removeErrorString(  respByte );

        lw ("respByte before takeBoggus = "+   respByte  );

        respByte = takeBoggus ( respByte );

        lw ("respByte after takeBoggus = "+   respByte  );
        String response = respByte;

        lw ("XXXX - protocolGrp = "+ protocolGrp);
        lw ("XXXX - protocol = "+ protocol);
        lw ("XXXX - protocolDesc = "+ protocolDesc);

        if ( protocolGrp.contains("J1850")) {
            lw("respByte = " + respByte+", protocolGrp = "+protocolGrp );
            respByte = preByteJ1850Computation(respByte);
        } else if ( protocolGrp.contains("ISO")    ) {
            lw("respByte = " + respByte + ", protocolGrp = " + protocolGrp);
            respByte = preByteJ1850Computation(respByte);
        } else if ( protocolGrp.contains("CAN")    ) {
            lw("respByte = " + respByte + ", protocolGrp = " + protocolGrp);
            respByte = preByteCANComputation(respByte);
        }
        decodeTroubleCode();

        c.wdtc( theCommand , response , numOfDTCCodes+"", dTCArray  );
        return respByte;
    }

    public String takeBoggus ( String payLoad ) {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.takeBoggus" );
        String temp = payLoad.trim();
        lw("This is payLoad.trim() = "+temp+" ,VCommand = "+vCommand);

        if ( vCommand.contains("03") ) {
            if (temp.equals("4300")||temp.equals("43004300")||temp.equals("430043004300") || temp.equals("43000000000000")  || temp.equals("4300000000000043000000000000") ) {
                lw("temp = equals one of this");
                temp = "";
            }
        } else if ( vCommand.contains("07") ) {
            lw ("THis is 07");
            if (temp.equals("4700")||temp.equals("47004700")||temp.equals("470047004700") || temp.equals("47000000000000")  || temp.equals("4700000000000047000000000000") ) {
                temp = "";
            }
        } else if ( vCommand.contains("0A") ) {
            if (temp.equals("4A00")||temp.equals("4A004A00")||temp.equals("4A004A004A00") || temp.equals("4A000000000000")  || temp.equals("4A0000000000004A000000000000") ) {
                temp = "";
            }
        }

        lw("This is temp = "+temp);
        return temp;
    }

    public String preByteCANComputation ( String payLoad ) {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.preByteCANComputation" );
        lw("This is PayLoad = "+payLoad);
        String newPayLoad = "";
        try {
            if (payLoad.isEmpty()) {
                lw("PayLoad is Empty....");
                //return payLoad;
            } else if ( respByte.substring(0, 2).equals(theCommandResponse)) {
                lw("There is no PreByte : = respByte = " + respByte);
                newPayLoad =  payLoad;
            } else if ( respByte.substring(3, 5).equals(theCommandResponse) ) {
                String ByteCANNoDTC = respByte.substring(5,7);
                numberingdtcFrame = ConvertValues.HexToInteger(  ByteCANNoDTC );
                lw ("XXXX - Number of DTC ByteCANNoDTC = "+ByteCANNoDTC+", numberingdtcFrame = "+numberingdtcFrame);
                String payLoadRemovePreByte = respByte.substring( 5 );
                lw("There is PreByte : = payLoadRemovePreByte = " + payLoadRemovePreByte);
                newPayLoad = payLoadRemovePreByte;
            }
        } catch ( Exception e ) {
            lw ( "ERROR ERROR : OBDReadDTCCodesNew.preByteComputation. Error = " + e );
        }
        return newPayLoad;
    }

    final char[] dtcLetters = {'P', 'C', 'B', 'U'};
    final char[] hexArray = "0123456789ABCDEF".toCharArray();

    String deframedResponse;

    public String deframeJ1850Bytes ( String troubleResponse) {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.deframeJ1850Bytes" );
        deframedResponse = "";
        int tripleCounter = 0;
        // int embedNoOfDTC = ConvertValues.HexToInteger( troubleResponse.substring(0,2) );
        // lw (  "Embeded NoOfDTC = "+ embedNoOfDTC );
        int begin = 2;
        int end = begin + 4;
        int stringLength = troubleResponse.length();
        lw (" deframeJ1850Bytes stringLength = " + stringLength);
        try {
            for (int t = 0; t < stringLength; t++) {
                String tempString = troubleResponse.substring(begin, end);
                deframedResponse = deframedResponse + tempString;
                tripleCounter++;
                lw("["+tripleCounter+"] deframeJ1850Bytes = " + deframedResponse);
                if ( tripleCounter == 3 ) {
                    begin = end + 2;
                    tripleCounter = 0;
                } else {
                    begin = end;
                }
                end = begin + 4;
            }
        } catch ( Exception e ) {
            lw ("WARNING   : OBDReadDTCCodesNew.deframeJ1850Bytes = " + e);
        }
        return deframedResponse;
    }

    public String preByteJ1850Computation ( String payLoad ) {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.preByteJ1850Computation" );
        lw("This is PayLoad = "+payLoad);
        String newPayLoad = "";
        try {
            if (payLoad.isEmpty()) {
                lw("PayLoad is Empty....");
                // return payLoad;
            } else if ( respByte.substring(0, 2).equals(theCommandResponse)) {
                lw("There is PreByte [0,2] : = respByte = " + respByte);
                newPayLoad = payLoad;
            } else if ( respByte.substring(3, 5).equals(theCommandResponse) ) {
                String ByteCANNoDTC = respByte.substring(5,7);
                numberingdtcFrame = ConvertValues.HexToInteger(  ByteCANNoDTC );
                lw ("XXXX - Number of DTC ByteCANNoDTC = "+ByteCANNoDTC+", numberingdtcFrame = "+numberingdtcFrame);
                String payLoadRemovePreByte = respByte.substring( 5 );
                lw("There is PreByte : = payLoadRemovePreByte = " + payLoadRemovePreByte);
                newPayLoad = payLoadRemovePreByte;
            }
        }    catch ( Exception e ) {
            lw ( "ERROR ERROR : OBDReadDTCCodesNew.preByteJ1850Computation" + e );
        }
        return payLoad;
    }

    public String deframeCANBytes ( String troubleResponse) {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.deframeCANBytes" );
        deframedResponse = "";
        int tripleCounter = 0;
        int begin = 2;
        int end = begin + 4;
        int stringLength = troubleResponse.length();
        lw (" deframeCANBytes stringLength = " + stringLength);
        try {
            for (int t = 0; t < stringLength; t++) {
                String tempString = troubleResponse.substring(begin, end);
                deframedResponse = deframedResponse + tempString;
                tripleCounter++;
                lw("["+tripleCounter+"] deframeCANBytes = " + deframedResponse);
                begin = end;
                end = begin + 4;
            }
        } catch ( Exception e ) {
            lw ("WARNING   : OBDReadDTCCodesNew.deframeCANBytes = " + e);
        }
        return deframedResponse;
    }

    public void decodeTroubleCode() {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.decodeTroubleCode" );
        final String troubleResponse = respByte;
        String firstByte;
        String SecondByte;
        String twoBytes;
        String deframedString = "";
        lw ("troubleResponse = "+troubleResponse );
        if (protocolGrp.contains("J1850")) {
            deframedString = deframeJ1850Bytes(troubleResponse);
        } else if (protocolGrp.contains("ISO")) {
            deframedString = deframeJ1850Bytes(troubleResponse);
        } else if (protocolGrp.contains("CAN"))  {
            deframedString = deframeCANBytes(troubleResponse);
        }
        lw ("deframedString = "+deframedString );

        int begin = 0;
        int end = begin + 4;

        if (theCommand.contains("07") || theCommand.contains("0A")   )  {
            numOfDTCCodes = 20;
            lw ( "numberingdtcFrame = " + numberingdtcFrame );
        }

        try {
            for (int i = 0; i < numOfDTCCodes; i++) {
                twoBytes = deframedString.substring(begin, end );
                firstByte = twoBytes.substring( 0, 2 );
                SecondByte = twoBytes.substring( 2, 4 );

                if ( twoBytes.equals("0000") ) {
                    return;
                }else {
                    lw ("firstByte = " + firstByte + ", SecondByte = " + SecondByte);
                    begin = end;
                    end = begin + 4;
                    String firstBinary = ConvertValues.hexToBin(firstByte);
                    String secondBinary = ConvertValues.hexToBin(SecondByte);
                    lw ("firstBinary = " + firstBinary + ", secondBinary = " + secondBinary);
                    generateDTCCodes(firstBinary, secondBinary);
                }
            }
        } catch ( Exception e ) {
            lw("ERROR ERROR : OBDReadDTCCodesNew.decodeTroubleCode. Error = " + e);
        }
    }

    public void generateDTCCodes (String firstBinary, String secondBinary ) {
        lw  ( "PROGRAM   : OBDReadDTCCodesNew.generateDTCCodes" );
        String finalTroubleCode = "";
        try {
            if ((firstBinary.isEmpty()) || (secondBinary.isEmpty())) {
                lw("ERROR - First or Second binary is null");
            } else {
                String first2bits = firstBinary.substring(0, 2);
                //System.out.println( "This is first 2 bits first2bits"+first2bits );
                String Second2bits = firstBinary.substring(2, 4);
                //System.out.println( "This is second 2 bits Second2bits "+Second2bits );
                String firstNum = firstBinary.substring(4, 8);
                String SecondNum = secondBinary.substring(0, 4);
                String ThirdNum = secondBinary.substring(4, 8);

                finalTroubleCode = ConvertValues.ConvertFirstDtc(first2bits);
                finalTroubleCode = finalTroubleCode + ConvertValues.ConvertSecondDtc(Second2bits);

                finalTroubleCode = finalTroubleCode + ConvertValues.ConvertBinStringToHexString(firstNum);
                finalTroubleCode = finalTroubleCode + ConvertValues.ConvertBinStringToHexString(SecondNum);
                finalTroubleCode = finalTroubleCode + ConvertValues.ConvertBinStringToHexString(ThirdNum);

                if (finalTroubleCode.equals("P0000")) {

                } else {
                    ArrayList<String> dtcRecord = new ArrayList();
                    numberingdtc++;
                    dtcRecord.add("(" + numberingdtc + ")");
                    dtcRecord.add(finalTroubleCode);
                    dtcRecord.add(loadupCategory(finalTroubleCode));
                    dtcRecord.add(loadupTCode(finalTroubleCode));
                    // System.out.println("Final DTC Code = " + finalTroubleCode);
                    DTCCodeArray.add(dtcRecord);
                    dTCArray.add(  "(" + numberingdtc + ") "+finalTroubleCode  );
                }
            }
        } catch ( Exception e ) {
            lw("ERROR ERROR : OBDReadDTCCodesNew.generateDTCCodes. Error = " + e);
        }
    }

    public String loadupCategory (String tCode ){
        ArrayList<String> troubleCoderow = new ArrayList<String>();
        troubleCoderow.add( 0, tCode  );
        String firstCodeCharacter = tCode.substring(0,1 );
        if ( firstCodeCharacter.equals("P") ) {
            return "PowerTrain";
        } else if ( firstCodeCharacter.equals("C") ) {
            return "Chasis";
        } else if ( firstCodeCharacter.equals("U") ) {
            return "Network";
        } else if ( firstCodeCharacter.equals("B") ) {
            return "Body";
        } else {
            return "No Data";
        }
    }

    public String loadupTCode (String tCode ){
        ArrayList<String> troubleCoderow = new ArrayList<String>();
        troubleCoderow.add( 0, tCode  );
        String firstCodeCharacter = tCode.substring(0,1 );
        if ( firstCodeCharacter.equals("P") ) {
            return powertrain.PowerTrainCodes1(tCode);
        } else if ( firstCodeCharacter.equals("C") ) {
            return chasisworx.ChasisTroubleCodes1(tCode);
        } else if ( firstCodeCharacter.equals("U") ) {
            return networx.NetworkTroubleCodes1(tCode );
        } else if ( firstCodeCharacter.equals("B") ) {
            return bodyworx.BodyTroubleCodes1(tCode);
        } else {
            return "No Data";
        }
    }

}
