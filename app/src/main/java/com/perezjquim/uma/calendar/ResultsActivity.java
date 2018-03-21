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

import static com.perezjquim.UIHelper.hideProgressDialog;
import static com.perezjquim.UIHelper.showProgressDialog;

public class ResultsActivity extends AppCompatActivity
{
    private LinearLayout lay;
    private SharedPreferencesHelper prefs;
    private static final String PROGRESS_MESSAGE = "Preparando..";
    private static final int PADDING = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        new Thread(()->
        {
            runOnUiThread(()->
                    showProgressDialog(this,PROGRESS_MESSAGE));

            prefs = new SharedPreferencesHelper(this);
            lay = findViewById(R.id.lay);

            String s = prefs.getString("misc", "events");
            ICalendar ical = Biweekly.parse(s).first();
            List<VEvent> events = ical.getEvents();
            long today = Calendar.getInstance().getTimeInMillis();

            runOnUiThread(()->
                   hideProgressDialog());

            for (VEvent e : events)
            {
                long eventDate = e.getDateStart().getValue().getTime();
                if (today <= eventDate)
                {
                    // "Engenharia de Requisitos"
                    String cadeira = e.getSummary().getValue();

                    // "Sala de aula nº 5 -> TP2 (Sandra Mendonça)"
                    // { "Sala de aula nº 5" , "TP2 (Sandra Mendonça)" }
                    String[] info = e.getLocation().getValue().split(" -> ");

                    // "Sala de aula nº 5"
                    String sala = info[0];

                    // { "TP2" , "Sandra Mendonça" }
                    info = info[1].split("[()]");

                    // "TP2"
                    String tipo = info[0];

                    // "Sandra Mendonça"
                    String prof = info[1];

                    String date = e.getDateStart().getValue().toString().substring(0, 10);
                    String start = e.getDateStart().getValue().toString().substring(11, 16);
                    String end = e.getDateEnd().getValue().toString().substring(11, 16);

                    EventView out = new EventView(this, cadeira, tipo, prof, sala, date, start, end);
                    out.setPadding(PADDING, PADDING, PADDING, PADDING);

                    runOnUiThread(() ->
                            lay.addView(out));
                }
            }
        }).start();
    }
}
