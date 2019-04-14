package com.example.excecutiveschedulergo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.Connection.Connection;
import com.example.adapters.ListViewAdapter;
import com.example.model.Event;
import com.example.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Shows events that take place on the specified day
 */
public class PortraitFragment extends Fragment {

    FragmentActivity activity;          // Parent activity
    View view;                          // Fragment xml
    ListView mList;                     // Shows events
    LinearLayout mCardView;             // Shows longClicked event details
    Button mPrevButton;                 // Show previous day
    Button mNextButton;                 // Show next day
    TextView mCurrentDate;              // Shows chosen day
    ProgressBar mCalendarProgress;      // Shown while loading events from backend

    Calendar startCal;                  // Delimits event dates to search for
    Calendar endCal;                    // when sending request to backend

    // Singleton connection to backend
    Connection c = Connection.getInstance();

    public PortraitFragment() {
        // Required empty public constructor
    }

    /**
     * Sets parent activity
     * @param activity
     */
    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set xml fragment
        view = inflater.inflate(R.layout.fragment_portrait, container, false);

        // Set components
        mList = view.findViewById(R.id.list);

        mPrevButton = view.findViewById(R.id.prevDayButton);
        mNextButton = view.findViewById(R.id.nextDayButton);

        mCurrentDate = view.findViewById(R.id.currentDate);

        mCalendarProgress = view.findViewById(R.id.calendarProgress);
        mCardView = view.findViewById(R.id.CardView);

        // Set current day
        Calendar today = new GregorianCalendar();

        startCal = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DATE, 1);

        // Set component listeners
        setListeners();

        // Reload list with events from current day
        reloadData();

        return view;
    }

    /**
     * Makes a string out of a date
     * @param date
     * @return
     */
    private String dateString(Date date) {
        Locale loc = new Locale("is", "IS");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, loc);

        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    /**
     * Sets all component listeners
     */
    private void setListeners(){

        // When event is clicked, the toolbars current event is set to the clicked item
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                activity.toolbar.event = (Event) parent.getItemAtPosition(position);
                mCardView.setVisibility(View.GONE);         // Hide event details idempotent
            }
        });

        // Populate cardView component with clicked items details
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                View whole = PortraitFragment.this.view;
                Event event = (Event) parent.getItemAtPosition(position);
                mCardView.setVisibility(View.VISIBLE);
                TextView mTitle = whole.findViewById(R.id.CardTitle);
                mTitle.setText(event.getTitle());
                TextView mDescription = whole.findViewById(R.id.CardDescription);
                mDescription.setText(event.getDescription());
                TextView mStartDate = whole.findViewById(R.id.CardStartDate);
                mStartDate.setText(dateString(event.getStartDate()));
                TextView mEndDate = whole.findViewById(R.id.CardEndDate);
                mEndDate.setText(dateString(event.getEndDate()));

                // Set userlist on card
                ListView mUserList = whole.findViewById(R.id.CardUsers);
                List<User> users = event.getUsers();

                // Adapter for userlist inside cardView
                ArrayAdapter<User> adapter = new ArrayAdapter<User>(
                        activity.getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        users
                );

                mUserList.setAdapter(adapter);
                return true;
            }
        });

        // Hide cardView when clicked
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardView.setVisibility(View.GONE);
            }
        });

        // Show events of previous day
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DATE, -1);
                endCal.add(Calendar.DATE, -1);
                reloadData();
            }
        });

        // Show events of next day
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DATE, 1);
                endCal.add(Calendar.DATE, 1);
                reloadData();
            }
        });
    }

    /**
     * Gets relevant events from backend and populates mList with them
     */
    private void reloadData() {
        // Set progress bar visibility
        mCalendarProgress.setVisibility(View.VISIBLE);

        // Clear list view
        ListViewAdapter clearEvents = new ListViewAdapter(activity, new ArrayList<Event>());

        mList.setAdapter(clearEvents);

        mCurrentDate.setText("" + startCal.get(Calendar.DAY_OF_MONTH) + "." + startCal.get(Calendar.MONTH) + "." + startCal.get(Calendar.YEAR));

        // Authentication for backend
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

                    ListViewAdapter adapter = new ListViewAdapter(activity, events);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCalendarProgress.setVisibility(View.GONE);
                            mList.setAdapter(adapter);
                        }
                    });
                } else {
                    // If unauthorized redirect to login.
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        activity.redirectToLogin();
                    }
                    Log.e("Get events", json);
                }
            }
        });

    }

}
