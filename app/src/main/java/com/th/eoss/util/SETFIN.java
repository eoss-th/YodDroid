package com.th.eoss.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by eossth on 2/28/2017 AD.
 */

public class SETFIN {

    public static final DateFormat xdDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final DateFormat xdDateFormat2 = new SimpleDateFormat("dd MMM yy", Locale.US);

    public static final NumberFormat numberFormat = new DecimalFormat("0.00");

    public static final List<String> cache_symbols = new ArrayList<>();
    public static final Map<String, SETFIN> cache = new TreeMap<>();

    public static final Map<String, Map<String, List<String>>> map = new TreeMap<String, Map<String, List<String>>>();;
    public static final Map<Integer, List<String>> DICT = new HashMap<>();

    public static final String [] MEAN_HEADERS = {
            "Net Growth %",
            "Margin Growth %",
            "E/A Growth %",
            "ROE",
            "ROA",
            "P/E",
            "P/BV",
            "EPS Growth %",
            "DVD Growth %",
            "DVD %",
            "PEG"
    };

    public static final List<String> LOW_IS_BETTER = new ArrayList<>();

    public static final List<String> HIGH_IS_BETTER = new ArrayList<>();

    static {
        LOW_IS_BETTER.addAll(Arrays.asList(new String[] {
                "P/E",
                "P/BV",
                "PEG"
        }));
        HIGH_IS_BETTER.addAll(Arrays.asList(new String[]  {
                "Net Growth %",
                "Margin Growth %",
                "E/A Growth %",
                "ROE",
                "ROA",
                "EPS Growth %",
                "DVD Growth %",
                "DVD %"
        }));
    }

    public static final String csvURL = "http://eoss-setfin.appspot.com/csvYod?";

    public static final String indexURL = "http://eoss-setfin.appspot.com/SETIndexServlet";

    public static final String historicalURL = "http://eoss-setfin.appspot.com/his?s=";

    public String industry;
    public String sector;
    public String symbol;
    public String date;
    public String xd;
    public String dvd;
    public boolean isStillXD;
    
    public Map<String, Number> values;

    public YahooHistory yahooHistory;

    public List<SETHistorical> historicals;

    private SETFIN(String line) {
        
        values = new HashMap<>();
        String [] tokens = line.split(",");

        industry = tokens[0];
        sector = tokens[1];
        symbol = tokens[2];
        date = tokens[3];
        
        values.put("E/A Growth %", Float(tokens[4]));
        values.put("Revenue Growth %", Float(tokens[5]));
        values.put("Net Growth %", Float(tokens[6]));
        values.put("EPS Growth %", Float(tokens[7]));
        values.put("ROE Growth %", Float(tokens[8]));
        values.put("Margin Growth %", Float(tokens[9]));
        values.put("DVD Growth %", Float(tokens[10]));
        values.put("EPS", Float(tokens[11]));
        values.put("ROA", Float(tokens[12]));
        values.put("ROE", Float(tokens[13]));
        values.put("Margin", Float(tokens[14]));
        values.put("Last", Float(tokens[15]));
        values.put("P/E", Float(tokens[16]));
        values.put("P/BV", Float(tokens[17]));
        values.put("DVD %", Float(tokens[18]));
        values.put("Market Cap:Estimated E", Float(tokens[19]));
        values.put("Estimated Asset", Float(tokens[20]));
        values.put("Estimated Equity", Float(tokens[21]));
        values.put("Estimated Revenue", Float(tokens[22]));
        values.put("Estimated Net", Float(tokens[23]));

        String dvdText = tokens[24].trim();
        if ( !dvdText.isEmpty() && !dvdText.equals("null") )
            dvd = dvdText;
        else
            dvd = "";

        try {
            Date xdDate = xdDateFormat.parse(tokens[25]);
            xd = xdDateFormat2.format(xdDate);

            Calendar xdCalendar = Calendar.getInstance(Locale.US);
            xdCalendar.setTime(xdDate);

            Calendar nowCalendar = Calendar.getInstance(Locale.US);

            isStillXD = nowCalendar.before(xdCalendar);

        } catch (Exception e) {
            xd = "";
            isStillXD = false;
        }

        values.put("Predict MA", Float(tokens[26]));
        values.put("Predict %", Float(tokens[27]));
        values.put("MA5", Float(tokens[28]));

        if (values.get("Predict MA").floatValue() > 0) {

            if (values.get("Last").floatValue() < values.get("MA5").floatValue()) {

                values.put("Trend Score", 2);

            } else {

                values.put("Trend Score", 1);

            }

        } else if (values.get("Predict MA").floatValue() < 0) {

            if (values.get("Last").floatValue() > values.get("MA5").floatValue()) {

                values.put("Trend Score", -2);

            } else {

                values.put("Trend Score", -1);
            }

        } else {

            values.put("Trend Score", 0);

        }

        float asset=values.get("Estimated Asset").floatValue()==0?1:values.get("Estimated Asset").floatValue();

        float equity=values.get("Estimated Equity").floatValue()==0?1:values.get("Estimated Equity").floatValue();

        float net=values.get("Estimated Net").floatValue();

        float equityGrowth = equity + equity * Math.abs(getFloatValue("E/A Growth %")/100.0f);
        values.put("EG", equityGrowth);

        float netGrowth;

        float netGrowthPerent = values.get("Net Growth %").floatValue();

        if (net < 0 && netGrowthPerent < 0) {
            netGrowth = net - net * netGrowthPerent/100.0f;
        } else if (netGrowthPerent > 0) {
            netGrowth = net + Math.abs(net) * netGrowthPerent/100.0f;
        } else {
            netGrowth = net + net * netGrowthPerent/100.0f;
        }
        values.put("NG", netGrowth);

        if ( netGrowth!=0 ) {
            values.put("N/NG", net/netGrowth);
        } else {
            values.put("N/NG", 1f);
        }

        if (netGrowth>equityGrowth) {
            print(this);
        }

        float nge = netGrowth / equity;

        //Trim
        /*
        if (Math.abs(nge)>1) {
            nge = nge / Math.abs(nge);
        }
        */
        values.put("NG/E", nge);

        if ( equityGrowth!=0 ) {
            values.put("E/G", equity / equityGrowth);
        } else {
            values.put("E/G", 1f);
        }

        //Trim
        if (equityGrowth > asset) {
            equityGrowth = asset;
        }

        values.put("G/A", equityGrowth / asset);

        try {
            values.put("PEG", round(values.get("P/E").floatValue() / netGrowthPerent));
        } catch (Exception e) {
            values.put("PEG", 0);
        }

/*
        //Cleansing Data for Sorting
        if let pe = values["P/E"] {

            if pe == 0 {
                values["P/E"] = Float.infinity
            }
        } else {
            values["P/E"] = Float.infinity
        }

        if let _ = values["Last"] {

        } else {
            values["Last"] = 0
        }

        if let _ = values["Net Growth %"] {

        } else {
            values["Net Growth %"] = 0
        }

        if let _ = values["E/A Growth %"] {

        } else {
            values["E/A Growth %"] = 0
        }

        if let _ = values["Predict MA"] {

        } else {
            values["Predict MA"] = 0
        }

        if let _ = values["Predict Chg %"] {

        } else {
            values["Predict Chg %"] = 0
        }

*/
        Map<String, List<String>> dict = map.get(industry);
        if (dict==null) {
            dict = new TreeMap<String, List<String>> ();
            map.put(industry, dict);
        }

        List<String> list = dict.get(sector);
        if (list==null) {
            list = new ArrayList<String> ();
            dict.put(sector, list);
        }

        list.add(symbol);

    }

    public static List<SETFIN> load() {

        cache_symbols.clear();
        cache.clear();
        List<SETFIN> result = new ArrayList<>();

        try {
            URL url = new URL(csvURL+System.currentTimeMillis());
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            br.readLine();

            while ((line=br.readLine())!=null) {
                result.add(new SETFIN(line));
            }

            for (SETFIN set:result) {
                cache_symbols.add(set.symbol);
                cache.put(set.symbol, set);

                for (String head:MEAN_HEADERS) {
                    Mean.add(set.industry + "." + set.sector + "." + head, set.getFloatValue(head));
                }

            }

            br.close();


        } catch (Exception e) {

        }

        return result;
    }

    public static void fetchQuotes() {

        Set<String> symbols = cache.keySet();

        SETFIN setfin;
        SETQuote setQuote;
        for (String symbol:symbols) {

            setfin = cache.get(symbol);
            setQuote = new SETQuote(symbol);
            setfin.values.put("Chg %", setQuote.chgPercent);

        }
    }

    private float Float(String text) {
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {

        }
        return 0;
    }

    public static void main(String[]args) {
        List<SETFIN> list = SETFIN.load();

        System.out.print("Symbol");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("Asset");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("Equity");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("Net");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("E/A Growth %");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("Equity Growth");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("Net Growth %");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("Net Growth");
        System.out.println();
        for (SETFIN set:list) {

            if (invalidEquity(set)) {
                System.out.println("Invalid Equity");
                print(set);
            }

            if (invalidNet(set)) {
                System.out.println("Invalid Net");
                print(set);
            }

            if (invalidNetGrowth(set)) {
                System.out.println("Invalid NG");
                print(set);
            }

            if (invalidNetPerGrowth(set)) {
                System.out.println("Invalid N/G");
                print(set);
            }

            if (invalidNetAndNetGrowth(set)) {
                System.out.println("Invalid Net and Growth");
                print(set);
            }

            if (invalidNGPerE(set)) {
                System.out.println("Invalid NG/E");
                print(set);
            }

            if (invalidEPerG(set)) {
                System.out.println("Invalid E/G");
                print(set);
            }

            if (invalidGPerA(set)) {
                System.out.println("Invalid G/A");
                print(set);
            }

        }
    }

    private static boolean invalidEquity(SETFIN set) {
        return set.getFloatValue("Estimated Equity") > set.getFloatValue("Estimated Asset") || set.getFloatValue("Estimated Equity") < 0;
    }

    private static boolean invalidNet(SETFIN set) {
        return set.getFloatValue("Estimated Net") > set.getFloatValue("Estimated Asset");
    }

    private static boolean invalidNetGrowth(SETFIN set) {

        if (set.getFloatValue("Net Growth %") > 0)
            return set.getFloatValue("NG") < set.getFloatValue("Estimated Net");
        else
            return set.getFloatValue("NG") > set.getFloatValue("Estimated Net");

    }

    private static boolean invalidNetPerGrowth(SETFIN set) {

        //return set.getFloatValue("N/NG") < 0 /*|| set.values.get("N/NG") == 1*/;
        return false;
    }

    private static boolean invalidNetAndNetGrowth(SETFIN set) {

        //return set.getFloatValue("Estimated Net") < 0 && set.getFloatValue("Net Growth %") > 0;
        return false;
    }


    public static boolean invalidNGPerE(SETFIN set) {
        //return set.values.get("NG/E") > 1;
        return false;
    }

    public static boolean invalidEPerG(SETFIN set) {
        return set.getFloatValue("E/G") < 0;
        //return false;
    }

    public static boolean invalidGPerA(SETFIN set) {
        //return set.values.get("G/A") == 1;
        return false;
    }

    private static void print(SETFIN set) {
        System.out.print(set.symbol);
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("Estimated Asset"));
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("Estimated Equity"));
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("Estimated Net"));
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("E/A Growth %"));
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("EG"));
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("Net Growth %"));
        System.out.print("\t");
        System.out.print("\t");
        System.out.print("\t");
        System.out.print(set.values.get("NG"));

        System.out.println();
        System.out.println();
    }

    public String symbol() {
        return symbol;
    }

    public float getFloatValue(String valueName) {
        try {
            return values.get(valueName).floatValue();
        } catch (Exception e) {
        }
        return 0;
    }

    public int getIntValue(String valueName) {
        try {
            return values.get(valueName).intValue();
        } catch (Exception e) {
        }
        return 0;
    }

    public long getLongValue(String valueName) {
        try {
            return values.get(valueName).longValue();
        } catch (Exception e) {
        }
        return 0;
    }

    public YahooHistory yahooHistory() {

        return yahooHistory;
    }

    private float round (float num) {
        return (float) (Math.round(num * 100.0)/100.0);
    }
}
