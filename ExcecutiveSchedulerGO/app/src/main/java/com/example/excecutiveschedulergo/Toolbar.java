package com.example.excecutiveschedulergo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.Connection.Connection;

public class Toolbar {

    Activity activity;
    Button mCreate, mEdit, mShare, mLogout;

    public Toolbar(Activity activity){
        mCreate = activity.findViewById(R.id.create_event);
        mEdit   = activity.findViewById(R.id.edit_event);
        mShare  = activity.findViewById(R.id.share_event);
        mLogout = activity.findViewById(R.id.logout);
        this.activity = activity;
    }

    public void setListeners(){

        mCreate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity.getApplicationContext(), CreateEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Type", 0);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        mEdit.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity.getApplicationContext(), CreateEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Type", 1);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        mShare.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity.getApplicationContext(), CreateEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Type", 2);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        mLogout.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TokenStore.setToken("",activity.getApplicationContext());
                activity.finish();
            }
        });

    }
}
