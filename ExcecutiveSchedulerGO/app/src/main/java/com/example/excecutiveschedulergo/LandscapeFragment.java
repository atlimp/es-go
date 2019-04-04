package com.example.excecutiveschedulergo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.model.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class LandscapeFragment extends Fragment {

    View clickSource;
    View touchSource;
    ArrayList<ListView> all;
    int offset = 0;

    public LandscapeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landscape, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setFields(view);
    }

    public void setFields(View view){
        String[] days = {"labels","monday","tuesday","wednesday","thursday","friday","saturday","sunday"};
        all = getComponents(view);

        DisplayMetrics dm   = view.getResources().getDisplayMetrics();
        int width           = dm.widthPixels;
        int height          = dm.heightPixels;


        int max             = (width > height) ? width : height;
        int counter         = 0;

        for( ListView lv : all){
            if(lv.getId() != R.id.labels) {
                ArrayList<String> list = fillList();
                ArrayAdapter<String> adapter = createAdapter(list);
                lv.setAdapter(adapter);
            } else {
                ArrayList<String> list = fillLabels();
                ArrayAdapter<String> adapter = createAdapter(list);
                lv.setAdapter(adapter);
            }
            ViewGroup.LayoutParams lp = lv.getLayoutParams();
            lp.width = (int) (max / 8);
            View header = getLayoutInflater().inflate(R.layout.listview_header,null);
            lv.addHeaderView(header);
            TextView headerText = header.findViewById(R.id.header);
            headerText.setText(days[counter++]);
        }
        tandem();
    }


    private ArrayList<ListView> getComponents(View view) {
        LinearLayout layout = view.findViewById(R.id.list_holder);
        ArrayList<ListView> result = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof ListView) {
                result.add((ListView) v);

            }
        } return result;
    }

    private ArrayList<String> fillList(){
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            result.add("");
        } return result;
    }

    private ArrayList<String> fillLabels(){
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            if( i < 10) result.add( "0" + i + ":00" );
            else        result.add( i + ":00" );
        } return result;
    }


    private <T> ArrayAdapter<T> createAdapter(ArrayList<T> day){
        ArrayAdapter<T> result = new ArrayAdapter<T>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                day
        );
        return result;
    }

    /**
     * From https://stackoverflow.com/a/12342853
     * Makes all the lists move together.
     */
    private void tandem(){
        for(ListView lv : all) {
            lv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (touchSource == null)
                        touchSource = v;

                    if (v == touchSource) {
                        for (ListView other : all) {
                            if(other != v) other.dispatchTouchEvent(event);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            clickSource = v;
                            touchSource = null;
                        }
                    }

                    return false;
                }
            });
        }
    }

}
