package edu.hust.marketflow.model;

import java.util.Date;

public record StockPriceRecord(
        String symbol,
        Date date,
        double adjustedPrice,
        double closePrice,
        double change,
        long matchedVolume,
        double matchedValue,
        long negotiatedVolume,
        double negotiatedValue,
        double openPrice,
        double highPrice,
        double lowPrice
) {}
