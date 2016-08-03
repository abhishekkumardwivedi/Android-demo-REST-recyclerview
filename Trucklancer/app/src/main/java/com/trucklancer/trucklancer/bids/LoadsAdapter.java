package com.trucklancer.trucklancer.bids;

import com.trucklancer.trucklancer.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LoadsAdapter extends RecyclerView.Adapter<LoadsAdapter.MyViewHolder> {

    private List<Load> mLoadsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre, budget, vehicle;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }

    public LoadsAdapter(List<Load> loadsList) {
        this.mLoadsList = loadsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bids_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Load load = mLoadsList.get(position);
        holder.title.setText(load.getTitle());
        holder.genre.setText(load.getGenre());
        holder.year.setText(load.getYear());
    }

    @Override
    public int getItemCount() {
        return mLoadsList.size();
    }
}
