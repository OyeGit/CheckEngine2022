package com.velexsoft.checkengine.modess;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by WoleProject on 10/1/2016.
 */
public class OBDInitializeDelayForProt {
    protected ArrayList<Integer> vBuffer = null;
    protected String vCommand = null;
    public StringBuilder res = new StringBuilder();
    Integer threadSleepSendCmd = 100 ;

    public OBDInitializeDelayForProt(String pCommand, int vNum, Integer vValue ) {
        this.vCommand = pCommand;
        this.vBuffer = new ArrayList<Integer>();
        threadSleepSendCmd = vValue;
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

        String returnValue = "";
        returnValue = res.toString();
        return returnValue;
    }

}

