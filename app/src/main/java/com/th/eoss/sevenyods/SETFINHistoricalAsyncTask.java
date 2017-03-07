package com.th.eoss.sevenyods;

import android.os.AsyncTask;

import com.github.mikephil.charting.charts.Chart;
import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETHistorical;

import java.util.List;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETFINHistoricalAsyncTask extends AsyncTask<Void, Void, List<SETHistorical>> {

    private SETFIN set;
    private ChartFragment chartFragment;

    public SETFINHistoricalAsyncTask(SETFIN set, ChartFragment chartFragment) {
        this.set = set;
        this.chartFragment = chartFragment;
    }

    @Override
    protected List<SETHistorical> doInBackground(Void... params) {
        return SETHistorical.load(set.symbol);
    }

    @Override
    protected void onPostExecute(List<SETHistorical> historicals) {
        set.historicals = historicals;
        chartFragment.loadHistoricals(set);
    }
}
