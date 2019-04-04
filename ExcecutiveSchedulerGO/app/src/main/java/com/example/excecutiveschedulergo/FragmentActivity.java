package com.example.excecutiveschedulergo;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class FragmentActivity extends AppCompatActivity {

    private LandscapeFragment mLandscapeFragment;
    private PortraitFragment mPortaitFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.e("Fragment", "change");

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("Fragment", "landscape");
            if (mLandscapeFragment == null) {
                mLandscapeFragment = new LandscapeFragment();
            } setFragment(mLandscapeFragment);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.e("Fragment", "portrait");
            if (mPortaitFragment == null) {
                mPortaitFragment = new PortraitFragment();
            } setFragment(mPortaitFragment);
        }
    }

    private void setFragment(Fragment f) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment, f)
                .commit();
    }
}
