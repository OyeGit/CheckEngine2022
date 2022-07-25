package com.velexsoft.checkengine.modess;

/**
 * Created by WoleProject on 9/27/2016.
 */

public class OBDInitialize extends OBDCommand {

    private StringBuffer codes = null;

    public OBDInitialize(String pCode  ) {
        super(pCode);

        codes = new StringBuffer();
    }

}