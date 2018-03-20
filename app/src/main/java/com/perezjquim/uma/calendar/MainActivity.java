package com.perezjquim.uma.calendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.perezjquim.SharedPreferencesHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
{
    private TextView field;
    private Dialog dialog;
    private SharedPreferencesHelper prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        field = findViewById(R.id.field);
        prefs = new SharedPreferencesHelper(this);
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
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String events = s.hasNext() ? s.next() : "";
                prefs.setString("misc","events",events);
                startActivity( new Intent(this,ResultsActivity.class));
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

    public void loadPreviousCalendar(View v)
    {
        startActivity( new Intent(this,ResultsActivity.class));
    }
}
