package com.th.eoss.sevenyods;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.th.eoss.util.SETFIN;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by eossth on 3/14/2017 AD.
 */

public class FilteredStackedBarFragment extends StackedBarFragment implements View.OnClickListener, View.OnLongClickListener {

    private Map<ToggleButton, String> toggleButtonMap = new HashMap<>();

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
        adapter = new FilteredStackedBarAdapter(symbols, this, getColumnWidth(1));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.symbolToggle), "Symbols");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.netGrowthToggle), "Net Growth %");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.marginGrowthToggle), "Margin Growth %");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.equityGrowthToggle), "E/A Growth %");

        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.roeToggle), "ROE");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.roaToggle), "ROA");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.peToggle), "P/E");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.pbvToggle), "P/BV");

        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.epsGrowthToggle), "EPS Growth %");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.dvdGrowthToggle), "DVD Growth %");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.dvdPercentToggle), "DVD %");
        toggleButtonMap.put((ToggleButton)rootView.findViewById(R.id.xdToggle), "XD");

        int buttonWidth = displayMetrics.widthPixels / 4;
        Set<ToggleButton> toggleButtons = toggleButtonMap.keySet();
        for (ToggleButton toggleButton:toggleButtons) {
            toggleButton.setText(toggleButton.getTextOff());
            toggleButton.setTextOn(toggleButton.getTextOff());
            toggleButton.getLayoutParams().width = buttonWidth;
            toggleButton.setOnClickListener(this);
            toggleButton.setOnLongClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToggles();
    }

    @Override
    public void onClick(View view) {

        filterSortManager.toggleSort(toggleButtonMap.get(view));
        applySort();
        reload();
        updateToggles();

    }

    @Override
    public boolean onLongClick(View view) {

        ToggleButton toggleButton = (ToggleButton) view;

        final String valueName = toggleButtonMap.get(toggleButton);
        final String valueTitle = toggleButton.getTextOff().toString();

        if (filterSortManager.isFilter(valueName)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    popupFilterDialog(valueName, valueTitle);
                }
            });
        }
        return false;
    }

    private void popupFilterDialog(String valueName, String valueTitle) {
        Dialog dialog = filterSortManager.buildFilterDialog(getActivity(), valueName, valueTitle, new FilterSortManager.FilterToggleButtonManagerListener() {
            @Override
            public void onChange() {
                applyFilter();
                reload();
                updateToggles();
            }
        });
        dialog.show();
    }

    private void updateToggles() {
        Set<ToggleButton> toggleButtons = toggleButtonMap.keySet();
        for (ToggleButton toggleButton:toggleButtons) {
            filterSortManager.update(toggleButtonMap.get(toggleButton), toggleButton);
        }
    }

}
