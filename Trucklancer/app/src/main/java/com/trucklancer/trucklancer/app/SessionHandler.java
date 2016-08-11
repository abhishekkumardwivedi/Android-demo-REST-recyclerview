package com.trucklancer.trucklancer.app;

/**
 * Created by abhishek on 10/8/16.
 */

import com.trucklancer.trucklancer.activity.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionHandler {
    public static final String PHONE = "phone";
    public static final String PASS = "pass";
    public static final String SESSION_ID = "session_id";
    private static final String SHAREDPREF_NAME = "sharedpref";
    private static final String IS_LOGIN = "IsLoggedIn";

    private static SessionHandler instance = null;
    SharedPreferences pref;
    Editor mEditor;
    Context mContext;
    int PRIVATE_MODE = 0;

    public static synchronized SessionHandler getInstance(Context context){
        if (instance == null) instance = new SessionHandler(context);
        return instance;
    }

    // Constructor
    public SessionHandler(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(SHAREDPREF_NAME, PRIVATE_MODE);
        mEditor = pref.edit();
    }

    /**
     * Login Session
     * @param phone
     * @param pass
     * @param sessionId
     */
    public void saveLoginSession(String phone, String pass, String sessionId) {
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putString(PHONE, phone);
        mEditor.putString(PASS, pass);
        mEditor.putString(SESSION_ID, sessionId);
        mEditor.commit();
    }

    /**
     * check if user is Logged in
     */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(mContext, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
        }

    }


    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(PHONE, pref.getString(PHONE, null));
        user.put(SESSION_ID, pref.getString(SESSION_ID, null));

        return user;
    }

    public String getSessionId() {
        return pref.getString(SESSION_ID, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // wipe shared preference
        mEditor.clear();
        mEditor.commit();

        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
