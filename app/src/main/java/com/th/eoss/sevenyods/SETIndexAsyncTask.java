package com.th.eoss.sevenyods;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.th.eoss.util.SETIndex;

import java.util.List;
import java.util.Set;

/**
 * Created by wisarut on 29/9/2559.
 */

public class SETIndexAsyncTask extends AsyncTask<NavigationView, Void, SETIndex> {

    private NavigationView navigationView;
    @Override
    protected SETIndex doInBackground(NavigationView...params) {
        this.navigationView = params[0];
        return new SETIndex();
    }

    @Override
    protected void onPostExecute(SETIndex setIndex) {
        Menu menu = navigationView.getMenu();

        Set<String> industries = setIndex.map().keySet();
        SubMenu subMenu;
        Set<String> sections;
        List<String> symbols;
        MenuItem item;
        int itemId = Menu.FIRST + 1;
        int group = 0;
        int seq = 0;
        for (String industry:industries) {
            subMenu = menu.addSubMenu(industry);
            sections = setIndex.map().get(industry).keySet();
            for (String section:sections) {
                item = subMenu.add(group, itemId, seq, section);
                symbols = setIndex.map().get(industry).get(section);
                SETIndex.DICT.put(itemId, symbols);
                itemId ++;
                seq ++;
            }
            group ++;
        }
    }
}
