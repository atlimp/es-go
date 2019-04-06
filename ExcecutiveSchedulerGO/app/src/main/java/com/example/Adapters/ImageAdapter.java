package com.example.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.excecutiveschedulergo.CreateEventActivity;
import com.example.excecutiveschedulergo.FragmentActivity;
import com.example.excecutiveschedulergo.LoginActivity;
import com.example.excecutiveschedulergo.R;
import com.example.excecutiveschedulergo.TokenStore;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private Boolean mLogged;

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.ic_calendaricon,
            R.drawable.ic_createnewicon,
            R.drawable.ic_loginicon
    };

    // Constructor
    public ImageAdapter(Context c, Boolean logged) {
        mContext = c;
        mLogged = logged;
        setLogin(logged);
    }

    public int getCount() { return mThumbIds.length; }

    public Object getItem(int position) { return null; }

    public long getItemId(int position) { return 0; }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            DisplayMetrics dm = parent.getResources().getDisplayMetrics();
            int width = (int) dm.widthPixels/2;
            imageView.setLayoutParams(new GridView.LayoutParams(width, width));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        } imageView.setImageResource(mThumbIds[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(position) {
                    case 0:
                        if(TokenStore.getToken(mContext) == null) {
                            Intent login = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(login);
                        } else {
                            Intent calendar = new Intent(mContext, FragmentActivity.class);
                            mContext.startActivity(calendar);
                        }
                        break;
                    case 1:
                        Log.v("createEventListener", "listener activates");
                        if(TokenStore.getToken(mContext) == null){
                            Intent login = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(login);
                        } else {
                            Intent createEvent = new Intent(mContext, CreateEventActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("Type", 0);
                            createEvent.putExtras(bundle);
                            mContext.startActivity(createEvent);
                        }
                        break;
                    case 2:
                        setLogin(TokenStore.getToken(mContext) == null);
                        if (TokenStore.getToken(mContext) != null) {
                            TokenStore.deleteToken(mContext);
                        } else {
                            Intent login = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(login);
                        }
                        break;
                    default:

                }
            }
        });
        return imageView;
    }

    public void setLogin(Boolean logged){
        if(logged) {
            mThumbIds[2] = R.drawable.ic_loginicon;
            mLogged = false;
        } else {
            mThumbIds[2] = R.drawable.ic_logouticon;
            mLogged = true;
        } notifyDataSetChanged();
    }
}
