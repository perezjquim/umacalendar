package com.perezjquim.uma.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perezjquim.SharedPreferencesHelper;

import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class ResultsActivity extends AppCompatActivity {

    private LinearLayout lay;
    private SharedPreferencesHelper prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        prefs = new SharedPreferencesHelper(this);
        lay = findViewById(R.id.lay);
        String s = prefs.getString("misc","events");
        System.out.println(s);
        ICalendar ical = Biweekly.parse(s).first();
        List<VEvent> events = ical.getEvents();
        for(VEvent e : events)
        {
            String label = e.getSummary().getValue();
            String info = e.getLocation().getValue();
            String date = e.getDateStart().getValue().getRawComponents().toDate().toString();
            EventView out = new EventView(this,label,info,date);
            runOnUiThread(()->
                    lay.addView(out));
        }

    }
}
