package com.th.eoss.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eossth on 3/3/2017 AD.
 */

public class SETHistorical {

    public static final String historicalURL = "http://eoss-setfin.appspot.com/his?s=";

    public String asOfDate;

    public Map<String, Float> values = new HashMap<>();

    public SETHistorical(String asOfDate, float assets, float liabilities, float equity, float paidUpCapital, float revenue, float netProfit, float eps, float roa, float roe, float netProfitMargin, float dvdYield) {
        this.asOfDate = asOfDate;

        values.put("assets", assets);
        values.put("liabilities", liabilities);
        values.put("equity", equity);
        values.put("paidUpCapital", paidUpCapital);
        values.put("revenue", revenue);
        values.put("netProfit", netProfit);
        values.put("eps", eps);
        values.put("roa", roa);
        values.put("roe", roe);
        values.put("netProfitMargin", netProfitMargin);
        values.put("dvdYield", dvdYield);
    }

    public static List<SETHistorical> load(String symbol) {

        String s = symbol.replace("&", "%26");
        s = s.replace(" ", "%20");

        List<SETHistorical> result = new ArrayList<>();

        try {
            URL url = new URL(historicalURL+s);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            String [] tokens;
            br.readLine();

            while ((line=br.readLine())!=null) {
                tokens = line.split(",");
                result.add(new SETHistorical(tokens[0],
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3]),
                        Float.parseFloat(tokens[4]),
                        Float.parseFloat(tokens[5]),
                        Float.parseFloat(tokens[6]),
                        Float.parseFloat(tokens[7]),
                        Float.parseFloat(tokens[8]),
                        Float.parseFloat(tokens[9]),
                        Float.parseFloat(tokens[10]),
                        Float.parseFloat(tokens[11])
                        ));
            }

        } catch (Exception e) {

        }

        return result;
    }

}
