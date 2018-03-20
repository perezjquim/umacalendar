package com.perezjquim.uma.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import static com.perezjquim.util.UIHelper.toast;

public class MainActivity extends AppCompatActivity
{
    private TextView console;
    private TextView field;
    private RequestQueue queue;
    private ProgressBar progress;
    private LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        console = findViewById(R.id.console);
        field = findViewById(R.id.field);
        queue = Volley.newRequestQueue(this);
        progress = findViewById(R.id.progress);
        list = findViewById(R.id.lay);
    }
    public void requestCalendar(View v)
    {
        toast(this,"aaa");
        new Thread(()->
        {
            runOnUiThread(()->
            {
                progress.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
                InputStream is = null;
            try {
                is = new URL("http://calendar.uma.pt/"+field.getText()).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ICalendar ical = null;
            try {
                ical = Biweekly.parse(is).first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<VEvent> events = ical.getEvents();


           /* try {
                JSONArray array = new JSONArray(ical.writeJson());
                ical.w
                JSONArray events = (JSONArray) array.get(2);
                for (int i = 2; i< array.length() ; i++)
                {
                        System.out.println(array.get(i));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            toast(this,"ola");
            runOnUiThread(()->
            {
                for(VEvent e : events)
                {
                    TextView t = new TextView(this);
                    t.setText( e.getSummary().getValue());
                    list.addView(t);
                    System.out.println("-- EVENTO -- ");
                    System.out.println(e.toString());
                    System.out.println("Aula: " + e.getSummary().getValue());
                    System.out.println("Date start: " + e.getDateStart().getValue().getRawComponents().getHour());
                    System.out.println("Date end: " + e.getDateEnd().getValue());
                    System.out.println("-- --- -- ");
                }
                progress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        }).start();
    }
}
