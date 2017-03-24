package com.th.eoss.sevenyods;

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

        TextView score, scoreLabel, predictLabel;

        FilteredStackedBarHolder(View view) {
            super(view);

            score = (TextView) view.findViewById(R.id.score);
            scoreLabel = (TextView) view.findViewById(R.id.scoreLabel);
            predictLabel = (TextView) view.findViewById(R.id.predictLabel);
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

        float predictPercentChg = set.getFloatValue("Predict % Chg");

        if (predictPercentChg > 0) {

            holder.predictPercentChg.setText("+" + set.getFloatValue("Predict % Chg") + "%");

        } else {

            holder.predictPercentChg.setText(set.getFloatValue("Predict % Chg") + "%");

        }

        int predictPercent = set.getIntValue("Predict %");
        holder.score.setText("" + predictPercent);

        holder.trend.setVisibility(View.VISIBLE);
        holder.predictLabel.setVisibility(View.VISIBLE);
        holder.predictPercentChg.setVisibility(View.VISIBLE);
        holder.scoreLabel.setVisibility(View.VISIBLE);
        holder.score.setVisibility(View.VISIBLE);

    }


}
