package org.ifaco.aminyab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Restarter extends BroadcastReceiver {
    SharedPreferences sp;

    @Override
    public void onReceive(Context c, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            sp = PreferenceManager.getDefaultSharedPreferences(c);
            String[] coordinates = null;
            try {
                coordinates = sp.getString(Main.gotCords, Main.defCords).split(Main.splitCoorsBy);
            } catch (Exception ignored) {
            }
            if (coordinates != null) {
                try {
                    Alarm.alarmType = Integer.parseInt(coordinates[5]);
                } catch (Exception ignored) {
                }
                try {
                    Alarm.alarmStart = Integer.parseInt(coordinates[6]);
                } catch (Exception ignored) {
                }
                try {
                    Alarm.interval = Long.parseLong(coordinates[7]);
                } catch (Exception ignored) {
                }
            }
            Alarm.awaken(c);
        }
    }
}
