package com.trucklancer.trucklancer.detailedbid;

import com.trucklancer.trucklancer.R;
import com.trucklancer.trucklancer.app.AppConfig;
import com.trucklancer.trucklancer.app.AppConstants;
import com.trucklancer.trucklancer.app.SessionHandler;
import com.trucklancer.trucklancer.bids.DividerItemDecoration;
import com.trucklancer.trucklancer.bids.Load;
import com.trucklancer.trucklancer.bids.LoadsAdapter;
import com.trucklancer.trucklancer.bids.PoastedLoads;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
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

public class QuoteDetails extends AppCompatActivity {
    private static final String TAG = QuoteDetails.class.getSimpleName();

    public static List<Quote> mQuoteList = new ArrayList<>();

    private RecyclerView recyclerView;
    private QuotesAdapter mAdapter;

    private int index;
    private Load load;
    private TextView infoTitleTv;
    private TextView postedDateTv;
    private TextView weightTv;
    private TextView materialTv;
    private TextView truckTv;
    private TextView messageTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedbid_activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        index = getIntent().getIntExtra(AppConstants.INTENT_EXTRA_SELECT_INDEX, -1);
        load = PoastedLoads.mLoadList.get(index);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle(load.getFromCity() + " to " + load.getToCity());

        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


        infoTitleTv = (TextView) findViewById(R.id.info_title);
        messageTv = (TextView) findViewById(R.id.message);
        postedDateTv = (TextView) findViewById(R.id.date);
        weightTv = (TextView) findViewById(R.id.weight);
        materialTv = (TextView) findViewById(R.id.material);
        truckTv = (TextView) findViewById(R.id.truck);

        infoTitleTv.setText(load.getFromCity() + " to " + load.getToCity());
        messageTv.setText(load.getMessage());
        postedDateTv.setText("Posted on: " + load.getPostDate());
        weightTv.setText("Weight: " + load.getWeight() + " " + load.getWeightUnit());
        materialTv.setText("Material: " + load.getMaterial());
        truckTv.setText("Truck type: " + load.getTruckType());

        SessionHandler session = SessionHandler.getInstance(getApplicationContext());
        String sessionId = session.getSessionId();
        if (sessionId == null) {
            session.logoutUser();
        }
        new quoteAsync().execute(sessionId, load.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class quoteAsync extends AsyncTask<String, Void, String> {

        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        ProgressDialog pdLoading = new ProgressDialog(QuoteDetails.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Method running in UI thread
            Log.d(TAG, "Getting bids list ..");
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

//
//        private String getMaterialTypes(String payload) {
//            try {
//                JSONArray jsonArray = new JSONArray(payload);
//                Log.d(TAG, "Length: " + jsonArray.get(0));
//                JSONObject jsonObject = null;
//                int length = jsonArray.length();
//                Load load = null;
//                for (int i = 0; i < length; i++) {
//                    jsonObject = (JSONObject) jsonArray.get(i);
//                    load = new Load();
//                    load.addToMaterialMap(jsonObject.getInt("id"),
//                            jsonObject.getString("materialname"));
//                }
//                return AppConstants.SUCCESS;
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return AppConstants.FAIL;
//            }
//        }

        private String getQuotesList(String payload) {
            if(payload == null) {
                return AppConstants.OLD_DATA;
            }
            mQuoteList.clear();
            try {
                JSONArray jsonArray = new JSONArray(payload.toString());
                JSONObject jsonObject = null;
                int length = jsonArray.length();
                Log.d(TAG, "Length: " + length);
                Quote quote = null;

                for (int i = 0; i < length; i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);

                    quote.setId(jsonObject.getString("id"));
                    quote.setPostId(jsonObject.getString("postid"));
                    quote.setQuoteId(jsonObject.getString("partialacid"));
                    quote.setAcceptId(jsonObject.getString("accepterid"));
                    quote.setPrice(jsonObject.getString("price"));
                    quote.setDescription(jsonObject.getString("description"));
                    quote.setAcceptDate(jsonObject.getString("acceptdatetime"));
                    quote.setStatus(jsonObject.getString("status"));

                    mQuoteList.add(quote);
                }
                return AppConstants.SUCCESS;
            } catch (JSONException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.d(TAG, "Connecting to server. Expect some log of status");
                url = new URL(AppConfig.URL_QUOTE);

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(AppConstants.SESSIONID, params[0])
                        .appendQueryParameter(AppConstants.POST_ID, params[1])
                        .appendQueryParameter(AppConstants.REQUEST, AppConstants.GET_QUOTE);
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

                    Log.d(TAG, "result: " + result.toString());

                    String response = getQuotesList(result.toString());
                    updateQuotesDisplay();

                    return response;
                } else {
                    return Integer.toString(response_code);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "url_unreachable";
            } catch (IOException e) {
                e.printStackTrace();
                return AppConstants.NO_CONNECTION;
            }
        }

        private void updateQuotesDisplay() {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            mAdapter = new QuotesAdapter(mQuoteList);

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
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
                Log.d(TAG, "response: " + response);
            }
        }
    }
}
