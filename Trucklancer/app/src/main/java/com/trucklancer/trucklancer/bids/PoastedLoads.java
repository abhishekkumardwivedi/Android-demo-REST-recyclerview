package com.trucklancer.trucklancer.bids;

import com.trucklancer.trucklancer.R;
import com.trucklancer.trucklancer.app.AppConfig;
import com.trucklancer.trucklancer.app.AppConstants;
import com.trucklancer.trucklancer.app.SessionHandler;
import com.trucklancer.trucklancer.detailedbid.QuoteDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PoastedLoads extends AppCompatActivity {
    private static final String TAG = PoastedLoads.class.getSimpleName();
    public static List<Load> mLoadList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LoadsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bids_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new LoadsAdapter(mLoadList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Load load = mLoadList.get(position);
                Intent intent = new Intent(PoastedLoads.this, QuoteDetails.class);
                intent.putExtra(AppConstants.INTENT_EXTRA_SELECT_INDEX, position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getPostedLoadsList();
    }

    private void getPostedLoadsList() {

        SessionHandler session = SessionHandler.getInstance(getApplicationContext());
        String sessionId = session.getSessionId();
        if (sessionId == null) {
            session.logoutUser();
        }
        new LoadsAsync().execute(sessionId);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private PoastedLoads.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PoastedLoads.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    class LoadsAsync extends AsyncTask<String, Void, String> {

        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        ProgressDialog pdLoading = new ProgressDialog(PoastedLoads.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Method running in UI thread
            Log.d(TAG, "Getting poasted loads ..");
        }

        @Override
        protected String doInBackground(String... params) {

            // check if material type is available
            Load load = new Load();
            if(load.isMaterialMapEmpty()) {
                getMaterialTypes(httpPostRequest(params[0], AppConstants.GET_MATERIAL_TYPES));
            }
            // check truck type is available
            if(load.isTruckTypeMapEmpty()) {
                getTruckTypes(httpPostRequest(params[0], AppConstants.GET_TRUCK_TYPES));
            }
            // make truck listing request
            getListings(httpPostRequest(params[0], AppConstants.GET_LISTING));

            return AppConstants.SUCCESS;
        }

        private String httpPostRequest(String sessionId, String reqType) {
            try {
                Log.d(TAG, "Connecting to server. Expect some log of status");
                url = new URL(AppConfig.URL_LOADS);

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(AppConstants.SESSIONID, sessionId)
                        .appendQueryParameter(AppConstants.REQUEST, reqType);
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

                    Log.d(TAG,"" + result.toString());
                    return result.toString();

                } else {

                    return AppConstants.FAIL;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "url_unreachable";
            } catch (IOException e) {
                e.printStackTrace();
                return AppConstants.NO_CONNECTION;
            }
        }

        private String getMaterialTypes(String payload) {
            try {
                JSONArray jsonArray = new JSONArray(payload);
                Log.d(TAG, "Length: " + jsonArray.get(0));
                JSONObject jsonObject = null;
                int length = jsonArray.length();
                Load load = null;
                for (int i = 0; i < length; i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    load = new Load();
                    load.addToMaterialMap(jsonObject.getInt("id"),
                            jsonObject.getString("materialname"));
                }
                return AppConstants.SUCCESS;
            } catch (JSONException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        }

        private String getTruckTypes(String payload) {
            try {
                JSONArray jsonArray = new JSONArray(payload);

                Log.d(TAG, "Length: " + jsonArray.get(0));
                JSONObject jsonObject = null;
                int length = jsonArray.length();
                Load load = null;
                for (int i = 0; i < length; i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    load = new Load();
                    load.addToTruckMap(jsonObject.getInt("id"),
                            jsonObject.getString("truckname"));
                }
                return AppConstants.SUCCESS;
            } catch (JSONException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        }

        private String getListings(String payload) {
            if(payload == null) {
                return AppConstants.OLD_DATA;
            }
            mLoadList.clear();
            try {
                JSONArray jsonArray = new JSONArray(payload);

                Log.d(TAG, "Length: " + jsonArray.get(0));
                JSONObject jsonObject = null;
                int length = jsonArray.length();
                Load load = null;
                for (int i = 0; i < length; i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    load = new Load();

                    load.setFromCity(jsonObject.getString("fromcity"));
                    load.setToCity(jsonObject.getString("tocity"));
                    load.setMaterialId(jsonObject.getInt("material"));
                    load.setTruckId(jsonObject.getInt("trucktype"));
                    load.setWeight(jsonObject.getString("weight"));
                    load.setMessage(jsonObject.getString("message"));
                    load.setPostId(jsonObject.getString("postid"));
                    load.setHideProfile(jsonObject.getString("hide_profile"));
                    load.setPostDate(jsonObject.getString("spostdate"));
                    load.setId(jsonObject.getString("id"));
                    mLoadList.add(load);
                }
                return AppConstants.SUCCESS;

            } catch (JSONException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            pdLoading.dismiss();

            if (response.equals(AppConstants.NO_CONNECTION)) {
                Toast.makeText(getApplicationContext(), "Connection failed !!", Toast.LENGTH_LONG).show();
                return;
            }

            if (response.equals(AppConstants.SUCCESS)) {
                Log.d(TAG, "success on async!!");
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "Fetch error!!", Toast.LENGTH_LONG).show();
            }
        }
    }

}
