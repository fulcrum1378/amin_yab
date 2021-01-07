package org.ifaco.aminyab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Calendar;

class Fun {
    static DisplayMetrics dm = new DisplayMetrics();
    static SharedPreferences sp;

    static boolean isOnline(Context c) {
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    static ValueAnimator VA(View v, String prop, int dur, float val1, float val2) {
        final ValueAnimator mValueAnimator1 = ObjectAnimator.ofFloat(v, prop, val1, val2).setDuration(dur);
        mValueAnimator1.start();
        return mValueAnimator1;
    }

    static ObjectAnimator OA(View v, String prop, float value, int dur) {
        ObjectAnimator mAnim = ObjectAnimator.ofFloat(v, prop, value).setDuration(dur);
        mAnim.start();
        return mAnim;
    }

    static int dp(int px) {
        return (int) (px * dm.density);
    }

    static void exit(AppCompatActivity that) {
        that.moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    static void explode(Context c, View v, int src, long dur, float max, float alpha) {
        ConstraintLayout parent = null;
        try {
            parent = (ConstraintLayout) v.getParent();
        } catch (Exception ignored) {
            return;
        }
        if (parent == null) return;

        View ex = new View(c);
        ConstraintLayout.LayoutParams exLP = new ConstraintLayout.LayoutParams(0, 0);
        exLP.topToTop = v.getId();
        exLP.leftToLeft = v.getId();
        exLP.rightToRight = v.getId();
        exLP.bottomToBottom = v.getId();
        ex.setBackgroundResource(src);
        ex.setTranslationX(v.getTranslationX());
        ex.setTranslationY(v.getTranslationY());
        ex.setScaleX(v.getScaleX());
        ex.setScaleY(v.getScaleY());
        ex.setAlpha(alpha);
        parent.addView(ex, parent.indexOfChild(v), exLP);

        AnimatorSet explode = new AnimatorSet().setDuration(dur);
        ObjectAnimator hide = ObjectAnimator.ofFloat(ex, "alpha", 0f);
        hide.setStartDelay(explode.getDuration() / 4);
        explode.playTogether(
                ObjectAnimator.ofFloat(ex, "scaleX", ex.getScaleX() * max),
                ObjectAnimator.ofFloat(ex, "scaleY", ex.getScaleY() * max),
                hide
        );
        final ConstraintLayout PARENT = parent;
        final View EX = ex;
        explode.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                PARENT.removeView(EX);
            }
        });
        explode.start();
    }

    static String compileTime(long time) {
        Calendar lm = Calendar.getInstance();
        lm.setTimeInMillis(time);
        SolarHijri shamsi = new SolarHijri(lm);
        return z(lm.get(Calendar.YEAR)) + "." + z(lm.get(Calendar.MONTH) + 1) + "." + z(lm.get(Calendar.DAY_OF_MONTH)) +
                " (" + z(shamsi.Y) + "/" + z(shamsi.M + 1) + "/" + z(shamsi.D) + ") - " +
                z(lm.get(Calendar.HOUR_OF_DAY)) + ":" + z(lm.get(Calendar.MINUTE)) + ":" + z(lm.get(Calendar.SECOND));
    }

    static String z(int n) {
        String s = Integer.toString(n);
        if (s.length() == 1) return "0" + s;
        else return s;
    }

    static String capitalize(String s) {
        if (s.length() > 1) return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        else return s;
    }

    static String dist(Context c, Location here, LatLng latlng) {
        if (here == null) return "";
        else {
            float[] results = new float[1];
            Location.distanceBetween(here.getLatitude(), here.getLongitude(), latlng.latitude, latlng.longitude, results);
            return "\n" + new DecimalFormat("#.##").format(results[0]) + " " +
                    c.getResources().getString(R.string.metres);
        }
    }

    static View.OnClickListener doNothing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
}
