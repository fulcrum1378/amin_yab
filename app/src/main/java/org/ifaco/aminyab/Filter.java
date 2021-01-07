package org.ifaco.aminyab;

import android.content.Context;

import java.util.ArrayList;

class Filter {
    int year, month;
    ArrayList<Integer> items;

    Filter(int year, int month, ArrayList<Integer> items) {
        this.year = year;
        this.month = month;
        this.items = items;
    }

    void put(int item) {
        this.items.add(item);
    }

    String titleInShamsi(Context c) {
        return c.getResources().getStringArray(R.array.shamsiCalendar)[month] + " " + year;
    }

    String title(Context c) {
        return c.getResources().getStringArray(R.array.calendarFull)[month] + " " + year;
    }
}
