package com.swiftythesloth.splash;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Contains several methods common to all activities
 */
public abstract class BaseActivity extends FragmentActivity {

    public enum AppStart {
        FIRST_TIME,
        FIRST_TIME_VERSION,
        NORMAL
    }

    /**
     * Puts screen into immersive mode,
     * i.e. removes top and bottom bars
     */
    protected void makeScreenImmersive() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * Perform Haptic feedback on the provided view
     *
     * @param v View to feed back to
     */
    protected void vibrateView(View v) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.SHARED_PREFS_VIBRATION, true)) {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    /**
     * Checks if sounds are enabled.
     * If so, selected sound is played by given player
     *
     * @param sound  Sound to play
     * @param player SoundPoolPlayer to play sound
     */
    protected void playSound(Constants.Sounds sound, SoundPoolPlayer player) {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.SHARED_PREFS_SOUND, true)) {
            switch (sound) {
                case TILE_CLICK:
                    player.playShortResource(R.raw.sound_tile_click);
                    break;
                case TIME_TICK:
                    player.playShortResource(R.raw.sound_tick);
                    break;
                case GAME_OPTIONS:
                    player.playShortResource(R.raw.sound_game_options);
                    break;
                case BOMB:
                    player.playShortResource(R.raw.sound_bomb);
                    break;
                case NEW_GAME:
                    player.playShortResource(R.raw.sound_new_game);
                    break;
                case BURST:
                    player.playShortResource(R.raw.sound_burst);
                    break;
                case INVALID_MOVE:
                    player.playShortResource(R.raw.sound_invalid_move);
                    break;
                case UNDO:
                    player.playShortResource(R.raw.sound_undo);
                    break;
                case GAME_FINISHED:
                    player.playShortResource(R.raw.sound_game_finished);
                    break;
                case NONE:
                default:
                    return;
            }
        }
    }

    /**
     * Add seperators to number string.
     * i.e. 1000000 becomes 1,000,000
     *
     * @param value Number to convert
     * @return Formatted value String
     */
    protected String addCommasToValue(int value) {
        String result = "";
        String digits = Integer.toString(value);
        int len = digits.length();
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--) {
            result = digits.charAt(i) + result;
            nDigits++;
            if (((nDigits % 3) == 0) && (i > 0)) {
                result = "," + result;
            }
        }
        return result;
    }

    protected ArrayList<Integer> getColours(String themeName) {
        ArrayList<Integer> tempColours = new ArrayList<>();

        switch (themeName) {
            case Constants.THEME_NORMAL:
                tempColours.add(0xFFC2185B);        //pink
                tempColours.add(0xFFC62828);        //red
                tempColours.add(0xFF7B1FA2);        //purple
                tempColours.add(0xFF1976D2);        //blue
                tempColours.add(0xFF388E3C);        //green
                tempColours.add(0xFFFBC02D);        //yellow
                tempColours.add(0xFFFF7043);        //deep orange
                tempColours.add(0xFF5D4037);        //brown
                tempColours.add(0xFF009688);        //teal
                tempColours.add(0xFFCDDC39);        //lime
                break;
            case Constants.THEME_AUTUMN:
                tempColours.add(0xFF755330);        //brown
                tempColours.add(0xFFDBA72E);        //yellow
                tempColours.add(0xFF8B2B1E);        //red
                tempColours.add(0xFFDF2F00);        //bright red
                tempColours.add(0xFF455C4F);        //dirty green
                tempColours.add(0xFF6E352C);        //burgundy
                tempColours.add(0xFF6E612F);        //olive
                tempColours.add(0xFFEDB579);        //beige
                tempColours.add(0xFFEB712F);        //orange
                tempColours.add(0xFF9E9D24);        //lime
                break;
            case Constants.THEME_RETRO:
                tempColours.add(0xFF666547);        //olive
                tempColours.add(0xFFFB2E01);        //red
                tempColours.add(0xFF6FCB9F);        //green
                tempColours.add(0xFFFFFEB3);        //yellow
                tempColours.add(0xFFFF6F69);        //pink
                tempColours.add(0xFFFFCC5C);        //bold yellow
                tempColours.add(0xFF7DF9FF);        //light blue
                tempColours.add(0xFF0892D0);        //dark blue
                tempColours.add(0xFF5F6070);        //purple grey
                tempColours.add(0xFF21909E);        //teal
                break;
            case Constants.THEME_SUMMER:
                tempColours.add(0xFFF72408);        //bright red
                tempColours.add(0xFFFBC02D);        //bright yellow
                tempColours.add(0xFFFF5722);        //bold orange
                tempColours.add(0xFF0AA451);        //green
                tempColours.add(0xFF00BFAF);        //blue
                tempColours.add(0xFF94CE14);        //bright green
                tempColours.add(0xFFE1F5C4);        //pale green
                tempColours.add(0xFFFF598F);        //hot pink
                tempColours.add(0xFF61C9FE);        //arctic blue
                tempColours.add(0xFFFD8A5E);        //bright orange
                break;
            case Constants.THEME_WINTER:
                tempColours.add(0xFFFFC2E0);        //pale pink
                tempColours.add(0xFFD3D5D5);        //pale grey
                tempColours.add(0xFFE3F6F3);        //pale green
                tempColours.add(0xFFC4EDE4);        //green
                tempColours.add(0xFFADD8C7);        //darker green
                tempColours.add(0xFF8AC7DE);        //blue
                tempColours.add(0xFFBDCDD0);        //grey
                tempColours.add(0xFFD7D7B8);        //beige
                tempColours.add(0xFF8BA6AC);        //darker grey
                tempColours.add(0xFFD0D2E3);        //light purple
                break;
            case Constants.THEME_ROYAL:
                tempColours.add(0xFF6A0050);        //deep purple
                tempColours.add(0xFF817A8A);        //pale purple
                tempColours.add(0xFF723100);        //brown
                tempColours.add(0xFFFFBA11);        //rich yellow
                tempColours.add(0xFFFF8118);        //orange
                tempColours.add(0xFFD60000);        //red
                tempColours.add(0xFF1C2244);        //dark blue
                tempColours.add(0xFF3E4E55);        //dark grey
                tempColours.add(0xFFAC8F55);        //tan
                tempColours.add(0xFF204D02);        //green
                break;
        }

        return tempColours;
    }

    protected void applyTheme(Context context, boolean isLightsOn) {
        if (isLightsOn) {
            context.setTheme(R.style.AppTheme);
        } else {
            context.setTheme(R.style.AppTheme_Dark);
        }
    }

    protected int getResIdFromAttribute(final int attr) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

    protected void changeBackgroundTheme(View v, boolean isLightsOn) {
        if (isLightsOn) {
            v.setBackgroundColor(getResources().getColor(R.color.md_grey_300));
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.md_black_1000));
        }
    }

    protected void changeTextTheme(TextView v, boolean isLightsOn) {
        if (isLightsOn) {
            v.setTextColor(getResources().getColor(R.color.md_grey_800));
        } else {
            v.setTextColor(getResources().getColor(R.color.md_white_1000));
        }
    }

    protected void animateExit(final ArrayList<View> views, int duration) {
        final Handler handler = new Handler();

        for (int i = 0; i < views.size(); i++) {
            final int j = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    views.get(j).startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
                }
            }, i * duration);
        }
    }

    protected void animateEntry(final ArrayList<View> views, int duration) {
        final Handler handler = new Handler();

        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < views.size(); i++) {
            final int j = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    views.get(j).startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in_from_pin));
                    views.get(j).setVisibility(View.VISIBLE);
                }
            }, i * duration);
        }

    }

    protected ArrayList<View> addAllViewsToList(ViewGroup viewGroup, ArrayList<View> views) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                addAllViewsToList((ViewGroup) viewGroup.getChildAt(i), views);
            } else {
                views.add(viewGroup.getChildAt(i));
            }
        }
        return views;
    }
}
