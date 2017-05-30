package com.th.eoss.sevenyods;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETHistorical;
import com.th.eoss.util.YahooHistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by wisarut on 5/10/2559.
 */

public class ChartFragment extends Fragment implements View.OnClickListener {

    private static DateFormat monthOnlyFormat = new SimpleDateFormat("MMM", Locale.US);
    private static DateFormat yearOnlyFormat = new SimpleDateFormat("yyyy", Locale.US);

    private static String [] DAY_RANGE_LABELS = {"H", "M", "6M", "Y", "3Y", "6Y"};
    private static int [] DAY_RANGES = {0, 30, 180, 365, 365*3, 365*6};

    private CombinedChart combinedChart;

    private SETFIN set;

    private int daysRangeIndex=0;

    private int columnWidth;

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
        combinedChart.getAxisRight().setDrawGridLines(false);
        //combinedChart.getAxisRight().setDrawLabels(false);

        combinedChart.getXAxis().setDrawGridLines(false);

        LinearLayout head = (LinearLayout) rootView.findViewById(R.id.headerLayout);

        /*
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
        }*/

        combinedChart.setNoDataText("Please select a Symbol");

        return rootView;
    }

    public void loadHistoricals(final SETFIN set) {

        if (set==null) return;

        this.set = set;

        combinedChart.clear();
        combinedChart.getXAxis().resetAxisMinimum();
        combinedChart.getXAxis().resetAxisMaximum();
        combinedChart.setNoDataText("Loading...");
        combinedChart.invalidate();

        if (set.historicals==null) {
            return;
        }

        BarDataSet assetDataSet = new BarDataSet(createBarEntries(set.historicals, "equity", "liabilities"), "");
        assetDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        assetDataSet.setColors(new int[]{Color.parseColor("#dee8eb"), Color.parseColor("#f2f2ef")});
        assetDataSet.setStackLabels(new String[]{"Equity", "Liabilities"});

        BarDataSet paidUpCapitalDataSet = new BarDataSet(createBarEntries(set.historicals, "paidUpCapital"), "Paidup Capital");
        paidUpCapitalDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        paidUpCapitalDataSet.setColor(Color.parseColor("#7ea4b3"));
        paidUpCapitalDataSet.setDrawValues(false);

        BarData barData = new BarData();
        barData.addDataSet(assetDataSet);
        barData.addDataSet(paidUpCapitalDataSet);
        barData.setBarWidth(0.8f);

        LineDataSet revenueDataSet = new LineDataSet(createEntries(set.historicals, "revenue"), "Revenue");
        revenueDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        revenueDataSet.setColor(Color.CYAN);
        revenueDataSet.setDrawValues(false);
        revenueDataSet.setDrawCircles(false);

        LineDataSet netDataSet = new LineDataSet(createEntries(set.historicals, "netProfit"), "Net");
        netDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        netDataSet.setColor(Color.GREEN);
        netDataSet.setDrawValues(false);
        netDataSet.setDrawCircles(false);

        LineData lineData = new LineData();
        lineData.addDataSet(revenueDataSet);
        lineData.addDataSet(netDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        combinedChart.getDescription().setText(set.symbol);
        combinedChart.setData(combinedData);

        combinedChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String date="";
                int index = (int) value;
                if (index>=0&&index<set.historicals.size()) {
                    date = set.historicals.get(index).asOfDate;
                }
                return date;
            }

        });

        combinedChart.getAxisLeft().setAxisMinimum(combinedData.getYMin() > 0 ? 0:combinedData.getYMin() * 1.2f);
        combinedChart.getAxisLeft().setAxisMaximum(combinedData.getYMax() * 1.2f);
        combinedChart.getAxisLeft().setSpaceTop(10f);
        //combinedChartView.leftAxis.enabled = false

        combinedChart.getAxisRight().setAxisMinimum(combinedData.getYMin() > 0 ? 0:combinedData.getYMin() * 1.2f);
        combinedChart.getAxisRight().setAxisMaximum(combinedData.getYMax() * 1.2f);
        combinedChart.getAxisRight().setSpaceTop(10f);

        combinedChart.getXAxis().setAxisMinimum(-1f);
        combinedChart.getXAxis().setAxisMaximum(set.historicals.size());
        combinedChart.invalidate();

    }

    public void loadYahoo(final SETFIN set) {

        if (set==null) return;

        this.set = set;

        combinedChart.clear();
        combinedChart.getXAxis().resetAxisMinimum();
        combinedChart.getXAxis().resetAxisMaximum();
        combinedChart.setNoDataText("Loading...");
        combinedChart.invalidate();

        if (set.yahooHistory==null) {
            new YahooHistoryAsyncTask(set, this).execute();
            return;
        }

        final YahooHistory.HiLo [] hiloes = set.yahooHistory.hilos;

        LineDataSet closeDataSet = new LineDataSet(subList(createEntries(set.yahooHistory.closes),DAY_RANGES[daysRangeIndex]), "Closed");
        closeDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        closeDataSet.setDrawCircles(false);
        closeDataSet.setDrawValues(false);

        LineDataSet ema5DataSet = new LineDataSet(subList(createEntries(set.yahooHistory.ema5),DAY_RANGES[daysRangeIndex]), "EMA5");
        ema5DataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        ema5DataSet.setDrawCircles(false);
        ema5DataSet.setDrawValues(false);
        ema5DataSet.setColor(Color.GREEN);

        LineDataSet ema20DataSet = new LineDataSet(subList(createEntries(set.yahooHistory.ema20),DAY_RANGES[daysRangeIndex]), "EMA20");
        ema20DataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        ema20DataSet.setDrawCircles(false);
        ema20DataSet.setDrawValues(false);
        ema20DataSet.setColor(Color.CYAN);

        LineDataSet ema80DataSet = new LineDataSet(subList(createEntries(set.yahooHistory.ema80),DAY_RANGES[daysRangeIndex]), "EMA80");
        ema80DataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        ema80DataSet.setDrawCircles(false);
        ema80DataSet.setDrawValues(false);
        ema80DataSet.setColor(Color.parseColor("#ffa500"));

        LineData lineData = new LineData();
        //lineData.addDataSet(closeDataSet);
        lineData.addDataSet(ema5DataSet);
        lineData.addDataSet(ema20DataSet);
        lineData.addDataSet(ema80DataSet);

        CandleDataSet candleDataSet = new CandleDataSet(subList(createEntries(hiloes), DAY_RANGES[daysRangeIndex]), "Candle");
        candleDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        candleDataSet.setIncreasingColor(Color.parseColor("#b4ecb4"));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setDecreasingColor(Color.parseColor("#ffb2ae"));
        candleDataSet.setDrawValues(false);

        CandleData candleData = new CandleData();
        candleData.addDataSet(candleDataSet);

        long [] volumes = new long [hiloes.length];
        for (int i=0; i<volumes.length; i++) {
            volumes[i] = hiloes[i].volume;
        }

        BarDataSet volumeDataSet = new BarDataSet(subList(createEntries(volumes), DAY_RANGES[daysRangeIndex]), "Volume");
        volumeDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        volumeDataSet.setColor(Color.parseColor("#d3d3d3"));
        volumeDataSet.setDrawValues(false);

        BarData barData = new BarData();
        barData.addDataSet(volumeDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(barData);
        combinedData.setData(candleData);

        combinedChart.getDescription().setText(set.symbol);
        combinedChart.setData(combinedData);

        final DateFormat dateFormatter;
        if (daysRangeIndex<=3) {
            dateFormatter = monthOnlyFormat;
        } else {
            dateFormatter = yearOnlyFormat;
        }

        combinedChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String date="";
                int index = (int) value;
                if (index>=0&&index<hiloes.length) {
                    try {
                        date = dateFormatter.format(hiloes[index].datetime);
                    } catch (Exception e) {

                    }
                }
                return date;
            }

        });

        combinedChart.getAxisLeft().setAxisMinimum(volumeDataSet.getYMin());
        combinedChart.getAxisLeft().setAxisMaximum(volumeDataSet.getYMax() * 3.0f);
        combinedChart.getAxisLeft().setSpaceTop(20f);

        combinedChart.getAxisRight().setAxisMinimum(closeDataSet.getYMin() * 0.95f);
        combinedChart.getAxisRight().setAxisMaximum(closeDataSet.getYMax() * 1.05f);
        combinedChart.getAxisRight().setSpaceTop(20f);

        //combinedChart.getXAxis().setAxisMinValue(0);
      //  combinedChart.getXAxis().setAxisMaxValue(closeDataSet.getEntryCount());

      //  combinedChart.moveViewToX(closeDataSet.getEntryCount()-1);

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

    private List<Entry> createEntries(List<SETHistorical> historicals, String valueName) {
        List<Entry> entries = new ArrayList<>();

        int x = 0;
        for (SETHistorical his:historicals) {
            entries.add(new Entry(x, his.values.get(valueName)));
            x ++;
        }
        return entries;
    }

    private List<BarEntry> createBarEntries(List<SETHistorical> historicals, String valueName) {
        List<BarEntry> entries = new ArrayList<>();

        int x = 0;
        for (SETHistorical his:historicals) {
            entries.add(new BarEntry(x, his.values.get(valueName)));
            x ++;
        }
        return entries;
    }

    private List<BarEntry> createBarEntries(List<SETHistorical> historicals, String valueName1, String valueName2) {
        List<BarEntry> entries = new ArrayList<>();

        int x = 0;
        for (SETHistorical his:historicals) {
            entries.add(new BarEntry(x, new float[]{his.values.get(valueName1), his.values.get(valueName2)}));
            x ++;
        }
        return entries;
    }

    private <T> List<T> subList(List<T> entries, int days) {

        if (days > entries.size()) days = entries.size();

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

        if (daysRangeIndex==0) {
            loadHistoricals(set);
        } else {
            loadYahoo(set);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        combinedChart.invalidate();
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
