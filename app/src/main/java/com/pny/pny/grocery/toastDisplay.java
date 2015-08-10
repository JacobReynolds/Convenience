package com.pny.pny.grocery;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by pny on 8/9/15.
 */
public class toastDisplay {

    public void display(String text, Context context) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
