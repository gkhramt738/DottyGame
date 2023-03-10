package com.zybooks.dotty;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import java.util.ArrayList;

public class SoundEffects {

    private static SoundEffects mSoundEffects;

    private SoundPool mSoundPool;
    private final ArrayList<Integer> mSelectSoundIds;
    private int mSoundIndex;
    private final int mEndGameSoundId;

    private SoundEffects(Context context) {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        mSelectSoundIds = new ArrayList<>();
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_e, 1));
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_f, 1));
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_f_sharp, 1));
        mSelectSoundIds.add(mSoundPool.load(context, R.raw.note_g, 1));

        mEndGameSoundId = mSoundPool.load(context, R.raw.game_over, 1);

        resetTones();
    }

    public static SoundEffects getInstance(Context context) {
        if (mSoundEffects == null) {
            mSoundEffects = new SoundEffects(context);
        }
        return mSoundEffects;
    }

    public void resetTones() {
        mSoundIndex = -1;
    }

    public void playTone(boolean advance) {
        if (advance) {
            mSoundIndex++;
        } else {
            mSoundIndex--;
        }

        if (mSoundIndex < 0) {
            mSoundIndex = 0;
        }
        else if (mSoundIndex >= mSelectSoundIds.size()) {
            mSoundIndex = 0;
        }

        mSoundPool.play(mSelectSoundIds.get(mSoundIndex), 1, 1, 1, 0, 1);
    }

    public void playGameOver() {
        mSoundPool.play(mEndGameSoundId, 0.5f, 0.5f, 1, 0, 1);
    }

    public void release() {
        mSoundPool.release();
        mSoundPool = null;
    }
}