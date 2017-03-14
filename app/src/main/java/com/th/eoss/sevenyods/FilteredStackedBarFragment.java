package com.th.eoss.sevenyods;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.th.eoss.util.SETFIN;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eossth on 3/14/2017 AD.
 */

public class FilteredStackedBarFragment extends StackedBarFragment {

    private List<ToggleButton> toggleButtons = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        load(SETFIN.cache_symbols);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.filtered_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new StackedBarAdapter(symbols, this, getColumnWidth(1));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        LinearLayout togglesLayout1 = (LinearLayout) rootView.findViewById(R.id.toggles1);

        for (int i=0; i<togglesLayout1.getChildCount(); i++) {
            toggleButtons.add((ToggleButton) togglesLayout1.getChildAt(i));
        }

        LinearLayout togglesLayout2 = (LinearLayout) rootView.findViewById(R.id.toggles2);

        for (int i=0; i<togglesLayout2.getChildCount(); i++) {
            toggleButtons.add((ToggleButton) togglesLayout2.getChildAt(i));
        }

        for (ToggleButton toggleButton:toggleButtons) {
            toggleButton.setText(toggleButton.getTextOff());
            toggleButton.setTextOn(toggleButton.getTextOff());
            toggleButton.setOnClickListener(this);
        }

        return rootView;
    }

}
