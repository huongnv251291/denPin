package com.tohsoft.app.ui.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.tohsoft.app.data.ApplicationModules;

import java.io.IOException;

public class AudioUlti implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SOUND_IS_ON_OR_OFF = "SOUND_IS_ON_OR_OFF";
    private static AudioUlti mAudioUlti;
    private static final Object sLock = new Object();
    private Context mContextApp;
    private SoundPool mSoundPool;
    private SoundTone soundTone;
    private boolean isTurnOnSound;
    private SharedPreferences sharedPreferences;

    public static AudioUlti getInstance() {
        if (mAudioUlti == null) {
            init(ActivityUtils.getTopActivity().getApplication());
        }
        return mAudioUlti;
    }

    public static void init(Application application) {
        synchronized (sLock) {
            if (mAudioUlti == null) {
                mAudioUlti = new AudioUlti();
                mAudioUlti.mContextApp = application.getApplicationContext();
                mAudioUlti.firstInit();
            }
        }


    }

    private void firstInit() {
        mSoundPool = createNewSoundPool();
        soundTone = new SoundTone();
        soundTone.loadDataToSoundPool(mSoundPool, mContextApp);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContextApp);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        isTurnOnSound = sharedPreferences.getBoolean(SOUND_IS_ON_OR_OFF, true);
    }

    public void startRotateDP() {
        try {
//            stopPlaying();
            playSound(3, isTurnOnSound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOnPin() {
        try {
//            stopPlaying();
            playSound(1, isTurnOnSound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffPin() {
        try {
//            stopPlaying();
            playSound(2, isTurnOnSound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SoundPool createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    private void playSound(int id, boolean isTurnOnSound) throws IOException {
        soundTone.playSoundEffect(id, mSoundPool, mContextApp, isTurnOnSound ? 100f : 0f);
    }

    public boolean isHaveSound() {
        return isTurnOnSound;
    }

    private void stopPlaying() {
        try {
            soundTone.destroy();
            mSoundPool.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void destroy() {
        mAudioUlti.stopPlaying();
        mAudioUlti = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (TextUtils.equals(key, SOUND_IS_ON_OR_OFF)) {
            isTurnOnSound = sharedPreferences.getBoolean(key, true);
        }
    }

    public void soundChange() {
        try {

            if (isTurnOnSound) {
                playSound(2, true);
            } else {
                playSound(1, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean temp = !isTurnOnSound;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SOUND_IS_ON_OR_OFF, temp);
        editor.apply();
    }
}
