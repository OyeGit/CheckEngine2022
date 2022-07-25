package com.velexsoft.checkengine;

import android.os.Bundle;
import android.widget.TextView;

public class MonitorManual extends ConnActivityParent {

    String lineSep = System.getProperty("line.separator");

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_monitor_manual;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTitle("Monitors Overview");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        TextView tx0 = (TextView) findViewById( R.id.tx0 );
        TextView tx1 = (TextView) findViewById( R.id.tx1 );
        TextView tx2 = (TextView) findViewById( R.id.tx2 );
        TextView tx3 = (TextView) findViewById( R.id.tx3 );

        String returnString0 =   getString( R.string.getMonitorsInfo01 ) ;
        tx0.setText( returnString0 );

        String returnString1 =

                getString( R.string.getMonitorsInfo02 ) + lineSep +

                getString (   R.string.getMonitorsInfo03 );

                tx1.setText( returnString1 );

                String returnString2 = getString( R.string.getMonitorsInfo ) + lineSep + lineSep +

                //getString( R.string.getMonitorsInfo1 )  + lineSep + lineSep +
                getString( R.string.getMonitorsInfo7 ) + lineSep + lineSep +
                getString( R.string.getMonitorsInfo8 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo9 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo10 ) ;

                tx2.setText( returnString2 );
                /*
                getString( R.string.getMonitorsInfo12 ) + lineSep +
                getString( R.string.getMonitorsInfo13 ) + lineSep +
                getString( R.string.getMonitorsInfo14 ) + lineSep +
                getString( R.string.getMonitorsInfo15 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo16 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo16A ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo16B ) + lineSep + lineSep +
                */

                String returnString3 =       getString( R.string.getMonitorsInfo11 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo17 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo18 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo19 ) + lineSep + lineSep +
                getString( R.string.getMonitorsInfo20 )+ lineSep +
                getString( R.string.getMonitorsInfo21 ) + lineSep +
                getString( R.string.getMonitorsInfo22 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo23 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo24 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo25 ) + lineSep + lineSep +

                getString( R.string.getMonitorsInfo26 ) + lineSep + lineSep;


        tx3.setText( returnString3 );


    }


}
