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

        TextView pbv, eps;

        FilteredStackedBarHolder(View view) {
            super(view);

            pbv = (TextView) view.findViewById(R.id.pbv);
            eps = (TextView) view.findViewById(R.id.eps);
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

        holder.pbv.setText("" + set.getFloatValue("P/BV"));
        holder.eps.setText("" + set.getFloatValue("EPS"));
    }


}
