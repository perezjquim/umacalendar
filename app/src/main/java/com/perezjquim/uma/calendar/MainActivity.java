package com.perezjquim.uma.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.perezjquim.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

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
    private static final String PROGRESS_MESSAGE = "Baixando calendário..";
    private static final String LOADING_MESSAGE = "Carregando calendário..";
    private static final String ERROR_NETWORK_MESSAGE = "Falha na conectividade!";
    private static final String ERROR_NMEC_MESSAGE = "Número mecanográfico inválido!";
    private static final String CALENDAR_URL = "http://calendar.uma.pt/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);
        field = findViewById(R.id.field);
        prefs = new SharedPreferencesHelper(this);
        lastNr = prefs.getInt(
                Prefs.FILE_MISC+"",
                Prefs.KEY_LASTNR + "");
        if(lastNr != -1) field.setText(lastNr+"");

        AndroidNetworking.initialize(getApplicationContext());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        closeProgressDialog(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        closeProgressDialog(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        closeProgressDialog(this);
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
        switch(item.getItemId())
        {
            case R.id.refresh:
                requestCalendar();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void listAulas(View v)
    { loadPreviousCalendar(true);}

    public void listAvaliacoes(View v)
    { loadPreviousCalendar(false);}

    private void showResults(boolean isAulas)
    {
        openProgressDialog(this, LOADING_MESSAGE);

        String s;
        if(isAulas) s = prefs.getString(
                Prefs.FILE_MISC+"",
                Prefs.KEY_AULAS+"");
        else s = prefs.getString(
                Prefs.FILE_MISC+"",
                Prefs.KEY_AVALIACOES+"");

        ICalendar ical = Biweekly.parse(s).first();
        filterCalendar(ical, isAulas);

        int size = ical.getEvents().size();

        if(size < 1)
        {
            toast(this,((isAulas) ? "Aulas" : "Avaliações") + " inexistentes!" );
        }
        else
        {
            Intent i = new Intent(this, ResultsActivity.class);
            i.putExtra("isAulas", isAulas);
            startActivity(i);
        }

        closeProgressDialog(this);
    }

    private void requestCalendar(StringRequestListener listener)
    {
        if (field.getText().length() == 0)
        {
            toast(this, ERROR_NMEC_MESSAGE);
        }
        else
        {
            openProgressDialog(this,PROGRESS_MESSAGE);

            AndroidNetworking.get(CALENDAR_URL + field.getText())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(listener);
        }
    }

    private void requestCalendar()
    {
        MainActivity self = this;

        requestCalendar(new StringRequestListener()
        {
            @Override
            public void onResponse(String events)
            {
                ICalendar ical = Biweekly.parse(events).first();
                filterCalendar(ical);
                closeProgressDialog(self);
            }

            @Override
            public void onError(ANError anError)
            {
          		self.handleError(anError);
            }
        });
    }

    private void requestCalendar(boolean isAulas)
    {
        MainActivity self = this;

        requestCalendar(new StringRequestListener()
        {
            @Override
            public void onResponse(String events)
            {
                ICalendar ical = Biweekly.parse(events).first();
                filterCalendar(ical);
                closeProgressDialog(self);
                showResults(isAulas);
            }

            @Override
            public void onError(ANError anError)
            {
                self.handleError(anError);
            }
        });
    }

    private void loadPreviousCalendar(boolean isAulas)
    {
        new Thread(()->
        {
            if (lastNr == -1 || lastNr != Integer.parseInt(field.getText()+""))
            {
                requestCalendar(isAulas);
            }
            else
            {
                showResults(isAulas);
            }
        }).start();
    }

    private void filterCalendar(ICalendar ical)
    {
        Date now = Calendar.getInstance().getTime();

        ArrayList<VEvent> events = new ArrayList<>(ical.getEvents());
        Collections.sort(events, (a, b) -> a.getDateStart().getValue().compareTo(b.getDateStart().getValue()));
        Iterator<VEvent> iterator = new ArrayList<>(events).iterator();

        ICalendar aulas = new ICalendar();
        ICalendar avaliacoes = new ICalendar();

        while(iterator.hasNext())
        {
            VEvent event = iterator.next();
            Date eventDate = event.getDateEnd().getValue();

            if(!now.before(eventDate))
            {
                iterator.remove();
            }
            else
            {
                if (event.getSummary().getValue().contains("Avaliação"))
                    avaliacoes.addEvent(event);
                else
                    aulas.addEvent(event);
            }
        }

        String strAulas = Biweekly.write(aulas).go();
        String strAvaliacoes = Biweekly.write(avaliacoes).go();

        lastNr = Integer.parseInt(field.getText() + "");
        prefs.setString(
                Prefs.FILE_MISC + "",
                Prefs.KEY_AULAS + "",
                strAulas);
        prefs.setString(
                Prefs.FILE_MISC + "",
                Prefs.KEY_AVALIACOES + "",
                strAvaliacoes);
        prefs.setInt(
                Prefs.FILE_MISC + "",
                Prefs.KEY_LASTNR + "",
                lastNr);
    }

    private void filterCalendar(ICalendar ical, boolean isAulas)
    {
        ArrayList<VEvent> events = new ArrayList<>(ical.getEvents());
        Collections.sort(events, (a, b) -> a.getDateStart().getValue().compareTo(b.getDateStart().getValue()));

        int initialSize = events.size();
        Iterator<VEvent> iterator = new ArrayList<>(events).iterator();

        Date now = Calendar.getInstance().getTime();

        while(iterator.hasNext())
        {
            VEvent event = iterator.next();
            Date eventDate = event.getDateEnd().getValue();
            if(!now.before(eventDate)) iterator.remove();
        }

        if(events.size() < initialSize)
        {
            String s = Biweekly.write(ical).go();
            if(isAulas) prefs.setString(
                    Prefs.FILE_MISC + "",
                    Prefs.KEY_AULAS + "",
                    s);
            else prefs.setString(
                    Prefs.FILE_MISC + "",
                    Prefs.KEY_AVALIACOES + "",
                    s);
        }
    }

    private void handleError(ANError anError)
    {
        closeProgressDialog(this);
        if(anError.getErrorCode() == 500)
        {
            toast(this, ERROR_NMEC_MESSAGE);
        }
        else
        {
            toast(this,ERROR_NETWORK_MESSAGE);
        }
    }
}
