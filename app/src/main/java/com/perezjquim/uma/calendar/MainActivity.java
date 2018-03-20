package com.perezjquim.uma.calendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class MainActivity extends AppCompatActivity
{
    private TextView console;
    private TextView field;
    private LinearLayout list;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        console = findViewById(R.id.console);
        field = findViewById(R.id.field);
        list = findViewById(R.id.lay);
        dialog = new Dialog(this,R.style.TransparentProgressDialog);
        dialog.setCancelable(false);
        dialog.addContentView(new ProgressBar(this),new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
    }
    public void requestCalendar(View v)
    {
        new Thread(()->
        {
            runOnUiThread(()->
                    dialog.show());

            try
            {
                InputStream is = new URL("http://calendar.uma.pt/"+field.getText()).openStream();
                ICalendar ical = Biweekly.parse(is).first();
                List<VEvent> events = ical.getEvents();
                Intent i = new Intent(this,ResultsActivity.class);
                /*for(VEvent e : events)
                {
                    TextView t = new TextView(this);
                    t.setText(e.getSummary().getValue());
                    runOnUiThread(()->
                            list.addView(t));
                    System.out.println("-- EVENTO -- ");
                    System.out.println(e.toString());
                    System.out.println("Aula: " + e.getSummary().getValue());
                    System.out.println("Date start: " + e.getDateStart().getValue().getRawComponents().getHour());
                    System.out.println("Date end: " + e.getDateEnd().getValue());
                    System.out.println("-- --- -- ");
                }*/
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                runOnUiThread(()->
                        dialog.dismiss());
            }
        }).start();
    }
}
