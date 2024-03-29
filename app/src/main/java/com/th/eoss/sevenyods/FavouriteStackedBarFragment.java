package com.th.eoss.sevenyods;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.th.eoss.util.Filter;
import com.th.eoss.util.SETFIN;

/**
 * Created by eossth on 3/14/2017 AD.
 */

public class FavouriteStackedBarFragment extends StackedBarFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        load(mainActivity().loadFromFavorite());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.recommended_main, container, false);

        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new SETQuoteAsyncTask(symbols()) {

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        swipe.setRefreshing(false);
                        reload();
                    }

                }.execute();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new FilteredStackedBarAdapter(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new SETQuoteAsyncTask(symbols()) {

            @Override
            protected void onPostExecute(Void aVoid) {
                reload();
            }

        }.execute();

    }
}
