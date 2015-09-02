package com.swiftythesloth.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sam on 08/08/2015.
 */
public class SettingsActivity extends BaseActivity {

    private final int ANIMATION_TIME_ENTRY = 75;
    private final int ANIMATION_TIME_EXIT = 10;

    private SoundPoolPlayer mSoundPlayer;
    private SharedPreferences mPrefs;

    private TextView mSoundSwitch;
    private TextView mVibrationSwitch;
    private TextView mLightSwitch;
    private TextView mColourBlindSwitch;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSoundPlayer = new SoundPoolPlayer(this);

        mSoundSwitch = (TextView) findViewById(R.id.settings_sound_switch);
        mVibrationSwitch = (TextView) findViewById(R.id.settings_vibration_switch);
        mLightSwitch = (TextView) findViewById(R.id.settings_light_switch);
        mColourBlindSwitch = (TextView) findViewById(R.id.settings_colour_blind_switch);

        if (!mPrefs.getBoolean(Constants.SHARED_PREFS_SOUND, true)) {
            mSoundSwitch.setText(getResources().getString(R.string.settings_off));
        }
        if (!mPrefs.getBoolean(Constants.SHARED_PREFS_VIBRATION, true)) {
            mVibrationSwitch.setText(getResources().getString(R.string.settings_off));
        }
        if (!mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false)) {
            mLightSwitch.setText(getResources().getString(R.string.settings_off));
        }
        if (!mPrefs.getBoolean(Constants.SHARED_PREFS_COLOUR_BLIND, false)) {
            mColourBlindSwitch.setText(getResources().getString(R.string.settings_off));
        }

        mSoundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapSound(v);
            }
        });

        mVibrationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapVibration(v);
            }
        });

        mLightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLights(v);
            }
        });

        mColourBlindSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapColourBlind(v);
            }
        });

        LinearLayout container = (LinearLayout) findViewById(R.id.settings_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        ImageView back = (ImageView) findViewById(R.id.settings_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                onBackPressed();
            }
        });

        mAnimatableViews = addAllViewsToList(container, mAnimatableViews);
        animateEntry(mAnimatableViews, ANIMATION_TIME_ENTRY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeScreenImmersive();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSoundPlayer != null) {
            mSoundPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getBaseContext(), MainMenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);
    }

    public void swapSound(View v) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_SOUND, true)) {
            ((TextView) v).setText(getResources().getString(R.string.settings_off));
            editor.putBoolean(Constants.SHARED_PREFS_SOUND, false);
        } else {
            ((TextView) v).setText(getResources().getString(R.string.settings_on));
            editor.putBoolean(Constants.SHARED_PREFS_SOUND, true);
        }

        editor.apply();
        vibrateView(v);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
    }

    public void swapVibration(View v) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_VIBRATION, true)) {
            ((TextView) v).setText(getResources().getString(R.string.settings_off));
            editor.putBoolean(Constants.SHARED_PREFS_VIBRATION, false);
        } else {
            ((TextView) v).setText(getResources().getString(R.string.settings_on));
            editor.putBoolean(Constants.SHARED_PREFS_VIBRATION, true);
        }

        editor.apply();
        vibrateView(v);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
    }

    public void swapLights(View v) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false)) {
            ((TextView) v).setText(getResources().getString(R.string.settings_off));
            setTheme(R.style.AppTheme_Dark);
            editor.putBoolean(Constants.SHARED_PREFS_LIGHTS, false);
        } else {
            ((TextView) v).setText(getResources().getString(R.string.settings_on));
            setTheme(R.style.AppTheme);
            editor.putBoolean(Constants.SHARED_PREFS_LIGHTS, true);
        }

        editor.apply();
        vibrateView(v);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
        changeTheme();
    }

    public void swapColourBlind(View v) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_COLOUR_BLIND, false)) {
            ((TextView) v).setText(getResources().getString(R.string.settings_off));
            editor.putBoolean(Constants.SHARED_PREFS_COLOUR_BLIND, false);
        } else {
            ((TextView) v).setText(getResources().getString(R.string.settings_on));
            editor.putBoolean(Constants.SHARED_PREFS_COLOUR_BLIND, true);
        }

        editor.apply();
        vibrateView(v);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
    }

    private void changeTheme() {
        LinearLayout container = (LinearLayout) findViewById(R.id.settings_container);

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false)) {
            changeBackgroundTheme(container, true);
            changeTextTheme(mSoundSwitch, true);
            changeTextTheme(mVibrationSwitch, true);
            changeTextTheme(mLightSwitch, true);
            changeTextTheme(mColourBlindSwitch, true);
        } else {
            changeBackgroundTheme(container, false);
            changeTextTheme(mSoundSwitch, false);
            changeTextTheme(mVibrationSwitch, false);
            changeTextTheme(mLightSwitch, false);
            changeTextTheme(mColourBlindSwitch, false);
        }
    }

}

