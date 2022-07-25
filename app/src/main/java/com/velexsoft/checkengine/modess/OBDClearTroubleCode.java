package com.velexsoft.checkengine.modess;


/**
 * Created by WoleProject on 11/12/2016.
 */

public class OBDClearTroubleCode extends OBDCommand {


    private StringBuffer codes = null;


    public OBDClearTroubleCode(String cmd ) {
        super(cmd);

        codes = new StringBuffer();

    }

    public OBDClearTroubleCode(OBDClearTroubleCode other) {
        super(other);
        codes = new StringBuffer();
    }



}
