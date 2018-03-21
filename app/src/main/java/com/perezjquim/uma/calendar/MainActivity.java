package com.perezjquim.uma.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    }

    public void requestCalendar(View v)
    {
        new Thread(()->
        {
            runOnUiThread(()->
                    showProgressDialog(this,PROGRESS_MESSAGE));
            try
            {
                if(field.getText().length() == 0) throw new IOException();

                InputStream is = new URL(CALENDAR_URL+field.getText()).openStream();
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String events = s.hasNext() ? s.next() : "";
                prefs.setString("misc","events",events);
                startActivity( new Intent(this,ResultsActivity.class));
            }
            catch (IOException e)
            {
                toast(this,ERROR_MESSAGE);
                e.printStackTrace();
            }
            finally
            {
                runOnUiThread(()->
                        hideProgressDialog());
            }
        }).start();
    }

    public void loadPreviousCalendar(View v)
    {
        new Thread(()->
                startActivity(new Intent(this,ResultsActivity.class))).start();
    }
}
