package com.sam_chordas.android.stockhawk.widget;

public class ListItemModel {


    public String stockName;
    public String stockBid;
    public String stockPercentageChange;

    public ListItemModel(String stockName, String stockBid, String stockPercentageChange) {
        this.stockName = stockName;
        this.stockBid = stockBid;
        this.stockPercentageChange = stockPercentageChange;
    }
}
