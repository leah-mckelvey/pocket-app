package com.interview.leah.pocketactivityapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

/**
 * Created by leah on 4/25/16.
 */
public class PocketBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Set<String> keys = bundle.keySet();

        for (String key : keys) {
            Object value = bundle.get(key);
            Log.d("Leah", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }
        String[] strings = intent.getStringArrayExtra("message");
        for (String string : strings) {
            Log.d("Leah", string);
            if (string.contains("Good luck!")) {
                Intent activityTask = new Intent("com.pocket.doorway.GameActivity");
                activityTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.sendBroadcast(activityTask);
            }
        }
    }
}
