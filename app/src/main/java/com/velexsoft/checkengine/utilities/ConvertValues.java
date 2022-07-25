package com.velexsoft.checkengine.utilities;

import android.content.Context;
import android.content.res.Resources;

import com.velexsoft.checkengine.R;
import com.velexsoft.checkengine.modess.InterpreteMode01;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by WoleProject on 10/1/2016.
 */

public abstract class ConvertValues {

    public static String ConvertBinStringToHexString(String binary ) {

        String stringToReturn = "";
        if (binary.equals("0000")) {
            stringToReturn = "0";
        } else if (binary.equals("0001")) {
            stringToReturn =  "1";
        } else if (binary.equals("0010")) {
            stringToReturn =  "2";
        }  else if (binary.equals("0011")) {
            stringToReturn = "3";
        }  else if (binary.equals("0100")) {
            stringToReturn = "4";
        }  else if (binary.equals("0101")) {
            stringToReturn = "5";
        }  else if (binary.equals("0110")) {
            stringToReturn = "6";
        }  else if (binary.equals("0111")) {
            stringToReturn = "7";
        }  else if (binary.equals("1000")) {
            stringToReturn = "8";
        }  else if (binary.equals("1001")) {
            stringToReturn = "9";
        }  else if (binary.equals("1010")) {
            stringToReturn = "A";
        }  else if (binary.equals("1011")) {
            stringToReturn = "B";
        }  else if (binary.equals("1100")) {
            stringToReturn = "C";
        }  else if (binary.equals("1101")) {
            stringToReturn = "D";
        }  else if (binary.equals("1110")) {
            stringToReturn = "E";
        }  else if (binary.equals("1111")) {
            stringToReturn = "F";
        }
        return stringToReturn;
    }


    public static boolean isStringHex( String str ) {

        boolean tmp = true;

        for (char c : str.toCharArray()) {
            System.out.println("pos "  + " : " + c);
            if (  isHex2 ( c ) )  {
            }else {
                tmp = false;
            }
        }
        return tmp;
    }


    private static boolean isHex2(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                return true;
            default:
                return false;
        }
    }


    public static String removeFrameHeader ( String  frameHeader ) {
        String text = frameHeader.replace("0:", "").replace("1:", "").replace("2:", "").replace("3:", "").replace("4:", "").replace("5:", "") ;
        text = text.replace("6:", "").replace("6:", "").replace("7:", "").replace("8:", "").replace("9:", "").replace("A:", "").replace("B:", "").replace("C:", "").replace("D:", "").replace("E:", "").replace("F:", "");
        text = text.replace("\n", "").replace("\r", "");
        return text;
    }

    public static String removeErrorString ( String  frameError ) {
        String text = frameError.replace("7F0111", "").replace("7F0211", "").replace("7F0311", "").replace("7F0411", "").replace("7F0511", "").replace("7F0611", "") ;
        text = text.replace("7F0711", "").replace("7F0811", "").replace("7F0911", "").replace("7F0A11", "");  //.replace("9:", "").replace("A:", "").replace("B:", "").replace("C:", "").replace("D:", "").replace("E:", "").replace("F:", "");
        //text = text.replace("\n", "").replace("\r", "");
        return text;
    }

    public static String ConvertFirstDtc(String binary ){
        String firstDtc = "";

        if (binary.equals("00")) {
            firstDtc = "P";
        } else if (binary.equals("01")) {
            firstDtc = "C";
        } else if(binary.equals("10")){
            firstDtc = "B";
        } else if(binary.equals("11")) {
            firstDtc = "U";
        }
        return firstDtc;
    }

    public static double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }

    public static String ConvertSecondDtc(String binary ){
        String SecondDtc = "";
        if (binary.equals("00")) {
            SecondDtc = "0";
        } else if (binary.equals("01")) {
            SecondDtc = "1";
        } else if(binary.equals("10")){
            SecondDtc = "2";
        } else if(binary.equals("11")) {
            SecondDtc = "3";
        }
        return SecondDtc;
    }

    public static String IntegerToHex(Integer num ) {

        return   Integer.toHexString(num);
    }

    public static String MoneyFormat(double amt ) {

        double money = amt;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(money);
        return  moneyString;
    }

    public static String BinaryStringToDECString(String binary ) {
        Integer convertedNum = Integer.parseInt(binary, 2);
        String returnVal = convertedNum.toString();

        return  returnVal;
    }

    public static Integer BinaryStringToInteger(String binary ) {
        Integer convertedNum = Integer.parseInt(binary, 2);
        return  convertedNum;
    }

    public static String hexToBin(String hex){
        String bin = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for(int i = 0; i < hex.length(); i++){
            iHex = Integer.parseInt(""+hex.charAt(i),16);
            binFragment = Integer.toBinaryString(iHex);

            while(binFragment.length() < 4){
                binFragment = "0" + binFragment;
            }
            bin += binFragment;
        }
        return bin;
    }

    public static Integer HexToInteger(String hex ){
        Integer intValue;
        //String hex = "2A"; //The answer is 42
        intValue = Integer.parseInt(hex, 16);
        return intValue;
    }

    public String convertStreamToString (InputStream response ) {
        Scanner s = new Scanner(response).useDelimiter("\\A");
        return s.hasNext( ) ? s.next() : "";
    }

    public static ArrayList<String> getTitleResult( String val , String vResponse, Context context, String mode ) {
        ArrayList<String>  newRow  = new ArrayList<String>();
        ArrayList<String>  subRow  = new ArrayList<String>();

        InterpreteMode01 interprete = new InterpreteMode01();
        try {
            if ( val.equals("P01"))  {
                //newRow.add( context.getString ( R.string.P01 ) );  newRow.add( interprete.P01( vResponse ) );
            }
            else if ( val.equals("P02")) {
                if (mode.equals("02") ) {
                    newRow.add(context.getString(R.string.P02));
                    try {
                        String dtc = interprete.generateDTCCodes(    ConvertValues.hexToBin(vResponse.substring(4, 6) ),
                                ConvertValues.hexToBin(vResponse.substring(6, 8) ) );
                        newRow.add(dtc);
                    } catch (Exception e){
                        System.out.println ( "ERROR ERROR : Cannot decode DTC. Error = "+e);
                    }

                }
            }
            else if ( val.equals("P03")) {
                newRow.add(context.getString(R.string.P03));  newRow.add( interprete.P03( vResponse ) );

            }
            else if ( val.equals("P04")) {
                newRow.add( context.getString ( R.string.P04 ));  newRow.add( interprete.P04( vResponse ) );
            }
            else if ( val.equals("P05")) {
                newRow.add(context.getString ( R.string.P05 ));   newRow.add( interprete.P05( vResponse ) );
            }
            else if ( val.equals("P06")) {
                newRow.add( context.getString ( R.string.P06 )); newRow.add( interprete.P06( vResponse ) );
            }
            else if ( val.equals("P07")) {
                val = context.getString ( R.string.P07 );
                newRow.add( val );    newRow.add( interprete.P06( vResponse ) );
            }
            else if ( val.equals("P08")) {
                val = context.getString ( R.string.P08 );
                newRow.add( val );   newRow.add( interprete.P06( vResponse ) );
            }
            else if ( val.equals("P09")) {
                val = context.getString ( R.string.P09 );
                newRow.add( val );   newRow.add( interprete.P06( vResponse ) );
            }
            else if ( val.equals("P0A")) {
                val = context.getString ( R.string.P0A );
                newRow.add( val );   newRow.add( interprete.P0A( vResponse ) );
            }
            else if ( val.equals("P0B")) {
                val = context.getString ( R.string.P0B );
                newRow.add( val );   newRow.add( interprete.P0B( vResponse ) );
            }
            else if ( val.equals("P0C")) {
                val = context.getString ( R.string.P0C );
                newRow.add( val );   newRow.add( interprete.P0C( vResponse ) );
            }
            else if ( val.equals("P0D")) {
                val = context.getString ( R.string.P0D );
                newRow.add( val );  newRow.add( interprete.P0D( vResponse ) );
            }
            else if ( val.equals("P0E")) {
                val = context.getString ( R.string.P0E );
                newRow.add( val );  newRow.add( interprete.P0E( vResponse ) );
            }
            else if ( val.equals("P0F")) {
                val = context.getString ( R.string.P0F );
                newRow.add( val );  newRow.add( interprete.P0F( vResponse ) );
            }
            else if ( val.equals("P10")) {
                val = context.getString ( R.string.P10 );
                newRow.add( val );  newRow.add( interprete.P10( vResponse ) );
            }
            else if ( val.equals("P11")) {
                val = context.getString ( R.string.P11 );
                newRow.add( val );  newRow.add( interprete.P11( vResponse ) );
            }
            else if ( val.equals("P12")) {
                val = context.getString ( R.string.P12 );
                newRow.add( val );  newRow.add( interprete.P12( vResponse ) );
            }
            else if ( val.equals("P13")) {
                val = context.getString ( R.string.P13 );
                newRow.add( val );   newRow.add( interprete.P13( vResponse ) );
            }
            else if ( val.equals("P14")) {
                val = context.getString ( R.string.P14 );
                newRow.add( val );  newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P15")) {
                val = context.getString ( R.string.P15 );
                newRow.add( val );   newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P16")) {
                val = context.getString ( R.string.P16 );
                newRow.add( val );   newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P17")) {
                val = context.getString ( R.string.P17 );
                newRow.add( val );   newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P18")) {
                val = context.getString ( R.string.P18 );
                newRow.add( val );  newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P19")) {
                val = context.getString ( R.string.P19 );
                newRow.add( val );   newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P1A")) {
                val = context.getString ( R.string.P1A );
                newRow.add( val );  newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P1B")) {
                val = context.getString ( R.string.P1B );
                newRow.add( val );  newRow.add( interprete.P14( vResponse ) );
            }
            else if ( val.equals("P1C")) {
                val = context.getString ( R.string.P1C );
                newRow.add( val );   newRow.add( interprete.P1C( vResponse ) );
            }
            else if ( val.equals("P1D")) {
                val = context.getString ( R.string.P1D );
                newRow.add( val );    newRow.add( interprete.P1D( vResponse ) );
            }
            else if ( val.equals("P1E")) {
                val = context.getString ( R.string.P1E );
                newRow.add( val );   newRow.add( interprete.P1E( vResponse ) );
            }
            else if ( val.equals("P1F")) {
                val = context.getString ( R.string.P1F );
                newRow.add( val );    newRow.add( interprete.P1F( vResponse ) );
            }
            else if ( val.equals("P21")) {
                val = context.getString ( R.string.P21 );
                newRow.add( val );    newRow.add( interprete.P21( vResponse ) );
            }
            else if ( val.equals("P22")) {
                val = context.getString ( R.string.P22 );
                newRow.add( val );   newRow.add( interprete.P22( vResponse ) );
            }
            else if ( val.equals("P23")) {
                val = context.getString ( R.string.P23 );
                newRow.add( val );    newRow.add( interprete.P23( vResponse ) );
            }
            else if ( val.equals("P24")) {
                val = context.getString ( R.string.P24 );
                newRow.add( val );   newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P25")) {
                val = context.getString ( R.string.P25 );
                newRow.add( val );   newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P26")) {
                val = context.getString ( R.string.P26 );
                newRow.add( val );    newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P27")) {
                val = context.getString ( R.string.P27 );
                newRow.add( val );    newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P28")) {
                val = context.getString ( R.string.P28 );
                newRow.add( val );   newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P29")) {
                val = context.getString ( R.string.P29 );
                newRow.add( val );   newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P2A")) {
                val = context.getString ( R.string.P2A );
                newRow.add( val );   newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P2B")) {
                val = context.getString ( R.string.P2B );
                newRow.add( val );   newRow.add( interprete.P24( vResponse ) );
            }
            else if ( val.equals("P2C")) {
                val = context.getString ( R.string.P2C );
                newRow.add( val );   newRow.add( interprete.P2C( vResponse ) );
            }
            else if ( val.equals("P2D")) {
                val = context.getString(R.string.P2D);
                newRow.add( val );   newRow.add( interprete.P2D( vResponse ) );
            }
            else if ( val.equals("P2E")) {
                val = context.getString ( R.string.P2E );
                newRow.add( val );   newRow.add( interprete.P2E( vResponse ) );
            }
            else if ( val.equals("P2F")) {
                val = context.getString ( R.string.P2F );
                newRow.add( val );   newRow.add( interprete.P2F( vResponse ) );
            }
            else if ( val.equals("P30")) {
                val = context.getString ( R.string.P30 );
                newRow.add( val );    newRow.add( interprete.P30( vResponse ) );
            }
            else if ( val.equals("P31")) {
                val = context.getString ( R.string.P31 );
                newRow.add( val );    newRow.add( interprete.P31( vResponse ) );
            }
            else if ( val.equals("P32")) {
                val = context.getString ( R.string.P32 );
                newRow.add( val );   newRow.add( interprete.P32( vResponse ) );
            }
            else if ( val.equals("P33")) {
                val = context.getString ( R.string.P33 );
                newRow.add( val );   newRow.add( interprete.P33( vResponse ) );
            }
            else if ( val.equals("P34")) {
                val = context.getString ( R.string.P34 );
                newRow.add( val );   newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P35")) {
                val = context.getString ( R.string.P35 );
                newRow.add( val );    newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P36")) {
                val = context.getString ( R.string.P36 );
                newRow.add( val );   newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P37")) {
                val = context.getString ( R.string.P37 );
                newRow.add( val );   newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P38")) {
                val = context.getString ( R.string.P38 );
                newRow.add( val );   newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P39")) {
                val = context.getString ( R.string.P39 );
                newRow.add( val );  newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P3A")) {
                val = context.getString ( R.string.P3A );
                newRow.add( val );   newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P3B")) {
                val = context.getString ( R.string.P3B );
                newRow.add( val );   newRow.add( interprete.P34( vResponse ) );
            }
            else if ( val.equals("P3C")) {
                val = context.getString ( R.string.P3C );
                newRow.add( val );   newRow.add( interprete.P3C( vResponse ) );
            }
            else if ( val.equals("P3D")) {
                val = context.getString ( R.string.P3D );
                newRow.add( val );   newRow.add( interprete.P3C( vResponse ) );
            }
            else if ( val.equals("P3E")) {
                val = context.getString ( R.string.P3E );
                newRow.add( val );   newRow.add( interprete.P3C( vResponse ) );
            }
            else if ( val.equals("P3F")) {
                val = context.getString ( R.string.P3F );
                newRow.add( val );   newRow.add( interprete.P3C( vResponse ) );
            }
            /*
            else if ( val.equals("P41")) {
                val = context.getString ( R.string.P41 );
                newRow.add( val );   newRow.add( vResponse );
            }
            */
            else if ( val.equals("P42")) {
                val = context.getString ( R.string.P42 );
                newRow.add( val );   newRow.add( interprete.P42( vResponse ) );
            }
            else if ( val.equals("P43")) {
                val = context.getString ( R.string.P43 );
                newRow.add( val );   newRow.add( interprete.P43( vResponse ) );
            }
            else if ( val.equals("P44")) {
                val = context.getString ( R.string.P44 );
                newRow.add( val );   newRow.add( interprete.P44( vResponse ) );
            }
            else if ( val.equals("P45")) {
                val = context.getString ( R.string.P45 );
                newRow.add( val );   newRow.add( interprete.P45( vResponse ) );
            }
            else if ( val.equals("P46")) {
                val = context.getString ( R.string.P46 );
                newRow.add( val );  newRow.add( interprete.P46( vResponse ) );
            }
            else if ( val.equals("P47")) {
                val = context.getString ( R.string.P47 );
                newRow.add( val );   newRow.add( interprete.P47( vResponse ) );
            }
            else if ( val.equals("P48")) {
                val = context.getString ( R.string.P48 );
                newRow.add( val );   newRow.add( interprete.P47( vResponse ) );
            }
            else if ( val.equals("P49")) {
                val = context.getString ( R.string.P49 );
                newRow.add( val );  newRow.add( interprete.P47( vResponse ) );
            }
            else if ( val.equals("P4A")) {
                val = context.getString ( R.string.P4A );
                newRow.add( val ); newRow.add( interprete.P47( vResponse ) );
            }
            else if ( val.equals("P4B")) {
                val = context.getString ( R.string.P4B );
                newRow.add( val );  newRow.add( interprete.P47( vResponse ) );
            }
            else if ( val.equals("P4C")) {
                val = context.getString ( R.string.P4C );
                newRow.add( val );  newRow.add( interprete.P47( vResponse ) );
            }
            else if ( val.equals("P4D")) {
                val = context.getString ( R.string.P4D );
                newRow.add( val );   newRow.add( interprete.P4D( vResponse ) );
            }
            else if ( val.equals("P4E")) {
                val = context.getString ( R.string.P4E );
                newRow.add( val );  newRow.add( interprete.P4E( vResponse ) );
            }
            else if ( val.equals("P4F")) {
                val = context.getString ( R.string.P4F );
                newRow.add( val );  newRow.add( interprete.P4F( vResponse ) );
            }
            else if ( val.equals("P50")) {
                val = context.getString ( R.string.P50 );
                newRow.add( val );   newRow.add(  interprete.P50( vResponse ) );
            }
            else if ( val.equals("P51")) {
                val = context.getString ( R.string.P51 );
                newRow.add( val );   newRow.add(  interprete.P51( vResponse ));
            }
            else if ( val.equals("P52")) {
                val = context.getString ( R.string.P52 );
                newRow.add( val );   newRow.add( interprete.P52( vResponse ) );
            }
            else if ( val.equals("P53")) {
                val = context.getString ( R.string.P53 );
                newRow.add( val );   newRow.add( interprete.P53( vResponse ) );
            }
            else if ( val.equals("P54")) {
                val = context.getString ( R.string.P54 );
                newRow.add( val );   newRow.add( interprete.P54( vResponse ) );
            }
            else if ( val.equals("P55")) {
                val = context.getString ( R.string.P55 );
                newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P56")) {
                val = context.getString ( R.string.P56 );
                newRow.add( val );  newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P57")) {
                val = context.getString ( R.string.P57 );
                newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P58")) {
                val = context.getString ( R.string.P58 );
                newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P59")) {
                val = context.getString ( R.string.P59 );
                newRow.add( val );  newRow.add( interprete.P59( vResponse ) );
            }
            else if ( val.equals("P5A")) {
                val = context.getString ( R.string.P5A );
                newRow.add( val ); newRow.add( interprete.P5A( vResponse ) );
            }
            else if ( val.equals("P5B")) {
                val = context.getString ( R.string.P5B );
                newRow.add( val );  newRow.add( interprete.P5B( vResponse ) );
            }
            else if ( val.equals("P5C")) {
                val = context.getString ( R.string.P5C );
                newRow.add( val );  newRow.add( interprete.P5C( vResponse ) );
            }
            else if ( val.equals("P5D")) {
                val = context.getString ( R.string.P5D );
                newRow.add( val );   newRow.add( interprete.P5D( vResponse ) );
            }
            else if ( val.equals("P5E")) {
                val = context.getString ( R.string.P5E );
                newRow.add( val );  newRow.add( interprete.P5E( vResponse ) );
            }
            else if ( val.equals("P5F")) {
                val = context.getString ( R.string.P5F );
                newRow.add( val );  newRow.add( interprete.P5F( vResponse ) );
            }
            else if ( val.equals("P51")) {
                val = context.getString ( R.string.P51 );
                newRow.add( val );   newRow.add(  interprete.P51( vResponse ));
            }
            else if ( val.equals("P52")) {
                val = context.getString ( R.string.P52 );
                newRow.add( val );   newRow.add( interprete.P52( vResponse ) );
            }
            else if ( val.equals("P53")) {
                val = context.getString ( R.string.P53 );
                newRow.add( val );   newRow.add( interprete.P53( vResponse ) );
            }
            else if ( val.equals("P54")) {
                val = context.getString ( R.string.P54 );
                newRow.add( val );   newRow.add( interprete.P54( vResponse ) );
            }
            else if ( val.equals("P55")) {
                val = context.getString ( R.string.P55 );
                newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P56")) {
                val = context.getString ( R.string.P56 );
                newRow.add( val );  newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P57")) {
                val = context.getString ( R.string.P57 );
                newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P58")) {
                val = context.getString ( R.string.P58 );
                newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P59")) {
                val = context.getString ( R.string.P59 );
                newRow.add( val );  newRow.add( interprete.P59( vResponse ) );
            }
            else if ( val.equals("P5A")) {
                val = context.getString ( R.string.P5A );
                newRow.add( val ); newRow.add( interprete.P5A( vResponse ) );
            }
            else if ( val.equals("P5B")) {
                val = context.getString ( R.string.P5B );
                newRow.add( val );  newRow.add( interprete.P5B( vResponse ) );
            }
            else if ( val.equals("P5C")) {
                val = context.getString ( R.string.P5C );
                newRow.add( val );  newRow.add( interprete.P5C( vResponse ) );
            }
            else if ( val.equals("P5D")) {
                val = context.getString ( R.string.P5D );
                newRow.add( val );   newRow.add( interprete.P5D( vResponse ) );
            }
            else if ( val.equals("P5E")) {
                val = context.getString ( R.string.P5E );
                newRow.add( val );  newRow.add( interprete.P5E( vResponse ) );
            }
            else if ( val.equals("P5F")) {
                val = context.getString ( R.string.P5F );
                newRow.add( val );  newRow.add( interprete.P5F( vResponse ) );
            }
            else if ( val.equals("P61")) {
                val = context.getString ( R.string.P61 );
                newRow.add( val );   newRow.add(  interprete.P51( vResponse ));
            }
            else if ( val.equals("P62")) {
                val = context.getString ( R.string.P62 );
                newRow.add( val );   newRow.add( interprete.P52( vResponse ) );
            }
            else if ( val.equals("P63")) {
                val = context.getString ( R.string.P63 );
                newRow.add( val );   newRow.add( interprete.P53( vResponse ) );
            }
            else if ( val.equals("P64")) {
                val = context.getString ( R.string.P64 );
                newRow.add( val );   newRow.add( interprete.P54( vResponse ) );
            }
            else if ( val.equals("P65")) {
                val = context.getString ( R.string.P65 );
                //newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P66")) {
                val = context.getString ( R.string.P66 );
                //newRow.add( val );  newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P67")) {
                val = context.getString ( R.string.P67 );
                //newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P68")) {
                val = context.getString ( R.string.P68 );
                //newRow.add( val );   newRow.add( interprete.P55( vResponse ) );
            }
            else if ( val.equals("P69")) {
                val = context.getString ( R.string.P69 );
                //newRow.add( val );  newRow.add( interprete.P59( vResponse ) );
            }
            else if ( val.equals("P6A")) {
                val = context.getString ( R.string.P6A );
                //newRow.add( val ); newRow.add( interprete.P5A( vResponse ) );
            }
            else if ( val.equals("P6B")) {
                val = context.getString ( R.string.P6B );
                //newRow.add( val );  newRow.add( interprete.P5B( vResponse ) );
            }
            else if ( val.equals("P6C")) {
                val = context.getString ( R.string.P6C );
                //newRow.add( val );  newRow.add( interprete.P5C( vResponse ) );
            }
            else if ( val.equals("P6D")) {
                val = context.getString ( R.string.P6D );
                //newRow.add( val );   newRow.add( interprete.P5D( vResponse ) );
            }
            else if ( val.equals("P6E")) {
                val = context.getString ( R.string.P6E );
                //newRow.add( val );  newRow.add( interprete.P5E( vResponse ) );
            }
            else if ( val.equals("P6F")) {
                val = context.getString ( R.string.P6F );
                //newRow.add( val );  newRow.add( interprete.P5F( vResponse ) );
            }
            else if ( val.equals("P71")) {
                val = context.getString ( R.string.P71 );
                //newRow.add( val );   newRow.add( interprete.P71( vResponse ) );
            }
            else if ( val.equals("P72")) {
                val = context.getString ( R.string.P72 );
                //newRow.add( val );  newRow.add( interprete.P72( vResponse ) );
            }
            else if ( val.equals("P73")) {
                val = context.getString ( R.string.P73 );
                //newRow.add( val );  newRow.add( interprete.P73( vResponse ) );
            }
            else if ( val.equals("P74")) {
                val = context.getString ( R.string.P74 );
                //newRow.add( val );   newRow.add( interprete.P74( vResponse ) );
            }
            else if ( val.equals("P75")) {
                val = context.getString ( R.string.P75 );
                //newRow.add( val );  newRow.add( interprete.P75( vResponse ) );
            }
            else if ( val.equals("P76")) {
                val = context.getString ( R.string.P76 );
                //newRow.add( val );  newRow.add( interprete.P76( vResponse ) );
            }
            else if ( val.equals("P77")) {
                val = context.getString ( R.string.P77 );
                //newRow.add( val );   newRow.add( interprete.P77( vResponse ) );
            }
            else if ( val.equals("P78")) {
                val = context.getString ( R.string.P78 );
                //newRow.add( val );  newRow.add( interprete.P78( vResponse ) );
            }
            else if ( val.equals("P79")) {
                val = context.getString ( R.string.P79 );
                //newRow.add( val );  newRow.add( interprete.P79( vResponse ) );
            }
            else if ( val.equals("P7A")) {
                val = context.getString ( R.string.P7A );
                //newRow.add( val ); newRow.add( interprete.P7A( vResponse ) );
            }
            else if ( val.equals("P7B")) {
                val = context.getString ( R.string.P7B );
                //newRow.add( val );  newRow.add( interprete.P7B( vResponse ) );
            }
            else if ( val.equals("P7C")) {
                val = context.getString ( R.string.P7C );
                //newRow.add( val );  newRow.add( interprete.P7C( vResponse ) );
            }
            else if ( val.equals("P7D")) {
                val = context.getString ( R.string.P7D );
                //newRow.add( val );   newRow.add( interprete.P7D( vResponse ) );
            }
            else if ( val.equals("P7E")) {
                val = context.getString ( R.string.P7E );
                //newRow.add( val );  newRow.add( interprete.P7E( vResponse ) );
            }
            else if ( val.equals("P7F")) {
                val = context.getString ( R.string.P7F );
                //newRow.add( val );  newRow.add( interprete.P7F( vResponse ) );
            }
            else {
                // val = "To be Completed";
                // newRow.add( val );   newRow.add( "Response Data" );
            }

            return newRow;
        }catch ( Exception e) {
            e.printStackTrace();
            return newRow;
        }
    }

}
