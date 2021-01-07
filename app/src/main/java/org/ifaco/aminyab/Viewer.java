package org.ifaco.aminyab;

import android.content.Context;
import android.util.JsonReader;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.ifaco.aminyab.Main.hViewer;

class Viewer extends Thread {
    private Context c;
    private String[] tables;
    private boolean[] downloading = new boolean[2];
    private boolean downloadingLists = false;

    Viewer(Context c, String... tables) {
        this.c = c;
        this.tables = tables;
    }

    @Override
    public void run() {
        if (downloadingLists) return;
        downloadingLists = true;
        for (int w = 0; w < tables.length; w++) view(c, w, tables[w]);
    }

    void view(final Context c, final int which, final String table) {
        downloading[which] = true;
        RequestQueue view = Volley.newRequestQueue(c);
        StringRequest srt = new StringRequest(Request.Method.POST, Main.manager + "?action=view",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        ArrayList<L.Coordinates> list = null;
                        try {
                            list = readCoordinates(res);
                        } catch (IOException ignored) {
                        }
                        hViewer.obtainMessage(which, list).sendToTarget();
                        downloading[which] = false;
                        checkCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hViewer.obtainMessage(which, null).sendToTarget();
                downloading[which] = false;
                checkCompleted();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("code", Fun.sp.getString(Main.gotPass, ""));
                params.put("user", table);
                return params;
            }
        };
        srt.setTag("view" + which);
        srt.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        view.add(srt);
    }

    private void checkCompleted() {
        if (!downloading[0] && !downloading[1]) downloadingLists = false;
    }

    private ArrayList<L.Coordinates> readCoordinates(String res) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(
                new ByteArrayInputStream(res.getBytes(Charset.forName("UTF-8")))));
        try {
            return readCoorArray(reader);
        } finally {
            reader.close();
        }
    }

    private static ArrayList<L.Coordinates> readCoorArray(JsonReader reader) throws IOException {
        ArrayList coors = new ArrayList<L.Coordinates>();
        reader.beginArray();
        while (reader.hasNext()) coors.add(readCoor(reader));
        reader.endArray();
        return coors;
    }

    private static L.Coordinates readCoor(JsonReader reader) throws IOException {
        long id = -1, time = 0;
        double longitude = 0.0, latitude = 0.0;

        reader.beginObject();
        while (reader.hasNext()) {
            String param = reader.nextName();
            switch (param) {
                case "id":
                    id = reader.nextLong();
                    break;
                case "longitude":
                    longitude = reader.nextDouble();
                    break;
                case "latitude":
                    latitude = reader.nextDouble();
                    break;
                case "time":
                    time = reader.nextLong();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new L.Coordinates(longitude, latitude, time).set(L.ID, id);
    }
}
