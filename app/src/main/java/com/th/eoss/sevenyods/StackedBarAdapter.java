package com.th.eoss.sevenyods;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.th.eoss.util.SETFIN;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by wisarutsrisawet on 3/12/17.
 */

public class StackedBarAdapter extends RecyclerView.Adapter<StackedBarAdapter.StackedBarViewHolder> {

    private final List<String> symbols;
    private SETFINListener listener;
    private int stackedWidth;

    class StackedBarViewHolder extends RecyclerView.ViewHolder {

        LinearLayout asset, equityGrowth, equity, netGrowth, net;
        TextView symbol, last, pe;

        StackedBarViewHolder (View view) {
            super(view);

            asset = (LinearLayout) view.findViewById(R.id.asset);
            equityGrowth = (LinearLayout) view.findViewById(R.id.equityGrowth);
            equity = (LinearLayout) view.findViewById(R.id.equity);
            netGrowth = (LinearLayout) view.findViewById(R.id.netGrowth);
            net = (LinearLayout) view.findViewById(R.id.net);

            symbol = (TextView) view.findViewById(R.id.symbol);
            last = (TextView) view.findViewById(R.id.last);
            pe = (TextView) view.findViewById(R.id.pe);
        }

    }

    public StackedBarAdapter(List<String> symbols, SETFINListener listener, int stackedWidth) {
        this.symbols = symbols;
        this.listener = listener;
        this.stackedWidth = stackedWidth;


    }

    @Override
    public StackedBarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StackedBarViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.stackedbar_card, parent, false));
    }

    @Override
    public void onBindViewHolder(StackedBarViewHolder holder, int position) {
        if ( position<symbols.size() ) {

            String symbol = symbols.get(position);
            final SETFIN set = SETFIN.cache.get(symbol);

            if (set!=null) {

                holder.asset.setBackgroundColor(Color.WHITE);
                holder.asset.getLayoutParams().width = stackedWidth;

                float eaGrowthPercent = set.getFloatValue("E/A Growth %");

                int gaColor;
                if (eaGrowthPercent > 0) {
                    gaColor = Color.parseColor("#dee8eb");
                } else {
                    gaColor = Color.parseColor("#7ea4b3");
                }

                holder.equityGrowth.setBackgroundColor(gaColor);
                float equityGrowthWidth = set.getFloatValue("G/A") * holder.asset.getLayoutParams().width;
                holder.equityGrowth.setLayoutParams(new LinearLayout.LayoutParams((int) equityGrowthWidth, LinearLayout.LayoutParams.MATCH_PARENT));

                holder.equity.setBackgroundColor(Color.WHITE);
                float equityWidth = set.getFloatValue("E/G") * equityGrowthWidth;
                holder.equity.setLayoutParams(new LinearLayout.LayoutParams((int) equityWidth, LinearLayout.LayoutParams.MATCH_PARENT));

                float netGrowthPercent = set.getFloatValue("Net Growth %");

                int ngColor;
                if (netGrowthPercent > 0) {
                    ngColor = Color.parseColor("#b4ecb4");
                } else {
                    ngColor = Color.parseColor("#ffb2ae");
                }

                holder.netGrowth.setBackgroundColor(ngColor);
                float netGrowthWidth = Math.abs(set.getFloatValue("NG/E") * equityWidth);
                holder.netGrowth.setLayoutParams(new LinearLayout.LayoutParams((int) netGrowthWidth, LinearLayout.LayoutParams.MATCH_PARENT));

                float estimatedNet = set.getFloatValue("Estimated Net");
                int netColor;
                if (estimatedNet > 0) {
                    netColor = Color.WHITE;
                } else {
                    netColor = Color.parseColor("#ff6961");
                }

                holder.net.setBackgroundColor(netColor);
                float netWidth = Math.abs(set.getFloatValue("N/NG") * netGrowthWidth);
                holder.net.setLayoutParams(new LinearLayout.LayoutParams((int) netWidth, LinearLayout.LayoutParams.MATCH_PARENT));

                holder.symbol.setText(symbol);
                holder.last.setText("" + set.getFloatValue("Last"));
                holder.pe.setText("" + set.getFloatValue("P/E"));

                holder.asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener!=null)
                            listener.onClicked(set);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return symbols.size();
    }

}
