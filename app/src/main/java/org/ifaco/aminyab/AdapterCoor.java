package org.ifaco.aminyab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

import org.ifaco.aminyab.L.*;

import static org.ifaco.aminyab.Fun.capitalize;
import static org.ifaco.aminyab.Fun.compileTime;
import static org.ifaco.aminyab.Fun.dist;
import static org.ifaco.aminyab.Fun.dm;
import static org.ifaco.aminyab.Fun.dp;
import static org.ifaco.aminyab.Fun.z;
import static org.ifaco.aminyab.Main.HisCoors;
import static org.ifaco.aminyab.Main.HisMarkers;
import static org.ifaco.aminyab.Main.HisName;
import static org.ifaco.aminyab.Main.MyCoors;
import static org.ifaco.aminyab.Main.MyMarkers;
import static org.ifaco.aminyab.Main.MyName;
import static org.ifaco.aminyab.Main.here;
import static org.ifaco.aminyab.Main.map;
import static org.ifaco.aminyab.Main.showLists;
import static org.ifaco.aminyab.Main.sounds;
import static org.ifaco.aminyab.Main.spOpening3;
import static org.ifaco.aminyab.Main.spOpening4;

class AdapterCoor extends RecyclerView.Adapter<AdapterCoor.MyViewHolder> {
    private final Context c;
    private final ArrayList<Coordinates> data;
    private final int which;

    static final int bgPos = 0, tvNumberPos = 2, tvTimePos = 3, checkboxPos = 4, highlightPos = 5;

    AdapterCoor(Context c, ArrayList<Coordinates> data, int which) {
        this.c = c;
        this.data = data;
        this.which = which;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl;

        MyViewHolder(ConstraintLayout cl) {
            super(cl);
            this.cl = cl;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout cl = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coordinates, parent, false);
        //View bg = cl.getChildAt(bgPos);
        //TextView tvNumber = (TextView) cl.getChildAt(tvNumberPos), tvTime = (TextView) cl.getChildAt(tvTimePos);
        ImageView checkbox = (ImageView) cl.getChildAt(checkboxPos);
        //View highlight = cl.getChildAt(highlightPos);

        // CheckBox
        skullColour(checkbox, false);

        return new MyViewHolder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder h, int i) {
        /*if ((which == 0 && itemCreated1.get(i)) || (which == 1 && itemCreated2.get(i))) {
            h.cl.setScaleX(1f);
            h.cl.setScaleY(1f);
            h.cl.setTranslationX(0f);
        } else {
            int animDur = 92;
            OA(h.cl, "scaleX", 1f, animDur);
            OA(h.cl, "scaleY", 1f, animDur);
            OA(h.cl, "translationX", 0f, animDur);
            switch (which) {
                case 0:
                    itemCreated1.set(i, true);
                    break;
                case 1:
                    itemCreated2.set(i, true);
                    break;
            }
        }*/
        //View bg = h.cl.getChildAt(bgPos);
        TextView tvNumber = (TextView) h.cl.getChildAt(tvNumberPos), tvTime = (TextView) h.cl.getChildAt(tvTimePos);
        final ImageView checkbox = (ImageView) h.cl.getChildAt(checkboxPos);
        View highlight = h.cl.getChildAt(highlightPos);

        // Fields
        tvNumber.setText((i + 1) + "");
        Calendar lm = Calendar.getInstance();
        lm.setTimeInMillis(data.get(i).time);
        tvTime.setText(z(lm.get(Calendar.HOUR_OF_DAY)) + ":" + z(lm.get(Calendar.MINUTE)) + ":" + z(lm.get(Calendar.SECOND)));

        // Click
        highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int i = h.getLayoutPosition();
                switch (which) {
                    case 0:
                        if (MyMarkers.get(i) == null) {
                            LatLng ll = new LatLng(MyCoors.get(i).longitude, MyCoors.get(i).latitude);
                            Marker NEW = map.addMarker(new MarkerOptions()
                                    .position(ll)
                                    .title((i + 1) + ". " + capitalize(MyName) + c.getResources().getString(R.string.wasHereAt))
                                    .snippet(compileTime(MyCoors.get(i).time) + dist(c, here, ll))
                            );
                            NEW.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            MyMarkers.set(i, NEW);
                        } else MyMarkers.get(i).setVisible(!MyMarkers.get(i).isVisible());
                        skullColour(checkbox, MyMarkers.get(i).isVisible());
                        if (MyMarkers.get(i).isVisible()) laugh1();
                        break;
                    case 1:
                        if (HisMarkers.get(i) == null) {
                            LatLng ll = new LatLng(HisCoors.get(i).longitude, HisCoors.get(i).latitude);
                            Marker NEW = map.addMarker(new MarkerOptions()
                                    .position(ll)
                                    .title((i + 1) + ". " + capitalize(HisName) + c.getResources().getString(R.string.wasHereAt))
                                    .snippet(compileTime(HisCoors.get(i).time) + dist(c, here, ll))
                            );
                            NEW.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                            HisMarkers.set(i, NEW);
                        } else HisMarkers.get(i).setVisible(!HisMarkers.get(i).isVisible());
                        skullColour(checkbox, HisMarkers.get(i).isVisible());
                        if (HisMarkers.get(i).isVisible()) laugh1();
                        break;
                }
            }
        });
        highlight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int i = h.getLayoutPosition();
                switch (which) {
                    case 0:
                        if (MyMarkers.get(i) == null) return false;
                        if (!MyMarkers.get(i).isVisible()) return false;
                        showLists(false);
                        map.animateCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(MyCoors.get(i).longitude, MyCoors.get(i).latitude)));
                        MyMarkers.get(i).showInfoWindow();
                        laugh2();
                        break;
                    case 1:
                        if (HisMarkers.get(i) == null) return false;
                        if (!HisMarkers.get(i).isVisible()) return false;
                        showLists(false);
                        map.animateCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(HisCoors.get(i).longitude, HisCoors.get(i).latitude)));
                        HisMarkers.get(i).showInfoWindow();
                        laugh2();// Don't move this!!
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void skullColour(ImageView iv, boolean b) {
        if (b) iv.clearColorFilter();
        else iv.setColorFilter(ContextCompat.getColor(c, R.color.mListsSkullDisabled));
    }

    private void laugh1() {
        try {
            sounds.play(spOpening3, 0.92f, 0.92f, 2, 0, 1f);
        } catch (Exception ignored) {
        }
    }

    private void laugh2() {
        try {
            sounds.play(spOpening4, 1f, 1f, 2, 0, 1f);
        } catch (Exception ignored) {
        }
    }


    private static int oYear = -1, oMonth = -1, oDay = -1, dYear = -1, dMonth = -1, dDay = -1;
    public static float idTextSize = 12f, idMarginLeft = 22f;
    static RecyclerView.ItemDecoration itemDecoration1 = new RecyclerView.ItemDecoration() {
        private int groupSpacing = dp(48);
        private Paint paint = new Paint();

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (MyCoors == null) return;
            int i = parent.getChildAdapterPosition(view);
            if (i == -1 || i >= MyCoors.size()) return;
            int year = -1;
            int month = -1;
            int day = -1;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(MyCoors.get(i).time);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            boolean changed = oYear != year || oMonth != month || oDay != day;
            if (changed || i == 0) {
                oYear = year;
                oMonth = month;
                oDay = day;
                outRect.set(0, groupSpacing, 0, 0);
            }
        }

        @Override
        public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            float marginLeft = idMarginLeft * dm.density, textSize = idTextSize * dm.density;

            if (MyCoors != null) for (int ch = 0; ch < parent.getChildCount(); ch++) {
                View view = parent.getChildAt(ch);
                int i = parent.getChildAdapterPosition(view);
                if (i == -1 || i >= MyCoors.size()) return;
                int year = -1;
                int month = -1;
                int day = -1;
                int week = -1;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(MyCoors.get(i).time);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                week = cal.get(Calendar.DAY_OF_WEEK);
                SolarHijri shamsi = new SolarHijri(cal);
                boolean changed = dYear != year || dMonth != month || dDay != day;
                if (changed || i == 0) {
                    dYear = year;
                    dMonth = month;
                    dDay = day;

                    paint.setTextSize(textSize);
                    paint.setFakeBoldText(true);
                    paint.setColor(ContextCompat.getColor(Main.c, R.color.mListsSpaceText));
                    paint.setAntiAlias(true);
                    canvas.drawText(Main.c.getResources().getStringArray(R.array.daysOfWeek)[week - 1] + ", " +
                            day + " " + Main.c.getResources().getStringArray(R.array.calendar)[month] + // + " " + year
                            ", " + shamsi.D + " " + Main.c.getResources().getStringArray(R.array.shamsiCalendar)[shamsi.M]
                            + " " + shamsi.Y, marginLeft, ((float) view.getTop()) - paint.getTextSize(), paint
                    );
                }
            }
        }
    }, itemDecoration2 = new RecyclerView.ItemDecoration() {
        private int groupSpacing = dp(48);
        private Paint paint = new Paint();

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (HisCoors == null) return;
            int i = parent.getChildAdapterPosition(view);
            if (i == -1 || i >= HisCoors.size()) return;
            int year = -1;
            int month = -1;
            int day = -1;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(HisCoors.get(i).time);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            boolean changed = oYear != year || oMonth != month || oDay != day;
            if (changed || i == 0) {
                oYear = year;
                oMonth = month;
                oDay = day;
                outRect.set(0, groupSpacing, 0, 0);
            }
        }

        @Override
        public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            float marginLeft = idMarginLeft * dm.density, textSize = idTextSize * dm.density;

            if (HisCoors != null) for (int ch = 0; ch < parent.getChildCount(); ch++) {
                View view = parent.getChildAt(ch);
                int i = parent.getChildAdapterPosition(view);
                if (i == -1 || i >= HisCoors.size()) return;
                int year = -1;
                int month = -1;
                int day = -1;
                int week = -1;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(HisCoors.get(i).time);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                week = cal.get(Calendar.DAY_OF_WEEK);
                SolarHijri shamsi = new SolarHijri(cal);
                boolean changed = dYear != year || dMonth != month || dDay != day;
                if (changed || i == 0) {
                    dYear = year;
                    dMonth = month;
                    dDay = day;

                    paint.setTextSize(textSize);
                    paint.setFakeBoldText(true);
                    paint.setColor(ContextCompat.getColor(Main.c, R.color.mListsSpaceText));
                    paint.setAntiAlias(true);
                    canvas.drawText(Main.c.getResources().getStringArray(R.array.daysOfWeek)[week - 1] + ", " +
                            day + " " + Main.c.getResources().getStringArray(R.array.calendar)[month] + // + " " + year
                            ", " + shamsi.D + " " + Main.c.getResources().getStringArray(R.array.shamsiCalendar)[shamsi.M]
                            + " " + shamsi.Y, marginLeft, ((float) view.getTop()) - paint.getTextSize(), paint
                    );
                }
            }
        }
    };
}