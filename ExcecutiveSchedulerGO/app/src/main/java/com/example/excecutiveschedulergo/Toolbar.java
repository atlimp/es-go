package com.example.excecutiveschedulergo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.model.Event;

/**
 * Toolbar is inserted into activities and used as part of activity it is inserted into
 */
public class Toolbar {

    private Activity    activity;                           // Activity assigned to toolbar
    public  Event       event;                              // Event set in activity
    public  Button      mCreate, mEdit, mShare, mLogout;    // Buttons pressed in activity

    /**
     * Constructor sets components, listeners and activity
     * @param _activity
     */
    public Toolbar(Activity _activity){
        mCreate  = _activity.findViewById(R.id.create_event);
        mEdit    = _activity.findViewById(R.id.edit_event);
        mShare   = _activity.findViewById(R.id.share_event);
        mLogout  = _activity.findViewById(R.id.logout);
        activity = _activity;
        setListeners();
    }

    /**
     * Constructor that behaves differently if activity is mainActivity
     * @param _activity
     * @param isMain
     */
    public Toolbar(Activity _activity, boolean isMain) {
        mCreate  = _activity.findViewById(R.id.create_event);
        mEdit    = _activity.findViewById(R.id.edit_event);
        mShare   = _activity.findViewById(R.id.share_event);
        mLogout  = _activity.findViewById(R.id.logout);
        activity = _activity;

        mCreate.setVisibility(View.GONE);
        mEdit.setVisibility(View.GONE);
        mShare.setVisibility(View.GONE);

        setListeners();
    }

    /**
     * From https://stackoverflow.com/a/3913720
     * Passing int parameter to opening Activity to determine the layout.
     */
    public void setListeners(){

        // Opens createEventActivity with purpose create
        mCreate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity.getApplicationContext(), CreateEventActivity.class);
                intent.putExtra("Type", 0);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        // Opens createEventActivity with purpose Edit
        mEdit.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context context = activity.getApplicationContext();
                if(event == null) {
                    Toast toast = Toast.makeText(context, "Please select an event", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Intent intent = new Intent(context, CreateEventActivity.class);
                intent.putExtra("Event", event);
                intent.putExtra("Type", 1);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        // Opens shareEventActivity
        mShare.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context context = activity.getApplicationContext();
                if(event == null) {
                    Toast toast = Toast.makeText(context, "Please select an event", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Intent intent = new Intent(context, ShareEventActivity.class);
                intent.putExtra("Event", event);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        // Logs out user
        mLogout.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TokenStore.deleteToken(activity.getApplicationContext());
                activity.finish();
            }
        });

    }
}
