/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.velexsoft.checkengine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ChooserActivity extends ConnActivityParent implements AdapterView.OnItemClickListener {

    SharedPreferences settings;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context,"AUTHCHOSER: " + msg);
            //System.out.println ("AUTHCHOSER: " + msg);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_chooser;
    }


    private static final Class[] CLASSES = new Class[]{
            PhoneAuthActivity.class,        //1
            //FacebookLoginActivity.class,  //2
            //TwitterLoginActivity.class,   //3
            EmailPasswordActivity.class,    //4
            GoogleSignInActivity.class,     //5
            AnonymousAuthActivity.class,    //6
            //PasswordlessActivity.class,
            //FirebaseUIActivity.class,
            //CustomAuthActivity.class
    };

    private static final String[] classDescription = new String[]{
           "Phone Number Authentication",        //1
           // "Facebook Authentication",         //2
            // TwitterLoginActivity.class,       //3
            "Email/Password Authentication",     //4
            "Google Sign in Authentication",     //5
            "Anonymous Authentication",          //6
            //PasswordlessActivity.class,
            //FirebaseUIActivity.class,
            //CustomAuthActivity.class
    };

    private static final int[] DESCRIPTION_IDS = new int[] {
            R.string.desc_phone_auth,
            //R.string.desc_facebook_login,
            //  R.string.desc_twitter_login,
            R.string.desc_emailpassword,
            R.string.desc_google_sign_in,
            R.string.desc_anonymous_auth,
            R.string.desc_passwordless,
            R.string.desc_firebase_ui,
            R.string.desc_custom_auth,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        l.fileName = ConnActivityParent.logFileName;
        lw (  "-------------------------------------------------------------------------------");
        lw (  "ACTIVITY  : Chooser Activity Started");
        lw (  "-------------------------------------------------------------------------------");
        settings = getSharedPreferences(PREFS_NAME, 0);

        lw ( "<<<<<<<<  globalMACAddress  >>>>>>>>");
        String globalMACAddress = settings.getString("globalMACAddress", "");
        lw ( "This is globalMACAddress = "+ globalMACAddress  );

        ListView listView = findViewById(R.id.list_view);

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        lw("PROGRAM   : ChooserActivity.onPostCreate ");

        try {
            lw ( "About to check GPS version");
            int v = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
            System.out.println ( "THis is the GPS version nnumber = "+v);
            if ( v < 12211000) {
                String textMessage = "Google Play Services(GPS) version is low [Current:"+v+",Required:12211000]."+
                        "Please upgrade...This Error is usually encoutered when a phone does not have a valid number or identity."+
                        "So, it does not automatically update.";
                velexMessageNow(context, "Google Play Services(GPS) version is low. Please upgrade.");
            }
        } catch (Exception e) {

        }
    }

    public void velexMessageNow( Context pcontext, String exitMsg){
        lw (  "PROGRAM   : ChooserActivity.velexMessageNow MSG = "+exitMsg );
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pcontext);
            alertDialogBuilder.setMessage(exitMsg);
            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch ( Exception e ) {
            lw (  "ERROR ERROR : ConnectionScreen.velexMessageNow Error = "+e );
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lw (  "PROGRAM   : ChooserActivity.onItemClick " );
        Class clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class> {

        private Context mContext;
        private Class[] mClasses;
        private int[] mDescriptionIds;

        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) view.findViewById(android.R.id.text1)).setText(classDescription[position]);
            ((TextView) view.findViewById(android.R.id.text2)).setText(mDescriptionIds[position]  );

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            mDescriptionIds = descriptionIds;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //hideProgressDialog();
    }
}
