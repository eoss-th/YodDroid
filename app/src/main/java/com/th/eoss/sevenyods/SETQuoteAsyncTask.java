package com.th.eoss.sevenyods;

import android.os.AsyncTask;

import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETQuote;
import com.th.eoss.util.SETSummary;
import com.th.eoss.util.YahooHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETQuoteAsyncTask extends AsyncTask<Void, Void, Void> {

    private List<String> symbols;

    SETQuoteAsyncTask(List<String> symbols) {
        //Avoid Concurrent Modification Exception
        this.symbols = new ArrayList<>();
        this.symbols.addAll(symbols);
     }

    SETQuoteAsyncTask(String symbol) {
        this.symbols = new ArrayList<>();
        this.symbols.add(symbol);
    }


    @Override
    protected Void doInBackground(Void... voids) {

        SETQuote quote;
        SETFIN setfin;
        for (String symbol:symbols) {
            setfin = SETFIN.cache.get(symbol);

            if (setfin!=null) {

                quote = new SETQuote(symbol);
                setfin.values.put("Last", quote.last);
                setfin.values.put("% Chg", quote.chgPercent);
                setfin.values.put("Quote Timestamp", System.currentTimeMillis());

            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }
}
