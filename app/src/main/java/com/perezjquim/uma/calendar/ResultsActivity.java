package com.perezjquim.uma.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.perezjquim.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd MMM yyyy");

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

            for (VEvent e : events)
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

                String date = dateFormatter.format(e.getDateStart().getValue());
                String start = timeFormatter.format(e.getDateStart().getValue());
                String end = timeFormatter.format(e.getDateEnd().getValue());

                EventView out = new EventView(this, cadeira, tipo, prof, sala, date, start, end);

                runOnUiThread(() -> lay.addView(out));
            }

            closeProgressDialog(this);

        }).start();
    }
}
