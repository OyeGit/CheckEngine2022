package com.velexsoft.checkengine.modess;

import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.R;
import com.velexsoft.checkengine.utilities.ConvertValues;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ooyeg on 12/15/2017.
 */

public class InterpreteMode01 {

    DecimalFormat df = new DecimalFormat("#.##");
    String DEGREE = "\u00B0";
    String lineSep = System.getProperty("line.separator");

    public InterpreteMode01() {

    }

    ArrayList<String>  returnList ;


    public String  P01 (String A ) {
        return A;
    }

    public String P02 (String AB ) {
        return "";
    }

    public String P03 (String AB ) {

        String result = "";
        int num = ConvertValues.HexToInteger(AB.substring(4,6));
        if ( num==1 ) {
            result = "Open loop due to insufficient engine temperature";
        } else if ( num==2 ) {
            result = "Closed loop, using oxygen sensor feedback to determine fuel mix";
        } else if ( num==4 ) {
            result = "Open loop due to engine load OR fuel cut due to deceleration";
        } else if ( num==8 ) {
            result = "Open loop due to system failure";
        } else if ( num==16 ) {
            result = "Closed loop, using at least one oxygen sensor but there is a fault in the feedback system";
        }
        return result;
    }

    public String P04 (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (   dec/2.55  ) ;
        return df.format(result)+" %";
    }

    public String P05 (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec - 40  ) ;
        System.out.println( "This is result 05"+result );
        return df.format(result)+DEGREE+" C";
    }

    public double P05INT (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec - 40  ) ;
        System.out.println( "This is result 05"+result );
        return result;
    }

    public String P06 (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec/1.28 - 100  ) ;
        return df.format(result)+" %";
    }

    public String  P0A (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec * 3 ) ;
        return df.format(result)+" kPa";
    }

    public String  P0B (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec  ) ;
        return df.format(result)+" kPa";
    }

    public String  P0C (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ( (256*A) + B )/ 4;
        return result+" rpm";
    }
    public String  P0D (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec  ) ;
        return df.format(result)+" km/hr";
    }
    public String  P0E (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  (dec/2) - 64  ) ;
        return df.format(result)+DEGREE+" TDC";
    }
    public String  P0F (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec - 40  ) ;
        return df.format(result)+DEGREE+" C";
    }

    public String  P10 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ( (256*A) + B )/ 100;
        return result+" grams/sec";
    }
    public String  P11 (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        double result =  (  dec * 0.392156  ) ;
        return df.format(result)+" %";
    }
    public String  P12 (String A ) {
        Integer dec = ConvertValues.HexToInteger( A.substring(4,6) );
        System.out.println ( "Command Secondary Air = "+  dec );
        String result = CommadedSecAirStatus(dec);
        return result;
    }

    public String  P13 (String A ) {
        String P13Bin = ConvertValues.hexToBin( A.substring(4,6) );
        System.out.println ( "P13Bin = "+P13Bin);
        String stgToReturn = "";

        if (P13Bin.substring(0,1).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 2, Sensor 4"+lineSep;
        } else if (P13Bin.substring(1,2).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 2, Sensor 3"+lineSep;
        } else if (P13Bin.substring(2,3).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 2, Sensor 2"+lineSep;
        } else if (P13Bin.substring(3,4).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 2, Sensor 1"+lineSep;
        } else if (P13Bin.substring(4,5).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 1, Sensor 4"+lineSep;
        } else if (P13Bin.substring(5,6).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 1, Sensor 3"+lineSep;
        } else if (P13Bin.substring(6,7).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 1, Sensor 2"+lineSep;
        } else if (P13Bin.substring(7,8).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 1, Sensor 1"+lineSep;
        }

        return stgToReturn+"";
    }
    public String  P14 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result1 = A/200;
        double result2 =  (0.78125*B) - 100 ;
        return df.format(result1)+" volts/"+df.format(result2)+"%";
    }

    public String  P1C (String A ) {
        Integer result = ConvertValues.HexToInteger ( A.substring(4,6) );
        return getOBDStandard( result+"" );
    }
    public String  P1D (String A ) {
        String P1DBin = ConvertValues.hexToBin( A.substring(4,6) );
        System.out.println ( "P1DBin = "+P1DBin);
        String stgToReturn = "";

        if (P1DBin.substring(0,1).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 4, Sensor 2"+lineSep;
        } else if (P1DBin.substring(1,2).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 4, Sensor 1"+lineSep;
        } else if (P1DBin.substring(2,3).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 3, Sensor 2"+lineSep;
        } else if (P1DBin.substring(3,4).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 3, Sensor 1"+lineSep;
        } else if (P1DBin.substring(4,5).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 2, Sensor 2"+lineSep;
        } else if (P1DBin.substring(5,6).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 2, Sensor 1"+lineSep;
        } else if (P1DBin.substring(6,7).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 1, Sensor 2"+lineSep;
        } else if (P1DBin.substring(7,8).equals("1") ) {
            stgToReturn = stgToReturn + "Bank 1, Sensor 1"+lineSep;
        }
        return stgToReturn;
    }

    public String  P1E (String A ) {
        String P1EBin = ConvertValues.hexToBin( A.substring(4,6) );
        String   stgToReturn = "";
        if (P1EBin.substring(7,8).equals("1") ) {
            stgToReturn = "Power Take Off active (ON)";
        } else if (P1EBin.substring(7,8).equals("0") ) {
            stgToReturn = "Power Take Off not active (OFF)";
        }
        return stgToReturn;
    }
    public String  P1F (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ( 256*A ) + B;
        return result+" seconds";
    }
    public String  P21 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ( 256*A ) + B;
        return result+" km";
    }
    public String  P22 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ( 0.079*(256*A + B) );
        return df.format(result)+" kPa";
    }
    public String  P23 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ( 10*(256*A + B) );
        return df.format(result)+" kPa";
    }
    public String  P24 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double C = ConvertValues.HexToInteger ( AB.substring(8,10) );
        double D = ConvertValues.HexToInteger ( AB.substring(10,12) );
        double result1 = ( 0.00003051 *( (256*A) + B) );
        double result2 = ( 0.00012207 *( (256*C) + D) );
        return df.format(result1)+"/"+df.format(result2)+"V";
    }
    public String  P2C (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return df.format(result)+" %";
    }
    public String  P2D (String A ) {
        double result = ConvertValues.HexToInteger ( A.substring(4,6) );
        result =  ((100/128)*result) - 100 ;
        return df.format(result)+" %";
    }
    public String  P2E (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return df.format(result)+" %";
    }
    public String  P2F (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return df.format(result)+" %";
    }

    public double  P2FINT (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return result;
    }
    public String  P30 (String A ) {
        double result = ConvertValues.HexToInteger ( A.substring(4,6) );
        return result+"";
    }
    public String  P31 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = (256*A) + B;
        return result+" km";
    }
    public String  P32 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)/4;
        return df.format(result)+" Pa";
    }
    public String  P33 (String A ) {
        double result = ConvertValues.HexToInteger ( A.substring(4,6) );
        return df.format(result)+" kPa";
    }
    public String  P34 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double C = ConvertValues.HexToInteger ( AB.substring(8,10) );
        double D = ConvertValues.HexToInteger ( AB.substring(10,12) );
        double result1 = ( 0.0000305175 *(256*A + B) );
        double result2 = ( (((256*C)+D/256) - 128) );
        return df.format(result1)+"/"+df.format(result2)+"mA";

    }
    public String  P3C (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B) - 40;
        return result+DEGREE+" C";
    }
    public String  P42 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)/1000;
        return result+" V";
    }
    public String  P43 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = (0.39215686)* ((256*A) + B);
        return df.format(result)+" %";
    }
    public String  P44 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)* 0.000030517;
        return df.format(result)+"";
    }
    public String  P45 (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return df.format(result)+" %";
    }
    public String  P46 (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = resultA - 40;
        return result+" %";
    }
    public String  P47 (String A ) {
        System.out.println("THIS IS 47. A = "+A);
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        System.out.println("THIS IS 47. resultA = "+resultA);
        double result = (0.39215686) * resultA;
        System.out.println("THIS IS 47. result = "+result);
        return df.format(result)+" %";
    }

    public String  P4D (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256)* A) + B;
        return result+" minutes";
    }
    public String  P4E (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256)* A) + B;
        return result+" minutes";
    }
    public String  P4F (String ABCD ) {
        // TODO PID 41 to be completed
        String stgToReturn = "";
        System.out.println (  "4 bytes = "+ABCD  );
        double A = ConvertValues.HexToInteger ( ABCD.substring(4,6) );
        double B = ConvertValues.HexToInteger ( ABCD.substring(6,8) );
        double C = ConvertValues.HexToInteger ( ABCD.substring(8,10) );
        double D = ConvertValues.HexToInteger ( ABCD.substring(10,12) );
        return stgToReturn;
    }
    public String  P50 (String ABCD ) {
        double A = ConvertValues.HexToInteger ( ABCD.substring(4,6) );
        return (A*10)+" g/s";
    }

    public String  P51 (String A ) {
        Integer fuelType = ConvertValues.HexToInteger ( A.substring(4,6) );
        System.out.println( "fuelType = "+fuelType );
        String answer = getFuelType(fuelType.toString());
        System.out.println( "answer = "+answer );
        return answer;
    }
    public String  P52 (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return result+" %";
    }
    public String  P53 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)/200;
        return result+" kPa";
    }
    public String  P54 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)-32767;
        return result+" Pa";
    }
    public String  P55 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)-32767;
        return result+" Pa";
    }
    public String  P59 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B) * 10;
        return result+" kPa";
    }
    public String  P5A (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return result+" %";
    }
    public String  P5B (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result = (0.39215686) * resultA;
        return result+" %";
    }
    public String  P5C (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result =  resultA - 40;
        return df.format(result)+DEGREE+"C";
    }
    public double  P5CINT (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result =  resultA - 40;
        return result;
    }
    public String  P5D (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = (((256*A) + B)/128 ) - 210 ;
        return result+DEGREE;
    }
    public String  P5E (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = ((256*A) + B)/20  ;
        return result+" L/h";
    }
    public String  P5F (String A ) {
        String stgA =  A.substring(4,6) ;
        String stgToReturn = "";
        if (stgA.equals("0E")) {
            stgToReturn = "Heavy Duty Vehicles (EURO IV) B1 ";
        } else if (stgA.equals("0F")) {
            stgToReturn = "Heavy Duty Vehicles (EURO V) B2  ";
        } else if (stgA.equals("10")) {
            stgToReturn = "Heavy Duty Vehicles (EURO EEV) C";
        } else {
            int decValue = ConvertValues.HexToInteger ( A.substring(4,6) );
            if (  (decValue > 0) && ( decValue <= 13  )  ) {
                stgToReturn =  "ISO/SAE reserved ";
            } else if (  (decValue > 17) && ( decValue <= 255  )  ) {
                stgToReturn =  "ISO/SAE reserved ";
            }
        }
        return stgToReturn;
    }
    public String  P61 (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result =  resultA - 125;
        return result+" %";
    }
    public String  P62 (String A ) {
        double resultA = ConvertValues.HexToInteger ( A.substring(4,6) );
        double result =  resultA - 125;
        return result+" %";
    }
    public String  P63 (String AB ) {
        double A = ConvertValues.HexToInteger ( AB.substring(4,6) );
        double B = ConvertValues.HexToInteger ( AB.substring(6,8) );
        double result = (256*A) + B  ;
        return result+" Nm";
    }

    public String  P64 (String ABCDE ) {
        String stgToReturn = "";
        double A = ConvertValues.HexToInteger ( ABCDE.substring(4,6) );
        stgToReturn = stgToReturn + "Idle : "+ (A-125)  + lineSep ;
        double B = ConvertValues.HexToInteger ( ABCDE.substring(6,8) );
        stgToReturn = stgToReturn + "Engine Point 1 : "+ (B-125)  + lineSep ;
        double C = ConvertValues.HexToInteger ( ABCDE.substring(8,10) );
        stgToReturn = stgToReturn + "Engine Point 2 : "+ (C-125)  + lineSep ;
        double D = ConvertValues.HexToInteger ( ABCDE.substring(10,12) );
        stgToReturn = stgToReturn + "Engine Point 3 : "+ (D-125)  + lineSep ;
        double E = ConvertValues.HexToInteger ( ABCDE.substring(12,14) );
        stgToReturn = stgToReturn + "Engine Point 4 : "+ (E-125)  + lineSep ;

        return stgToReturn;
    }
    public String  P65 (String AB ) {
        System.out.println ( "P65 2 Bytes" );
        String P56ABin = ConvertValues.hexToBin( AB.substring(4,6) );
        String P56BBin = ConvertValues.hexToBin( AB.substring(6,8) );
        String stgToReturn = "";
        if ( P56ABin.substring(0,1).equals("1") ) {
            if ( P56BBin.substring(0,1).equals( "1" )   ) {
                stgToReturn = "Power Take Off (PTO) Status : Active (ON)" + lineSep;
            } else if ( P56BBin.substring(0,1).equals( "0" )   ) {
                stgToReturn = "Power Take Off (PTO) Status : Not Active (OFF)" + lineSep;
            }
        } else if ( P56ABin.substring(1,2).equals("1") ) {
            if ( P56BBin.substring(1,2).equals( "1" )   ) {
                stgToReturn = stgToReturn + "Auto Trans Neutral Drive Status : Active (ON)" + lineSep;
            } else if ( P56BBin.substring(1,2).equals( "0" )   ) {
                stgToReturn = stgToReturn + "Auto Trans Neutral Drive Status : Not Active (OFF)" + lineSep;
            }
        } else if ( P56ABin.substring(2,3).equals("1") ) {
            if ( P56BBin.substring(2,3).equals( "1" )   ) {
                stgToReturn = stgToReturn + "Manual Trans Neutral Gear Status : Active (ON)" + lineSep;
            } else if ( P56BBin.substring(2,3).equals( "0" )   ) {
                stgToReturn = stgToReturn + "Manual Trans Neutral Gear Status : Not Active (OFF)" + lineSep;
            }
        } else if ( P56ABin.substring(3,4).equals("1") ) {
            if ( P56BBin.substring(3,4).equals( "1" )   ) {
                stgToReturn = stgToReturn + "Glow Plug Lamp Status Status : Active (ON)" + lineSep;
            } else if ( P56BBin.substring(3,4).equals( "0" )   ) {
                stgToReturn = stgToReturn + "Glow Plug Lamp Status Status : Not Active (OFF)" + lineSep;
            }
        }
        return stgToReturn;
    }
    public String  P66 (String ABCDE ) {
        String P56ABin = ConvertValues.hexToBin( ABCDE.substring(4,6) );
        return ABCDE;
    }
    public String  P67 (String ABC ) {

        return ABC;
    }
    public String  P68 (String A ) {

        return A;
    }
    public String  P69 (String A ) {

        return A;
    }
    public String  P6A (String A ) {

        return A;
    }
    public String  P6B (String A ) {

        return A;
    }
    public String  P6C (String A ) {

        return A;
    }
    public String  P6D (String A ) {

        return A;
    }
    public String  P6E (String A ) {

        return A;
    }
    public String  P6F (String A ) {

        return A;
    }
    public String  P70 (String A ) {

        return A;
    }
    public String  P71 (String A ) {

        return A;
    }
    public String  P72 (String A ) {

        return A;
    }
    public String  P73 (String A ) {

        return A;
    }
    public String  P74 (String A ) {

        return A;
    }
    public String  P75 (String A ) {

        return A;
    }
    public String  P76 (String A ) {

        return A;
    }
    public String  P77 (String A ) {

        return A;
    }
    public String  P78 (String A ) {

        return A;
    }
    public String  P79 (String A ) {

        return A;
    }
    public String  P7A (String A ) {

        return A;
    }
    public String  P7B (String A ) {

        return A;
    }
    public String  P7C (String A ) {

        return A;
    }
    public String  P7D (String A ) {

        return A;
    }
    public String  P7E (String A ) {

        return A;
    }
    public String  P7F (String A ) {

        return A;
    }

    public static String CommadedSecAirStatus (int vval ) {

        String vresult = "No Data";

        if ( vval==1 ) vresult = "Upstream";
        else if( vval==2 ) vresult = "Downstream of catalytic converter";
        else if( vval==4 ) vresult = "From the outside atmosphere or off";
        else if( vval==8 ) vresult = "Pump commanded on for diagnostics";
        return vresult;
    }

    public static String getFuelType (String vval ) {

        String vresult = "No Data";

        if ( vval.equals("1") ) vresult = " Gasoline";
        else if( vval.equals("2") ) vresult = "Methanol";
        else if( vval.equals("3") ) vresult = "Ethanol ";
        else if( vval.equals("4") ) vresult = "Diesel ";
        else if( vval.equals("5") ) vresult = "LPG";
        else if( vval.equals("6") ) vresult = "CNG ";
        else if( vval.equals("7") ) vresult = "Propane ";
        else if( vval.equals("8") ) vresult = "Electric  ";
        else if( vval.equals("9") ) vresult = "Bifuel running Gasoline";
        else if( vval.equals("10") ) vresult = "Bifuel running Methanol";
        else if( vval.equals("11") ) vresult = "Bifuel running Ethanol";
        else if( vval.equals("12") ) vresult = "Bifuel running LPG";
        else if( vval.equals("13") ) vresult = "Bifuel running CNG";
        else if( vval.equals("14") ) vresult = "Bifuel running Propane";
        else if( vval.equals("15") ) vresult = "Bifuel running Electricity";
        else if( vval.equals("16") ) vresult = "Bifuel running electric and combustion engine";
        else if( vval.equals("17") ) vresult = "Hybrid gasoline";
        else if( vval.equals("18") ) vresult = "Hybrid Ethanol";
        else if( vval.equals("19") ) vresult = "Hybrid Diesel";
        else if( vval.equals("20") ) vresult = "Hybrid Electric";
        else if( vval.equals("21") ) vresult = "Hybrid running electric and combustion engine";
        else if( vval.equals("22") ) vresult = "Hybrid Regenerative";
        else if( vval.equals("23") ) vresult = "Bifuel running diesel";

        return vresult;
    }

    public static String getFuelSystem (String vval ) {

        String vresult = "No Data";

        if ( vval.equals("1") ) vresult = "Open loop due to insufficient engine temperature";
        else if( vval.equals("2") ) vresult = "Closed loop, using oxygen sensor feedback to determine fuel mix";
        else if( vval.equals("4") ) vresult = "Open loop due to engine load OR fuel cut due to deceleration";
        else if( vval.equals("8") ) vresult = "Open loop due to system failure";
        else if( vval.equals("16") ) vresult = "Closed loop, using at least one oxygen sensor but there is a fault in the feedback system";

        return vresult;
    }

    public static String getOBDStandard (String vval ) {

        String vresult = "No Data";

        if ( vval.equals("1") )  vresult = "OBD-II as defined by the CARB";
        else if( vval.equals("2") ) vresult = "OBD as defined by the EPA";
        else if( vval.equals("3") )  vresult = "OBD and OBD-II";
        else if( vval.equals("4") ) vresult = "OBD-I";
        else if( vval.equals("5") ) vresult = "Not OBD compliant";
        else if( vval.equals("6") ) vresult = "EOBD (Europe)";
        else if( vval.equals("7") ) vresult = "EOBD and OBD-II";
        else if( vval.equals("8") ) vresult = "EOBD and OBD";
        else if( vval.equals("9") ) vresult = "EOBD, OBD and OBD II";
        else if( vval.equals("10") ) vresult = "JOBD (Japan)";
        else if( vval.equals("11") ) vresult = "JOBD and OBD II";
        else if( vval.equals("12") ) vresult = "JOBD and EOBD";
        else if( vval.equals("13") ) vresult = "JOBD, EOBD, and OBD II";
        else if( vval.equals("14") ) vresult = "Reserved";
        else if( vval.equals("15") ) vresult = "Reserved";
        else if( vval.equals("16") ) vresult = "Reserved";
        else if( vval.equals("17") ) vresult = "Engine Manufacturer Diagnostics (EMD)";
        else if( vval.equals("18") ) vresult = "Engine Manufacturer Diagnostics Enhanced (EMD+)";
        else if( vval.equals("19") ) vresult = "Heavy Duty On-Board Diagnostics (Child/Partial) (HD OBD-C)";
        else if( vval.equals("20") ) vresult = "Heavy Duty On-Board Diagnostics (HD OBD)";
        else if( vval.equals("21") ) vresult = "World Wide Harmonized OBD (WWH OBD)";
        else if( vval.equals("22") ) vresult = "Reserved";
        else if( vval.equals("23") ) vresult = "Heavy Duty Euro OBD Stage I without NOx control (HD EOBD-I)";
        else if( vval.equals("24") ) vresult = "Heavy Duty Euro OBD Stage I with NOx control (HD EOBD-I N)";
        else if( vval.equals("25") ) vresult = "Heavy Duty Euro OBD Stage II without NOx control (HD EOBD-II)";
        else if( vval.equals("26") ) vresult = "Heavy Duty Euro OBD Stage II with NOx control (HD EOBD-II N)";
        else if( vval.equals("27") ) vresult = "Reserved";
        else if( vval.equals("28") ) vresult = "Brazil OBD Phase 1 (OBDBr-1)";
        else if( vval.equals("29") ) vresult = "Brazil OBD Phase 2 (OBDBr-2)";
        else if( vval.equals("30") ) vresult = "Korean OBD (KOBD)";
        else if( vval.equals("31") ) vresult = "India OBD I (IOBD I)";
        else if( vval.equals("32") ) vresult = "India OBD II (IOBD II)";
        else if( vval.equals("33") ) vresult = "Heavy Duty Euro OBD Stage VI (HD EOBD-IV)";

        return vresult;
    }

    public String generateDTCCodes (String firstBinary, String secondBinary ) {

        String finalTroubleCode = "No Data";

        if ( ( firstBinary.isEmpty() ) || ( secondBinary.isEmpty() ) )  {
           // System.out.println( "ERROR - First or Second binary is null" );
        }else
        {   // System.out.println( "first Binary ="+ firstBinary+", Second Binary = "+ secondBinary );
            String first2bits = firstBinary.substring(0,2);
            String Second2bits = firstBinary.substring(2,4);
            String firstNum = firstBinary.substring(4,8);
            String SecondNum = secondBinary.substring(0,4);
            String ThirdNum = secondBinary.substring(4,8);

            finalTroubleCode = ConvertValues.ConvertFirstDtc(first2bits);
            finalTroubleCode = finalTroubleCode + ConvertValues.ConvertSecondDtc ( Second2bits  );

            finalTroubleCode = finalTroubleCode + ConvertValues.ConvertBinStringToHexString(firstNum  );
            finalTroubleCode = finalTroubleCode + ConvertValues.ConvertBinStringToHexString(SecondNum  );
            finalTroubleCode = finalTroubleCode + ConvertValues.ConvertBinStringToHexString(ThirdNum  );

        }
        return finalTroubleCode;
    }


}
