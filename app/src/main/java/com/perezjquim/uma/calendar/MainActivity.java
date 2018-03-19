package com.perezjquim.uma.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private TextView console;
    private TextView field;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        console = findViewById(R.id.console);
        field = findViewById(R.id.field);
    }
    public void requestCalendar(View v)
    {
        //ICalendar ical = Biweekly.parse(file).first();
        //console.setText(ical.writeJson());
    }
}
