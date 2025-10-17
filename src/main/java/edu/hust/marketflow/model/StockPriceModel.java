package edu.hust.marketflow.model;

import java.util.Date;

public class StockPriceModel {
    private String symbol;
    private Date date;
    private double adjustedPrice;
    private double closePrice;
    private double change;
    private long matchedVolume;
    private double matchedValue;
    private long negotiatedVolume;
    private double negotiatedValue;
    private double openPrice;
    private double highPrice;
    private double lowPrice;

    public StockPriceModel(String symbol, Date date, double adjustedPrice, double closePrice, double change,
                           long matchedVolume, double matchedValue, long negotiatedVolume, double negotiatedValue,
                           double openPrice, double highPrice, double lowPrice) {
        this.symbol = symbol;
        this.date = date;
        this.adjustedPrice = adjustedPrice;
        this.closePrice = closePrice;
        this.change = change;
        this.matchedVolume = matchedVolume;
        this.matchedValue = matchedValue;
        this.negotiatedVolume = negotiatedVolume;
        this.negotiatedValue = negotiatedValue;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
    }

    public String getSymbol() { return symbol; }
    public Date getDate() { return date; }
    public double getAdjustedPrice() { return adjustedPrice; }
    public double getClosePrice() { return closePrice; }
    public double getChange() { return change; }
    public long getMatchedVolume() { return matchedVolume; }
    public double getMatchedValue() { return matchedValue; }
    public long getNegotiatedVolume() { return negotiatedVolume; }
    public double getNegotiatedValue() { return negotiatedValue; }
    public double getOpenPrice() { return openPrice; }
    public double getHighPrice() { return highPrice; }
    public double getLowPrice() { return lowPrice; }
}
