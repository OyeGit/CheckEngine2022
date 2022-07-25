package com.velexsoft.checkengine;

import android.os.Bundle;
import android.widget.TextView;

public class TripManual extends ConnActivityParent {

    String lineSep = System.getProperty("line.separator");

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_monitor_manual;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_trip_manual);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        TextView tx = (TextView) findViewById(R.id.tx1);
        String returnString =
                getString(R.string.getMonitorsInfo2) + lineSep +
                getString(R.string.getMonitorsInfo3) + lineSep +
                getString(R.string.getMonitorsInfo4) + lineSep +
                getString(R.string.getMonitorsInfo5) + lineSep + lineSep +
                getString(R.string.getMonitorsInfo6) + lineSep + lineSep +

                getString( R.string.getGenericDriveCycle0) + lineSep +
                getString( R.string.getGenericDriveCycle1) + lineSep +
                getString( R.string.getGenericDriveCycle2) + lineSep +
                getString( R.string.getGenericDriveCycle3) + lineSep +
                getString( R.string.getGenericDriveCycle4) + lineSep +
                getString( R.string.getGenericDriveCycle5) + lineSep +
                getString( R.string.getGenericDriveCycle6) + lineSep +
                getString( R.string.getGenericDriveCycle7) + lineSep +
                getString( R.string.getGenericDriveCycle8) + lineSep +
                getString( R.string.getGenericDriveCycle9) + lineSep +
                getString( R.string.getGenericDriveCycle10) + lineSep ;

        tx.setText(returnString);
    }

}
