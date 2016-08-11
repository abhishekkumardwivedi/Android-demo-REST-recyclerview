package com.trucklancer.trucklancer.detailedbid;

import com.trucklancer.trucklancer.R;
import com.trucklancer.trucklancer.app.AppConfig;
import com.trucklancer.trucklancer.app.AppConstants;
import com.trucklancer.trucklancer.app.SessionHandler;
import com.trucklancer.trucklancer.bids.Load;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuoteDetails extends AppCompatActivity {
    private static final String TAG = QuoteDetails.class.getSimpleName();

    public static List<Quote> mQuoteList = new ArrayList<>();
    public static Map<String, String> transportMap = new HashMap<>();

    private RecyclerView recyclerView;
    private QuotesAdapter mAdapter;
    public  String sessionId;

    private int index;
    private Load load;
    private TextView infoTitleTv;
    private TextView postedDateTv;
    private TextView weightTv;
    private TextView materialTv;
    private TextView truckTv;
    private TextView messageTv;
    private EditText priceEt;
    private EditText quoteMsg;

    private Button quoteBtn;


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
        priceEt = (EditText) findViewById(R.id.amount);

        quoteMsg = (EditText) findViewById(R.id.quote_message);
        quoteBtn = (Button) findViewById(R.id.button_quote);


        infoTitleTv.setText(load.getFromCity() + " to " + load.getToCity());
        messageTv.setText(load.getMessage());
        postedDateTv.setText("Posted on: " + load.getPostDate());
        weightTv.setText("Weight: " + load.getWeight() + " " + load.getWeightUnit());
        materialTv.setText("Material: " + load.getMaterial());
        truckTv.setText("Truck type: " + load.getTruckType());

        SessionHandler session = SessionHandler.getInstance(getApplicationContext());
        sessionId = session.getSessionId();
        if (sessionId == null) {
            session.logoutUser();
        }
        new quoteAsync().execute(sessionId, load.getId(), AppConstants.GET_QUOTE);

        quoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = String.valueOf(priceEt.getText());
                String message = String.valueOf(quoteMsg.getText());
                SessionHandler session = SessionHandler.getInstance(getApplicationContext());
                String user = session.getPhone();
                if (user == null) {
                    Log.d(TAG, "Logged in user details are goofed up!!");
                    return;
                }
                new quoteAsync().execute(sessionId, load.getId(), AppConstants.SUBMIT_QUOTE,
                        amount, message, user);
            }
        });
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
        private String reqType;

        ProgressDialog pdLoading = new ProgressDialog(QuoteDetails.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Method running in UI thread
            Log.d(TAG, "Getting bids list ..");
//            pdLoading.setMessage("\tLoading...");
//            pdLoading.setCancelable(false);
//            pdLoading.show();
        }

        private String getQuotesList(String payload) {
            if (payload == null) {
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
                    quote = new Quote();
                    quote.setId(jsonObject.getString("id"));
                    quote.setPostId(jsonObject.getString("postid"));
                    quote.setQuoteId(jsonObject.getString("partalacid"));
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

        private String getTruckNameList(String payload) {
            if (payload == null) {
                return AppConstants.OLD_DATA;
            }
            mQuoteList.clear();
            try {
                JSONArray jsonArray = new JSONArray(payload.toString());
                JSONObject jsonObject = null;
                int length = jsonArray.length();
                Log.d(TAG, "Length: " + length);
                for (int i = 0; i < length; i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    transportMap.put(jsonObject.getString("id"), jsonObject.getString("trnsprtname"));
                }
                return AppConstants.SUCCESS;
            } catch (JSONException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        }

        @Override
        protected String doInBackground(String... params) {

            reqType = params[2];
            if (params[2].equals(AppConstants.GET_QUOTE)) {
                if (transportMap.size() == 0) {
                    postHTTPRequest(params[0], params[1], AppConstants.GET_TRANSPORT_NAME);
                }
            }
            return postHTTPRequest(params);
        }

        protected String postHTTPRequest(String... params) {

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
                        .appendQueryParameter(AppConstants.REQUEST, params[2]);

                if (params[2].equals(AppConstants.SUBMIT_QUOTE)) {
                    builder.appendQueryParameter(AppConstants.SUBMIT_AMOUNT, params[3])
                            .appendQueryParameter(AppConstants.SUBMIT_MESSAGE, params[4])
                            .appendQueryParameter(AppConstants.USERNAME, params[5]);
                }

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
                    String response;
                    if (params[2].equals(AppConstants.GET_QUOTE)) {
                        response = getQuotesList(result.toString());
                        return response;
                    } else if (params[2].equals(AppConstants.GET_TRANSPORT_NAME)) {
                        response = getTruckNameList(result.toString());
                        return response;
                    } else if (params[2].equals(AppConstants.SUBMIT_QUOTE)) {
                        return AppConstants.SUCCESS;
                    }else {
                        response = AppConstants.BAD_REQUEST;
                    }

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
                if(reqType.equals(AppConstants.SUBMIT_QUOTE)) {

                    new quoteAsync().execute(sessionId, load.getId(), AppConstants.GET_QUOTE);
                }
                Log.d(TAG, "success on async!!");
                updateQuotesDisplay();
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "Fetch error!!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "response: " + response);
            }
        }
    }
}
