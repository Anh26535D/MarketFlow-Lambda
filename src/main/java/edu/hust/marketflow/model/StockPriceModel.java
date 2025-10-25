package edu.hust.marketflow.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockPriceModel implements Serializable {
    private String symbol;
    private Date date;
    private long negotiatedVolume;
    private long matchedVolume;
    private double negotiatedValue;
    private double matchedValue;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double adjustedPrice;
    private double change;

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

    @Override
    public String toString() {
        return "StockPriceModel@" + hashCode() + "{" +
                "symbol='" + symbol + '\'' +
                ", date=" + date +
                ", adjustedPrice=" + adjustedPrice +
                ", closePrice=" + closePrice +
                ", change=" + change +
                ", matchedVolume=" + matchedVolume +
                ", matchedValue=" + matchedValue +
                ", negotiatedVolume=" + negotiatedVolume +
                ", negotiatedValue=" + negotiatedValue +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                '}';
    }

    public String toJsonFormat() {
        String format = "{\"symbol\":\"%s\",\"date\":\"%s\",\"adjustedPrice\":%.2f,\"closePrice\":%.2f,\"change\":%.2f," +
                "\"matchedVolume\":%d,\"matchedValue\":%.2f,\"negotiatedVolume\":%d,\"negotiatedValue\":%.2f," +
                "\"openPrice\":%.2f,\"highPrice\":%.2f,\"lowPrice\":%.2f}";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format(format,
                getSymbol(),
                df.format(getDate()),
                getAdjustedPrice(),
                getClosePrice(),
                getChange(),
                getMatchedVolume(),
                getMatchedValue(),
                getNegotiatedVolume(),
                getNegotiatedValue(),
                getOpenPrice(),
                getHighPrice(),
                getLowPrice()
        );
    }

}
