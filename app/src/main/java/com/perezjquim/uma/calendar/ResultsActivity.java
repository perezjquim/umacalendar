package com.perezjquim.uma.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.perezjquim.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import static com.perezjquim.UIHelper.closeProgressDialog;
import static com.perezjquim.UIHelper.openProgressDialog;

public class ResultsActivity extends AppCompatActivity
{
    private LinearLayout lay;
    private SharedPreferencesHelper prefs;
    private static final String PROGRESS_MESSAGE = "Preparando..";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent i = getIntent();
        boolean isAulas = i.getBooleanExtra("isAulas",false);

        new Thread(()->
        {
            openProgressDialog(this,PROGRESS_MESSAGE);

            prefs = new SharedPreferencesHelper(this);
            lay = findViewById(R.id.lay);

            String s;
            if(isAulas) s = prefs.getString(
                    Prefs.FILE_MISC+"",
                    Prefs.KEY_AULAS+"");
            else s = prefs.getString(
                    Prefs.FILE_MISC+"",
                    Prefs.KEY_AVALIACOES+"");

            ICalendar ical = Biweekly.parse(s).first();
            ArrayList<VEvent> events = new ArrayList<>(ical.getEvents());
            Date today = Calendar.getInstance().getTime();

            for (VEvent e : events)
            {
                Date eventDate = e.getDateStart().getValue();
                if (today.before(eventDate))
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

                    runOnUiThread(() ->
                            lay.addView(out));
                }
            }

            closeProgressDialog();

        }).start();
    }
}
