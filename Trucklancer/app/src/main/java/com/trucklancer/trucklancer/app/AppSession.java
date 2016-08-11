package com.trucklancer.trucklancer.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 1. On app start, check if session_id is available in shared preference if not, go for login
 * 2. Once user is authenticated session_id is stored locally in shared preference
 * 3. Sending session_id in the header of each of your http requests.
 * 4. If something happens on the server side to the session, the transaction will not be allowed.
 * 5. When logging out, delete the session_id from shared preference and send a logout to the server
 *    as well to remove session_id from server too.
 */
public class AppSession {

    private AppSession() {
       // SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }
}
