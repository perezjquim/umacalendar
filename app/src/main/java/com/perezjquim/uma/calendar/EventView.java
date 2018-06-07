package com.perezjquim.uma.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EventView extends CardView
{
    private LinearLayout content;
    private TextView txtLabel;
    private TextView txtProf;
    private TextView txtLocation;
    private TextView txtDate;
    private TextView txtTime;

    private static final int FONT_SIZE = 20;
    private static final int PADDING = 10;

    public EventView(Context context, AttributeSet attrs, String label, String tipo, String prof, String sala, String date, String start, String end)
    {
        super(context, attrs);
        init(context);
        setLabel(label + " - " + tipo);
        setProf(prof);
        setLocation(sala);
        setDate(date);
        setTime(start+" - "+end);
    }

    public EventView(Context context, String label, String tipo, String prof, String sala, String date, String start, String end)
    {
        super(context);
        init(context);
        setLabel(label + " - " + tipo);
        setProf(prof);
        setLocation(sala);
        setDate(date);
        setTime(start+" - "+end);
    }

    public EventView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    public EventView(Context context)
    {
        super(context);
        init(context);
    }

    public void setLabel(String label)
    {
        txtLabel.setText(label);
    }
    public void setProf(String prof) { txtProf.setText(prof); }
    public void setLocation(String location) { txtLocation.setText(location); }
    public void setDate(String date)
    {
        txtDate.setText(date);
    }
    public void setTime(String time) { txtTime.setText(time); }

    private void init(Context c)
    {
        this.setLayoutParams(new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        content = new LinearLayout(c);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        content.setPadding(PADDING,PADDING,PADDING,PADDING);

        txtLabel = new TextView(c);
        txtProf = new TextView(c);
        txtLocation = new TextView(c);
        txtDate = new TextView(c);
        txtTime = new TextView(c);

        initLabel(c,txtLabel);
        initInfo(c,txtProf,android.R.drawable.ic_menu_info_details);
        initInfo(c,txtLocation,android.R.drawable.ic_dialog_map);
        initInfo(c,txtDate,android.R.drawable.ic_menu_my_calendar);
        initInfo(c,txtTime,android.R.drawable.ic_menu_recent_history);

        addView(content);
    }

    private void initLabel(Context c, TextView t)
    {
        t.setAllCaps(true);
        t.setTypeface(null, Typeface.BOLD);
        t.setTextSize(FONT_SIZE);
        content.addView(t);
    }

    private void initInfo(Context c, TextView t, int icon)
    {
        t.setTextSize(FONT_SIZE);
        t.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
        content.addView(t);
    }
}