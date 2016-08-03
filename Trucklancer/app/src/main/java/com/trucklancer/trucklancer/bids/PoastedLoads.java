package com.trucklancer.trucklancer.bids;

import com.trucklancer.trucklancer.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PoastedLoads extends AppCompatActivity {
    private List<Load> mLoadList = new ArrayList<>();
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
              //  Toast.makeText(getApplicationContext(), load.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PoastedLoads.this, com.trucklancer.trucklancer.detailedbid.BidDetails.class);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareMovieData();
    }

    private void prepareMovieData() {
        Load load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016", null,null);
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016", null, null);
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016", null, null);
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016", null, null);
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016", null, null);
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016");
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016");
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016");
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016");
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016");
        mLoadList.add(load);

        load = new Load("Pune to Belgaum", "15 Tons of Chemical in closed container.", "By: 20/09/2016");
        mLoadList.add(load);

        mAdapter.notifyDataSetChanged();
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

}
