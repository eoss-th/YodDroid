package com.th.eoss.sevenyods;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETQuote;
import com.th.eoss.util.YahooHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisarut on 5/10/2559.
 */

public class ChartFragment extends Fragment implements View.OnClickListener {

    private static ChartFragment chartFragment;

    private static String [] DAY_RANGE_LABELS = {"W", "M", "6M", "Y", "2Y", "3Y", "6Y"};
    private static int [] DAY_RANGES = {7, 30, 180, 365, 365*2, 365*3, 365*6};

    private CombinedChart combinedChart;

    private SETFIN set;

    private int daysRangeIndex;

    private int columnWidth;

    public static ChartFragment chartFragment() {
        if (chartFragment==null) {
            chartFragment = new ChartFragment();
        }
        return chartFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.chart, container, false);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        columnWidth = size.x / DAY_RANGE_LABELS.length;

        combinedChart = (CombinedChart) rootView.findViewById(R.id.combinedChart);

        combinedChart.getAxisLeft().setDrawGridLines(false);
        combinedChart.getAxisLeft().setDrawLabels(false);

        LinearLayout head = (LinearLayout) rootView.findViewById(R.id.headerLayout);

        Button button;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(head.getLayoutParams());
        for (String label:DAY_RANGE_LABELS) {
            button = new Button(getContext());
            button.setText(label);
            button.setTextSize(9);
            button.setLayoutParams(params);
            button.getLayoutParams().width = columnWidth;
            button.setOnClickListener(this);
            head.addView(button);
        }

        load(set);

        return rootView;
    }

    public void load(final SETFIN set) {

        if (set==null) return;

        this.set = set;

        if (set.yahooHistory==null) {
            new SETFINYahooHistoryAsyncTask(set).execute();
            return;
        }

        final YahooHistory.HiLo [] hiloes = set.yahooHistory.hilos;

        LineDataSet closeDataSet = new LineDataSet(subList(createEntries(set.yahooHistory.closes),DAY_RANGES[daysRangeIndex]), "Closed");
        closeDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineData lineData = new LineData();
        lineData.addDataSet(closeDataSet);

        CandleDataSet candleDataSet = new CandleDataSet(subList(createEntries(hiloes), DAY_RANGES[daysRangeIndex]), "Candle");
        candleDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        candleDataSet.setIncreasingColor(Color.GREEN);
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setDecreasingColor(Color.RED);

        CandleData candleData = new CandleData();
        candleData.addDataSet(candleDataSet);

        long [] volumes = new long [hiloes.length];
        for (int i=0; i<volumes.length; i++) {
            volumes[i] = hiloes[i].volume;
        }

        BarDataSet volumeDataSet = new BarDataSet(subList(createEntries(volumes), DAY_RANGES[daysRangeIndex]), "Volume");
        volumeDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        volumeDataSet.setColor(Color.LTGRAY);

        BarData barData = new BarData();
        barData.addDataSet(volumeDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(barData);
        combinedData.setData(candleData);

        combinedChart.setDescription(set.yahooHistory.symbol);
        combinedChart.setData(combinedData);

        combinedChart.getXAxis().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (index<hiloes.length)
                    return hiloes[index].date;
                return "";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        combinedChart.getAxisLeft().setAxisMinValue(volumeDataSet.getYMin());
        combinedChart.getAxisLeft().setAxisMaxValue(volumeDataSet.getYMax() * 2.0f);

        combinedChart.invalidate();
    }

    private List<Entry> createEntries(float [] values) {
        List<Entry> entries = new ArrayList<>();

        int x = 0;
        for (float value:values) {
            entries.add(new Entry(x, value));
            x ++;
        }
        return entries;
    }

    private List<BarEntry> createEntries(long [] values) {
        List<BarEntry> entries = new ArrayList<>();

        int x = 0;
        for (float value:values) {
            entries.add(new BarEntry(x, value));
            x ++;
        }
        return entries;
    }

    private List<CandleEntry> createEntries(YahooHistory.HiLo [] hiloes) {
        List<CandleEntry> entries = new ArrayList<>();

        int x = 0;
        for (YahooHistory.HiLo hilo:hiloes) {
            entries.add(new CandleEntry(x, hilo.high, hilo.low, hilo.open, hilo.close));
            x++;
        }

        return entries;
    }

    private <T> List<T> subList(List<T> entries, int days) {
        int fromIndex = entries.size() - days;
        int toIndex = entries.size();
        return entries.subList(fromIndex, toIndex);
    }

    @Override
    public void onClick(View v) {

        Button button = (Button) v;
        String label = button.getText().toString();
        int index = 0;
        for (String dayRangeLabel:DAY_RANGE_LABELS) {
            if (dayRangeLabel.equals(label)) {
                daysRangeIndex = index;
                break;
            }
            index ++;
        }

        load(set);
    }

    @Override
    public void onResume() {
        super.onResume();
        load(set);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
/*
        if (visible && set.yahooHistory!=null) {
            new SETQuoteAsyncTask(set.yahooHistory.symbol, new SETQuote.SETQuoteListener() {
                @Override
                public void onLoaded(SETQuote quote) {
                    set.yahooHistory.updateLast(new YahooHistory.HiLo(quote.date, quote.open, quote.high, quote.low, quote.last, quote.volume));
                    combinedChart.invalidate();
                }
            }).execute();

        }
        */
    }
}
