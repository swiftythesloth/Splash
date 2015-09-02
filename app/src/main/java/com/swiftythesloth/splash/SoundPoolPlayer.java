package com.swiftythesloth.splash;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

/**
 * Class used to play a given sound file from the raw folder
 */
public class SoundPoolPlayer {
    private static final String TAG = SoundPoolPlayer.class.getSimpleName();
    private SoundPool mShortPlayer = null;
    private HashMap mSounds = new HashMap();

    /**
     * Constructor for {@link SoundPoolPlayer}, initialises sound pool and list of available sounds
     * @param context       Context to use
     */
    public SoundPoolPlayer(Context context) {
        mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

        mSounds.put(R.raw.sound_new_game, this.mShortPlayer.load(context, R.raw.sound_new_game, 1));
        mSounds.put(R.raw.sound_bomb, this.mShortPlayer.load(context, R.raw.sound_bomb, 1));
        mSounds.put(R.raw.sound_tile_click, this.mShortPlayer.load(context, R.raw.sound_tile_click, 1));
        mSounds.put(R.raw.sound_game_options, this.mShortPlayer.load(context, R.raw.sound_game_options, 1));
        mSounds.put(R.raw.sound_burst, this.mShortPlayer.load(context, R.raw.sound_burst, 1));
        mSounds.put(R.raw.sound_invalid_move, this.mShortPlayer.load(context, R.raw.sound_invalid_move, 1));
        mSounds.put(R.raw.sound_game_finished, this.mShortPlayer.load(context, R.raw.sound_game_finished, 1));
        mSounds.put(R.raw.sound_tick, this.mShortPlayer.load(context, R.raw.sound_tick, 1));
        mSounds.put(R.raw.sound_undo, this.mShortPlayer.load(context, R.raw.sound_undo, 1));
    }

    /**
     * Plays a sound resource, based on id
     * @param resource      Index of {@link SoundPoolPlayer#mSounds} to find file
     */
    public void playShortResource(int resource) {
        try {
            int soundId = (Integer) mSounds.get(resource);
            mShortPlayer.play(soundId, 0.99f, 0.99f, 0, 0, 1);
        } catch (NullPointerException e) {
            Log.e(TAG, "Sound resource not found");
        }
    }

    /**
     * Release the {@link SoundPoolPlayer} from memory
     */
    public void release() {
        mShortPlayer.release();
        mShortPlayer = null;
    }
}