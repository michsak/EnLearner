package com.project.enlearner;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public static int number;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        number = 1;
        handler = new Handler();
        runnable = new Runnable()
        {
            public void run()
            {
                handler.postDelayed(runnable, 3600000);
            }
        };

        handler.postDelayed(runnable, 15000);
        Log.i("info", "has started");
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        BackgroundService.number = 1;
    }
}
