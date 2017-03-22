package com.th.eoss.sevenyods;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.th.eoss.util.SETFIN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private StackedBarFragment filteredStackedBarFragment = new FilteredStackedBarFragment();
    private StackedBarFragment recommendedStackedBarFragment = new RecommendedStackedBarFragment();
    private ChartFragment chartFragment = new ChartFragment();

    private ViewPager pager;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TabLayout tab;

    private Set<String> favouriteSymbols = new TreeSet<>();
    private int groupId;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        pager = (ViewPager) findViewById(R.id.pager);
        FragmentPagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);

        tab = (TabLayout) findViewById(R.id.tab);
        tab.setupWithViewPager(pager);

        int [] icons = {android.R.drawable.ic_menu_zoom, android.R.drawable.star_big_on, android.R.drawable.ic_menu_gallery};

        TextView text;
        for (int i = 0; i < tab.getTabCount(); i++) {
            text = new TextView(getApplicationContext());
            text.setCompoundDrawablesWithIntrinsicBounds(0, icons[i], 0, 0);
            tab.getTabAt(i).setCustomView(text);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /**
         * Create Side Menu
         */
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
           navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        Set<String> industries = SETFIN.map.keySet();
        SubMenu subMenu;
        Set<String> sections;
        List<String> symbols;
        int itemId = Menu.FIRST + 2;
        int group = 0;
        int seq = 0;
        for (String industry:industries) {
            subMenu = menu.addSubMenu(industry);
            sections = SETFIN.map.get(industry).keySet();
            for (String section:sections) {
                subMenu.add(group, itemId, seq, section);
                symbols = SETFIN.map.get(industry).get(section);
                SETFIN.DICT.put(itemId, symbols);
                itemId ++;
                seq ++;
            }
            group ++;
        }

        /**
         * Create Search
         */
        SearchView searchView = (SearchView) navigationView.getHeaderView(0).findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String [] searchSymbols = query.trim().split(" ");

                List<String> symbols = new ArrayList<String>();

                Set<String> cacheSymbols = SETFIN.cache.keySet();
                for (String searchSymbol:searchSymbols) {

                    for (String s:cacheSymbols) {
                        if (s.startsWith(searchSymbol.toUpperCase())) {
                            symbols.add(s);
                        }
                    }
                }

                filteredStackedBarFragment.load(symbols);

                toolbar.setTitle("");

                drawer.closeDrawer(GravityCompat.START);
                pager.setCurrentItem(0);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.trim().isEmpty()) {
                    //Return to last group
                    loadGroup(groupId, title);
                }

                return false;
            }
        });

        /**
         * Display Home
         */
        pager.setCurrentItem(1);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        Log.d("EOSS-TH", item.getSubMenu().getItem().toString() +":"+item.toString());

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        String title = item.getTitle().toString();

        loadGroup(id, title);

        drawer.closeDrawer(GravityCompat.START);
        pager.setCurrentItem(0);

        return true;
    }

    void loadGroup(int id, String title) {

        List<String> symbols;

        if (id==R.id.fav) {
            symbols = loadFromFavorite();
        } else if (id==R.id.all) {

            symbols = SETFIN.cache_symbols;

        } else {
            symbols = SETFIN.DICT.get(id);
        }

        if (symbols!=null) {
            filteredStackedBarFragment.load(symbols);
        }

        toolbar.setTitle(title);

        this.groupId = id;
        this.title = title;
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private Fragment [] fragments = new Fragment[] {filteredStackedBarFragment, recommendedStackedBarFragment, chartFragment};

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments [position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    Set<String> favouriteSymbols() {
        return favouriteSymbols;
    }

    List<String> loadFromFavorite() {
        String filename = "favorites";
        File file = new File(getApplicationContext().getFilesDir(), filename);
        BufferedReader br = null;
        List<String> symbols = new ArrayList<>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String symbol;
            while ((symbol=br.readLine())!=null) {
                symbols.add(symbol);
            }

            favouriteSymbols.clear();
            favouriteSymbols.addAll(symbols);

        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
             if (br!=null)
                 try {
                     br.close();
                 } catch (IOException e) {
                 }
        }
        return symbols;
    }

    private void updateFavourite () {
        String filename = "favorites";
        PrintWriter out = null;

        try {
            out = new PrintWriter(openFileOutput(filename, Context.MODE_PRIVATE));

            for (String s: favouriteSymbols) {
                out.println(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out!=null)
                try { out.close(); } catch (Exception e) {}
        }
    }

    public boolean containsFavourite(String symbol) {
        return favouriteSymbols.contains(symbol);
    }

    public void addToFavourite (String symbol) {
        favouriteSymbols.add(symbol);
        updateFavourite();
        filteredStackedBarFragment.reload();
        recommendedStackedBarFragment.reload();

        if (groupId==R.id.fav) {
            loadGroup(groupId, toolbar.getTitle().toString());
        }
    }

    public void removeFromFavourite (String symbol) {
        favouriteSymbols.remove(symbol);
        updateFavourite();
        filteredStackedBarFragment.reload();
        recommendedStackedBarFragment.reload();

        if (groupId==R.id.fav) {
            loadGroup(groupId, toolbar.getTitle().toString());
        }
    }

    public void displayChart(SETFIN set) {
        pager.setCurrentItem(2);
        chartFragment.loadHistoricals(set);
    }

    /*
    private void clearCache(String sector) {
        File file = new File(getApplicationContext().getFilesDir(), sector);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String asOfDate = br.readLine();
            if (asOfDate!=null) {
                Document doc = Jsoup.connect(SETSummary.URL+"A").get();
                Element dateElement = doc.select("td[style='background-color:#000000; color:#FFFFFF; font-weight:bold;']").first();
                String currentAsOfDate = dateElement.text().replace(" ", "");

                //if (asOfDate)

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br!=null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }
    }*/

}
