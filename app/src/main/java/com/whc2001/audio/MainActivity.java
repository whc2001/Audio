package com.whc2001.audio;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    enum State
    {
        STANDBY,
        RUNNING,
        DASHING,
        FINISHED
    };

    SoundPool sp;
    LoopMediaPlayer runPlayer;
    MediaPlayer dashPlayer;
    Timer clockTimer, stopwatchTimer, flashingTimer;
    boolean flashingFlag = false;
    State currentState = State.STANDBY;
    TextView lblClock, lblStopwatch;
    Button btnPlayPause, btnDash;

    DateTime startTimestamp = null, stopTimestamp = null;

    DateTimeFormatter clockFormatter = DateTimeFormat.forPattern("HH:mm:ss");

    PeriodFormatter stopwatchFormatter = new PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .appendSeparator(".")
            .appendMillis3Digit()
            .toFormatter();

    private void DashBegin()
    {
        DashStop();
        dashPlayer = MediaPlayer.create(this, R.raw.dash);
        dashPlayer.setLooping(true);
        dashPlayer.start();
    }

    private void DashStop()
    {
        if(dashPlayer != null)
        {
            dashPlayer.stop();
            dashPlayer.release();
            dashPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);

        lblClock = findViewById(R.id.lblClock);
        lblStopwatch = findViewById(R.id.lblStopwatch);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnDash = findViewById(R.id.btnDash);

        clockTimer = new Timer();
        clockTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                clockTimer_Tick();
            }

        }, 0, 500);

        stopwatchTimer = new Timer();
        stopwatchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopwatchTimer_Tick();
            }

        }, 0, 10);

        flashingTimer = new Timer();
        flashingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                flashingTimer_Tick();
            }

        }, 0, 333);

    }

    public void btnPlayPause_Click(View view)
    {
        switch (currentState)
        {
            case STANDBY:
            case FINISHED:
                startTimestamp = DateTime.now();
                stopTimestamp = null;
                runPlayer = new LoopMediaPlayer(this, R.raw.loop, R.raw.start);
                currentState = State.RUNNING;
                break;
            case RUNNING:
            case DASHING:
                stopTimestamp = DateTime.now();
                runPlayer.Dispose();
                runPlayer = null;
                DashStop();
                currentState = State.FINISHED;
                break;
        }
    }

    public void btnDash_Click(View view)
    {
        switch (currentState)
        {
            case RUNNING:
                runPlayer.Pause();
                DashBegin();
                currentState = State.DASHING;
                break;
            case DASHING:
                DashStop();
                runPlayer.Resume();
                currentState = State.RUNNING;
                break;
        }
    }

    private void clockTimer_Tick()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lblClock.setText(clockFormatter.print(DateTime.now()));
            }
        });
    }

    private void stopwatchTimer_Tick()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(currentState == State.FINISHED)
                {
                    lblStopwatch.setText(stopwatchFormatter.print(new Duration(startTimestamp, stopTimestamp).toPeriod()));
                }
                else if(currentState == State.RUNNING || currentState == State.DASHING)
                {
                    lblStopwatch.setText(stopwatchFormatter.print(new Duration(startTimestamp, DateTime.now()).toPeriod()));
                }
                else
                    lblStopwatch.setText("--:--:--.---");
            }
        });
    }

    private void flashingTimer_Tick()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flashingFlag = ! flashingFlag;
                if(currentState == State.STANDBY || currentState == State.FINISHED)
                {
                    btnPlayPause.setBackgroundResource(android.R.drawable.btn_default);
                    btnDash.setBackgroundResource(android.R.drawable.btn_default);
                }
                else if(currentState == State.RUNNING)
                {
                    if(flashingFlag)
                    {
                        btnPlayPause.setBackgroundColor(Color.RED);
                        btnDash.setBackgroundResource(android.R.drawable.btn_default);
                    }
                    else
                    {
                        btnPlayPause.setBackgroundResource(android.R.drawable.btn_default);
                        btnDash.setBackgroundResource(android.R.drawable.btn_default);
                    }
                }
                else if(currentState == State.DASHING)
                {
                    if(flashingFlag)
                    {
                        btnPlayPause.setBackgroundColor(Color.RED);
                        btnDash.setBackgroundColor(Color.RED);
                    }
                    else
                    {
                        btnPlayPause.setBackgroundResource(android.R.drawable.btn_default);
                        btnDash.setBackgroundResource(android.R.drawable.btn_default);
                    }
                }
            }
        });
    }
}
