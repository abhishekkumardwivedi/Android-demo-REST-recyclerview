package com.trucklancer.trucklancer.detailedbid;

import com.trucklancer.trucklancer.R;
import com.trucklancer.trucklancer.bids.Load;
import com.trucklancer.trucklancer.bids.PoastedLoads;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.MyViewHolder> {

    private List<Quote> mQuotesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView date;
        public TextView price;
        public TextView avg;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.bidder_name);
            date = (TextView) view.findViewById(R.id.bid_date);
            price = (TextView) view.findViewById(R.id.bid_price);
//            avg = (TextView) view.findViewById(R.id.bid_avg);
        }
    }

    public QuotesAdapter(List<Quote> quotesList) {
        this.mQuotesList = quotesList;
    }

    private String getAvg() {
        return " ";
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bid_table, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Quote quote = mQuotesList.get(position);
        holder.name.setText(getName(quote.getQuoteId()));
        holder.date.setText(quote.getAcceptDate());
        holder.price.setText(quote.getPrice());
  //      holder.avg.setText(getAvg());
    }

    private String getName(String id) {
        String name = QuoteDetails.transportMap.get(id);
        return (name == null) ? id : name;
    }
    @Override
    public int getItemCount() {
        return mQuotesList.size();
    }
}
