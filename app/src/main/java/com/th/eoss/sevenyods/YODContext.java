package com.th.eoss.sevenyods;

import com.th.eoss.util.SETFIN;

import java.util.List;
import java.util.Set;

/**
 * Created by eossth on 3/14/2017 AD.
 */

interface YODContext {
    void onClicked(SETFIN setfin);
    void onLongClicked(SETFIN setfin);
    List<String> symbols();
    MainActivity mainActivity();
    int getColumnWidth(int numColumns);
}
