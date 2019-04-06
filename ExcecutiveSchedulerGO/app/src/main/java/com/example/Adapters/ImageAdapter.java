package com.example.Adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.excecutiveschedulergo.R;

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
        if(logged) mThumbIds[2] = R.drawable.ic_logouticon;
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
