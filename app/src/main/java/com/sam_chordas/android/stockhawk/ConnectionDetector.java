package com.sam_chordas.android.stockhawk;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector  {

    public static boolean isAvailiable(Context ctx) {

        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();

        return  i!=null && i.isConnectedOrConnecting();

    }
}
