/*
 * Recruit test for JP Morgan, simple as requested
 * @author Francisco Blanca Ortega
 */
package hellojpmorgan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Command line or a window to show the results
 */
public class HelloJPMorgan extends Application {

    /* Table1. Sample data from the Global Beverage Corporation Exchange */
    private static final TreeMap<String, int[]> STOCKS = new TreeMap<String, int[]>() {
        {
            put("TEA", new int[]{0, 0, -1, 100, 3});
            put("POP", new int[]{0, 8, -1, 100, 13});
            put("ALE", new int[]{0, 23, -1, 60, 30});
            put("GIN", new int[]{1, 8, 2, 100, 25});
            put("JOE", new int[]{0, 13, -1, 250, 41});
        }  // Stock {Type (0=Com/1=Pre), Last Div, Fixed Div, Par Value, Ticker Price}
    };  // Super simple?

    private static final int NUM_TRADES = 3;

    @Override
    public void start(Stage primaryStage) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));
        Text stitle = new Text("Super Simple Stocks");
        stitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        stitle.setFill(Color.DARKBLUE);
        grid.add(stitle, 0, 0, 2, 1);
        Line line = new Line(0, 150, 445, 150);
        line.setStroke(Color.DARKBLUE);
        line.setStrokeWidth(2);
        grid.add(line, 0, 1, 2, 1);
        Text slegend = new Text("Stock [Type, Last Div, Fixed Div, Par Value, Ticker Price]");
        slegend.setFont(Font.font("Courrier", FontWeight.BOLD, 14));
        grid.add(slegend, 0, 2, 2, 1);
        Text ssign = new Text("Francisco Blanca Ortega\npacob@gmx.es");
        ssign.setFont(Font.font("Tahoma", FontWeight.NORMAL, 11));
        ssign.setFill(Color.DARKBLUE);
        grid.add(ssign, 1, 22, 2, 1);

        /* Just showing the Table1 */
        int row = 3;
        for (String stock : STOCKS.keySet()) {
            String key = stock;
            String value = Arrays.toString(STOCKS.get(stock)).replace("-1", "NoValue").
                    replace("[0", "[Common").replace("[1", "[Preferred");
            System.out.println(key + " " + value);
            Text table1 = new Text(key + " " + value);
            table1.setFont(Font.font("Courrier", FontWeight.NORMAL, 14));
            grid.add(table1, 1, row++, 2, 1);
        }

        final Text atarget1 = new Text();
        final Text atarget2 = new Text();
        final Text atarget3 = new Text();
        final Text atarget4 = new Text();
        final Text atarget5 = new Text();
        grid.add(atarget1, 1, 11);
        grid.add(atarget2, 1, 12);
        grid.add(atarget3, 1, 13);
        grid.add(atarget4, 1, 14);
        grid.add(atarget5, 1, 15);

        /* Showing the results and running random generator for fun */
        Button btn = new Button("Record new trades & show the results again");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BASELINE_LEFT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 9);

        Map<String, Float> results1 = dividendYield();
        Map<String, Float> results2 = peRatio();
        Map<String, List> records = tradesRecords();
        Map<String, Integer> results4 = stockPrice(records);
        float result5 = geometricMean(results4);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                atarget1.setText("a.i. Dividend Yield in % = " + results1);
                atarget2.setText("a.ii. P/E Ratio = " + results2);
                Map<String, List> records = tradesRecords();
                atarget3.setText("a.iii. Random Trade Records [0=buy/1=sell, time, quantity, price]\n\n" + myToString(records));
                Map<String, Integer> results4 = stockPrice(records);
                atarget4.setText("a.iv. Stock Price = " + results4);
                float result5 = geometricMean(results4);
                atarget5.setText("b. GBCE All Share Index (geometric mean) = " + result5);
            }
        });

        Scene scene = new Scene(grid, 620, 620);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome JPM recruiter");
        primaryStage.setScene(scene);

        System.out.println("a.i. Dividend Yield (%) = " + results1);
        System.out.println("a.ii. P/E Ratio = " + results2);
        System.out.println("a.iii. Random Trade Records [0=buy/1=sell, time, quantity, price]\n" + myToString(records));
        System.out.println("a.iv. Stock Price = " + results4);
        System.out.println("b. GBCE All Share Index (geometric mean) = " + result5);

        atarget1.setText("a.i. Dividend Yield in % = " + results1);
        atarget2.setText("a.ii. P/E Ratio = " + results2);
        atarget3.setText("a.iii. Random Trade Records [0=buy/1=sell, time, quantity, price]\n\n" + myToString(records));
        atarget4.setText("a.iv. Stock Price = " + results4);
        atarget5.setText("b. GBCE All Share Index (geometric mean) = " + result5);

        primaryStage.show();
    }

    // All in the same file is super simple, but not the best practice
    /**
     * Dividend Yield formula
     *
     * @return a key-float map with the results in %
     */
    public TreeMap<String, Float> dividendYield() {

        TreeMap<String, Float> results = new TreeMap<>();
        Float value;
        for (String stock : STOCKS.keySet()) {
            String key = stock;
            if (STOCKS.get(stock)[4] == 0) {  // Super simple exception handler
                value = Float.NaN;
            } else if (STOCKS.get(stock)[0] == 0) {
                value = (float) STOCKS.get(stock)[1] / STOCKS.get(stock)[4];
            } else {
                value = STOCKS.get(stock)[1] * ((float) STOCKS.get(stock)[2] / 100) * STOCKS.get(stock)[3] / STOCKS.get(stock)[4];
            }
            results.put(key, round(value * 100, 2));
        }
        return results;
    }

    /**
     * Price-earnings Ratio formula
     *
     * @return a key-float map with the results
     */
    public TreeMap<String, Float> peRatio() {

        TreeMap<String, Float> results = new TreeMap<>();
        Float value;
        for (String stock : STOCKS.keySet()) {
            String key = stock;
            if (STOCKS.get(stock)[1] == 0) {  // Super simple exception handler
                value = Float.NaN;
            } else if (STOCKS.get(stock)[0] == 0) {
                value = (float) STOCKS.get(stock)[4] / STOCKS.get(stock)[1];
            } else {
                value = (float) STOCKS.get(stock)[4] / STOCKS.get(stock)[1] * STOCKS.get(stock)[2] * 100;
            }
            results.put(key, round(value, 2));
        }
        return results;
    }

    /**
     * Trades Random Generator
     *
     * @param price integer with the Stock Ticker Price
     * @param numtrades integer with the number of trades to be created
     * @return list of arrays with the random values [b/s, time, quantity, price]
     */
    public static List<Arrays> tradesRandomGenerator(int price, int numtrades) {

        List trades = new ArrayList();
        for (int i = 0; i < numtrades; i++) {
            int[] trade = {
                ThreadLocalRandom.current().nextInt(0, 2), // 0=buy, 1=sell
                (int) Instant.now().getEpochSecond(), // current timestamp for UNIX
                ThreadLocalRandom.current().nextInt(1, 1000), // quantity of shares
                ThreadLocalRandom.current().nextInt(price - 2, price + 3) // smart trade
            };
            trades.add(trade);
        }
        return trades;
    }

    /**
     * Trades Records
     *
     * @return map with all the random values for the stocks
     */
    public static Map<String, List> tradesRecords() {

        Map<String, List> records = new TreeMap<>();
        for (String stock : STOCKS.keySet()) {
            records.put(stock, tradesRandomGenerator(STOCKS.get(stock)[4], NUM_TRADES));
        }
        return records;
    }

    /**
     * Stock Price formula
     *
     * @param records
     * @return a key-float map with the results in %
     */
    public static TreeMap<String, Integer> stockPrice(Map<String, List> records) {

        TreeMap<String, Integer> results = new TreeMap<>();
        for (String record : records.keySet()) {
            String key = record;
            int numerator = 0, denominator = 0;
            for (int j = 0; j < NUM_TRADES; j++) {
                numerator += ((int[]) records.get(record).get(j))[2] * ((int[]) records.get(record).get(j))[3];
            }
            for (int j = 0; j < NUM_TRADES; j++) {
                denominator += ((int[]) records.get(record).get(j))[2];
            }
            results.put(key, numerator / denominator);
        }
        return results;
    }

    /**
     * Geometric Mean formula
     *
     * We are going to suppose no negative prices
     *
     * @param prices map with the prices for all stocks
     * @return double with the result
     */
    public float geometricMean(Map<String, Integer> prices) {

        int product = 1;
        for (String price : prices.keySet()) {
            product = product * prices.get(price);
        }
        return round((float) Math.pow(product, 1.0 / prices.size()), 2);
    }

    /**
     * My very personal print about a Map(key, list of int[])
     *
     * @param records
     * @return a super simple pretty string?
     */
    public String myToString(Map<String, List> records) {

        String string = "", achain;
        for (String record : records.keySet()) {
            achain = "";
            for (int j = 0; j < NUM_TRADES; j++) {
                achain += Arrays.toString((int[]) records.get(record).get(j)) + " ";
            }
            string += record + ": " + achain + "\n";
        }
        return string;
    }

    /**
     * Round to certain number of decimals
     *
     * @param fl float
     * @param nd integer with the number of decimals
     * @return float
     */
    public float round(Float fl, int nd) {

        if (Float.isNaN(fl)) {
            return Float.NaN;
        }
        return BigDecimal.valueOf(fl).setScale(nd, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

}
