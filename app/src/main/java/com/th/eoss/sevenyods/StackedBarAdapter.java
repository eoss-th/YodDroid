package com.th.eoss.sevenyods;

import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.th.eoss.util.SETFIN;

import java.util.List;

/**
 * Created by wisarutsrisawet on 3/12/17.
 */

public class StackedBarAdapter extends RecyclerView.Adapter<StackedBarAdapter.StackedBarViewHolder> {

    protected YODContext yodContext;
    protected int stackedWidth;

    class StackedBarViewHolder extends RecyclerView.ViewHolder {

        LinearLayout asset, equityGrowth, equity, netGrowth, net;
        TextView symbol, last, percentChg, peg;
        ImageView star;

        StackedBarViewHolder (View view) {
            super(view);

            asset = (LinearLayout) view.findViewById(R.id.asset);
            equityGrowth = (LinearLayout) view.findViewById(R.id.equityGrowth);
            equity = (LinearLayout) view.findViewById(R.id.equity);
            netGrowth = (LinearLayout) view.findViewById(R.id.netGrowth);
            net = (LinearLayout) view.findViewById(R.id.net);

            symbol = (TextView) view.findViewById(R.id.symbol);
            last = (TextView) view.findViewById(R.id.last);
            percentChg = (TextView) view.findViewById(R.id.percentChg);
            peg = (TextView) view.findViewById(R.id.peg);

            star = (ImageView) view.findViewById(R.id.star);
        }

    }

    public StackedBarAdapter(YODContext yodContext) {
        this.yodContext = yodContext;
        this.stackedWidth = yodContext.getColumnWidth(2);
    }

    @Override
    public StackedBarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StackedBarViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.stackedbar_card, parent, false));
    }

    @Override
    public void onBindViewHolder(final StackedBarViewHolder holder, int position) {
        if ( position<yodContext.symbols().size() ) {

            String symbol = yodContext.symbols().get(position);
            final SETFIN set = SETFIN.cache.get(symbol);

            if (set!=null) {

                onBindStackedBarViewHolder(set, holder);

                holder.asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        yodContext.onClicked(set);
                    }
                });


                holder.asset.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        yodContext.onLongClicked(set);
                        return false;
                    }
                });

                holder.star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (yodContext.mainActivity().favouriteSymbols().contains(set.symbol)) {

                            yodContext.mainActivity().removeFromFavourite(set.symbol);

                        } else {

                            yodContext.mainActivity().addToFavourite(set.symbol);

                        }

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return yodContext.symbols().size();
    }

    protected void onBindStackedBarViewHolder (SETFIN set, StackedBarViewHolder holder) {

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

        holder.symbol.setText(set.symbol);
        holder.last.setText("" + set.getFloatValue("Last"));

        if ( holder.percentChg!=null ) {

            float percentChg = set.getFloatValue("% Chg");

            holder.percentChg.setVisibility(View.VISIBLE);

            if (percentChg == 0) {
                holder.percentChg.setText("");
                holder.percentChg.setTextColor(Color.BLACK);
            } else if (percentChg > 0) {
                holder.percentChg.setText("(+" + percentChg + "%)");
                holder.percentChg.setTextColor(Color.parseColor("#006400"));
            } else {
                holder.percentChg.setText("(" + percentChg + "%)");
                holder.percentChg.setTextColor(Color.RED);
            }

        }

        holder.peg.setText("" + set.getFloatValue("PEG"));

        if (yodContext.mainActivity().favouriteSymbols().contains(set.symbol)) {
            holder.star.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.star.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

}
