package com.th.eoss.sevenyods;

import android.os.AsyncTask;

import com.th.eoss.util.SETQuote;
import com.th.eoss.util.SETSummary;
import com.th.eoss.util.YahooHistory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETQuoteAsyncTask extends AsyncTask<Void, Void, SETQuote> {

    private static Map<String, Long> timestampMap = new HashMap<>();
    private String symbol;
    private SETQuote.SETQuoteListener listener;

    public SETQuoteAsyncTask(String symbol, SETQuote.SETQuoteListener listener) {
        this.symbol = symbol;
        this.listener = listener;
    }

    @Override
    protected SETQuote doInBackground(Void... paramss) {
        Long lastTimestamp = timestampMap.get(symbol);
        if (lastTimestamp==null)
            lastTimestamp = 0L;

        if (System.currentTimeMillis() - lastTimestamp > 1000*60) {
            timestampMap.put(symbol, System.currentTimeMillis());
            return new SETQuote(symbol);
        }
        return null;
    }

    @Override
    protected void onPostExecute(SETQuote quote) {
        if (quote!=null)
            listener.onLoaded(quote);
    }
}
