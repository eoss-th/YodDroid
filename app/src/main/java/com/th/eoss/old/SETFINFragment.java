package com.th.eoss.old;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.th.eoss.sevenyods.MainActivity;
import com.th.eoss.sevenyods.R;
import com.th.eoss.sevenyods.SETFINFilterToggleButton;
import com.th.eoss.util.Filter;
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

public class SETFINFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private static SETFINFragment[] fragments;

    private static List<String> symbols = new ArrayList<>();
    private static Map<String, SETFIN> map = new TreeMap<>();

    private GridView gridView;

    private String [] columns = new String[0];
    private int headerIndex;
    private int columnWidth;
    private int fontSize = 5;

    private List<SETFINFilterToggleButton> toggleButtons;

    public static SETFINFragment[] tableFragments () {
        if (fragments ==null) {
            fragments = new SETFINFragment[SETFIN.HEADERS.length];
            for (int i = 0; i< fragments.length; i++) {
                SETFINFragment tableFragment = new SETFINFragment();
                tableFragment.columns = SETFIN.HEADERS[i];
                tableFragment.headerIndex = i;
                fragments[i] = tableFragment;
            }
        }
        return fragments;
    }

    public static void reloadAll() {
        for (SETFINFragment t: fragments) {
            t.reload();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.table, container, false);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        columnWidth = size.x / SETFIN.HEADERS[0].length;

        LinearLayout head = (LinearLayout) rootView.findViewById(R.id.headerLayout);
        toggleButtons = new ArrayList<>();
        SETFINFilterToggleButton button;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(head.getLayoutParams());
        for (String c:columns) {
            button = new SETFINFilterToggleButton(getContext(), c);
            button.setLayoutParams(params);
            button.getLayoutParams().width = columnWidth;
            button.setTextSize(fontSize);
            button.setOnLongClickListener(this);
            button.setOnClickListener(this);
            head.addView(button);
            toggleButtons.add(button);
        }

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return symbols.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LinearLayout row;
                if ( convertView==null ) {
                    convertView = inflater.inflate(R.layout.row, null);
                    row = (LinearLayout) convertView.findViewById(R.id.rowLayout);
                    TextView textView;
                    int id = 0;
                    for (String c:columns) {
                        textView = new TextView(getContext());
                        textView.setId(id++);
                        textView.setMinWidth(columnWidth);
                        textView.setMaxWidth(columnWidth);
                        textView.setTextSize(fontSize);
                        row.addView(textView);
                    }
                } else {
                    row = (LinearLayout) convertView.findViewById(R.id.rowLayout);
                }

                if ( position<symbols.size() ) {

                    String symbol = symbols.get(position);
                    final SETFIN set = map.get(symbol);

                    TextView textView;

                    for (int i=0; i<SETFIN.HEADERS[0].length; i++) {
                        textView= (TextView) row.findViewById(i);
                        if (i==0) {
                            textView.setText(symbol);
                        } else {
                            //textView.setText(set.getValuesFormatted()[headerIndex][i-1]);
                        }
                    }

                    if (headerIndex==5) {
                        /*
                        new SETQuoteAsyncTask(symbol, new SETQuote.SETQuoteListener() {
                            @Override
                            public void onLoaded(SETQuote quote) {
                                //set.updateQuote(quote);
                                reload();
                            }
                        }).execute();*/
                    }

                }

                return convertView;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if ( position<symbols.size() ) {
                    String symbol = symbols.get(position);
                    SETFIN set = map.get(symbol);
                    if (set.yahooHistory()!=null)
                        ((MainActivity)getActivity()).displayChart(set);
                }
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if ( position < symbols.size() ) {
                    String symbol = symbols.get(position);
                    SETFIN set = map.get(symbol);
                    popupFavoriteDialog(set);
                }
                return true;
            }
        });

        return rootView;
    }


    public static void add(SETFIN set) {

        if (!symbols.contains(set.symbol)) {
            symbols.add(set.symbol);
            map.put(set.symbol, set);

            for ( String head:SETFIN.MEAN_HEADERS) {
                Mean.add(head, set.getFloatValue(head));
            }
        }

        reloadAll();
    }

    public void reload() {
        if (gridView!=null)
            gridView.invalidateViews();
        updateToggle();
    }

    public static void clear() {
        symbols.clear();
        map.clear();
        SETFINFilterToggleButton.clear();
    }

    public void updateToggle() {
        if (toggleButtons!=null) {
            for (SETFINFilterToggleButton tb:toggleButtons) {
                tb.update();
            }
        }
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
            symbols = resultSortedSymbol;
        }

        reloadAll();
    }

    private void applyFilter() {
        Set<String> symbols = map.keySet();
        SETFINFragment.symbols.clear();
        for (String s:symbols) {
            SETFINFragment.symbols.add(s);
        }
        SETFIN setfin;
        Filter filter;
        String filterText;
        for ( String s:symbols ) {
            setfin = map.get(s);
            if (!SETFINFilterToggleButton.isValid(setfin))
                SETFINFragment.symbols.remove(s);
        }

        reloadAll();
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
