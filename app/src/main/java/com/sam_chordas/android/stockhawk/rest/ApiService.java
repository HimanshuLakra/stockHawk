package com.sam_chordas.android.stockhawk.rest;

import com.sam_chordas.android.stockhawk.data.StockDataModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("v1/public/yql")
    Call<ArrayList<StockDataModel>> getStockData(@Query("q") String QueryDB ,
                                                 @Query("env") String databaseTable ,
                                                 @Query("format") String DataFormat);
}
