package com.example.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.excecutiveschedulergo.PortraitFragment;
import com.example.excecutiveschedulergo.R;
import com.example.model.Event;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<Event> mEvents;


    public ListViewAdapter(Activity activity, List<Event> events)
    {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEvents = events;
    }
    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEvents.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.row, parent, false);
        }
        Event event = mEvents.get(position);
        TextView tv1 = convertView.findViewById(R.id.textView1);
        TextView tv2 = convertView.findViewById(R.id.textView2);

        tv1.setText(event.getTitle());

        String start = dateString(event.getStartDate());
        String end = dateString(event.getEndDate());


        tv2.setText(start + " - " + end);

        return convertView;
    }

    private String dateString(Date date) {
        Locale loc = new Locale("is", "IS");
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, loc);

        return timeFormat.format(date);
    }

    private void color(){

    }

}
