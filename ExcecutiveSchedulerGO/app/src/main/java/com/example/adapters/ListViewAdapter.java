package com.example.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.excecutiveschedulergo.R;
import com.example.model.Event;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for ListView in portraitFragment.
 * It controls fonts and components of each item in list
 * and sorts events by time
 */
public class ListViewAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<Event> mEvents;        // List of events


    public ListViewAdapter(Activity activity, List<Event> events)
    {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEvents = events;
        // Sort the list of events
        Collections.sort(mEvents, new Comparator<Event>() {
            public int compare(Event left, Event right)  {
                // if left is smaller return negative, else return positive
                return (left.getStartDate().getTime() < right.getStartDate().getTime()) ? -1:1;
            }
        });
    }

    /**
     * Return list size
     * @return
     */
    @Override
    public int getCount() {
        return mEvents.size();
    }

    /**
     * Return requested item
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mEvents.get(position);
    }

    /**
     * Return event id
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return mEvents.get(position).getId();
    }

    /**
     * Populates list based on an xml named row for each item
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.row, parent, false);
        }
        Event event = mEvents.get(position);                        // Current event
        TextView tv1 = convertView.findViewById(R.id.textView1);    // Set title textBox in row
        TextView tv2 = convertView.findViewById(R.id.textView2);    // Set time in row

        tv1.setText(event.getTitle());

        // Create strings from dates
        String start = dateString(event.getStartDate());
        String end = dateString(event.getEndDate());

        // Combine strings and set them to textBox
        tv2.setText(start + " - " + end);

        return convertView;
    }

    /**
     * Creates a string from a date
     * @param date
     * @return
     */
    private String dateString(Date date) {
        Locale loc = new Locale("is", "IS");
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, loc);

        return timeFormat.format(date);
    }
}
