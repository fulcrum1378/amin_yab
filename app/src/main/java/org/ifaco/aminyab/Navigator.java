package org.ifaco.aminyab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import org.ifaco.aminyab.L.*;

import androidx.room.Room;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Navigator extends BroadcastReceiver {
    Location here = null;
    FusedLocationProviderClient flpc;
    RequestQueue update;
    SharedPreferences sp;
    ArrayList<Coordinates> all = null;

    @Override
    public void onReceive(final Context c, Intent intent) {
        sp = PreferenceManager.getDefaultSharedPreferences(c);

        flpc = LocationServices.getFusedLocationProviderClient(c);
        flpc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                here = location;
                if (here != null) new CoManager(c, 0).start();
            }
        });
    }


    class CoManager extends Thread {
        Context c;
        int type;
        CoordManager cos;

        CoManager(Context c, int type) {
            this.c = c;
            this.type = type;
        }

        @Override
        public void run() {
            cos = Room.databaseBuilder(c, CoordManager.class, Main.database).build();
            switch (type) {
                case 0:// INSERT
                    cos.dao().insert(new Coordinates(here.getLatitude(), here.getLongitude(), here.getTime()));
                    check();
                    break;
                case 1:// DELETE
                    cos.dao().delete(all.get(0));
                    all.remove(0);
                    check();
                    break;
                case 2:// ERROR
                    check();
                    break;
            }
            cos.close();
        }

        private void check() {
            all = new ArrayList<>(cos.dao().getAll());
            if (!Fun.isOnline(c) || all == null) return;
            if (all.isEmpty()) return;

            Collections.sort(all, new SortCoordinates());
            update = Volley.newRequestQueue(c);
            StringRequest srt = new StringRequest(Request.Method.POST, Main.manager + "?action=sync",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String res) {// MAIN THREAD
                            switch (res) {
                                case "done":
                                    new CoManager(c, 1).start();

                                    SharedPreferences.Editor ed = sp.edit();
                                    ed.putLong(Main.exLastSynced, here.getTime());
                                    ed.apply();
                                    if (Main.navHandler != null)
                                        Main.navHandler.obtainMessage(0).sendToTarget();
                                    break;
                                case "repeated":
                                    new CoManager(c, 1).start();
                                    break;
                                default:
                                    //new CoManager(2).start();
                                    break;
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {// MAIN THREAD
                    //new CoManager(2).start();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("code", sp.getString(Main.gotPass, ""));
                    params.put("longitude", Double.toString(all.get(0).longitude));
                    params.put("latitude", Double.toString(all.get(0).latitude));
                    params.put("time", Long.toString(all.get(0).time));
                    return params;
                }
            };
            srt.setTag("sync");
            srt.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            update.add(srt);
        }
    }

    class SortCoordinates implements Comparator<Coordinates> {
        @Override
        public int compare(Coordinates a, Coordinates b) {
            return (int) (a.time - b.time);
        }
    }
}
