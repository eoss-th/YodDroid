package com.th.eoss.old;

import android.os.AsyncTask;

import com.th.eoss.util.SETSummary;
import com.th.eoss.util.YahooHistory;

/**
 * Created by wisarut on 30/9/2559.
 */

public class YahooHistoryAsyncTask extends AsyncTask<Void, Void, YahooHistory> {

    private SETSummary setSummary;

    public YahooHistoryAsyncTask(SETSummary setSummary) {
        this.setSummary = setSummary;
    }

    @Override
    protected YahooHistory doInBackground(Void... params) {
        return new YahooHistory(setSummary.symbol);
    }

    @Override
    protected void onPostExecute(YahooHistory yahooHistory) {
        setSummary.yahooHistory = yahooHistory;
    }
}
