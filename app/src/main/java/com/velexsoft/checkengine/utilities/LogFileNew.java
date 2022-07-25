package com.velexsoft.checkengine.utilities;

import android.content.Context;
import android.os.Environment;

import com.velexsoft.checkengine.ConnActivityParent;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LogFileNew {
    Context context;
    public String fileName = "checkEngineLog.txt" ;
    boolean logErrors = ConnActivityParent.logErrors;
    //boolean logErrors = false;
    File file ;

    public int createFolder(Context vcontext, String vFileName ) {

        context = vcontext;
        System.out.println("PROGRAM : LogFileNew.createFolder");
        file = new File(context.getCacheDir() + "/" + fileName );
        if (logErrors) {
            createFolderNow(context);
        }else {

        }
        return 1;
    }

    public int createFolderNow( Context vcontext ) {
        try {
            file.createNewFile();
            w( context, "*******************************************************************************************");
            w( context, "LOGFILENEW: Successfully Created log file. File name = "+fileName );
            w( context, "*******************************************************************************************");
            w( context, "MSG - Successfully Created CheckEngine Directory..."  );
            return 1;
        }   catch ( Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void w(Context c, String text) {
        file = new File(c.getCacheDir() + "/" + fileName );
        if ( logErrors ) {
            writeNow( text);
            System.out.println( text );
        }else {

        }
    }

    public void writeNow( String text) {
        Date currentTime = Calendar.getInstance().getTime();
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append("["+currentTime+"] : "+text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            w( context, "ERROR ERROR: This is writeNow error = "+e );
        }
    }


}
