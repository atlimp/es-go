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

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<Event> mEvents;


    public ListViewAdapter(Activity activity, ArrayList<Event> events)
    {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEvents = events;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mEvents.size();  //listview item count.
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return mEvents.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder vh;
        vh= new ViewHolder();

        if(convertView==null )
        {
            convertView = mInflater.inflate(R.layout.row, parent,false);
            //inflate custom layour
            vh.tv2= (TextView)convertView.findViewById(R.id.textView2);

        }
        else
        {
            convertView.setTag(vh);
        }
        //vh.tv2.setText("Position = "+position);
        vh.tv2.setText(mEvents.get(position).getTitle());
        //set text of second textview based on position

        return convertView;
    }

    class ViewHolder
    {
        TextView tv1,tv2;
    }

}
