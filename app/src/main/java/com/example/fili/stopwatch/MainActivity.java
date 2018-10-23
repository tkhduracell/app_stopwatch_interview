package com.example.fili.stopwatch;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_KEY = "TIMER";
    public static final String PREF_KEY_TIME = "KEY_TIME";
    private Timer mTimer;
    private TextView mTimerText;

    private long mTimeStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimerText = (TextView) findViewById(R.id.text);
        mTimer = new Timer();
    }

    public void stopWatch(View view) {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTime(0,0,0);
            }
        }, 100);
        mTimer.cancel();
        mTimer = new Timer();
        mTimeStart = 0;
        updateTime(0,0,0);
    }

    public void startWatch(View view) {
        mTimeStart = System.currentTimeMillis();
        startTick();
    }

    private void startTick() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long diff = System.currentTimeMillis() - mTimeStart;
                long minutes = diff / (1000 * 60); // Flooring
                long seconds = (diff / 1000) - (minutes * 60);
                long millis = diff % 1000;
                updateTime(minutes, seconds, millis);
            }
        }, 0, 1);
    }

    private void updateTime(final long minutes, final long seconds, final long millis) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimerText.setText(String.format("%2d:%2d %d", minutes, seconds, millis));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        long prevTime = getSharedPreferences(PREF_KEY, MODE_PRIVATE).getLong(PREF_KEY_TIME, 0);

        if (prevTime > 0) {
            mTimeStart = prevTime;
            startTick();
        } else {
            updateTime(0,0,0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        preferences.edit()
                .putLong(PREF_KEY_TIME, mTimeStart)
                .apply();

    }
}
