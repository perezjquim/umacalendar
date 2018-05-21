package com.perezjquim.uma.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.perezjquim.SharedPreferencesHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static com.perezjquim.UIHelper.hideProgressDialog;
import static com.perezjquim.UIHelper.showProgressDialog;
import static com.perezjquim.UIHelper.toast;

public class MainActivity extends AppCompatActivity
{
    private TextView field;
    private SharedPreferencesHelper prefs;
    private String lastNr;
    private static final String PROGRESS_MESSAGE = "Obtendo calendário..";
    private static final String ERROR_MESSAGE = "Número mecanográfico inválido ou falta de conectividade!";
    private static final String ERROR_MESSAGE_CALENDAR_NOT_FOUND = "Calendário inexistente!";
    private static final String CALENDAR_URL = "http://calendar.uma.pt/";
    private static final String PREFS_FILE = "misc";
    private static final String PREFS_LAST_NUMBER = "lastnr";
    private static final String PREFS_EVENTS_STRING = "events";
    private Thread tRequestCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        field = findViewById(R.id.field);
        prefs = new SharedPreferencesHelper(this);
        lastNr = prefs.getString(PREFS_FILE,PREFS_LAST_NUMBER);
        if(lastNr != null)
            field.setText(lastNr);
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
                runOnUiThread(() ->
                        showProgressDialog(this, PROGRESS_MESSAGE));

                if (field.getText().length() == 0) throw new IOException();

                lastNr = field.getText() + "";
                prefs.setString(PREFS_FILE, PREFS_LAST_NUMBER, lastNr);
                InputStream is = new URL(CALENDAR_URL + field.getText()).openStream();
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String events = s.hasNext() ? s.next() : "";
                prefs.setString(PREFS_FILE, PREFS_EVENTS_STRING, events);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                runOnUiThread(()->
                        hideProgressDialog());
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
                if (!lastNr.equals(field.getText()+""))
                {
                    requestCalendar();
                    tRequestCalendar.join();
                }

                boolean hasPreviousData = prefs.getString(PREFS_FILE, PREFS_EVENTS_STRING) != null;
                if (hasPreviousData)
                {
                    Intent i = new Intent(this, ResultsActivity.class);
                    i.putExtra("isAulas", isAulas);
                    startActivity(i);
                }
                else
                {
                    toast(this, ERROR_MESSAGE_CALENDAR_NOT_FOUND);
                }
            }
            catch(IOException | InterruptedException e)
            {
                toast(this,ERROR_MESSAGE);
                e.printStackTrace();
            }
        }).start();
    }
}
