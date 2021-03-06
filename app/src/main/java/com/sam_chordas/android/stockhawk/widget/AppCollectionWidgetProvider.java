package com.sam_chordas.android.stockhawk.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.DetailStockActivity;

public class AppCollectionWidgetProvider extends AppWidgetProvider {

    public static String CLICK_ACTION = "com.sam_chordas.android.quotelistviewwidget.CLICK";

    private static HandlerThread mWorkerThread;
    private static Handler mWorkerQueue;
    private static StockDataProviderObserver mDataObserver;

    public AppCollectionWidgetProvider() {
        mWorkerThread = new HandlerThread("AppCollectionWidgetProvider-worker");
        mWorkerThread.start();
        mWorkerQueue = new Handler(mWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        final ContentResolver r = context.getContentResolver();
        if (mDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, AppCollectionWidgetProvider.class);
            mDataObserver = new StockDataProviderObserver(mgr, cn, mWorkerQueue);
            r.registerContentObserver(QuoteProvider.Quotes.CONTENT_URI, true, mDataObserver);
        }
    }

    @Override
    public void onDisabled(Context context) {
        final ContentResolver r = context.getContentResolver();
        if (mDataObserver != null) {
            r.unregisterContentObserver(mDataObserver);
        }

    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(CLICK_ACTION)) {
            final String symbol = intent.getStringExtra("symbol");

            Intent i = new Intent(ctx, DetailStockActivity.class);
            i.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("StockSymbol", symbol);
            ctx.startActivity(i);
        }
        super.onReceive(ctx, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {


            // Set up the intent that starts the ListViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListWidgetService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_initial_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.list_view_widget, intent);
            rv.setEmptyView(R.id.list_view_widget, R.id.empty_view);


            final Intent onClickIntent = new Intent(context, AppCollectionWidgetProvider.class);
            onClickIntent.setAction(AppCollectionWidgetProvider.CLICK_ACTION);
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view_widget, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
}

class StockDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

   StockDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }
    @Override
    public void onChange(boolean selfChange) {

        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.list_view_widget);
    }
}