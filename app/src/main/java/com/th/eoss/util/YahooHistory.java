package com.th.eoss.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Stack;

/**
 * Created by wisarut on 28/9/2559.
 */

public class YahooHistory {
    public static final String YAHOO_URL = "http://ichart.finance.yahoo.com/table.csv?&a=01&b=19&c=2010&s=";
    public String symbol;

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static class HiLo {
        public String date;
        public float open;
        public float high;
        public float low;
        public float close;
        public long volume;

        public HiLo(String date, float open, float high, float low, float close, long volume) {
            this.date = date;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }

        public String toString() {
            return String.format("%s %.2f %.2f %.2f %.2f %d", date, open, high, low, close, volume);
        }
    }

    public HiLo [] hilos;
    public float [] closes;

    public YahooHistory (String symbol) {
        this.symbol = symbol;
        BufferedReader br = null;
        try {
            symbol = symbol.replace("&","&amp;");
            symbol = symbol.replace(" ","%20");
            symbol += ".BK";
            br = new BufferedReader(new InputStreamReader(new URL(YAHOO_URL+symbol).openStream()));
            String line;
            Stack<String> stack = new Stack<String> ();
            while ((line=br.readLine())!=null) {
                stack.push(line);
            }

            hilos = new HiLo[stack.size()-1];

            String [] tokens;
            for ( int i=0; i<hilos.length; i++ ) {
                line = stack.pop();
                tokens = line.split(",");
                hilos [i] = new HiLo (tokens[0], Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]), Long.parseLong(tokens[5]));
            }

            closes = new float[hilos.length];

            for ( int i=0; i<closes.length; i++ ) {
                closes[i] = hilos [i].close;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br!=null) {
               try { br.close(); } catch (Exception e) { }
            }
        }
    }

    public void updateLast(HiLo hilo) {

        if (hilos!=null && hilo.date !=null && hilo.date.equals(hilos[hilos.length-1].date)) {
            //Update
            hilos[hilos.length-1] = hilo;

        } else {
            //Append
            int newLength = hilos==null?1:hilos.length + 1;
            HiLo [] oldHilos = hilos;
            float [] oldCloses = closes;

            hilos = new HiLo[newLength];
            closes = new float[newLength];

            if (oldHilos!=null) {
                for (int i=0; i<oldHilos.length; i++) {
                    hilos[i] = oldHilos[i];
                    closes[i] = oldCloses[i];
                }
            }

            hilos[newLength-1] = hilo;
            closes[newLength-1] = hilo.close;
        }

    }
}
