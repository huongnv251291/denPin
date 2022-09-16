package com.tohsoft.app.ui.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import androidx.annotation.NonNull;

import com.tohsoft.app.R;

import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SoundTone {
    private static final String TAG = "SoundTone";
    private static final int ON = 1;
    private static final int OFF = 2;
    private static final int XOAY_DEN = 3;
    //        private SoundPool soundPool;
    private int idOff, idOn, idXoayden;
    @NotNull
    private Hashtable<Integer, Integer> soundPoolIdPlay;

    public SoundTone() {
        idOff = R.raw.off;
        idOn = R.raw.on;
        idXoayden = R.raw.xoayden;
        soundPoolIdPlay = new Hashtable<>();

    }


    public void destroy() {
        try {
            soundPoolIdPlay.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playSoundEffect(int keyFX, SoundPool soundPool, Context context, float volume) {
        Integer idSource;
        switch (keyFX) {
            case ON:
                idSource = soundPoolIdPlay.get(idOn);
                if (idSource == null) {
                    idSource = soundPool.load(context, idOn, 1);
                    soundPoolIdPlay.put(idOn, idSource);
                }
                break;
            case OFF:
                idSource = soundPoolIdPlay.get(idOff);
                if (idSource == null) {
                    idSource = soundPool.load(context, idOff, 1);
                    soundPoolIdPlay.put(idOff, idSource);
                }
                break;
            case XOAY_DEN:
                idSource = soundPoolIdPlay.get(idXoayden);
                if (idSource == null) {
                    idSource = soundPool.load(context, idXoayden, 1);
                    soundPoolIdPlay.put(idXoayden, idSource);
                }
                break;
//            case AudioManager.FX_KEY_CLICK:
//                playEffect(soundPoolIdPlay.get(idStandardSound),volume);
//                break;
            default:
                idSource = -1;
                break;

        }
        int finalIdSource = idSource;
        if (finalIdSource == -1) {
            return;
        }
        float finalVolume = volume > 1 ? volume / 100f : volume;
        soundPool.pause(finalIdSource);
        soundPool.play(finalIdSource, finalVolume, finalVolume, 1, 0, 1.0f);
    }

//    private void playEffect(Integer integer, float volume) {
//        Integer id = soundPoolIdPlay.get(integer);
//        if (id == null) {
//
//        }
//        Observable.interval(20, TimeUnit.MILLISECONDS).map(new Function<Long, Integer>() {
//            @Override
//            public Integer apply(Long aLong) throws Exception {
//                return soundPool.play(integer, volume, volume, 1, 0, 1.0f);
//            }
//        }).subscribeOn(Schedulers.io()).takeWhile(integer1 -> integer1 == 0).subscribe();
//        int id= soundPool.play(integer,volume,volume,1,0,1.0f);
//        Log.e(TAG,integer+"");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//               if(id==0){
//                   try {
//                       Thread.sleep(10);
//                       run();
//                   } catch (InterruptedException e) {
//                       e.printStackTrace();
//                   }
//               }
//
//            }
//        }).start();
//    }

    public void loadDataToSoundPool(SoundPool soundPool, Context context) {
        soundPoolIdPlay.put(idOn, soundPool.load(context, idOn, 1));
        soundPoolIdPlay.put(idOff, soundPool.load(context, idOff, 1));
        soundPoolIdPlay.put(idXoayden, soundPool.load(context, idXoayden, 1));
    }
}
