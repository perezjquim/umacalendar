package com.perezjquim.uma.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.perezjquim.SharedPreferencesHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import static com.perezjquim.UIHelper.closeProgressDialog;
import static com.perezjquim.UIHelper.openProgressDialog;
import static com.perezjquim.UIHelper.toast;

public class MainActivity extends AppCompatActivity
{
    private TextView field;
    private SharedPreferencesHelper prefs;
    private int lastNr;
    private Thread tRequestCalendar;
    private static final String PROGRESS_MESSAGE = "Obtendo calendário..";
    private static final String ERROR_MESSAGE = "Número mecanográfico inválido ou falta de conectividade!";
    private static final String CALENDAR_URL = "http://calendar.uma.pt/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        field = findViewById(R.id.field);
        prefs = new SharedPreferencesHelper(this);
        lastNr = prefs.getInt(
                Prefs.FILE_MISC+"",
                Prefs.KEY_LASTNR + "");
        if(lastNr != -1) field.setText(lastNr+"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        try
        {
            requestCalendar();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestCalendar() throws IOException
    {
        tRequestCalendar = new Thread(()->
        {
            try
            {
                runOnUiThread(()->
                        openProgressDialog(this, PROGRESS_MESSAGE));

                if (field.getText().length() == 0) throw new IOException();

                InputStream is = new URL(CALENDAR_URL + field.getText()).openStream();
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String events = s.hasNext() ? s.next() : "";

                Date today = Calendar.getInstance().getTime();
                ICalendar ical = Biweekly.parse(events).first();

                ArrayList<VEvent> eventsList = new ArrayList<>(ical.getEvents());
                Collections.sort(eventsList,(a, b) -> a.getDateStart().getValue().compareTo(b.getDateStart().getValue()));

                ICalendar aulas = new ICalendar();
                ICalendar avaliacoes = new ICalendar();

                for(VEvent e : eventsList)
                {
                    Date eventDate = e.getDateStart().getValue();
                    if(!today.before(eventDate)) continue;

                    if(e.getSummary().getValue().contains("Avaliação")) avaliacoes.addEvent(e);
                    else aulas.addEvent(e);
                }

                String strAulas = Biweekly.write(aulas).go();
                String strAvaliacoes = Biweekly.write(avaliacoes).go();

                lastNr = Integer.parseInt(field.getText()+"");
                prefs.setString(
                        Prefs.FILE_MISC+"",
                        Prefs.KEY_AULAS+"",
                        strAulas);
                prefs.setString(
                        Prefs.FILE_MISC+"",
                        Prefs.KEY_AVALIACOES+"",
                        strAvaliacoes);
                prefs.setInt(
                        Prefs.FILE_MISC+"",
                        Prefs.KEY_LASTNR+"",
                        lastNr);
            }
            catch (IOException e)
            {
                toast(this,ERROR_MESSAGE);
                e.printStackTrace();
            }
            finally
            {
                runOnUiThread(()->
                        closeProgressDialog());
            }
        });
        tRequestCalendar.start();
    }

    public void listAulas(View v)
    { loadPreviousCalendar(true);}

    public void listAvaliacoes(View v)
    { loadPreviousCalendar(false);}

    private void loadPreviousCalendar(boolean isAulas)
    {
        new Thread(()->
        {
            try
            {
                if (lastNr == -1 || lastNr != Integer.parseInt(field.getText()+""))
                {
                    requestCalendar();
                    tRequestCalendar.join();
                }

                Intent i = new Intent(this, ResultsActivity.class);
                i.putExtra("isAulas", isAulas);
                startActivity(i);
            }
            catch(IOException | InterruptedException e)
            {
                toast(this,ERROR_MESSAGE);
                e.printStackTrace();
            }
        }).start();
    }
}
