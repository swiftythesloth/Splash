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

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Shows all stats collated for current install
 */
public class GameOverActivity extends BaseActivity {

    private final int ANIMATION_TIME_EXIT = 2;

    private SharedPreferences mPrefs;
    private SoundPoolPlayer mSoundPlayer;

    private GridLayout mGrid;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();
    private GameGrid mGameGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        setContentView(R.layout.activity_game_over);

        mSoundPlayer = new SoundPoolPlayer(this);

        mGrid = (GridLayout) findViewById(R.id.game_over_grid);

        Gson gson = new Gson();
        String gridJson;

        gridJson = getIntent().getExtras().getString(Constants.BUNDLE_GAME_OVER_GRID, "");

        if (!gridJson.equals("")) {
            mGameGrid = gson.fromJson(gridJson, GameGrid.class);
        } else {
            mGameGrid = new GameGrid(GameGrid.GameType.ENDLESS, 8, 1, 1, 1, 15, getColours(Constants.THEME_NORMAL));
        }

        createGrid();

        TextView score = (TextView) findViewById(R.id.game_over_score);
        TextView coinsEarned = (TextView) findViewById(R.id.game_over_coins_earned);
        TextView coinsInBank = (TextView) findViewById(R.id.game_over_coins_in_bank);
        TextView store = (TextView) findViewById(R.id.game_over_store);
        ImageView storeIcon = (ImageView) findViewById(R.id.game_over_store_icon);

        store.setOnClickListener(new View.OnClickListener() {
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

        storeIcon.setOnClickListener(new View.OnClickListener() {
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

        score.setText(addCommasToValue(mGameGrid.getScore()));
        coinsEarned.setText(addCommasToValue(getIntent().getExtras().getInt(Constants.BUNDLE_GAME_OVER_COINS_EARNED, 0)));
        coinsInBank.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0)));

        LinearLayout container = (LinearLayout) findViewById(R.id.game_over_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        ImageView back = (ImageView) findViewById(R.id.game_over_back);
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

    private void createGrid() {

        int dimensions = mGameGrid.getDimensions();
        int length = ((int) getResources().getDimension(R.dimen.game_over_grid_size) / dimensions);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(length, length);

        mGrid.setColumnCount(dimensions);
        mGrid.setRowCount(dimensions);

        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++) {
                GameTile tile = mGameGrid.getPlayingTiles().get(i * dimensions + j);
                TextView tileView = createTileView(layoutParams, tile);
                mGrid.addView(tileView);
            }
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
            textView.setTextSize(10);
        } else if (mGameGrid.getDimensions() < 12) {
            textView.setTextSize(7);
        } else {
            textView.setTextSize(4);
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_COLOUR_BLIND, false)) {
            textView.setText(Integer.toString(tile.getNumber()));
        } else {
            textView.setText("");
        }

        getCorrectFinishedShape(textView);
        ((GradientDrawable) textView.getBackground()).setColor(tile.getColour());

        if (tile.getTileType().equals(GameTile.TileType.WALL)) {
            textView.setBackgroundResource(getResIdFromAttribute(R.attr.wall));
            textView.setText("");
        }

        return textView;
    }

    private void getCorrectFinishedShape(TextView textView) {
        switch (mPrefs.getString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL)) {
            case Constants.GRID_SHAPE_NORMAL:
                textView.setBackgroundResource(R.drawable.game_square_normal_finished);
                break;
            case Constants.GRID_SHAPE_CIRCLE:
                textView.setBackgroundResource(R.drawable.game_square_circle_finished);
                break;
            case Constants.GRID_SHAPE_SHARP:
                textView.setBackgroundResource(R.drawable.game_square_sharp_finished);
                break;
        }
    }
}
