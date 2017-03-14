package com.th.eoss.sevenyods;

import android.os.AsyncTask;
import android.util.Log;

import com.th.eoss.util.SETFIN;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisarut on 30/9/2559.
 */

public class SETFINAsyncTask extends AsyncTask<Void, Void, List<String>> {

    @Override
    protected List<String> doInBackground(Void... params) {

        List<String> symbols = new ArrayList<>();
        List<SETFIN> list = SETFIN.load();

        for (SETFIN set:list) {
            symbols.add(set.symbol);
        }

        return symbols;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        Log.d("EOSS", "Successs" + list.size());
    }
}
