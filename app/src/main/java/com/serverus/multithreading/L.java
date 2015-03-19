package com.serverus.multithreading;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by alvinvaldez on 3/18/15.
 */
public class L {

    public static void m(String message){
        Log.d("alvin", message);
    }

    public static void s(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
