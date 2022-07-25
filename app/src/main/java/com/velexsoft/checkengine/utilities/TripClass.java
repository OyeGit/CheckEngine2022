package com.velexsoft.checkengine.utilities;

import android.location.Location;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TripClass implements Serializable {

    public Date startTime ;
    public String formatedDate;
    public Date endTime ;
    public int tripNo;
    public double beginlong ;
    public double beginlat ;
    public double endlong ;
    public double endlat ;
    public ArrayList<ArrayList<Double>> tripGPS;
    public String sitanceTravelled = "X" ;
    public String timeSpentStg = "X" ;

    public TripClass ( int tripNum){

        startTime = new Date();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:dd");
        formatedDate = DATE_FORMAT.format(startTime);

        tripNo = tripNum;
        tripGPS= new ArrayList();
    }

    public void tripStop() {
        endTime = new Date();
    }

    public void storeLonLatList(  ArrayList<ArrayList<Double>> vval ) {
        System.out.println  (  "PROGRAM   : DriveCycleTripScreen.trip.storeLonLatList...["+tripNo+"]" );
        try {
            tripGPS = vval;
            int count = tripGPS.size();

            // lw ( "INFO      : Obtaining end long and Lat");
            beginlong = tripGPS.get(0).get(0);
            beginlat = tripGPS.get(0).get(1);

            endlong = tripGPS.get(count - 1).get(0);
            endlat = tripGPS.get(count - 1).get(1);

            System.out.println ( "endlong = "+endlong+", endlat = "+endlat );
            System.out.println ( "beginlong = "+beginlong+", beginlat = "+beginlat );
            System.out.println ( "INFO      : Obtaining location difference");

            float [] results = new float[5];
            Location.distanceBetween(beginlat, beginlong,
                    endlat,  endlong, results);

            float resultKM =  results[1] / 1000.0f ;
            float distResult = roundFloat(  resultKM , 1) ;

            sitanceTravelled = distResult + " Km";

            // sitanceTravelled = results[1] + " metres";

            int timeSpent = minutesDiff(startTime,endTime);
            String timespentfinal = timeSpent + " minutes";
            System.out.println("Timespent After: " + timespentfinal);
            timeSpentStg = timespentfinal;
        } catch (Exception e) {
            // lw ( "ERROR ERROR : Error in storeLonLatList..."+e );
        }
    }

    public static int minutesDiff( Date earlierDate, Date laterDate ) {
        long SECOND_MILLIS = 1000;
        long MINUTE_MILLIS = SECOND_MILLIS*60;
        if( earlierDate == null || laterDate == null ) return 0;
        return (int)((laterDate.getTime()/MINUTE_MILLIS) - (earlierDate.getTime()/MINUTE_MILLIS));
    }

    public float roundFloat (float number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


}
