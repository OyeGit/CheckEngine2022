package com.velexsoft.checkengine.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.velexsoft.checkengine.ConnActivityParent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.velexsoft.checkengine.ConnActivityParent.currProtocol;
import static com.velexsoft.checkengine.ConnActivityParent.currProtocolDesc;
import static com.velexsoft.checkengine.ConnActivityParent.currProtocolGroup;
import static com.velexsoft.checkengine.ConnActivityParent.currVinNumber;

import androidx.annotation.NonNull;

/**
 * Created by WoleProject on 12/26/2016.
 */

public class LogCloudFileNew {
    public String fileName ;
    File pathFileName ;
    boolean logErrors = false;
    boolean logCloud = false;
    String dataType ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String timezone;
    String fingerPrint;
    SharedPreferences sharedPreferences;
    String timestamp;
    String completeProtocol;

    public  LogCloudFileNew( String dataType, Context context ){

        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            logCloud = sharedPreferences.getBoolean("vibrate_on_connection", true);
            this.dataType = dataType;
            RegScreen Rs = new RegScreen();
            timezone = Rs.getTimeZone();
            fingerPrint = Rs.getFingerPrint();
            timestamp = Rs.getTime();
            completeProtocol = currProtocol + " [" + currProtocolGroup + "] - " + currProtocolDesc;
        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

    public void w(String c, String text, ArrayList<String> dtcList  ) {
        try {
            if (logCloud) {
                cloudLog(c, text, dtcList);
            } else {

            }
        } catch ( Exception e ) {

        }
    }

    public void wdtc(String c, String text, String numOfDTC, ArrayList<String> dtcList  ) {
        try {
            if (logCloud) {
                cloudLog1(c, text, numOfDTC, dtcList);
            } else {

            }
        } catch ( Exception e ) {

        }
    }

    public void wConn(String c, String text, ArrayList<String> connList  ) {

        System.out.println (  "ConnList.count = "+connList.size()  );
        try {
            if (logCloud) {
                cloudLogConn(c, text, connList);
            } else {

            }
        } catch ( Exception e ) {

        }
    }

    public void wMon(String c, String text, ArrayList<String> dtcList  ) {
        try {
            if (logCloud) {
                cloudLogMon(c, text, dtcList);
            } else {

            }
        } catch ( Exception e ) {

        }
    }

    public void cloudLog1 ( String tab, String resultBytes, String numOfDTC, ArrayList<String>  dtcList  ) {

        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("currVinNumber", currVinNumber );
        auditLog.put("completeProtocol", completeProtocol );
        auditLog.put("fingerPrint", fingerPrint);
        auditLog.put("timestamp", timestamp);
        auditLog.put("timezone", timezone);
        auditLog.put("requestCode", tab);
        auditLog.put("numOfDTC", numOfDTC);
        auditLog.put("responseBytes", resultBytes);
        auditLog.put("dtcList", dtcList);

        // Add a new document with a generated ID
        db.collection(dataType)
            .add(auditLog)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    System.out.println (  "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w(TAG, "Error adding document", e);
                    System.out.println("Error adding document");
                }
            });
    }

    public void cloudLogConn ( String tab, String resultBytes, ArrayList<String>  connList  ) {

        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("currVinNumber", currVinNumber );
        auditLog.put("completeProtocol", completeProtocol );
        auditLog.put("fingerPrint", fingerPrint);
        auditLog.put("timestamp", timestamp);
        auditLog.put("timezone", timezone);
        auditLog.put("requestCode", tab);
        auditLog.put("responseBytes", resultBytes);
        auditLog.put("ConnAudit", connList);

        // Add a new document with a generated ID
        db.collection(dataType)
            .add(auditLog)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    System.out.println (  "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w(TAG, "Error adding document", e);
                    System.out.println("Error adding document");
                }
            });
    }

    public void cloudLogMon ( String tab, String resultBytes, ArrayList<String>  dtcList  ) {

        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("currVinNumber", currVinNumber );
        auditLog.put("completeProtocol", completeProtocol );
        auditLog.put("fingerPrint", fingerPrint);
        auditLog.put("timestamp", timestamp);
        auditLog.put("timezone", timezone);
        auditLog.put("requestCode", tab);
        auditLog.put("responseBytes", resultBytes);
        auditLog.put("TestResultList", dtcList);

        // Add a new document with a generated ID
        db.collection(dataType)
            .add(auditLog)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    System.out.println (  "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w(TAG, "Error adding document", e);
                    System.out.println("Error adding document");
                }
            });
    }


    public void cloudLog ( String tab, String resultBytes, ArrayList<String>  dtcList  ) {

        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("currVinNumber", currVinNumber );
        auditLog.put("completeProtocol", completeProtocol );
        auditLog.put("fingerPrint", fingerPrint);
        auditLog.put("timestamp", timestamp);
        auditLog.put("timezone", timezone);
        auditLog.put("requestCode", tab);
        auditLog.put("responseBytes", resultBytes);
        auditLog.put("TestResultList", dtcList);

        // Add a new document with a generated ID
        db.collection(dataType)
            .add(auditLog)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    System.out.println (  "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w(TAG, "Error adding document", e);
                    System.out.println("Error adding document");
                }
            });
    }

}
