package com.trucklancer.trucklancer.activity;

import com.trucklancer.trucklancer.R;
import com.trucklancer.trucklancer.app.AppConfig;
import com.trucklancer.trucklancer.app.AppConstants;
import com.trucklancer.trucklancer.app.SessionHandler;
import com.trucklancer.trucklancer.bids.PoastedLoads;
import com.trucklancer.trucklancer.helper.SQLiteHandler;
import com.trucklancer.trucklancer.helper.SessionManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputPhone;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private boolean DEMO = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhone = (EditText) findViewById(R.id.phone);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            Log.d(TAG, "user has logged in session, let go to next page");
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "user doesn't have login session. Let's login now!!");
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String phone = inputPhone.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!phone.isEmpty() && !password.isEmpty()) {
                    // login user
                    if (DEMO) {
                        Intent intent;

                        if (phone.equals("0000")) {
                            intent = new Intent(LoginActivity.this,
                                    com.trucklancer.trucklancer.bids.PoastedLoads.class);
                        } else {
                            intent = new Intent(LoginActivity.this,
                                    com.trucklancer.trucklancer.Submit.SubmitActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        login(phone, password);
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void login(final String phone, final String password) {
        // Initialize  AsyncLogin() class
        new LoginAsync().execute(phone, password);
    }

    class LoginAsync extends AsyncTask<String, Void, String> {

        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Method running in UI thread
            Log.d(TAG, "Singning in, wait ..");
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.d(TAG, "Connecting to server. Expect some log of status");
                url = new URL(AppConfig.URL_LOGIN);

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();


                // check response of initiated connection

                int response_code = conn.getResponseCode();

                // Unauthorized on invalid user name or password
                if (response_code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    return AppConstants.BAD_CREDENTIALS;
                }
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    if (result != null) {
                        SessionHandler session = SessionHandler.getInstance(getApplicationContext());
                        session.saveLoginSession(params[0], params[1], result.toString());
                    }
                    Log.d(TAG, "------------------------------");
                    Log.d(TAG, "Response:");
                    Log.d(TAG, "" + result.toString());
                    Log.d(TAG, "------------------------------");
                    // Pass data to onPostExecute method
                    return (AppConstants.SESSION_CREATED);

                } else {

                    return ("failed ... !!");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "url_unreachable";
            } catch (IOException e) {
                e.printStackTrace();
                return AppConstants.NO_CONNECTION;
            }
//            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            pdLoading.dismiss();

            if (response.equals(AppConstants.NO_CONNECTION)) {
                Toast.makeText(getApplicationContext(), "Connection failed !!", Toast.LENGTH_LONG).show();
                return;
            }

            if (response.equals(AppConstants.BAD_CREDENTIALS)) {
                Toast.makeText(getApplicationContext(), "Wrong phone number or password !!", Toast.LENGTH_LONG).show();
                return;
            }

            if (response.equals(AppConstants.SESSION_CREATED)) {
                Intent intent = new Intent(LoginActivity.this, PoastedLoads.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LoginActivity.this.finish();

            } else {
                Toast.makeText(getApplicationContext(), "Login error!!", Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * function to verify login details in mysql db
     * */
//    private void checkLogin(final String phone, final String password) {
//        // Tag used to cancel the request
//        String tag_string_req = "req_login";
//
//        pDialog.setMessage("Logging in ...");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Method.POST,
//                AppConfig.URL_LOGIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Login Response: " + response.toString());
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//
//                    // Check for error node in json
//                    if (!error) {
//                        // user successfully logged in
//                        // Create v +login session
//                        session.setLogin(true);
//                        // Now store the user in SQLite
//                        String uid = jObj.getString("uid");
//
//                        JSONObject user = jObj.getJSONObject("user");
//                        String name = user.getString("name");
//                        String email = user.getString("email");
//                        String created_at = user
//                                .getString("created_at");
//
//                        // Inserting row in users table
//                        db.addUser(name, email, uid, created_at);
//
//                        // Launch main activity
//                        Intent intent = new Intent(LoginActivity.this,
//                                MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("phone", phone);
//                params.put("password", password);
//
//                return params;
//            }
//
//        };
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }

//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }
}
