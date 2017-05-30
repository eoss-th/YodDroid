package com.th.eoss.sevenyods;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.th.eoss.util.SETFIN;

import java.util.List;

/**
 * Created by eossth on 3/16/2017 AD.
 */

public class FilteredStackedBarAdapter extends StackedBarAdapter {

    class FilteredStackedBarHolder extends StackedBarViewHolder {

        TextView xd, xdLabel, dvd, dvdLabel, pe, growth;

        FilteredStackedBarHolder(View view) {
            super(view);

            xd = (TextView) view.findViewById(R.id.xd);
            xdLabel = (TextView) view.findViewById(R.id.xdLabel);
            dvd = (TextView) view.findViewById(R.id.dvd);
            dvdLabel = (TextView) view.findViewById(R.id.dvdLabel);

            pe = (TextView) view.findViewById(R.id.pe);

            growth = (TextView) view.findViewById(R.id.growth);

        }

    }

    public FilteredStackedBarAdapter(YODContext yodContext) {
        super(yodContext);
        this.stackedWidth = yodContext.getColumnWidth(1);
    }

    @Override
    public StackedBarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilteredStackedBarHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filtered_stackedbar_card, parent, false));
    }

    protected void onBindStackedBarViewHolder (SETFIN set, StackedBarViewHolder h) {
        super.onBindStackedBarViewHolder(set, h);

        FilteredStackedBarHolder holder = (FilteredStackedBarHolder) h;

        /*
        if (set.values.get("Quote Timestamp")==null ||
                System.currentTimeMillis() - set.values.get("Quote Timestamp").longValue() > 1000 * 60 * 5) {

            new SETQuoteAsyncTask(set.symbol) {

                @Override
                protected void onPostExecute(Void aVoid) {
                    yodContext.mainActivity().filteredStackedBarFragment.reload();
                }

            }.execute();

        }*/


        holder.dvd.setText(set.dvd);
        holder.xd.setText(set.xd);

        int netGrowthPercent = set.getIntValue("Net Growth %");

        holder.growth.setText(netGrowthPercent + "%");

        if (set.xd.isEmpty()) {
            holder.xd.setVisibility(View.GONE);
            holder.xdLabel.setVisibility(View.GONE);
            holder.dvd.setVisibility(View.GONE);
            holder.dvdLabel.setVisibility(View.GONE);
        } else {
            holder.xd.setVisibility(View.VISIBLE);
            holder.xdLabel.setVisibility(View.VISIBLE);
            holder.dvd.setVisibility(View.VISIBLE);
            holder.dvdLabel.setVisibility(View.VISIBLE);
        }

        if (set.isStillXD) {
            holder.xd.setTextColor(Color.parseColor("#006400"));
        } else {
            holder.xd.setTextColor(Color.RED);
        }

    }


}
