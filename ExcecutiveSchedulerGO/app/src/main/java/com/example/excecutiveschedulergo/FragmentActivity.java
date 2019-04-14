package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Shows daily or weekly events based on screen orientation
 */
public class FragmentActivity extends AppCompatActivity {

    private LandscapeFragment mLandscapeFragment;   // Fragment loaded if orientation is landscape
    private PortraitFragment mPortraitFragment;     // Fragment loaded if orientation is portrait

    public Toolbar toolbar;                         // Toolbar inserted into activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // Toolbar holds current event and buttons to modify event or logout
        toolbar = new Toolbar(this);

        // Load relevant fragment
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mPortraitFragment = new PortraitFragment();
            mPortraitFragment.setActivity(this);
            setFragment(mPortraitFragment);
        }
        else {
            mLandscapeFragment = new LandscapeFragment();
            mLandscapeFragment.setActivity(this);
            setFragment(mLandscapeFragment);
        }
    }

    /**
     * When orientation changes load relevant fragment
     * @param newConfig
     */
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
            if (mPortraitFragment == null) {
                mPortraitFragment = new PortraitFragment();
                mPortraitFragment.setActivity(this);
            }
            setFragment(mPortraitFragment);
        }
    }

    /**
     * Sets given fragment to layout
     * @param f
     */
    private void setFragment(Fragment f) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment, f)
                .commit();
    }

    /**
     * Attempt to make entire activity responsive to setting visibility of cardView
     * (see fragments)
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            Log.e("Fragment", "not null");
            mPortraitFragment.mCardView.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e("Fragment", e.getMessage());
        }
        return true;
    }

    /**
     * Go back to main instead of last screen
     */
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    /**
     * Redirect to loginActivity
     */
    public void redirectToLogin() {
        TokenStore.deleteToken(getApplicationContext());
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login);
    }
}
