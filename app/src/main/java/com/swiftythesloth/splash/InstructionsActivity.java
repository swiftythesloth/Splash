package com.swiftythesloth.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Sam on 14/08/2015.
 */
public class InstructionsActivity extends BaseActivity {

    private static final int NUM_PAGES = 4;

    private SharedPreferences mPrefs;
    private SoundPoolPlayer mSoundPlayer;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<InstructionsFragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        mSoundPlayer = new SoundPoolPlayer(getBaseContext());

        mPager = (ViewPager) findViewById(R.id.instructions_pager);
        mPagerAdapter = new InstructionsPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(0);
        mPager.setCurrentItem(0);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        LinearLayout container = (LinearLayout) findViewById(R.id.instructions_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        ImageView back = (ImageView) findViewById(R.id.instructions_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPlayer);
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        makeScreenImmersive();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), MainMenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    /**
     * Adapter for difficulty selector ViewPager.
     * Creates a new MainMenuSlideFragment dependind on
     * which page the user has swiped onto.
     */
    private class InstructionsPagerAdapter extends FragmentPagerAdapter {

        public InstructionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            InstructionsFragment instructionsSlideFragment = new InstructionsFragment();

            Bundle bundle = new Bundle();

            mFragments.add(instructionsSlideFragment);

            bundle.putInt(Constants.BUNDLE_INSTRUCTIONS_PAGE_NUMBER, i + 1);

            instructionsSlideFragment.setArguments(bundle);
            return instructionsSlideFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;

                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else {
                view.setAlpha(0);
            }
        }
    }
}
