package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FragmentActivity extends AppCompatActivity {

    private LandscapeFragment mLandscapeFragment;
    private PortraitFragment mPortaitFragment;

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        toolbar = new Toolbar(this);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mPortaitFragment = new PortraitFragment();
            mPortaitFragment.setActivity(this);
            setFragment(mPortaitFragment);
        }
        else {
            mLandscapeFragment = new LandscapeFragment();
            mLandscapeFragment.setActivity(this);
            setFragment(mLandscapeFragment);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.e("Fragment", "change");

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("Fragment", "landscape");

            //mCalendarToolbar.setVisibility(View.GONE);

            if (mLandscapeFragment == null) {
                mLandscapeFragment = new LandscapeFragment();
                mLandscapeFragment.setActivity(this);
            }
            setFragment(mLandscapeFragment);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            //mCalendarToolbar.setVisibility(View.VISIBLE);

            Log.e("Fragment", "portrait");
            if (mPortaitFragment == null) {
                mPortaitFragment = new PortraitFragment();
                mPortaitFragment.setActivity(this);
            }
            setFragment(mPortaitFragment);
        }
    }

    private void setFragment(Fragment f) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment, f)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }
}
