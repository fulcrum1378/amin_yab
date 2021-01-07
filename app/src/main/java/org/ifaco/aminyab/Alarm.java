package org.ifaco.aminyab;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;

import java.util.Calendar;

class Alarm {
    static long interval = (60000L * 60L);// * 1L
    static int alarmType = AlarmManager.ELAPSED_REALTIME, alarmStart = -1;
    static long onlineInterval = 60000;

    static void awaken(Context c) {
        //if (isAlarmSet()) return;// MAKES A PROBLEM WHILE CHANGING IN SERVER and MAY HAVE MADE THE PROBLEM WITH RESTARTING!!!
        AlarmManager alarmMgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr == null) return;
        boolean custom = interval != AlarmManager.INTERVAL_FIFTEEN_MINUTES && interval != AlarmManager.INTERVAL_HALF_HOUR &&
                interval != AlarmManager.INTERVAL_HOUR && interval != AlarmManager.INTERVAL_HALF_DAY && interval != AlarmManager.INTERVAL_DAY;
        long start = SystemClock.elapsedRealtime();
        if (alarmStart > -1) {
            int subtraction = alarmStart;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int hourOfDay = (int) (subtraction / (60000 * 60));
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            subtraction -= hourOfDay;
            int minute = (int) (subtraction / 60000);
            calendar.set(Calendar.MINUTE, minute);// For more subtraction, subtract minute from "subtraction"
            start = calendar.getTimeInMillis();
        }
        if (custom) alarmMgr.setRepeating(alarmType, start, interval, sync(c));
        else alarmMgr.setInexactRepeating(alarmType, start, interval, sync(c));
    }//alarmMgr.getNextAlarmClock()

    static PendingIntent sync(Context c) {
        return PendingIntent.getBroadcast(c, 0, new Intent(c, Navigator.class), 0);
    }

    static boolean isAlarmSet(Context c) {
        return PendingIntent.getBroadcast(c, 0, new Intent(c, Navigator.class), PendingIntent.FLAG_NO_CREATE) != null;
    }

    static void syncNow(Context c) {
        try {
            sync(c).send();
        } catch (PendingIntent.CanceledException ignored) {
        }
    }

    static void loopSync(final Context c) {
        new CountDownTimer(onlineInterval, onlineInterval) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                if (Main.loggedIn) {
                    syncNow(c);
                    loopSync(c);
                }
            }
        }.start();
    }
}
