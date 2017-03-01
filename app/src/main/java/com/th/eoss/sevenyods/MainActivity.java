package com.th.eoss.sevenyods;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETIndex;
import com.th.eoss.util.YahooHistory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    private ViewPager pager;
    private Toolbar toolbar;

    private Set<String> favouriteSymbols = new TreeSet<>();
    private int group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

// Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager) findViewById(R.id.pager);
        FragmentStatePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new SETIndexAsyncTask().execute(navigationView);
        new SETFINAsyncTask().execute();

        SearchView searchView = (SearchView) navigationView.getHeaderView(0).findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String [] searchSymbols = query.trim().split(" ");

                List<String> symbols = new ArrayList<String>();

                for (String searchSymbol:searchSymbols) {
                    SETIndex.DICT.values().contains(searchSymbol);
                    symbols.add(searchSymbol);
                }

                if (!symbols.isEmpty()) {
                    SETFINStackedBarFragment.load(symbols);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = item.getTitle().toString();

        loadGroup(id, title);

        return true;
    }

    private void loadGroup(int id, String title) {

        toolbar.setTitle(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        List<String> symbols;

        if (id==0) {
            symbols = loadFromFavorite();
        } else {
            symbols = SETIndex.DICT.get(id);
        }

        if (symbols!=null) {
            SETFINStackedBarFragment.load(symbols);
        }

        group = id;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position==getCount()-1)
                return ChartFragment.chartFragment();
            return SETFINStackedBarFragment.tableFragments()[position];
        }

        @Override
        public int getCount() {
            return SETFINStackedBarFragment.tableFragments().length + 1;
        }
    }

    private List<String> loadFromFavorite() {
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
            e.printStackTrace();
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
    }

    public void removeFromFavourite (String symbol) {
        favouriteSymbols.remove(symbol);
        updateFavourite();
        if (group==0) {
            loadGroup(group, toolbar.getTitle().toString());
        }
    }

    public void displayChart(SETFIN set) {
        pager.setCurrentItem(3);
        ChartFragment.chartFragment().load(set);
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
