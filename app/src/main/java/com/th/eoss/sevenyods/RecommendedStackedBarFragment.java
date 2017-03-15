package com.th.eoss.sevenyods;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.th.eoss.util.Filter;
import com.th.eoss.util.SETFIN;

/**
 * Created by eossth on 3/14/2017 AD.
 */

public class RecommendedStackedBarFragment extends StackedBarFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        load(SETFIN.cache_symbols);

        filterSortManager.clear();
        filterSortManager.put("Net Growth %", new Filter.HigherOrEqualThanFilter());
        filterSortManager.put("E/A Growth %", new Filter.HigherOrEqualThanFilter());
        filterSortManager.put("P/E", new Filter.LowerOrEqualThanFilter());
        filterSortManager.put("Predict %", new Filter.HigherOrEqualThanFilter());

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.recommended_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new StackedBarAdapter(symbols, this, getColumnWidth(2));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

}
