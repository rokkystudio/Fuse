package com.rokkystudio.fuse;

import android.content.Context;
import android.os.Vibrator;

public class Tools
{
    public static void vibrate(Context context, int time) {
        if (context == null) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) vibrator.vibrate(time);
    }
}
