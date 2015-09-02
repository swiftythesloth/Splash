package com.swiftythesloth.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sam on 04/08/2015.
 */
public class MainMenuActivity extends BaseActivity {

    private static final String LOG_TAG = MainMenuActivity.class.getSimpleName();
    private static final int NUMBER_RUNS_BEFORE_ASK_RATING = 20;

    private final int ANIMATION_TIME_ENTRY = 40;
    private final int ANIMATION_TIME_EXIT = 10;

    private SoundPoolPlayer mSoundPlayer;

    private TextView mStartNewEndless;
    private TextView mStartNewMoves;
    private TextView mStartNewTimeAttack;
    private TextView mStartNewCreate;

    private TextView mResumeEndless;
    private TextView mResumeMoves;
    private TextView mResumeTimeAttack;
    private TextView mResumeCreate;
    private LinearLayout mContainer;
    private SharedPreferences mPrefs;
    private ImageView mOpenStore;
    private ImageView mOpenSettings;
    private ImageView mOpenInstructions;
    private ImageView mOpenStats;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mSoundPlayer = new SoundPoolPlayer(this);

        //Check type if app start:
        //                        -first ever
        //                        -first time new version
        //                        -normal
        switch (checkAppStart()) {
            case NORMAL:
                break;
            case FIRST_TIME:
                animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), InstructionsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
                break;
            case FIRST_TIME_VERSION:
                //might do something with this later
                break;
        }

        mStartNewEndless = (TextView) findViewById(R.id.main_menu_new_endless);
        mStartNewMoves = (TextView) findViewById(R.id.main_menu_new_moves);
        mStartNewTimeAttack = (TextView) findViewById(R.id.main_menu_new_time_attack);
        mStartNewCreate = (TextView) findViewById(R.id.main_menu_new_create);

        mResumeEndless = (TextView) findViewById(R.id.main_menu_resume_endless);
        mResumeMoves = (TextView) findViewById(R.id.main_menu_resume_moves);
        mResumeTimeAttack = (TextView) findViewById(R.id.main_menu_resume_time_attack);
        mResumeCreate = (TextView) findViewById(R.id.main_menu_resume_create);

        checkResumeEnabledState();

        mStartNewEndless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(GameGrid.GameType.ENDLESS, v);
            }
        });

        mStartNewMoves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(GameGrid.GameType.MOVES, v);
            }
        });

        mStartNewTimeAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(GameGrid.GameType.TIME_ATTACK, v);
            }
        });

        mStartNewCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(GameGrid.GameType.CREATE, v);
            }
        });

        mResumeEndless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame(GameGrid.GameType.ENDLESS, v);
            }
        });

        mResumeMoves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame(GameGrid.GameType.MOVES, v);
            }
        });

        mResumeTimeAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame(GameGrid.GameType.TIME_ATTACK, v);
            }
        });

        mResumeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame(GameGrid.GameType.CREATE, v);
            }
        });

        mOpenStats = (ImageView) findViewById(R.id.main_menu_stats);
        mOpenStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), StatsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);
            }
        });

        mOpenStore = (ImageView) findViewById(R.id.main_menu_store);
        mOpenStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), StoreActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);
            }
        });

        mOpenInstructions = (ImageView) findViewById(R.id.main_menu_instructions);
        mOpenInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), InstructionsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);
            }
        });

        mOpenSettings = (ImageView) findViewById(R.id.main_menu_settings);
        mOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getBaseContext(), SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    }
                }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);
            }
        });

        mContainer = (LinearLayout) findViewById(R.id.main_menu_container);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        //Increase the number of runs stored
        mPrefs.edit().putInt(
                Constants.SHARED_PREFS_NUMBER_RUNS, mPrefs.getInt(
                        Constants.SHARED_PREFS_NUMBER_RUNS, 0) + 1).apply();

        if (mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_RUNS, 0) == NUMBER_RUNS_BEFORE_ASK_RATING) {
            GenericFragment ratingFragment = new GenericFragment();
            Bundle ratingArgs = new Bundle();
            ratingArgs.putSerializable(
                    Constants.DIALOG_FRAGMENT_TYPE, Constants.DialogFragmentType.RATING);
            ratingFragment.setArguments(ratingArgs);
            ratingFragment.show(getSupportFragmentManager(), null);
        }

        mAnimatableViews = addAllViewsToList(mContainer, mAnimatableViews);
        animateEntry(mAnimatableViews, ANIMATION_TIME_ENTRY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeBackgroundTheme(mContainer, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));
        makeScreenImmersive();
        checkResumeEnabledState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSoundPlayer != null) {
            mSoundPlayer.release();
        }
    }

    private AppStart checkAppStart() {
        PackageInfo pInfo;
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        AppStart appStart = AppStart.NORMAL;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersionCode = prefs.getInt(Constants.SHARED_PREFS_LAST_APP_VERSION, -1);
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);
            prefs.edit().putInt(Constants.SHARED_PREFS_LAST_APP_VERSION, currentVersionCode).commit();
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(LOG_TAG, "Unable to determine current app version");
        }

        return appStart;
    }

    private AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }

    private void startNewGame(final GameGrid.GameType gameType, View v) {
        vibrateView(v);
        v.setEnabled(false);
        playSound(Constants.Sounds.NEW_GAME, mSoundPlayer);
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameType.equals(GameGrid.GameType.CREATE)) {
                    Intent intent = new Intent(getBaseContext(), CreateGameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra(Constants.BUNDLE_IS_NEW_GAME_KEY, true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getBaseContext(), GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra(Constants.BUNDLE_GAMETYPE_KEY, gameType);
                    intent.putExtra(Constants.BUNDLE_IS_NEW_GAME_KEY, true);
                    startActivity(intent);
                }
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);

    }

    private void resumeGame(final GameGrid.GameType gameType, View v) {
        v.setEnabled(false);
        vibrateView(v);
        playSound(Constants.Sounds.NEW_GAME, mSoundPlayer);
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameType.equals(GameGrid.GameType.CREATE)) {
                    Intent intent = new Intent(getBaseContext(), GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra(Constants.BUNDLE_GAMETYPE_KEY, GameGrid.GameType.CREATE);
                    intent.putExtra(Constants.BUNDLE_IS_NEW_GAME_KEY, false);
                    intent.putExtra(Constants.BUNDLE_IS_CREATE_GAME_KEY, true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getBaseContext(), GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra(Constants.BUNDLE_GAMETYPE_KEY, gameType);
                    intent.putExtra(Constants.BUNDLE_IS_NEW_GAME_KEY, false);
                    startActivity(intent);
                }
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) *  ANIMATION_TIME_EXIT);

    }

    private void checkResumeEnabledState() {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        final float FADED_ALPHA = 0.4f;

        if (prefs.getString(Constants.getSharedPrefsSaveString(GameGrid.GameType.ENDLESS), "").equals("")) {
            mResumeEndless.setAlpha(FADED_ALPHA);
            mResumeEndless.setEnabled(false);
        } else {
            mResumeEndless.setEnabled(true);
            mResumeEndless.setAlpha(1f);
        }

        if (prefs.getString(Constants.getSharedPrefsSaveString(GameGrid.GameType.MOVES), "").equals("")) {
            mResumeMoves.setEnabled(false);
            mResumeMoves.setAlpha(FADED_ALPHA);
        } else {
            mResumeMoves.setEnabled(true);
            mResumeMoves.setAlpha(1f);
        }

        if (prefs.getString(Constants.getSharedPrefsSaveString(GameGrid.GameType.TIME_ATTACK), "").equals("")) {
            mResumeTimeAttack.setEnabled(false);
            mResumeTimeAttack.setAlpha(FADED_ALPHA);
        } else {
            mResumeTimeAttack.setEnabled(true);
            mResumeTimeAttack.setAlpha(1f);
        }

        if (prefs.getString(Constants.getSharedPrefsSaveString(GameGrid.GameType.CREATE), "").equals("")) {
            mResumeCreate.setEnabled(false);
            mResumeCreate.setAlpha(FADED_ALPHA);
        } else {
            mResumeCreate.setEnabled(true);
            mResumeCreate.setAlpha(1f);
        }

    }

}
