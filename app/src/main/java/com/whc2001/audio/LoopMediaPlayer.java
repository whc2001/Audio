package com.whc2001.audio;

import android.content.Context;
import android.media.MediaPlayer;

public class LoopMediaPlayer {

    public static final String TAG = LoopMediaPlayer.class.getSimpleName();

    private Context mContext = null;
    private int mPreResId = 0;
    private int mLoopResId = 0;

    private MediaPlayer mPrePlayer = null;
    private MediaPlayer mCurrentPlayer = null;
    private MediaPlayer mNextPlayer = null;

    /*public static LoopMediaPlayer create(Context context, int resId) {
        return new LoopMediaPlayer(context, resId);
    }*/

    public LoopMediaPlayer(Context context, int loopResId, final int preResId) {
        mContext = context;
        mPreResId = preResId;
        mLoopResId = loopResId;

        mCurrentPlayer = MediaPlayer.create(mContext, loopResId);
        mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if(preResId == 0)
                    mCurrentPlayer.start();
            }
        });

        if(preResId != 0)
        {
            mPrePlayer = MediaPlayer.create(mContext, preResId);
            mPrePlayer.setNextMediaPlayer(mCurrentPlayer);
            mPrePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer)
                {
                    mPrePlayer.start();
                }
            });
            mPrePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPrePlayer.release();
                    mPrePlayer = null;
                }
            });
        }

        createNextMediaPlayer();
    }

    private void createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, mLoopResId);
        mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
        mCurrentPlayer.setOnCompletionListener(onCompletionListener);
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            mCurrentPlayer = mNextPlayer;

            createNextMediaPlayer();
        }
    };

    public void Pause()
    {
        try {
            if (mPrePlayer != null)
                mPrePlayer.pause();
            else
                mCurrentPlayer.pause();
        }
        catch(Exception ex)
        {}
    }

    public void Resume()
    {
        try {
        if(mPrePlayer != null)
            mPrePlayer.start();
        else
            mCurrentPlayer.start();
    }
        catch(Exception ex)
        {}
    }

    public void Dispose()
    {
        if(mPrePlayer != null)
        {
            mPrePlayer.release();
            mPrePlayer = null;
        }
        if(mCurrentPlayer != null)
        {
            mCurrentPlayer.release();
            mCurrentPlayer = null;
        }
        if(mNextPlayer != null)
        {
            mNextPlayer.release();
            mNextPlayer = null;
        }
    }
}
