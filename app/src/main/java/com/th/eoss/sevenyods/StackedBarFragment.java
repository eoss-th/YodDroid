package com.th.eoss.sevenyods;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.th.eoss.util.Mean;
import com.th.eoss.util.SETFIN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * Created by wisarut on 30/9/2559.
 */

public class StackedBarFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private List<String> symbols = new ArrayList<>();
    private Map<String, SETFIN> map = new TreeMap<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        load(SETFIN.cache_symbols);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.content_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new StackedBarAdapter(symbols, map);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void load(List<String> list) {
        symbols.clear();
        map.clear();

        SETFIN set;
        for (String symbol:list) {
            symbols.add(symbol);
            set = SETFIN.cache.get(symbol);

            if (set!=null) {
                map.put(symbol, set);

                for ( String head:SETFIN.MEAN_HEADERS) {
                    Mean.add(head, set.getFloatValue(head));
                }
            }
        }

        reload();
    }

    public void reload() {

        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }

    private void toggleSort(final SETFINFilterToggleButton toggleButton) {
        SETFINFilterToggleButton.toggleSort(toggleButton);
        applySort();
    }

    private void popupFavoriteDialog(final SETFIN setfin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.containsFavourite(setfin.symbol)) {
            String [] items = new String[] {"Remove " + setfin.symbol + " from Favourite", "Cancel"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if ( which==0 ) {
                        mainActivity.removeFromFavourite(setfin.symbol);
                    }
                }
            });

        } else {
            String [] items = new String[] {"Add " + setfin.symbol + " to Favourite", "Cancel"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if ( which==0 ) {
                        mainActivity.addToFavourite(setfin.symbol);
                    }
                }
            });

        }
        builder.create();
        builder.show();
    }

    private void popupFilterDialog(final SETFINFilterToggleButton toggleButton) {
        Dialog dialog = SETFINFilterToggleButton.buildFilterDialog(getActivity(), toggleButton, new SETFINFilterToggleButton.FilterToggleButtonListener() {
            @Override
            public void onChange() {
                applyFilter();
            }
        });
        dialog.show();
    }

    private void applySort() {

        Map<String, SETFIN> sortedMap = SETFINFilterToggleButton.sort(map);

        if ( sortedMap!=null ) {
            sortedMap.putAll(map);
            Set<String> sortedSymbols = sortedMap.keySet();
            List<String> resultSortedSymbol = new ArrayList<>();
            for (String s:sortedSymbols) {
                if (symbols.contains(s))
                    resultSortedSymbol.add(s);
            }
            symbols.clear();
            symbols.addAll(resultSortedSymbol);
        }

        reload();
    }

    private void applyFilter() {
        Set<String> symbols = map.keySet();
        this.symbols.clear();
        for (String s:symbols) {
            this.symbols.add(s);
        }
        SETFIN setfin;
        for ( String s:symbols ) {
            setfin = map.get(s);
            if (!SETFINFilterToggleButton.isValid(setfin))
                this.symbols.remove(s);
        }

        reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onClick(View view) {
        toggleSort((SETFINFilterToggleButton)view);
    }

    @Override
    public boolean onLongClick(View view) {
        final SETFINFilterToggleButton toggleButton = (SETFINFilterToggleButton) view;
        if (SETFINFilterToggleButton.isFilter(toggleButton.getTextOff().toString())) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    popupFilterDialog(toggleButton);
                }
            });
        }
        return false;
    }

}
