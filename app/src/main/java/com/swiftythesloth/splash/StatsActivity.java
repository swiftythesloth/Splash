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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Shows all stats collated for current install
 */
public class StatsActivity extends BaseActivity {

    private final int ANIMATION_TIME_ENTRY = 30;
    private final int ANIMATION_TIME_EXIT = 10;

    private SharedPreferences mPrefs;
    private SoundPoolPlayer mSoundPlayer;
    private ArrayList<Integer> mColours;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();
    private TextView mGamesCompleted;
    private TextView mBestScore;
    private TextView mCoinsEarned;
    private TextView mCoinsSpent;
    private TextView mBestBurst;
    private TextView mFavouriteGame;
    private TextView mTimesPlayed;
    private GridLayout mBestBurstGrid;
    private TextView mPowerupsUsed;
    private ImageView mFavouritePowerup;
    private TextView mPlayerRanking;
    private TextView mPreviousBest1;
    private TextView mPreviousBest2;
    private TextView mPreviousBest3;
    private TextView mTilesBurst;
    private TextView mTilesBombed;
    private TextView mTilesDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mSoundPlayer = new SoundPoolPlayer(this);

        mGamesCompleted = (TextView) findViewById(R.id.stats_games_completed);
        mBestScore = (TextView) findViewById(R.id.stats_best_score);
        mCoinsEarned = (TextView) findViewById(R.id.stats_coins_earned);
        mCoinsSpent = (TextView) findViewById(R.id.stats_coins_spent);
        mBestBurst = (TextView) findViewById(R.id.stats_best_burst_count);
        mPlayerRanking = (TextView) findViewById(R.id.stats_player_ranking);
        mPreviousBest1 = (TextView) findViewById(R.id.stats_previous_best_1);
        mPreviousBest2 = (TextView) findViewById(R.id.stats_previous_best_2);
        mPreviousBest3 = (TextView) findViewById(R.id.stats_previous_best_3);
        mTilesBurst = (TextView) findViewById(R.id.stats_tiles_burst);
        mTilesBombed = (TextView) findViewById(R.id.stats_tiles_bombed);
        mTilesDestroyed = (TextView) findViewById(R.id.stats_tiles_destroyed);

        mColours = getColours(
                mPrefs.getString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_NORMAL));
        Collections.shuffle(mColours);

        mPlayerRanking.setText(getPlayerRanking());
        mGamesCompleted.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_GAMES_COMPLETED, 0)));
        mBestScore.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_BEST_SCORE, 0)));
        mCoinsEarned.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0)));
        mCoinsSpent.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0)
                - mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0)));
        mBestBurst.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_BEST_BURST, 0)));
        mPreviousBest1.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_1, 0)));
        mPreviousBest2.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_2, 0)));
        mPreviousBest3.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_PREVIOUS_SCORE_3, 0)));
        mTilesBurst.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_TILES_BURST, 0)));
        mTilesBombed.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_TILES_BOMBED, 0)));
        mTilesDestroyed.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_TILES_DESTROYED, 0)));

        getFavouriteGame();
        setupPowerupsStats();
        setupBestBurstGrid();

        ImageView back = (ImageView) findViewById(R.id.stats_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                onBackPressed();
            }
        });

        LinearLayout container = (LinearLayout) findViewById(R.id.stats_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
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
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

    private String getPlayerRanking() {
        final int GAMES_COMPLETED_SCORE_MODIFIER = 25;
        final int COINS_EARNED_SCORE_MODIFIER = 50000;
        int gamesCompleted = mPrefs.getInt(Constants.SHARED_PREFS_GAMES_COMPLETED, 0);
        int coinsEarned = mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0);
        int playerScore = 0;

        String playerRanking;

        playerScore += gamesCompleted / GAMES_COMPLETED_SCORE_MODIFIER;
        playerScore += coinsEarned / COINS_EARNED_SCORE_MODIFIER;

        if (playerScore < 2) {
            playerRanking = getResources().getString(R.string.player_ranking_rookie);
        } else if (playerScore < 4) {
            playerRanking = getResources().getString(R.string.player_ranking_amateur);
        } else if (playerScore < 12) {
            playerRanking = getResources().getString(R.string.player_ranking_professional);
        } else if (playerScore < 20) {
            playerRanking = getResources().getString(R.string.player_ranking_expert);
        } else {
            playerRanking = getResources().getString(R.string.player_ranking_legend);
        }

        String favouriteGame = getFavouriteGame();

        if (favouriteGame.equals(getResources().getString(R.string.gametype_endless))) {
            playerRanking += " " + getResources().getString(R.string.player_ranking_favourite_endless);
        } else if (favouriteGame.equals(getResources().getString(R.string.gametype_moves))) {
            playerRanking += " " + getResources().getString(R.string.player_ranking_favourite_moves);
        } else if (favouriteGame.equals(getResources().getString(R.string.gametype_time_attack))) {
            playerRanking += " " + getResources().getString(R.string.player_ranking_favourite_time_attack);
        } else {
            playerRanking += " " + getResources().getString(R.string.player_ranking_favourite_create);
        }

        return playerRanking;
    }

    private void setupBestBurstGrid() {
        mBestBurstGrid = (GridLayout) findViewById(R.id.stats_best_burst);
        int size = mPrefs.getInt(Constants.SHARED_PREFS_BEST_BURST, 0);
        int columnCount;

        if (size < 10) {
            columnCount = 3;
        } else if (size < 30) {
            columnCount = 5;
        } else {
            columnCount = 10;
        }

        int length = (int) getResources().getDimension(R.dimen.stats_best_burst_grid_size) / columnCount;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(length, length);

        mBestBurstGrid.setRowCount(size % columnCount);
        mBestBurstGrid.setColumnCount(columnCount);

        for (int i = 0; i < size; i++) {
            mBestBurstGrid.addView(createTileView(layoutParams));
        }

    }

    private void setupPowerupsStats() {
        mPowerupsUsed = (TextView) findViewById(R.id.stats_powerups_used);
        mFavouritePowerup = (ImageView) findViewById(R.id.stats_favourite_powerup);
        int destroyersUsed = mPrefs.getInt(Constants.SHARED_PREFS_DESTROYERS_USED, 0);
        int undosUsed = mPrefs.getInt(Constants.SHARED_PREFS_UNDOS_USED, 0);
        int stopTimesUsed = mPrefs.getInt(Constants.SHARED_PREFS_STOP_TIMES_USED, 0);
        int max = findMax(destroyersUsed, undosUsed, stopTimesUsed);

        mPowerupsUsed.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_POWERUPS_USED, 0)));

        if (max == destroyersUsed) {
            mFavouritePowerup.setImageDrawable(getResources().getDrawable(R.drawable.ic_destroyer));
        } else if (max == undosUsed) {
            mFavouritePowerup.setImageDrawable(getResources().getDrawable(R.drawable.ic_undo));
        } else if (max == stopTimesUsed) {
            mFavouritePowerup.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_time));
        } else {
            mFavouritePowerup.setImageDrawable(getResources().getDrawable(R.drawable.ic_destroyer));
        }

    }

    private TextView createTileView(ViewGroup.LayoutParams layoutParams) {
        TextView textView = new TextView(this);

        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(6);
        getCorrectGridShape(textView);

        ((GradientDrawable) textView.getBackground()).setColor(mColours.get(0));

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

    private String getFavouriteGame() {
        mFavouriteGame = (TextView) findViewById(R.id.stats_favourite_game);
        mTimesPlayed = (TextView) findViewById(R.id.stats_favourite_game_played_count);
        int endlessPlayed = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_ENDLESS_GAMES_PLAYED, 0);
        int movesPlayed = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_MOVES_GAMES_PLAYED, 0);
        int timeAttackPlayed = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_TIME_ATTACK_GAMES_PLAYED, 0);
        int createPlayed = mPrefs.getInt(Constants.SHARED_PREFS_NUMBER_CREATE_GAMES_PLAYED, 0);
        int max = findMax(endlessPlayed, movesPlayed, timeAttackPlayed, createPlayed);

        if (max == endlessPlayed) {
            mFavouriteGame.setText(getResources().getString(R.string.gametype_endless));
            mFavouriteGame.setBackground(getResources().getDrawable(R.drawable.main_menu_endless));
            mTimesPlayed.setText(addCommasToValue(endlessPlayed));
            return getResources().getString(R.string.gametype_endless);
        } else if (max == movesPlayed) {
            mFavouriteGame.setText(getResources().getString(R.string.gametype_moves));
            mFavouriteGame.setBackground(getResources().getDrawable(R.drawable.main_menu_moves));
            mTimesPlayed.setText(addCommasToValue(movesPlayed));
            return getResources().getString(R.string.gametype_moves);
        } else if (max == timeAttackPlayed) {
            mFavouriteGame.setText(getResources().getString(R.string.gametype_time_attack));
            mFavouriteGame.setBackground(getResources().getDrawable(R.drawable.main_menu_time_attack));
            mTimesPlayed.setText(addCommasToValue(timeAttackPlayed));
            return getResources().getString(R.string.gametype_time_attack);
        } else {
            mFavouriteGame.setText(getResources().getString(R.string.gametype_create));
            mFavouriteGame.setBackground(getResources().getDrawable(R.drawable.main_menu_create));
            mTimesPlayed.setText(addCommasToValue(createPlayed));
            return getResources().getString(R.string.gametype_create);
        }

    }

    private int findMax(int... values) {
        int max = Integer.MIN_VALUE;

        for (int i : values) {
            if (i > max) {
                max = i;
            }
        }

        return max;
    }

}
