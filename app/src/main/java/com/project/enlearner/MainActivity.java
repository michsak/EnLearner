package com.project.enlearner;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    public void determineRoundOrSquare(View view)
    {
        if(getResources().getConfiguration().isScreenRound())
        {
            //screen is round
        }
        else
        {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame_layout, new CustomCardFragment());
        fragmentTransaction.commit();

        setAmbientEnabled();

        startService(new Intent(this, BackgroundService.class));

    }
}