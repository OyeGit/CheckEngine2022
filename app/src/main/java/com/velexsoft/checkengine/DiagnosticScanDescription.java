package com.velexsoft.checkengine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DiagnosticScanDescription extends ConnActivityParent {

    final Context context = this;
    SharedPreferences settings;
    public static final String PREFS_NAME = "MyPrefsFile";
    boolean useDarkbkgd = false;
    String longText = "" ;
    TextView vehImage;
    TextView vehImage1;
    String lineSep = System.getProperty("line.separator");

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_diagnostic_scan_description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_diagnostic_scan_description);
        loadTitle(getString(  R.string.DTCScanInformation ));

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        useDarkbkgd = settings.getBoolean("useDarkbkgd", false );
        RelativeLayout layout =(RelativeLayout)findViewById(R.id.myRelativeLayout);

        vehImage1 = (TextView) findViewById(R.id.textView1);
        vehImage = (TextView) findViewById(R.id.textView2);
        vehImage1.setText("Diagnostic Trouble Code (DTC) Classification & Types" );
        vehImage.setText ( lineSep +
                "The task manager sets the Diagnostic Trouble Codes (DTC) when a test has failed. It stores the DTCs in memory according to the code CLASSIFICATION and STATUS. The task manager also prioritizes freeze frame data. Some failures require one trip to actually set the DTC, while others require two consecutive trips. " +
                ""+
                lineSep +
                "There are four types of CLASSIFICATIONS for DTCs:\n" +
                lineSep +
                "Type A\n" +
                "Type B\n" +
                "Type C\n" +
                "Type D\n" +
                lineSep +
                "Type A is an emission related DTC. It will illuminate the check engine light (MIL), and will flash the MIL if the catalyst is being damaged. The task manager will store a freeze frame of the first failure if the memory is empty. Some models store this in the history during the first trip. It will update the failure record each failure.\n" +

                lineSep +
                "Type B is an emission related DTC. It will be pending after the first trip failed, and the pending code will be discarded if the following trip passes. If the second trip fails, the MIL will illuminate. If the freeze frame is empty, freeze frame data will be stored for the first trip failure. The task manager stores in history when the second trip fails. It will store a failed record on the first failure, and update the fail record for each following failure.\n" +

                lineSep +
                "Type C is not an emissions related DTC. This will illuminate a service lamp or message on the first trip (not the MIL). The task manager will not store a freeze frame, but it will store it in history on the first trip. PCM will store all fail records and update the fail record for each failure.\n" +

                lineSep +
                "Type D is not an emissions related DTC. It will not illuminate any service lamp or message. No freeze frame will be stored. A fail record will be stored. Every time there is a failure, the fail record with be updated.\n" +
                lineSep +
                "FREEZE FRAMES are related to type A or B emissions related DTCs.\n" +
                "\n" +
                "There are three types of STATUS for DTCs:\n" +
                "History\n" +
                "Pending \n" +
                "Current\n" +
                "\n" +
                "A code is a history DTC when a fault has been detected. Whether the classification is type A, B, C, or D, the DTC remains in memory until a number of ignition cycles occur, or it is manually cleared by a technician.\n" +
                "\n" +
                "A code is a pending DTC when it has not been validated by a second test. This normally refers to type B codes. Pending codes only indicate that a fault has been recently detected.\n" +
                "\n" +
                "A code is a current DTC when a test has failed in the past trip. If it’s a classification of  type B, it is also a pending DTC until it fails on the next trip, passes during the next three trips, or it has been cleared. If type C or D, the DTC clears after the following trip. \n" +
                "\n"
        );
    }

}
