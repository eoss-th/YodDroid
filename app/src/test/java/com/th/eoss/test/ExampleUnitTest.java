package com.th.eoss.test;

import com.th.eoss.util.LinearRegression;
import com.th.eoss.util.SETIndex;
import com.th.eoss.util.SETQuote;
import com.th.eoss.util.SETSummary;
import com.th.eoss.util.YahooHistory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        Map<String, Map<String, List<String>>> map = new SETIndex().map();

        for (String i:map.keySet()) {
            Map<String, List<String>> dict = map.get(i);

            for (String s:dict.keySet()) {
                List<String> list = dict.get(s);
                for (String symbol:list) {
                    System.out.println(new SETSummary(symbol));
                }
            }
        }

    }

    @Test
    public void yahoo_isCorrect() throws Exception {
        YahooHistory yahoo = new YahooHistory("CPF");
        for (int i=0; i<yahoo.hilos.length; i++) {
            System.out.println(yahoo.hilos[i]);
        }
    }

    @Test
    public void quote_isCorrect() throws Exception {
        Map<String, Map<String, List<String>>> map = new SETIndex().map();

        for (String i:map.keySet()) {
            Map<String, List<String>> dict = map.get(i);

            for (String s:dict.keySet()) {
                List<String> list = dict.get(s);
                for (String symbol:list) {
                    System.out.println(new SETQuote(symbol));
                }
            }
        }

    }

    @Test
    public void regressionTest() throws Exception {
//        LinearRegression l = new LinearRegression(new double[] {0, 1, 2, 3}, new double[] {60.88, 64.1, 62.65, 70.22});
        LinearRegression l = new LinearRegression(new double[] {0, 1, 2, 3}, new double[] {0.84, 0.88, 0.83, 0.65});
        System.out.println(l.slope());
        System.out.println(l.intercept());
    }

    @Test
    public void asOfDateTest() throws Exception {
        Document doc = Jsoup.connect(SETSummary.URL+"A").get();
        Element dateElement = doc.select("td[style='background-color:#000000; color:#FFFFFF; font-weight:bold;']").first();
        String currentAsOfDate = dateElement.text().replace("\\h", "");
        System.out.println(currentAsOfDate);
    }
}