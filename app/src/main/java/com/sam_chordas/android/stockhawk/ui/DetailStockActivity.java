package com.sam_chordas.android.stockhawk.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.ItemTypeAdapterFactory;
import com.sam_chordas.android.stockhawk.data.StockDataModel;
import com.sam_chordas.android.stockhawk.rest.ApiService;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailStockActivity extends AppCompatActivity {

    String stockSymbol;
    ArrayList<StockDataModel> recievedList;

    @Bind(R.id.linechart)
    LineChart lineChartView;
    @Bind(R.id.toolbar_graph)
    Toolbar toolbar;

    String queryForDB;
    String queryForTable = "store://datatables.org/alltableswithkeys";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_graph);
        ButterKnife.bind(this);

        stockSymbol = getIntent().getStringExtra("StockSymbol");

        toolbar.setTitle(stockSymbol);
        toolbar.setNavigationIcon(R.drawable.md_nav_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toMainActivity = new Intent(DetailStockActivity.this,MyStocksActivity.class);
                startActivity(toMainActivity);
                finish();
            }
        });

        queryForDB = "select * from yahoo.finance.historicaldata where symbol = \"" + stockSymbol +
                "\" and startDate = \"2016-04-01\" and " +
                "endDate = \"2016-04-08\"";

        if (savedInstanceState == null) {
            recievedList = new ArrayList<>();
            RequestStockData(queryForDB, queryForTable);
        } else {
            recievedList = Parcels.unwrap(savedInstanceState.getParcelable("stockDataSet"));
            if (recievedList != null)
            setDataForLineChart(recievedList);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("stockDataSet", Parcels.wrap(recievedList));
        super.onSaveInstanceState(outState);
    }

    public void setDataForLineChart(ArrayList<StockDataModel> recievedDataList) {

        int size = recievedDataList.size();
        ArrayList<Entry> datavalues = new ArrayList<>();
        ArrayList<Entry> datavaluesOpen = new ArrayList<>();

        String[] labels = new String[size];

        for (int i = 0; i < size; i++) {
            datavalues.add(new Entry(Float.parseFloat(recievedDataList.get(i).getCloseStockValue()), i));
            datavaluesOpen.add(new Entry(Float.parseFloat(recievedDataList.get(i).getOpenStockValue()), i));
            labels[size - i - 1] = recievedDataList.get(i).getDate();
        }

        LineDataSet setComp1 = new LineDataSet(datavalues, "Stock Closed At");
        setComp1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineDataSet setCompOpen = new LineDataSet(datavaluesOpen, "Stock Open At");
        setCompOpen.setAxisDependency(YAxis.AxisDependency.RIGHT);
        setCompOpen.setColor(R.color.fab_color);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setCompOpen);

        ArrayList<String> xVals = new ArrayList<String>(Arrays.asList(labels));

        LineData data = new LineData(xVals, dataSets);
        lineChartView.setData(data);
        lineChartView.invalidate();
    }

    public void RequestStockData(String queryDB, String queryTable) {

        //ApiService(Rest api call function) interface object
        ApiService apiService;

        //Gson object for "results" JSONArray
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ItemTypeAdapterFactory()).create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //Retrofit object as per 2.0 version library
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.InitialUrl)).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);

        //Asynchronuos call to REST moviedb.org API
        Call<ArrayList<StockDataModel>> call = apiService.getStockData(queryDB, queryTable, "json");
        call.enqueue(new Callback<ArrayList<StockDataModel>>() {
            @Override
            public void onResponse(Call<ArrayList<StockDataModel>> call, Response<ArrayList<StockDataModel>> response) {

                if (response.isSuccessful()) {
                    //assign recieved arraylist to dataset
                    recievedList = response.body();
                    if (!recievedList.isEmpty()) {
                        setDataForLineChart(recievedList);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<StockDataModel>> call, Throwable t) {
                Toast.makeText(DetailStockActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
