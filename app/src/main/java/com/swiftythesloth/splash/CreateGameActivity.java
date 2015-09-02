package com.swiftythesloth.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sam on 06/08/2015.
 */
public class CreateGameActivity extends BaseActivity {

    private final int ANIMATION_TIME_ENTRY = 50;
    private final int ANIMATION_TIME_EXIT = 10;

    private SoundPoolPlayer mSoundPlayer;

    private SeekBar mGridDimensions;
    private SeekBar mBurstWidth;
    private SeekBar mQueuedTiles;
    private SeekBar mBombRadius;
    private SeekBar mWallDensity;

    private TextView mGridDimensionLabel;
    private TextView mBurstWidthLabel;
    private TextView mQueuedTilesLabel;
    private TextView mBombRadiusLabel;
    private TextView mWallDensityLabel;
    private TextView mStartEndless;
    private TextView mStartMoves;
    private TextView mStartTimeAttack;

    private final int MIN_VALUE_GRID_DIMENSIONS = 6;
    private final int MIN_VALUE_BURST_WIDTH = 4;
    private final int MIN_VALUE_QUEUED_TILES = 1;
    private final int MIN_VALUE_BOMB_RADIUS = 1;
    private final int MIN_VALUE_WALL_DENSITY = 0;

    private ArrayList<View> mAnimatableViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, prefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mSoundPlayer = new SoundPoolPlayer(this);

        mStartEndless = (TextView) findViewById(R.id.create_start_endless);
        mStartMoves = (TextView) findViewById(R.id.create_start_moves);
        mStartTimeAttack = (TextView) findViewById(R.id.create_start_time_attack);

        mGridDimensions = (SeekBar) findViewById(R.id.create_grid_dimensions);
        mGridDimensionLabel = (TextView) findViewById(R.id.create_grid_dimensions_label);
        mGridDimensionLabel.setText(Integer.toString(
                MIN_VALUE_GRID_DIMENSIONS + mGridDimensions.getProgress()));

        mBurstWidth = (SeekBar) findViewById(R.id.create_burst_width);
        mBurstWidthLabel = (TextView) findViewById(R.id.create_burst_width_label);
        mBurstWidthLabel.setText(Integer.toString(
                MIN_VALUE_BURST_WIDTH + mBurstWidth.getProgress()));

        mQueuedTiles = (SeekBar) findViewById(R.id.create_queued_tiles);
        mQueuedTilesLabel = (TextView) findViewById(R.id.create_queued_tiles_label);
        mQueuedTilesLabel.setText(Integer.toString(
                MIN_VALUE_QUEUED_TILES + mQueuedTiles.getProgress()));

        mBombRadius = (SeekBar) findViewById(R.id.create_bomb_radius);
        mBombRadiusLabel = (TextView) findViewById(R.id.create_bomb_radius_label);
        mBombRadiusLabel.setText(Integer.toString(
                MIN_VALUE_BOMB_RADIUS + mBombRadius.getProgress()));

        mWallDensity = (SeekBar) findViewById(R.id.create_wall_density);
        mWallDensityLabel = (TextView) findViewById(R.id.create_wall_density_label);
        mWallDensityLabel.setText(Integer.toString(
                MIN_VALUE_WALL_DENSITY + mWallDensity.getProgress()) + "%");

        mGridDimensions.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGridDimensionLabel.setText(Integer.toString(MIN_VALUE_GRID_DIMENSIONS + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBurstWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBurstWidthLabel.setText(Integer.toString(MIN_VALUE_BURST_WIDTH + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mQueuedTiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mQueuedTilesLabel.setText(Integer.toString(MIN_VALUE_QUEUED_TILES + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBombRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBombRadiusLabel.setText(Integer.toString(MIN_VALUE_BOMB_RADIUS + progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mWallDensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWallDensityLabel.setText(Integer.toString(MIN_VALUE_WALL_DENSITY + progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mStartEndless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameGrid.GameType.ENDLESS, mStartEndless);
            }
        });

        mStartMoves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameGrid.GameType.MOVES, mStartMoves);
            }
        });

        mStartTimeAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(GameGrid.GameType.TIME_ATTACK, mStartTimeAttack);
            }
        });

        LinearLayout container = (LinearLayout) findViewById(R.id.create_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        ImageView back = (ImageView) findViewById(R.id.create_back);
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
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

    private void startGame(final GameGrid.GameType gameType, View v) {
        vibrateView(v);
        playSound(Constants.Sounds.NEW_GAME, mSoundPlayer);

        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), GameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Constants.BUNDLE_DIMENSIONS_KEY,
                        Integer.parseInt(mGridDimensionLabel.getText().toString()));
                intent.putExtra(Constants.BUNDLE_BURST_WIDTH_KEY,
                        Integer.parseInt(mBurstWidthLabel.getText().toString()));
                intent.putExtra(Constants.BUNDLE_QUEUED_TILES_KEY,
                        Integer.parseInt(mQueuedTilesLabel.getText().toString()));
                intent.putExtra(Constants.BUNDLE_BOMB_RADIUS_KEY,
                        Integer.parseInt(mBombRadiusLabel.getText().toString()));
                intent.putExtra(Constants.BUNDLE_WALL_DENSITY_KEY,
                        Integer.parseInt(mWallDensityLabel.getText().toString().replace("%","")));
                intent.putExtra(Constants.BUNDLE_IS_CREATE_GAME_KEY, true);
                intent.putExtra(Constants.BUNDLE_IS_NEW_GAME_KEY, true);
                intent.putExtra(Constants.BUNDLE_GAMETYPE_KEY, gameType);
                startActivity(intent);
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

}
