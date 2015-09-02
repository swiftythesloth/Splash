package com.swiftythesloth.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends BaseActivity {

    private static final String INTENT_FLAG_RESET = "reset";

    private final int ANIMATION_TIME_ENTRY = 10;
    private final int ANIMATION_TIME_EXIT = 5;

    private final int COINS_UNDER_10000_POINTS = 800;
    private final int COINS_UNDER_50000_POINTS = 1500;
    private final int COINS_UNDER_100000_POINTS = 2500;
    private final int COINS_UNDER_500000_POINTS = 4500;
    private final int COINS_UNDER_1000000_POINTS = 8000;
    private final int COINS_UNDER_5000000_POINTS = 15000;
    private final int COINS_UNDER_10000000_POINTS = 35000;
    private final int COINS_OVER_10000000_POINTS = 75000;

    private final int DEFAULT_DIMENSIONS = 8;
    private final int DEFAULT_BURST_WIDTH = 5;
    private final int DEFAULT_QUEUED_TILES = 6;
    private final int DEFAULT_BOMB_RADIUS = 2;
    private final int DEFAULT_WALL_DENSITY = 18;

    private int mDimensions;
    private int mBurstWidth;
    private int mQueuedTiles;
    private int mBombRadius;
    private int mWallDensity;

    private final int NUM_MOVES_WHILE_TIME_STOPPED = 5;

    private SharedPreferences mPrefs;

    private GameGrid mGameGrid;

    private Stack<GameGrid> mUndoGrids = new Stack<>();

    private SoundPoolPlayer mSoundPlayer;

    private LinearLayout mGameContainer;
    private LinearLayout mDestroyerContainer;
    private LinearLayout mStopTimeContainer;
    private GridLayout mPlayingGridLayout;
    private GridLayout mQueuedGridLayout;
    private TextView mMovesLeftTextView;
    private TextView mScoreTextView;
    private TextView mTimeTextView;
    private TextView mUndoCountView;
    private TextView mDestroyerCountView;
    private TextView mStopTimeCountView;
    private TextView mPowerupExplanation;
    private ImageView mStopTime;

    private boolean mIsFinished = false;
    private boolean mIsPaused = false;
    private boolean mInDestroyerMode = false;
    private boolean mIsTimeStopped = false;

    private int mMainGridLengthOfTile;
    private int mQueuedGridLengthOfTile;
    private int mMovesMadeWhileStopped;
    private ViewGroup.LayoutParams mMainGridLayoutParams;
    private ViewGroup.LayoutParams mQueuedGridLayoutParams;
    private boolean mIsCreate;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();
    private LinearLayout mStatsContainer;
    private LinearLayout mOptionsContainer;
    private LinearLayout mPowerupsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_ADS_REMOVED, false)) {
            setContentView(R.layout.activity_game);
        } else {
            setContentView(R.layout.activity_game_ads);
            AdView adView = (AdView) findViewById(R.id.adView_bottom_game);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("484DCB495CD9242EB34F27DD68CE59D6")
                    .build();
            adView.loadAd(adRequest);
            mAnimatableViews.add(adView);
        }

        mSoundPlayer = new SoundPoolPlayer(this);

        mGameContainer = (LinearLayout) findViewById(R.id.game_container);
        mDestroyerContainer = (LinearLayout) findViewById(R.id.game_destroyer_frame);
        mStopTimeContainer = (LinearLayout) findViewById(R.id.game_stop_time_frame);
        mPlayingGridLayout = (GridLayout) findViewById(R.id.game_active_grid);
        mQueuedGridLayout = (GridLayout) findViewById(R.id.game_queued_grid);
        mMovesLeftTextView = (TextView) findViewById(R.id.game_moves_left);
        mScoreTextView = (TextView) findViewById(R.id.game_score);
        mTimeTextView = (TextView) findViewById(R.id.game_time);
        mUndoCountView = (TextView) findViewById(R.id.game_undo_count);
        mDestroyerCountView = (TextView) findViewById(R.id.game_destroyer_count);
        mStopTimeCountView = (TextView) findViewById(R.id.game_stop_time_count);
        mPowerupExplanation = (TextView) findViewById(R.id.game_powerup_explanation);
        mStopTime = (ImageView) findViewById(R.id.game_options_stop_time);

        if (getIntent().getExtras().getBoolean(INTENT_FLAG_RESET)) {
            createGrid(false);
        } else {
            if (getIntent().getExtras().getBoolean(Constants.BUNDLE_IS_NEW_GAME_KEY, false)) {
                createGrid(false);
            } else {
                createGrid(true);
            }
        }

        saveGame();
        updatePowerupCountViews();

        mMovesLeftTextView.setText(getResources().getString(R.string.game_default_moves_string)
                + Integer.toString(mGameGrid.getMovesLeft()));
        mScoreTextView.setText(getResources().getString(R.string.game_default_score_string)
                + addCommasToValue(mGameGrid.getScore()));
        mTimeTextView.setText("0:0" + Integer.toString((int) mGameGrid.getCurrentTime() / 1000));

        mGameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        if (!mGameGrid.getGameType().equals(GameGrid.GameType.MOVES)) {
            mMovesLeftTextView.setVisibility(View.GONE);
        }

        if (mGameGrid.getGameType().equals(GameGrid.GameType.TIME_ATTACK)) {
            final Timer redrawTimer = new Timer();

            redrawTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!mIsPaused && !mIsTimeStopped) {
                        playSound(Constants.Sounds.TIME_TICK, mSoundPlayer);
                        mGameGrid.timeElapsed();
                        if (mGameGrid.isRandomMoveMade()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playSound(mGameGrid.getSoundNeeded(), mSoundPlayer);
                                    mGameGrid.setSoundNeeded(Constants.Sounds.NONE);
                                    mIsFinished = mGameGrid.isGameFinished();
                                    redrawGrid();
                                    if (mIsFinished) {
                                        mIsTimeStopped = true;
                                        updateCoinTotal();
                                        gameOver();
                                    }
                                    mGameGrid.setRandomMoveMade(false);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeTextView.setText("0:0" + Integer.toString(
                                        (int) mGameGrid.getCurrentTime() / 1000));
                                if (mGameGrid.getCurrentTime() == 0) {
                                    mTimeTextView.startAnimation(AnimationUtils.loadAnimation(
                                            getBaseContext(), R.anim.flash_out));
                                }
                            }
                        });
                    }
                }
            }, 1000, 1000);
        } else {
            mTimeTextView.setVisibility(View.GONE);
            mStopTimeCountView.setVisibility(View.GONE);
            mStopTime.setVisibility(View.GONE);
        }

        ImageView sound = (ImageView) findViewById(R.id.game_options_sound);
        ImageView vibration = (ImageView) findViewById(R.id.game_options_vibration);

        if (!mPrefs.getBoolean(Constants.SHARED_PREFS_SOUND, true)) {
            sound.setImageDrawable(getResources().getDrawable(R.drawable.ic_sound_off));
        }
        if (!mPrefs.getBoolean(Constants.SHARED_PREFS_VIBRATION, true)) {
            vibration.setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration_off));
        }

        mOptionsContainer = (LinearLayout) findViewById(R.id.game_options_container);
        mStatsContainer = (LinearLayout) findViewById(R.id.game_stats_container);
        mPowerupsContainer = (LinearLayout) findViewById(R.id.game_powerups_container);

        mAnimatableViews = addAllViewsToList(mOptionsContainer, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mQueuedGridLayout, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mPlayingGridLayout, mAnimatableViews);
        mAnimatableViews.remove(mPowerupExplanation);

        mStatsContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in_from_pin));
        mPowerupsContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in_from_pin));
        animateEntry(mAnimatableViews, ANIMATION_TIME_ENTRY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
        saveGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;
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
        mIsTimeStopped = true;
        saveGame();
        mStatsContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        mPowerupsContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        mPowerupExplanation.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        mAnimatableViews.clear();
        mAnimatableViews = addAllViewsToList(mOptionsContainer, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mQueuedGridLayout, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mPlayingGridLayout, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mPowerupsContainer, mAnimatableViews);
        mAnimatableViews.remove(mPowerupExplanation);
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getBaseContext(), MainMenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

    /**
     * Called the first time the grid is drawn to the screen,
     * draws every {@link GameTile}
     */
    private void createGrid(boolean checkForSave) {
        GameGrid.GameType gameType = (GameGrid.GameType)
                getIntent().getExtras().getSerializable(Constants.BUNDLE_GAMETYPE_KEY);
        mIsCreate = getIntent().getExtras().getBoolean(Constants.BUNDLE_IS_CREATE_GAME_KEY);

        if (mIsCreate) {
            mDimensions = getIntent().getExtras().getInt(Constants.BUNDLE_DIMENSIONS_KEY);
            mBurstWidth = getIntent().getExtras().getInt(Constants.BUNDLE_BURST_WIDTH_KEY);
            mQueuedTiles = getIntent().getExtras().getInt(Constants.BUNDLE_QUEUED_TILES_KEY);
            mBombRadius = getIntent().getExtras().getInt(Constants.BUNDLE_BOMB_RADIUS_KEY);
            mWallDensity = getIntent().getExtras().getInt(Constants.BUNDLE_WALL_DENSITY_KEY);
        } else {
            mDimensions = DEFAULT_DIMENSIONS;
            mBurstWidth = DEFAULT_BURST_WIDTH;
            mQueuedTiles = DEFAULT_QUEUED_TILES;
            mBombRadius = DEFAULT_BOMB_RADIUS;
            mWallDensity = DEFAULT_WALL_DENSITY;
        }

        String themeName = mPrefs.getString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_NORMAL);

        //Resumed a game from main menu
        if (checkForSave) {
            Gson gson = new Gson();
            String gridJson;

            if (mIsCreate) {
                gridJson = mPrefs.getString(
                        Constants.getSharedPrefsSaveString(GameGrid.GameType.CREATE), "");
            } else {
                gridJson = mPrefs.getString(
                        Constants.getSharedPrefsSaveString(gameType), "");
            }

            if (!gridJson.equals("")) {
                mGameGrid = gson.fromJson(gridJson, GameGrid.class);
                mDimensions = mGameGrid.getDimensions();
                mBurstWidth = mGameGrid.getBurstWidth();
                mQueuedTiles = mGameGrid.getQueuedTiles().size();
                mBombRadius = mGameGrid.getBombRadius();
                mWallDensity = mGameGrid.getWallDensity();
            } else {
                mGameGrid = new GameGrid(
                        gameType, mDimensions, mBurstWidth, mQueuedTiles, mBombRadius, mWallDensity, getColours(themeName));
            }
        } else {
            mGameGrid = new GameGrid(
                    gameType, mDimensions, mBurstWidth, mQueuedTiles, mBombRadius, mWallDensity, getColours(themeName));
        }

        mMainGridLengthOfTile = ((int) getResources()
                .getDimension(R.dimen.game_grid_size) / mDimensions);
        mQueuedGridLengthOfTile = (int) getResources().getDimension(R.dimen.game_queue_grid_tile);

        mMainGridLayoutParams = new ViewGroup.LayoutParams(mMainGridLengthOfTile, mMainGridLengthOfTile);
        mQueuedGridLayoutParams = new ViewGroup.LayoutParams(mQueuedGridLengthOfTile, mQueuedGridLengthOfTile);

        mPlayingGridLayout.removeAllViews();
        mPlayingGridLayout.setRowCount(mDimensions);
        mPlayingGridLayout.setColumnCount(mDimensions);

        mQueuedGridLayout.removeAllViews();
        mQueuedGridLayout.setRowCount(1);
        mQueuedGridLayout.setColumnCount(mQueuedTiles);

        for (int i = 0; i < mDimensions; i++) {
            for (int j = 0; j < mDimensions; j++) {
                GameTile tile = mGameGrid.getPlayingTiles().get(i * mDimensions + j);
                TextView tileView = createTileView(mMainGridLayoutParams, tile);

                tileView.setOnClickListener(new OnTileClickedListener(tile));

                mPlayingGridLayout.addView(tileView);
            }
        }

        for (int i = 0; i < mQueuedTiles; i++) {
            GameTile tile = mGameGrid.getQueuedTiles().get(i);
            TextView tileView = createTileView(mQueuedGridLayoutParams, tile);

            mQueuedGridLayout.addView(tileView);
        }

        mScoreTextView.setText(getResources().getString(R.string.game_default_score_string)
                + addCommasToValue(mGameGrid.getScore()));
        mMovesLeftTextView.setText(getResources().getString(R.string.game_default_moves_string)
                + Integer.toString(mGameGrid.getMovesLeft()));
    }

    /**
     * Use GSON to save GameGrid to SharedPreferences
     */
    private void saveGame() {
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String jsonGrid;

        try {
            jsonGrid = gson.toJson(mGameGrid);
        } catch (ConcurrentModificationException e) {
            return;         //don't bother saving
        }

        //Don't want to save won grid
        if (mGameGrid.isGameFinished()) {
            jsonGrid = "";
        }

        if (mIsCreate) {
            editor.putString(
                    Constants.getSharedPrefsSaveString(GameGrid.GameType.CREATE), jsonGrid);
        } else {
            editor.putString(
                    Constants.getSharedPrefsSaveString(mGameGrid.getGameType()), jsonGrid);
        }
        editor.apply();
    }

    /**
     * Called after every move, only redraws those {@link GameTile} which
     * have been marked as 'dirty'.
     */
    private void redrawGrid() {
        boolean isChanged = false;

        if (mIsFinished) {
            for (GameTile tile : mGameGrid.getPlayingTiles()) {
                redrawTile(tile);
            }
        } else {
            //Shuffle to affect popping animation
            Collections.shuffle(mGameGrid.getDirtyTiles());
            for (GameTile tile : mGameGrid.getDirtyTiles()) {
                redrawTile(tile);
                isChanged = true;
            }
        }

        mGameGrid.getDirtyTiles().clear();

        mQueuedGridLayout.removeAllViews();

        for (int i = 0; i < mGameGrid.getQueuedTiles().size(); i++) {
            GameTile tile = mGameGrid.getQueuedTiles().get(i);
            TextView tileView = createTileView(mQueuedGridLayoutParams, tile);

            //Animate incoming tile
            if (i == mQueuedTiles - 1 && isChanged) {
                tileView.startAnimation(
                        AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_right));
            }
            mQueuedGridLayout.addView(tileView);
        }

        mScoreTextView.setText(getResources().getString(R.string.game_default_score_string)
                + addCommasToValue(mGameGrid.getScore()));
        mMovesLeftTextView.setText(getResources().getString(R.string.game_default_moves_string)
                + Integer.toString(mGameGrid.getMovesLeft()));
    }

    /**
     * Redraw a given {@link GameTile} to the screen
     *
     * @param tile {@link GameTile}
     */
    private void redrawTile(final GameTile tile) {
        final TextView tileView = createTileView(mMainGridLayoutParams, tile);

        mPlayingGridLayout.removeViewAt(tile.getRowPosition() * mDimensions + tile.getColPosition());
        mPlayingGridLayout.addView(tileView, tile.getRowPosition() * mDimensions + tile.getColPosition());

        if (!mIsFinished && !tile.getTileType().equals(GameTile.TileType.WALL)) {
            tileView.setOnClickListener(new OnTileClickedListener(tile));
        }

        final Handler handler = new Handler();

        if (tile.isJustBurst()) {
            int delay;
            if (mGameGrid.getDirtyTiles().size() < 5) {
                delay = 100;
            } else if (mGameGrid.getDirtyTiles().size() < 15) {
                delay = 50;
            } else {
                delay = 25;
            }
            tile.setJustBurst(false);
            //Using a Handler to animate in a snakelike way
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tileView.startAnimation(
                            AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce_out));
                }
            }, (mGameGrid.getDirtyTiles().indexOf(tile)) * delay);
        } else if (tile.isJustLaid()) {
            tileView.startAnimation(getLaidAnimation());
            tile.setIsJustLaid(false);
        }

    }

    private Animation getLaidAnimation() {
        switch (mPrefs.getString(Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_NORMAL)) {
            case Constants.ANIMATION_RAIN:
                return AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_top);
            case Constants.ANIMATION_BOUNCE:
                return AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce);
            case Constants.ANIMATION_NORMAL:
            default:
                return AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_grow_fade_in_from_bottom);
        }
    }

    /**
     * Create the TextView which represents a game square,
     * and make it invisible if it is the empty tile.
     *
     * @param layoutParams LayoutParams for the square
     * @param tile         {@link GameTile} current tile to draw
     * @return the created TextView
     */
    private TextView createTileView(
            ViewGroup.LayoutParams layoutParams, GameTile tile) {

        TextView textView = new TextView(this);

        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);

        if (mGameGrid.getDimensions() < 8) {
            textView.setTextSize(12);
        } else if (mGameGrid.getDimensions() < 12) {
            textView.setTextSize(9);
        } else {
            textView.setTextSize(6);
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_COLOUR_BLIND, false)) {
            textView.setText(Integer.toString(tile.getNumber()));
        } else {
            textView.setText("");
        }

        if (tile.getTileType().equals(GameTile.TileType.NORMAL)) {
            getCorrectGridShape(textView);
            if (tile.isFilled()) {
                ((GradientDrawable) textView.getBackground()).setColor(tile.getColour());
            } else {
                ((GradientDrawable) textView.getBackground()).setColor(GameGrid.DEFAULT_COLOUR);
            }
        } else if (tile.getTileType().equals(GameTile.TileType.BOMB)) {
            getCorrectBombShape(textView);
            textView.setText("B");
        } else if (tile.getTileType().equals(GameTile.TileType.WALL)) {
            textView.setBackgroundResource(getResIdFromAttribute(R.attr.wall));
            textView.setText("");
        }

        return textView;
    }

    private void getCorrectGridShape(TextView textView) {
        switch (mPrefs.getString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL)) {
            case Constants.GRID_SHAPE_NORMAL:
                textView.setBackgroundResource(R.drawable.game_square_normal);
                break;
            case Constants.GRID_SHAPE_CIRCLE:
                textView.setBackgroundResource(R.drawable.game_square_circle);
                break;
            case Constants.GRID_SHAPE_SHARP:
                textView.setBackgroundResource(R.drawable.game_square_sharp);
                break;
        }
    }

    private void getCorrectBombShape(TextView textView) {
        switch (mPrefs.getString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL)) {
            case Constants.GRID_SHAPE_NORMAL:
                textView.setBackgroundResource(R.drawable.game_square_normal_bomb);
                break;
            case Constants.GRID_SHAPE_CIRCLE:
                textView.setBackgroundResource(R.drawable.game_square_bomb_circle);
                break;
            case Constants.GRID_SHAPE_SHARP:
                textView.setBackgroundResource(R.drawable.game_square_sharp_bomb);
                break;
        }
    }

    private void updateCoinTotal() {
        int coinsEarned = getCoinsEarned();
        int coinsInBank = mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0);
        int newValue = coinsInBank + coinsEarned;

        mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, newValue).apply();
        mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + coinsEarned).apply();
    }

    private int getCoinsEarned() {
        final float ENDLESS_COIN_MODIFIER = 0.7f;
        final float MOVES_COIN_MODIFIER = 1.75f;
        final float TIME_ATTACK_COIN_MODIFIER = 1.4f;
        final float CREATE_MODIFIER = 0.5f;
        float currentModifier;
        int coinsEarned = 0;
        int score = mGameGrid.getScore();

        switch (mGameGrid.getGameType()) {
            case ENDLESS:
                currentModifier = ENDLESS_COIN_MODIFIER;
                break;
            case MOVES:
                currentModifier = MOVES_COIN_MODIFIER;
                break;
            case TIME_ATTACK:
                currentModifier = TIME_ATTACK_COIN_MODIFIER;
                break;
            default:
                currentModifier = 1f;
        }

        if (mIsCreate) {
            currentModifier = CREATE_MODIFIER;
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_COIN_DOUBLER, false)) {
            currentModifier *= 2;
        }

        if (score < 5000) {
            coinsEarned = 0;
        } else if (score >= 5000 && score < 10000) {
            coinsEarned = (int) (COINS_UNDER_10000_POINTS * currentModifier);
        } else if (score < 50000) {
            coinsEarned = (int) (COINS_UNDER_50000_POINTS * currentModifier);
        } else if (score < 100000) {
            coinsEarned = (int) (COINS_UNDER_100000_POINTS * currentModifier);
        } else if (score < 500000) {
            coinsEarned = (int) (COINS_UNDER_500000_POINTS * currentModifier);
        } else if (score < 1000000) {
            coinsEarned = (int) (COINS_UNDER_1000000_POINTS * currentModifier);
        } else if (score < 5000000) {
            coinsEarned = (int) (COINS_UNDER_5000000_POINTS * currentModifier);
        } else if (score < 10000000) {
            coinsEarned = (int) (COINS_UNDER_10000000_POINTS * currentModifier);
        } else if (score > 10000000) {
            coinsEarned = (int) (COINS_OVER_10000000_POINTS * currentModifier);
        }

        return coinsEarned;
    }

    private void gameOver() {
        SharedPreferences.Editor editor = mPrefs.edit();
        int totalGameCount = mPrefs.getInt(Constants.SHARED_PREFS_GAMES_COMPLETED, 0);
        int endlessGameCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_ENDLESS_GAMES_PLAYED, 0);
        int movesGameCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_MOVES_GAMES_PLAYED, 0);
        int timeAttackGameCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_TIME_ATTACK_GAMES_PLAYED, 0);
        int createGameCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_CREATE_GAMES_PLAYED, 0);
        int prevBestScore = mPrefs.getInt(Constants.SHARED_PREFS_BEST_SCORE, 0);
        int prevBestBurst = mPrefs.getInt(Constants.SHARED_PREFS_BEST_BURST, 0);

        playSound(Constants.Sounds.GAME_FINISHED, mSoundPlayer);

        mPlayingGridLayout.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_right_stay));
        mQueuedGridLayout.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_right_stay));

        editor.putInt(Constants.SHARED_PREFS_GAMES_COMPLETED, totalGameCount + 1);

        if (mIsCreate) {
            editor.putInt(Constants.SHARED_PREFS_NUMBER_CREATE_GAMES_PLAYED, createGameCount + 1);
        } else {
            switch (mGameGrid.getGameType()) {
                case ENDLESS:
                    editor.putInt(Constants.SHARED_PREFS_NUMBER_ENDLESS_GAMES_PLAYED, endlessGameCount + 1);
                    break;
                case MOVES:
                    editor.putInt(Constants.SHARED_PREFS_NUMBER_MOVES_GAMES_PLAYED, movesGameCount + 1);
                    break;
                case TIME_ATTACK:
                    editor.putInt(Constants.SHARED_PREFS_NUMBER_TIME_ATTACK_GAMES_PLAYED, timeAttackGameCount + 1);
                    break;
            }
        }
        if (mGameGrid.getScore() > prevBestScore) {
            editor.putInt(Constants.SHARED_PREFS_BEST_SCORE, mGameGrid.getScore());
        }

        if (mGameGrid.getBestBurst() > prevBestBurst) {
            editor.putInt(Constants.SHARED_PREFS_BEST_BURST, mGameGrid.getBestBurst());
        }

        editor.putInt(Constants.SHARED_PREFS_TILES_BURST,
                mPrefs.getInt(Constants.SHARED_PREFS_TILES_BURST, 0) + mGameGrid.getNumberTilesBurst());
        editor.putInt(Constants.SHARED_PREFS_TILES_BOMBED,
                mPrefs.getInt(Constants.SHARED_PREFS_TILES_BOMBED, 0) + mGameGrid.getNumberTilesBombed());
        editor.putInt(Constants.SHARED_PREFS_TILES_DESTROYED,
                mPrefs.getInt(Constants.SHARED_PREFS_TILES_DESTROYED, 0) + mGameGrid.getNumberTilesDestroyed());

        editor.apply();

        updatePreviousThreeScores();

        final Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
        Gson gson = new Gson();
        String jsonGrid;

        try {
            jsonGrid = gson.toJson(mGameGrid);
        } catch (ConcurrentModificationException e) {
            return;         //don't bother saving
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Constants.BUNDLE_GAME_OVER_GRID, jsonGrid);
        intent.putExtra(Constants.BUNDLE_GAME_OVER_COINS_EARNED, getCoinsEarned());

        saveGame();


        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(intent);
            }
        }, 500);
    }

    private void updatePreviousThreeScores() {
        int prev1 = mPrefs.getInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_1, 0);
        int prev2 = mPrefs.getInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_2, 0);

        mPrefs.edit().putInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_3, prev2).apply();
        mPrefs.edit().putInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_2, prev1).apply();
        mPrefs.edit().putInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_1, mGameGrid.getScore()).apply();
    }

    public void goBack(View v) {
        vibrateView(v);
        v.setEnabled(false);
        ImageView newGame = (ImageView) findViewById(R.id.game_new);
        newGame.setEnabled(false);          //caused crash when quickly pressing new game after back
        v.setEnabled(false);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        onBackPressed();
    }

    /**
     * Restart game
     *
     * @param v View from which the method was called
     */
    public void newGame(View v) {
        vibrateView(v);
        v.setEnabled(false);
        playSound(Constants.Sounds.NEW_GAME, mSoundPlayer);
        mStatsContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        mPowerupsContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        mPowerupExplanation.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        mAnimatableViews.clear();
        mAnimatableViews = addAllViewsToList(mOptionsContainer, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mQueuedGridLayout, mAnimatableViews);
        mAnimatableViews = addAllViewsToList(mPlayingGridLayout, mAnimatableViews);
        mAnimatableViews.remove(mPowerupExplanation);
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                if (mIsCreate) {
                    intent.putExtra(Constants.BUNDLE_IS_CREATE_GAME_KEY, true);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Constants.BUNDLE_DIMENSIONS_KEY, mGameGrid.getDimensions());
                intent.putExtra(Constants.BUNDLE_BURST_WIDTH_KEY, mGameGrid.getBurstWidth());
                intent.putExtra(Constants.BUNDLE_QUEUED_TILES_KEY, mGameGrid.getQueuedTiles().size());
                intent.putExtra(Constants.BUNDLE_BOMB_RADIUS_KEY, mGameGrid.getBombRadius());
                intent.putExtra(Constants.BUNDLE_WALL_DENSITY_KEY, mGameGrid.getWallDensity());
                intent.putExtra(Constants.BUNDLE_GAMETYPE_KEY, mGameGrid.getGameType());
                intent.putExtra(INTENT_FLAG_RESET, true);
                finish();
                startActivity(intent);
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

    /**
     * Perform an undo if a move has been previously performed
     *
     * @param v View which called the method
     */
    public void undoMove(View v) {
        final int undoCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_UNDO, 5);

        if (mIsFinished) {
            return;
        }
        vibrateView(v);
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (undoCount > 0) {
                    if (mUndoGrids.size() != 0) {
                        playSound(Constants.Sounds.UNDO, mSoundPlayer);
                        mQueuedGridLayout.getChildAt(mQueuedGridLayout.getChildCount() - 1).startAnimation(
                                AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out_right));

                        mGameGrid = new GameGrid(mUndoGrids.pop());
                        saveGame();
                        createGrid(true);

                        mPrefs.edit().putInt(Constants.SHARED_PREFS_NUMBERS_UNDO, undoCount - 1).apply();
                        incrementPowerupStats(Constants.PowerupType.UNDO);
                    } else {
                        playSound(Constants.Sounds.INVALID_MOVE, mSoundPlayer);
                    }
                    updatePowerupCountViews();
                } else {
                    playSound(Constants.Sounds.INVALID_MOVE, mSoundPlayer);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);
    }

    public void toggleDestroyer(View v) {
        int destroyerCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, 3);

        if (mIsFinished) {
            return;
        }

        if (destroyerCount > 0) {
            if (mInDestroyerMode) {
                mInDestroyerMode = false;
                popInPowerupView(mDestroyerContainer);
            } else {
                mInDestroyerMode = true;
                popOutPowerupView(mDestroyerContainer);
            }
        } else {
            vibrateView(v);
            v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
            playSound(Constants.Sounds.INVALID_MOVE, mSoundPlayer);
        }
    }

    public void stopTime(View v) {
        int stopTimeCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_STOP_TIME, 3);

        if (mIsFinished) {
            return;
        }

        if (stopTimeCount > 0) {
            if (!mIsTimeStopped && !mInDestroyerMode) {
                mIsTimeStopped = true;
                incrementPowerupStats(Constants.PowerupType.STOP_TIME);
                mMovesMadeWhileStopped = NUM_MOVES_WHILE_TIME_STOPPED;
                popOutPowerupView(mStopTimeContainer);
                mPrefs.edit().putInt(Constants.SHARED_PREFS_NUMBERS_STOP_TIME, stopTimeCount - 1).apply();
                updatePowerupCountViews();
            } else if (mIsTimeStopped) {
                mIsTimeStopped = false;
                popInPowerupView(mStopTimeContainer);
            }
        } else {
            vibrateView(v);
            v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
            playSound(Constants.Sounds.INVALID_MOVE, mSoundPlayer);
        }
    }

    private void popInPowerupView(View v) {
        vibrateView(v);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.pop_in_stay));
        if (mInDestroyerMode) {
            mPowerupExplanation.setText(getResources().getString(R.string.game_destroyer_explanation));
        } else if (mIsTimeStopped) {
            mPowerupExplanation.setText(getResources().getString(R.string.game_stop_time_explanation));
        } else {
            mPowerupExplanation.setVisibility(View.INVISIBLE);
        }
    }

    private void popOutPowerupView(View v) {
        vibrateView(v);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.pop_out_stay));
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        mPowerupExplanation.setVisibility(View.VISIBLE);
        if (mInDestroyerMode) {
            mPowerupExplanation.setText(getResources().getString(R.string.game_destroyer_explanation));
        } else if (mIsTimeStopped) {
            mPowerupExplanation.setText(getResources().getString(R.string.game_stop_time_explanation));
        }
    }

    private void timeStoppedMoveMade() {
        mMovesMadeWhileStopped--;

        if (mMovesMadeWhileStopped <= 0) {
            mIsTimeStopped = false;
            popInPowerupView(mStopTimeContainer);
        }

    }

    public void toggleSound(View v) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_SOUND, true)) {
            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_sound_off));
            editor.putBoolean(Constants.SHARED_PREFS_SOUND, false);
        } else {
            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_sound_on));
            editor.putBoolean(Constants.SHARED_PREFS_SOUND, true);
        }

        editor.apply();
        vibrateView(v);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
    }

    public void toggleVibration(View v) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_VIBRATION, true)) {
            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration_off));
            editor.putBoolean(Constants.SHARED_PREFS_VIBRATION, false);
        } else {
            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration_on));
            editor.putBoolean(Constants.SHARED_PREFS_VIBRATION, true);
        }

        editor.apply();
        vibrateView(v);
        playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
    }

    private void updatePowerupCountViews() {
        int undoCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_UNDO, 5);
        int destroyerCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, 5);
        int stopTimeCount = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_STOP_TIME, 5);

        mUndoCountView.setText(addCommasToValue(undoCount));
        mDestroyerCountView.setText(addCommasToValue(destroyerCount));
        mStopTimeCountView.setText(addCommasToValue(stopTimeCount));
    }

    private void incrementPowerupStats(Constants.PowerupType powerupType) {
        mPrefs.edit().putInt(Constants.SHARED_PREFS_POWERUPS_USED,
                mPrefs.getInt(Constants.SHARED_PREFS_POWERUPS_USED, 0) + 1).apply();
        switch (powerupType) {
            case UNDO:
                mPrefs.edit().putInt(Constants.SHARED_PREFS_UNDOS_USED,
                        mPrefs.getInt(Constants.SHARED_PREFS_UNDOS_USED, 0) + 1).apply();
                break;
            case DESTROYER:
                mPrefs.edit().putInt(Constants.SHARED_PREFS_DESTROYERS_USED,
                        mPrefs.getInt(Constants.SHARED_PREFS_DESTROYERS_USED, 0) + 1).apply();
                break;
            case STOP_TIME:
                mPrefs.edit().putInt(Constants.SHARED_PREFS_STOP_TIMES_USED,
                        mPrefs.getInt(Constants.SHARED_PREFS_STOP_TIMES_USED, 0) + 1).apply();
                break;
            default:
                return;
        }
    }

    /**
     * Listener for when a {@link GameTile} is clicked.
     * Performs a move validity check and swpas tiles.
     */
    private class OnTileClickedListener implements View.OnClickListener {
        private GameTile mGameTile;

        public OnTileClickedListener(GameTile tile) {
            mGameTile = tile;
        }

        @Override
        public void onClick(View v) {
            if (mInDestroyerMode) {
                if (mGameGrid.activateDestroyer(mGameTile)) {
                    vibrateView(v);
                    mUndoGrids.push(new GameGrid(mGameGrid));
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, 3) - 1).apply();
                    incrementPowerupStats(Constants.PowerupType.DESTROYER);
                    updatePowerupCountViews();
                    mInDestroyerMode = false;
                    popInPowerupView(mDestroyerContainer);
                    if (mIsTimeStopped) {
                        timeStoppedMoveMade();
                    }
                    redrawGrid();
                }
            } else {
                if (!mGameTile.isFilled()
                        || mGameGrid.isBurstable(mGameTile)
                        || mGameGrid.getQueuedTiles().get(0).getTileType().equals(GameTile.TileType.BOMB)) {
                    vibrateView(v);
                    if (mIsTimeStopped) {
                        timeStoppedMoveMade();
                    }
                    mUndoGrids.push(new GameGrid(mGameGrid));
                    mGameGrid.tileClicked(mGameTile);
                }

                redrawGrid();

                if (mGameGrid.isGameFinished()) {
                    mIsFinished = true;
                    mIsTimeStopped = true;
                    updateCoinTotal();
                    gameOver();
                }
            }

            playSound(mGameGrid.getSoundNeeded(), mSoundPlayer);
            mGameGrid.setSoundNeeded(Constants.Sounds.NONE);
        }
    }
}
