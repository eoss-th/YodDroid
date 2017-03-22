package com.th.eoss.sevenyods;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.th.eoss.util.SETFIN;

import org.w3c.dom.Text;

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

    public FilteredStackedBarAdapter(List<String> symbols, SETFINListener listener, int stackedWidth) {
        super(symbols, listener, stackedWidth);
    }

    @Override
    public StackedBarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilteredStackedBarHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filtered_stackedbar_card, parent, false));
    }

    protected void onBindStackedBarViewHolder (SETFIN set, StackedBarViewHolder h) {
        super.onBindStackedBarViewHolder(set, h);

        FilteredStackedBarHolder holder = (FilteredStackedBarHolder) h;

        float predictPercentChg = set.getFloatValue("Predict % Chg");

        if (predictPercentChg > 0) {

            holder.predictPercentChg.setText("+" + set.getFloatValue("Predict % Chg") + "%");

        } else {

            holder.predictPercentChg.setText(set.getFloatValue("Predict % Chg") + "%");

        }

        int predictPercent = set.getIntValue("Predict %");
        holder.score.setText("" + predictPercent);

        if (predictPercent > 50) {

            holder.scoreLabel.setVisibility(View.VISIBLE);
            holder.predictLabel.setVisibility(View.VISIBLE);
            holder.score.setVisibility(View.VISIBLE);

        } else {

            holder.scoreLabel.setVisibility(View.INVISIBLE);
            holder.predictLabel.setVisibility(View.INVISIBLE);
            holder.score.setVisibility(View.INVISIBLE);

        }

    }


}
