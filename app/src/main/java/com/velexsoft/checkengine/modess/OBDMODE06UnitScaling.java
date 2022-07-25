package com.velexsoft.checkengine.modess;

import android.content.Context;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.utilities.ConvertValues;
import com.velexsoft.checkengine.utilities.LogFileNew;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by ooyeg on 12/19/2017.
 */

public class OBDMODE06UnitScaling {

    LogFileNew l;
    Boolean logErrors = true;
    Context context;

    String DEGREE = "\u00B0";
    public OBDMODE06UnitScaling( Context vcontext ) {
        // l.fileName = ConnActivityParent.logFileName;
        this.context = vcontext;
    }

    public void lw( String msg ) {
        if ( logErrors ) {
            //k  l.w ( context, "UNITSCALES: "+msg );
            System.out.println( "UNITSCALES : "+msg  );
        }
    }

    public ArrayList<String> getDefinition ( String vResponse ) {
        DecimalFormat df = new DecimalFormat("#.###");
        ArrayList<String>  arr = new ArrayList();
        String unitScaling = vResponse.substring( 4,6 );
        String vUnit = "";
        boolean unsignedFlag = true;

        double scaleValue = 0.0000305;
        boolean valueAvailable = true;

        String resultValue = vResponse.substring(6,10);
        String min = vResponse.substring(10,14);
        String max = vResponse.substring(14,18);
        lw ( "HEX VALUES: min = "+min +", max = "+max +", resultValue = "+resultValue );

        if ( unitScaling.equals("01") ) {
            scaleValue = 1;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("02") ) {
            scaleValue = 0.1;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("03") ) {
            scaleValue = 0.01;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("04") ) {
            scaleValue = 0.001;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("05") ) {
            scaleValue = 0.0000305;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("06") ) {
            scaleValue = 0.000305;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("07") ) {
            scaleValue = 0.25;
            vUnit = "rpm";
            unsignedFlag = true;
        } else if ( unitScaling.equals("08") ) {
            scaleValue = 0.01;
            vUnit = "km/h";
            unsignedFlag = true;
        } else if ( unitScaling.equals("09") ) {
            scaleValue = 1;
            vUnit = "km/h";
            unsignedFlag = true;
        } else if ( unitScaling.equals("0A") ) {
            scaleValue = 0.122;
            vUnit = "mV";
            unsignedFlag = true;
        } else if ( unitScaling.equals("0B") ) {
            scaleValue = 0.001;
            vUnit = "V";
            unsignedFlag = true;
        } else if ( unitScaling.equals("0C") ) {
            scaleValue = 0.01;
            vUnit = "V";
            unsignedFlag = true;
        } else if ( unitScaling.equals("0D") ) {
            scaleValue =  0.00390625 ;
            vUnit = "mA";
            unsignedFlag = true;
        } else if ( unitScaling.equals("0E") ) {
            scaleValue = 0.001;
            vUnit = "A";
            unsignedFlag = true;
        } else if ( unitScaling.equals("0F") ) {
            scaleValue = 0.01;
            vUnit = "A";
            unsignedFlag = true;
        } else if ( unitScaling.equals("10") ) {
            scaleValue = 1;
            vUnit = "ms";
            unsignedFlag = true;
        } else if ( unitScaling.equals("11") ) {
            scaleValue = 100;
            vUnit = "ms";
            unsignedFlag = true;
        } else if ( unitScaling.equals("12") ) {
            scaleValue = 1;
            vUnit = "second";
            unsignedFlag = true;
        } else if ( unitScaling.equals("13") ) {
            scaleValue = 1;
            vUnit = "mOhm";
            unsignedFlag = true;
        } else if ( unitScaling.equals("14") ) {
            scaleValue = 1;
            vUnit = "Ohm";
            unsignedFlag = true;
        } else if ( unitScaling.equals("15") ) {
            scaleValue = 1;
            vUnit = "kOhm";
            unsignedFlag = true;
        } else if ( unitScaling.equals("16") ) {
            scaleValue = 0.1;
            vUnit = DEGREE;
            unsignedFlag = true;
        } else if ( unitScaling.equals("17") ) {
            scaleValue = 0.01;
            vUnit = "kPa";
            unsignedFlag = true;
        } else if ( unitScaling.equals("18") ) {
            scaleValue = 0.0117;
            vUnit = "kPa";
            unsignedFlag = true;
        } else if ( unitScaling.equals("19") ) {
            scaleValue = 0.079;
            vUnit = "kPa";
            unsignedFlag = true;
        } else if ( unitScaling.equals("1A") ) {
            scaleValue = 1;
            vUnit = "kPa";
            unsignedFlag = true;
        } else if ( unitScaling.equals("1B") ) {
            scaleValue = 10;
            vUnit = "kPa";
            unsignedFlag = true;
        } else if ( unitScaling.equals("1C") ) {
            scaleValue = 0.01;
            vUnit = DEGREE;
            unsignedFlag = true;
        } else if ( unitScaling.equals("1D") ) {
            scaleValue =  0.5 ;
            vUnit = DEGREE;
            unsignedFlag = true;
        } else if ( unitScaling.equals("1E") ) {
            scaleValue = 0.0000305 ;
            vUnit = "lambda";
            unsignedFlag = true;
        } else if ( unitScaling.equals("1F") ) {
            scaleValue = 0.01;
            vUnit = "A/F ratio";
        } else if ( unitScaling.equals("20") ) {
            scaleValue = 1;
            vUnit = "";
            unsignedFlag = true;
        } else if ( unitScaling.equals("21") ) {
            scaleValue = 1;
            vUnit = "mHz";
            unsignedFlag = true;
        } else if ( unitScaling.equals("22") ) {
            scaleValue = 1;
            vUnit = "Hz";
            unsignedFlag = true;
        } else if ( unitScaling.equals("23") ) {
            scaleValue = 1;
            vUnit = "KHz";
            unsignedFlag = true;
        } else if ( unitScaling.equals("24") ) {
            scaleValue = 1;
            vUnit = "counts";
            unsignedFlag = true;
        } else if ( unitScaling.equals("25") ) {
            scaleValue = 1;
            vUnit = "km";
            unsignedFlag = true;
        } else if ( unitScaling.equals("26") ) {
            scaleValue = 0.1;
            vUnit = "mV/ms";
            unsignedFlag = true;
        } else if ( unitScaling.equals("27") ) {
            scaleValue = 0.01;
            vUnit = "g/s";
            unsignedFlag = true;
        } else if ( unitScaling.equals("28") ) {
            scaleValue = 1;
            vUnit = "g/s";
            unsignedFlag = true;
        } else if ( unitScaling.equals("29") ) {
            scaleValue = 0.25;
            vUnit = "Pa/s";
            unsignedFlag = true;
        } else if ( unitScaling.equals("2A") ) {
            scaleValue = 0.001;
            vUnit = "kg/h";
            unsignedFlag = true;
        } else if ( unitScaling.equals("2B") ) {
            scaleValue = 1;
            vUnit = "switches";
            unsignedFlag = true;
        } else if ( unitScaling.equals("2C") ) {
            scaleValue = 0.01;
            vUnit = "g/cyl";
            unsignedFlag = true;
        } else if ( unitScaling.equals("2D") ) {
            scaleValue =  0.01 ;
            vUnit = "mg/stroke";
            unsignedFlag = true;
        } else if ( unitScaling.equals("2E") ) {
            scaleValue = 1 ;
            vUnit = "false";
            unsignedFlag = true;
        } else if ( unitScaling.equals("2F") ) {
            scaleValue = 0.01;
            vUnit = "%";
            unsignedFlag = true;
        } else if ( unitScaling.equals("30") ) {
            scaleValue = 0.001526;
            vUnit = "%";
            unsignedFlag = true;
        } else if ( unitScaling.equals("31") ) {
            scaleValue = 0.001;
            vUnit = "L";
            unsignedFlag = true;
        } else if ( unitScaling.equals("32") ) {
            scaleValue = 0.0000305;
            vUnit = "inches";
            unsignedFlag = true;
        } else if ( unitScaling.equals("33") ) {
            scaleValue = 0.00024414;
            vUnit = "lambda";
            unsignedFlag = true;
        } else if ( unitScaling.equals("34") ) {
            scaleValue = 1;
            vUnit = "mins";
            unsignedFlag = true;
        } else if ( unitScaling.equals("35") ) {
            scaleValue = 10;
            vUnit = "ms";
            unsignedFlag = true;
        } else if ( unitScaling.equals("36") ) {
            scaleValue = 0.01;
            vUnit = "g";
            unsignedFlag = true;
        } else if ( unitScaling.equals("37") ) {
            scaleValue = 0.1;
            vUnit = "g";
            unsignedFlag = true;
        } else if ( unitScaling.equals("38") ) {
            scaleValue = 1;
            vUnit = "g";
            unsignedFlag = true;
        } else if ( unitScaling.equals("39") ) {
            scaleValue = 1;
            vUnit = "%";
            unsignedFlag = true;
        } else if ( unitScaling.equals("81") ) {
            scaleValue = 1;
            vUnit = "";
            unsignedFlag = false;
        } else if ( unitScaling.equals("82") ) {
            scaleValue = 0.1;
            vUnit = "";
            unsignedFlag = false;
        } else if ( unitScaling.equals("83") ) {
            scaleValue = 0.01;
            vUnit = "";
            unsignedFlag = false;
        } else if ( unitScaling.equals("84") ) {
            scaleValue = 0.001;
            vUnit = "";
            unsignedFlag = false;
        } else if ( unitScaling.equals("85") ) {
            scaleValue = 0.0000305;
            vUnit = "";
            unsignedFlag = false;
        } else if ( unitScaling.equals("86") ) {
            scaleValue = 0.000305 ;
            vUnit = "";
            unsignedFlag = false;
        } else if ( unitScaling.equals("8A") ) {
            scaleValue = 0.122 ;
            vUnit = " mV";
            unsignedFlag = false;
        } else if ( unitScaling.equals("8B") ) {
            scaleValue = 0.001;
            vUnit = "V";
            unsignedFlag = false;
        } else if ( unitScaling.equals("8C") ) {
            scaleValue = 0.01;
            vUnit = "V";
            unsignedFlag = false;
        } else if ( unitScaling.equals("8D") ) {
            scaleValue =  0.00390625 ;
            vUnit = "mA";
            unsignedFlag = false;
        } else if ( unitScaling.equals("8E") ) {
            scaleValue = 0.001 ;
            vUnit = "A";
            unsignedFlag = false;
        } else if ( unitScaling.equals("90") ) {
            scaleValue = 1;
            vUnit = "ms";
            unsignedFlag = false;
        } else if ( unitScaling.equals("96") ) {
            scaleValue = 0.1;
            vUnit = DEGREE+"C";
            unsignedFlag = false;
        } else if ( unitScaling.equals("9C") ) {
            scaleValue =  0.01 ;
            vUnit = DEGREE;
            unsignedFlag = false;
        } else if ( unitScaling.equals("9D") ) {
            scaleValue = 0.5 ;
            vUnit = DEGREE;
            unsignedFlag = false;
        } else if ( unitScaling.equals("A8") ) {
            scaleValue = 1;
            vUnit = "g/s";
            unsignedFlag = false;
        } else if ( unitScaling.equals("A9") ) {
            scaleValue = 0.25;
            vUnit = "Pa/s";
            unsignedFlag = false;
        } else if ( unitScaling.equals("AF") ) {
            scaleValue =  0.01 ;
            vUnit = "%";
            unsignedFlag = false;
        } else if ( unitScaling.equals("B0") ) {
            scaleValue = 0.003052 ;
            vUnit = "%";
            unsignedFlag = false;
        } else if ( unitScaling.equals("B1") ) {
            scaleValue = 2;
            vUnit = "mV/s";
            unsignedFlag = false;
        } else if ( unitScaling.equals("FD") ) {
            scaleValue = 0.001;
            vUnit = "kPa";
            unsignedFlag = false;
        } else if ( unitScaling.equals("FE") ) {
            scaleValue = 0.25;
            vUnit = "Pa";
            unsignedFlag = false;
        } else {
            scaleValue = 0.0;
            vUnit = "";
            unsignedFlag = false;
            valueAvailable = false;
        }

        if (valueAvailable ) {
            if (unsignedFlag) {
                lw("Unsigned");
                double dMin = ConvertValues.HexToInteger(min) * scaleValue;
                double dMax = ConvertValues.HexToInteger(max) * scaleValue;
                double dResultValue = ConvertValues.HexToInteger(resultValue) * scaleValue;

                Integer minInt = ConvertValues.HexToInteger(min);
                Integer maxInt = ConvertValues.HexToInteger(max);
                Integer resultInt = ConvertValues.HexToInteger(resultValue);

                if (unitScaling.equals("2E")) {
                    String vResultBool = "false";
                    arr.add("false");
                    arr.add("true");
                    if (dResultValue == 1) vResultBool = "true";
                    arr.add(vResultBool);
                    arr.add("1");
                    arr.add("Angles");

                } else {

                    arr.add(df.format(dMin) + vUnit);
                    arr.add(df.format(dMax) + vUnit);
                    arr.add(df.format(dResultValue) + vUnit);
                    if ((resultInt >= minInt) && (resultInt <= maxInt)) {
                        arr.add("1");
                    } else {
                        arr.add("0");
                    }
                    arr.add("Angle");
                }
                return arr;
            } else {
                lw("-------------------------------------------------------------------------------");
                lw("vResponse = " + vResponse);

                lw("Signed");
                //lw("scaleValue = " + scaleValue );
                lw("HEX VALUES: min = " + min + ", max = " + max + ", resultValue = " + resultValue);
                lw("INTEGER VALUES: min = " + ConvertValues.HexToInteger(min) + ", max = " + ConvertValues.HexToInteger(max) + ", resultValue = " + ConvertValues.HexToInteger(resultValue));

                Integer minByteInt = convertSingedByte(min);
                Integer maxByteInt = convertSingedByte(max);
                Integer resultByteInt = convertSingedByte(resultValue);

                //lw("INTEGER VALUES: minByteInt = " + minByteInt + ", maxByteInt = " + maxByteInt + ", resultByteInt = " + resultByteInt);

                double dMin = minByteInt * scaleValue;
                double dMax = maxByteInt * scaleValue;
                double dResultValue = resultByteInt * scaleValue;

                arr.add(df.format(dMin )+ vUnit);
                arr.add(df.format(dMax) + vUnit);
                arr.add(df.format(dResultValue) + vUnit);

                if ((resultByteInt >= minByteInt) && (resultByteInt <= maxByteInt)) {
                    arr.add("1");
                } else {
                    arr.add("0");
                }
                lw(arr.toString());
                lw("-------------------------------------------------------------------------------");
                return arr;
            }
        } else {

            arr.add("-");
            arr.add("-");
            arr.add("-");
            arr.add("2");

            return arr;
        }
    }

    public Integer convertSingedByte ( String bbyte ) {
        lw ("convertSingedByte");
        Integer tmp = 0;
        Integer byteCount = ConvertValues.HexToInteger(bbyte);
        String BinMin = ConvertValues.hexToBin(bbyte);
        lw ( "byteCount = "+byteCount );
        String firstBit = BinMin.substring(0,1);
        String remaininBin = BinMin.substring(1,16);
        lw ( "firstBit = "+firstBit  );
        lw ( "remaininBin = "+remaininBin  );
        Integer Decresult = ConvertValues.BinaryStringToInteger(remaininBin);
        lw ( "Integer Value remaininBin = "+Decresult  );
        tmp = Decresult - 32766;
        lw( "Final Result = "+tmp);
        return tmp;
    }

}
