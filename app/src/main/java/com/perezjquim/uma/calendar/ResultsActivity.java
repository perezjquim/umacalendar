package com.perezjquim.uma.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.perezjquim.SharedPreferencesHelper;

import java.util.Calendar;
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
        ICalendar ical = Biweekly.parse(s).first();
        List<VEvent> events = ical.getEvents();
        long today = Calendar.getInstance().getTimeInMillis();
        for(VEvent e : events)
        {
            long eventDate = e.getDateStart().getValue().getTime();
            if(today <= eventDate)
            {
                String cadeira = e.getSummary().getValue();
                String[] info = e.getLocation().getValue().split(" -> ");
                String sala = info[0];
                info = info[1].split("[\\(\\)]");
                String tipo = info[0];
                String prof = info[1];
                String date = e.getDateStart().getValue().toString().substring(0, 10);
                String start = e.getDateStart().getValue().toString().substring(11,16);
                String end = e.getDateEnd().getValue().toString().substring(11,16);
                EventView out = new EventView(this,cadeira,tipo,prof,sala,date,start,end);
                out.setPadding(10,10,10,10);
                runOnUiThread(()->
                        lay.addView(out));
            }
        }

    }
}
