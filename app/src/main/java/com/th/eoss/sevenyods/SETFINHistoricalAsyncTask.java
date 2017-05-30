package com.th.eoss.sevenyods;

import android.os.AsyncTask;
import android.util.Log;

import com.github.mikephil.charting.charts.Chart;
import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETHistorical;

import java.util.List;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETFINHistoricalAsyncTask extends AsyncTask<Void, Void, List<SETHistorical>> {

    private String symbol;

    public SETFINHistoricalAsyncTask(String symbol) {
        this.symbol = symbol;
    }

    @Override
    protected List<SETHistorical> doInBackground(Void... params) {
        Log.d("CHART", symbol + " LOADING!!!!");
        return SETHistorical.load(symbol);
    }

    @Override
    protected void onPostExecute(List<SETHistorical> historicals) {
    }
}
