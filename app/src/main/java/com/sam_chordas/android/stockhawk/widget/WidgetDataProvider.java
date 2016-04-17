package com.sam_chordas.android.stockhawk.widget;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    ArrayList<ListItemModel> dataForWidget = new ArrayList<>();


    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    public void getDataFromDB() {

        Cursor c = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"}
                , null);

        if (c != null) {

            dataForWidget.clear();
            while (c.moveToNext()) {
                ListItemModel stockDatabse = new ListItemModel(c.getString(c.getColumnIndex("symbol")),
                        c.getString(c.getColumnIndex("bid_price")),
                        c.getString(c.getColumnIndex("percent_change")));

                dataForWidget.add(stockDatabse);
            }

            c.close();
        }

    }


    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        getDataFromDB();
        // Restore the identity - not sure if it's needed since we're going
        // to return right here, but it just *seems* cleaner
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return dataForWidget.size();
    }


    public RemoteViews getViewAt(int position) {

        // set all data here
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        view.setTextViewText(R.id.stock_symbol, dataForWidget.get(position).stockName);
        view.setTextViewText(R.id.bid_price, dataForWidget.get(position).stockBid);
        view.setTextViewText(R.id.change, dataForWidget.get(position).stockPercentageChange);

        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString("symbol",dataForWidget.get(position).stockName);
        fillInIntent.putExtras(extras);
        view.setOnClickFillInIntent(R.id.list_item, fillInIntent);

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
