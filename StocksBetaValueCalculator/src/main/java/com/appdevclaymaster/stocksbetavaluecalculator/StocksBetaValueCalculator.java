package com.appdevclaymaster.stocksbetavaluecalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Suraj Prajapati (Claymaster)
 */
public class StocksBetaValueCalculator {

    //Set the no of days data to be used to calculate beta value
    static int periodForBetaCalculation = 45;
    static final Logger logger = LogManager.getLogger(StocksBetaValueCalculator.class);

    public static void main(String[] args) {

        //Load Market Index data 
        String stockOhlcFilePath = "src\\main\\resources\\daily_ohlc_data\\NIFTY.csv";
        List<OHLC> marketData = readCSV(stockOhlcFilePath);
        logger.info("No. of days in list is " + marketData.size());

        //Load data for each stock
        String ohlcFolder = "src\\main\\resources\\daily_ohlc_data\\";

        System.out.printf("%-15s %-10s%n",
                "Stock Name", "Beta Value");
        System.out.println("----------------------------");
        File folder = new File(ohlcFolder);
        //String[] listofFiles = folder.list();
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                }
                //System.out.println("File " + ohlcFolder + listOfFiles[i].getName());
                String stockFilePath = ohlcFolder + listOfFiles[i].getName();
                List<OHLC> stockData = readCSV(stockFilePath);

                double[] stockReturns = calculateReturns(stockData);
                double[] marketReturns = calculateReturns(marketData);

                double beta = calculateBeta(stockReturns, marketReturns);
                //System.out.println("Beta value for " + listOfFiles[i].getName() + " is: " + beta);
                System.out.printf("%-15s: %.3f%n", listOfFiles[i].getName(), beta);
            }
        }

    }

    public static List<OHLC> readCSV(String filePath) {
        List<OHLC> ohlcList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Date date = sdf.parse(values[0].trim());
                double open = Double.parseDouble(values[1].trim());
                double high = Double.parseDouble(values[2].trim());
                double low = Double.parseDouble(values[3].trim());
                double close = Double.parseDouble(values[4].trim());
                double volume = Double.parseDouble(values[5].trim());
                ohlcList.add(new OHLC(date, open, high, low, close, volume));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return ohlcList.subList(ohlcList.size() - periodForBetaCalculation, ohlcList.size() - 1);
    }

    public static double[] calculateReturns(List<OHLC> ohlcList) {
        double[] returns = new double[ohlcList.size() - 1];
        for (int i = 1; i < ohlcList.size(); i++) {
            returns[i - 1] = (ohlcList.get(i).close - ohlcList.get(i - 1).close) / ohlcList.get(i - 1).close;
        }
        return returns;
    }

    public static double calculateBeta(double[] stockReturns, double[] marketReturns) {
        double stockMean = calculateMean(stockReturns);
        double marketMean = calculateMean(marketReturns);
        double covariance = calculateCovariance(stockReturns, marketReturns, stockMean, marketMean);
        double variance = calculateVariance(marketReturns, marketMean);
        return covariance / variance;
    }

    private static double calculateMean(double[] returns) {
        double sum = 0.0;
        for (double r : returns) {
            sum += r;
        }
        return sum / returns.length;
    }

    private static double calculateCovariance(double[] stockReturns, double[] marketReturns, double stockMean, double marketMean) {
        double sum = 0.0;
        for (int i = 0; i < stockReturns.length; i++) {
            sum += (stockReturns[i] - stockMean) * (marketReturns[i] - marketMean);
        }
        return sum / (stockReturns.length - 1);
    }

    private static double calculateVariance(double[] returns, double mean) {
        double sum = 0.0;
        for (double r : returns) {
            sum += Math.pow(r - mean, 2);
        }
        return sum / (returns.length - 1);
    }
}
