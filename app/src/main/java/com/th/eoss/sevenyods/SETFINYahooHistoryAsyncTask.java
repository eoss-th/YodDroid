package com.th.eoss.sevenyods;

import android.os.AsyncTask;

import com.th.eoss.util.SETFIN;
import com.th.eoss.util.YahooHistory;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETFINYahooHistoryAsyncTask extends AsyncTask<Void, Void, YahooHistory> {

    private SETFIN set;

    public SETFINYahooHistoryAsyncTask(SETFIN set) {
        this.set = set;
    }

    @Override
    protected YahooHistory doInBackground(Void... params) {
        return new YahooHistory(set.symbol);
    }

    @Override
    protected void onPostExecute(YahooHistory yahooHistory) {
        set.yahooHistory = yahooHistory;
        ChartFragment.chartFragment().loadYahoo(set);
    }
}
