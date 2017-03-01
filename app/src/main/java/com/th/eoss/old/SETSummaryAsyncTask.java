package com.th.eoss.old;

import android.os.AsyncTask;

import com.th.eoss.util.SETSummary;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETSummaryAsyncTask extends AsyncTask<Void, Void, SETSummary> {

    private String symbol;

    public SETSummaryAsyncTask (String symbol) {
        this.symbol = symbol;
    }

    @Override
    protected SETSummary doInBackground(Void... params) {
        return new SETSummary(symbol);
    }

    @Override
    protected void onPostExecute(SETSummary setSummary) {
        TableFragment.add(setSummary);
        new YahooHistoryAsyncTask(setSummary).execute();
    }
}
