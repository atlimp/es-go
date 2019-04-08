package com.example.excecutiveschedulergo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.Connection.Connection;
import com.example.model.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class LandscapeFragment extends Fragment {

    FragmentActivity activity;

    RelativeLayout[] days;
    ScrollView mLandscapeScrollview;

    Connection c = Connection.getInstance();

    Calendar startCal;
    Calendar endCal;

    List<Event> events;

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public LandscapeFragment() {
        // Required empty public constructor
    }

    private void initDays(View view) {
        RelativeLayout mCalendarMonday = view.findViewById(R.id.calendarMonday);
        RelativeLayout mCalendarTuesday = view.findViewById(R.id.calendarTuesday);
        RelativeLayout mCalendarWednesday = view.findViewById(R.id.calendarWednesday);
        RelativeLayout mCalendarThursday = view.findViewById(R.id.calendarThursday);
        RelativeLayout mCalendarFriday = view.findViewById(R.id.calendarFriday);
        RelativeLayout mCalendarSaturday = view.findViewById(R.id.calendarSaturday);
        RelativeLayout mCalendarSunday = view.findViewById(R.id.calendarSunday);

        days = new RelativeLayout[]{
                mCalendarMonday,
                mCalendarTuesday,
                mCalendarWednesday,
                mCalendarThursday,
                mCalendarFriday,
                mCalendarSaturday,
                mCalendarSunday
        };
    }

    private String print(Calendar c) {
        return
                c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1)
                + "." + c.get(Calendar.YEAR) + "  " + c.get(Calendar.HOUR_OF_DAY)
                + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landscape, container, false);

        initDays(view);

        mLandscapeScrollview = view.findViewById(R.id.landscape_scrollview);

        Calendar today = new GregorianCalendar();

        startCal = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        int dayOfWeek = (startCal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        startCal.add(Calendar.DAY_OF_YEAR, dayOfWeek);

        endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DAY_OF_YEAR, 6);
        endCal.add(Calendar.HOUR_OF_DAY, 23);
        endCal.add(Calendar.MINUTE, 59);
        endCal.add(Calendar.SECOND, 59);

        Log.e("StartCal", print(startCal));
        Log.e("EndCal", print(endCal));

        reloadData();

        return view;
    }

    private void setEvents(List<Event> events) {
        this.events = events;
    }

    private void updateUI() {
        int hour = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);

        int yOff = (int) ((hour - 2) * 30 * getResources().getDisplayMetrics().density);
        mLandscapeScrollview.smoothScrollTo(0, yOff);
            
        for (Event e : events) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.event_layout, null);
            TextView title = view.findViewById(R.id.event_landscape_title);
            title.setText(e.toString());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            float density = getResources().getDisplayMetrics().density;

            Calendar start = new GregorianCalendar();
            start.setTime(e.getStartDate());

            Calendar end = new GregorianCalendar();
            end.setTime(e.getEndDate());

            Log.e("StartTime", print(start));
            Log.e("EndTime", print(end));

            // Number of half hour timeslots
            int startTime = (start.get(Calendar.HOUR_OF_DAY) * 60) + start.get(Calendar.MINUTE);
            // Offset from top in dp
            int topOff = (int) ((1.0 * startTime / 60) * 30 * density);

            long duration = (end.getTimeInMillis() - start.getTimeInMillis()) / (1000 * 60);
            // Height in dp
            int height = (int) ((1.0 * duration / 60) * 30 * density);

            // Week starts at monday
            int index = (start.get(Calendar.DAY_OF_WEEK) + 5) % 7;

            params.setMargins(0, topOff, 0, 0);
            params.height = height;

            view.setLayoutParams(params);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Respond to clicked event
                    Log.e("Clicked event", "ID: " + e.getId() + " title: " + e.getTitle() + e.toString());
                }
            });

            // Add event to correct day
            days[index].addView(view);
        }
    }

    private void reloadData() {
        String token = TokenStore.getToken(activity.getApplicationContext());
        c.getEvents(new Date(startCal.getTimeInMillis()), new Date(endCal.getTimeInMillis()), token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get events", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.e("Get events", json);
                    Gson gson = new Gson();

                    Type type = new TypeToken<ArrayList<Event>>(){}.getType();
                    List events = gson.fromJson(json, type);
                    Log.e("List size", "" + events.size());

                    setEvents(events);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } else {
                    Log.e("Get events", json);
                }
            }
        });

    }
}
