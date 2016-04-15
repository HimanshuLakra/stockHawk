package com.sam_chordas.android.stockhawk.data;


import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class StockDataModel {

    private String Date;
    private String Close;
    private String Open;

    @ParcelConstructor
    public StockDataModel(String Date, String Close, String Open) {
        this.Date = Date;
        this.Close = Close;
        this.Open = Open;
    }

    public String getDate() {
        return this.Date;
    }

    public String getCloseStockValue() {
        return this.Close;
    }

    public String getOpenStockValue() {
        return this.Open;
    }

}
