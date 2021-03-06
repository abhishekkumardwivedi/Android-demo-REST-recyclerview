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
        public TextView from;
        public TextView to;
        public TextView postedDate;
        public TextView weight;
        public TextView material;
        public TextView truck;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            to = (TextView) view.findViewById(R.id.to);
            postedDate = (TextView) view.findViewById(R.id.date);
            weight = (TextView) view.findViewById(R.id.weight);
            material = (TextView) view.findViewById(R.id.material);
        }
    }

    public LoadsAdapter(List<Load> loadsList) {
        this.mLoadsList = loadsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bids_list_row2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Load load = mLoadsList.get(position);
        holder.from.setText(load.getFromCity());
        holder.to.setText(load.getToCity());
        holder.postedDate.setText(load.getPostDate());
        holder.weight.setText(load.getWeight() + " " + load.getWeightUnit());
        holder.material.setText(load.getMaterial());
    }

    @Override
    public int getItemCount() {
        return mLoadsList.size();
    }
}
