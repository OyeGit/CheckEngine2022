package com.velexsoft.checkengine.modess;

import android.content.Context;


import com.velexsoft.checkengine.ConnActivityParent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by WoleProject on 9/27/2016.
 */

public class OBDCommand {
    Context context;
    protected ArrayList<Integer> vBuffer = null;
    protected String vCommand = null;
    public StringBuilder res = new StringBuilder();
    int threadSleepSendCmd = ConnActivityParent.threadSleepSendCmd;


    public OBDCommand(String pCommand) {

        this.vCommand = pCommand;
        this.vBuffer = new ArrayList<Integer>();
    }

    public OBDCommand(OBDCommand other) {
        this(other.vCommand);
    }

    public void talkToDevice(InputStream in, OutputStream out) throws IOException,
            InterruptedException {
        sendInputStream(out);
        Thread.sleep( threadSleepSendCmd );
        getResponseStream(in);
    }

    public void sendInputStream ( OutputStream out )  throws IOException,
            InterruptedException {
        vCommand += "\r";
        out.write(vCommand.getBytes());
        out.flush();
    }

    public void getResponseStream ( InputStream in ) throws IOException {
        byte bData = 0;
        while ((char) (bData = (byte) in.read()) != '>') {
            res.append((char) bData);
        }
    }

    public String getResponse() {
        return res.toString();
    }
}
