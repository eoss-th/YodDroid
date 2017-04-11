package com.th.eoss.sevenyods;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

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

public abstract class StackedBarFragment extends Fragment implements YODContext {

    protected List<String> symbols = new ArrayList<>();
    private Map<String, SETFIN> map = new TreeMap<>();

    protected FilterSortManager filterSortManager;
    protected RecyclerView recyclerView;
    protected RecyclerView.Adapter adapter;

    protected DisplayMetrics displayMetrics = new DisplayMetrics();

    public StackedBarFragment() {
        filterSortManager = FilterSortManager.instance(getClass().getName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ((MainActivity)getActivity()).loadFromFavorite();
    }

    @Override
    public void onResume() {
        super.onResume();

        applyFilter();
        applySort();
        reload();
    }

    public void load(List<String> list) {

        symbols.clear();
        map.clear();

        symbols.addAll(list);

        SETFIN set;
        for (String symbol:list) {

            set = SETFIN.cache.get(symbol);

            if (set!=null) {
                map.put(symbol, set);

                /*
                for (String head:SETFIN.MEAN_HEADERS) {
                    Mean.add(head, set.getFloatValue(head));
                }*/
            }
        }

        applyFilter();
        applySort();
        reload();
    }

    @Override
    public void onClicked(SETFIN setfin) {
        ((MainActivity) getActivity()).displayChart(setfin);
    }

    @Override
    public void onLongClicked(SETFIN setfin) {
        popupFavoriteDialog(setfin);
    }

    @Override
    public List<String> symbols() {
        return symbols;
    }

    @Override
    public int getColumnWidth(int numColumns) {
        return (int) ((displayMetrics.widthPixels / numColumns) * 0.8);
    }

    @Override
    public MainActivity mainActivity() {
        return ((MainActivity) getActivity());
    }

    protected final void applySort() {

        Map<String, SETFIN> sortedMap = filterSortManager.sort(map);

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

    }

    protected final void applyFilter() {

        List<String> resultFilteredSymbol = new ArrayList<>();
        SETFIN setfin;
        for ( String s:symbols ) {
            setfin = map.get(s);
            if (filterSortManager.isValid(setfin))
                resultFilteredSymbol.add(s);
        }

        symbols.clear();
        symbols.addAll(resultFilteredSymbol);

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

    protected final void reload() {

        if (adapter!=null)
            adapter.notifyDataSetChanged();

        if (recyclerView!=null)
            recyclerView.invalidate();
    }

    protected final void launchSeto(String symbol) {
        String packageName = "com.th.eoss.setoperator";
        Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        } else {
            intent.putExtra("symbol", symbol);
        }

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            if (null != intent.resolveActivity(getContext().getPackageManager())) {
                getContext().startActivity(intent);
            }
        }

    }
}
