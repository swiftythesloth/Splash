package com.swiftythesloth.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftythesloth.splash.util.IabHelper;
import com.swiftythesloth.splash.util.IabResult;
import com.swiftythesloth.splash.util.Inventory;
import com.swiftythesloth.splash.util.Purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Sam on 06/08/2015.
 */
public class StoreActivity extends BaseActivity implements PurchaseCoinsFragment.OnCoinsPurchasedListener {

    private SharedPreferences mPrefs;

    private SoundPoolPlayer mSoundPoolPlayer;

    IabHelper mHelper;

    private final String SKU_PURCHASE_10000 = "purchase_10000_coins";
    private final String SKU_PURCHASE_50000 = "purchase_50000_coins";
    private final String SKU_PURCHASE_100000 = "purchase_100000_coins";
    private final String SKU_PURCHASE_200000 = "purchase_200000_coins";
    private final String SKU_PURCHASE_500000 = "purchase_500000_coins";
    private final String SKU_PURCHASE_2500000 = "purchase_2500000_coins";

    private String mPrice10000;
    private String mPrice50000;
    private String mPrice100000;
    private String mPrice200000;
    private String mPrice500000;
    private String mPrice2500000;

    private final int ANIMATION_TIME_ENTRY = 10;
    private final int ANIMATION_TIME_EXIT = 5;

    private static final int NUMBER_POWERUPS_PER_PURCHASE = 5;

    private static final int COST_5_STOP_TIME = 7000;
    private static final int COST_5_UNDO = 9500;
    private static final int COST_5_DESTROYER = 10000;

    private static final int COST_GRID_SHAPE_SHARP = 40000;
    private static final int COST_GRID_SHAPE_CIRCLE = 150000;

    private static final int COST_ANIMATION_BOUNCE = 30000;
    private static final int COST_ANIMATION_RAIN = 125000;

    private static final int COST_THEME_RETRO = 45000;
    private static final int COST_THEME_AUTUMN = 55000;
    private static final int COST_THEME_SUMMER = 65000;
    private static final int COST_THEME_WINTER = 80000;
    private static final int COST_THEME_ROYAL = 250000;

    private static final int COST_REMOVE_ADS = 350000;
    private static final int COST_COIN_DOUBLER = 200000;

    private static final float FADED_VIEW_ALPHA = 0.2f;

    private static final String SELECT = "Select";
    private static final String PURCHASED = "Purchased";

    private static final String LOG_TAG = StoreActivity.class.getSimpleName();

    private LinearLayout mCoinContainer;

    private TextView mNumberUndo;
    private TextView mPurchaseUndo;

    private TextView mNumberDestroyer;
    private TextView mPurchaseDestroyer;

    private TextView mNumberStopTime;
    private TextView mPurchaseStopTime;

    private TextView mGridShapeCircle;
    private TextView mGridShapeSharp;
    private TextView mGridShapeNormal;

    private TextView mPurchaseShapeCircle;
    private TextView mPurchaseShapeSharp;
    private TextView mPurchaseShapeNormal;

    private TextView mAnimationNormal;
    private TextView mAnimationRaining;
    private TextView mAnimationBounce;

    private TextView mPurchaseAnimationNormal;
    private TextView mPurchaseAnimationRaining;
    private TextView mPurchaseAnimationBounce;

    private GridLayout mThemeNormalGrid;
    private TextView mPurchaseThemeNormal;

    private GridLayout mThemeAutumnGrid;
    private TextView mPurchaseThemeAutumn;

    private GridLayout mThemeRetroGrid;
    private TextView mPurchaseThemeRetro;

    private GridLayout mThemeSummerGrid;
    private TextView mPurchaseThemeSummer;

    private GridLayout mThemeMonochromeGrid;
    private TextView mPurchaseThemeMonochrome;

    private GridLayout mThemeRoyalGrid;
    private TextView mPurchaseThemeRoyal;

    private TextView mPurchaseRemoveAds;
    private TextView mPurchaseCoinDoubler;

    private ArrayList<GridLayout> mThemeGrids = new ArrayList<>();
    private ArrayList<TextView> mThemePurchases = new ArrayList<>();

    private ArrayList<View> mAnimatableViews = new ArrayList<>();
    private LinearLayout mContainer;
    private TextView mCoinTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String b64Key1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmwag0WpaQHrQWTJbbBQnkl327XSaF6HlGIDXdU5Zv3bxUBIl3UXdILEs99bp0X2qw3kJjglbQMv5u0D";
        String b64Key2 = "OIQRaWmwkkXMc+gp/bc6fq+Zgq24bFjyPu1DvpsIWJru7i0t73ZuM2ZqsztpbP7FBNqn4l3+tzC5LZpXlPKoYx9/EVdWcnOBuQfMynUH0s7CViCXI5MmH57x1sp";
        String b64Key3 = "L5AispDSuTSJYmncRIu1IxbR8UUco2o0NolLZ2A9YEO3t+3uQLmmQj/ERfIZU2lJZq9YAg4KIJE4oBDHfGDChQWB56Sm3p4g5f+HLyV+3k7pBqaiyJlhg3YMOEfPuiXwVYg4B9fTV7qwIDAQAB";

        mPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        applyTheme(this, mPrefs.getBoolean(Constants.SHARED_PREFS_LIGHTS, false));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        mSoundPoolPlayer = new SoundPoolPlayer(this);

        mHelper = new IabHelper(this, b64Key1 + b64Key2 + b64Key3);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(LOG_TAG, "IAB setup failed");
                } else {
                    Log.d(LOG_TAG, "IAB setup succeeded");
                    String[] skus = {SKU_PURCHASE_10000, SKU_PURCHASE_50000, SKU_PURCHASE_100000, SKU_PURCHASE_200000, SKU_PURCHASE_500000, SKU_PURCHASE_2500000};
                    mHelper.queryInventoryAsync(true, Arrays.asList(skus), mQueryInventoryFinishedListener);
                }
            }
        });

        mCoinContainer = (LinearLayout) findViewById(R.id.store_purchase_coins);
        mCoinTotal = (TextView) findViewById(R.id.store_number_coins);

        mCoinContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
                openPurchaseCoinsDialog();
            }
        });

        mNumberUndo = (TextView) findViewById(R.id.store_number_undo);
        mPurchaseUndo = (TextView) findViewById(R.id.store_purchase_undo);

        mNumberDestroyer = (TextView) findViewById(R.id.store_number_destroyer);
        mPurchaseDestroyer = (TextView) findViewById(R.id.store_purchase_destroyer);

        mNumberStopTime = (TextView) findViewById(R.id.store_number_stop_time);
        mPurchaseStopTime = (TextView) findViewById(R.id.store_purchase_stop_time);

        mPurchaseShapeCircle = (TextView) findViewById(R.id.store_purchase_grid_shape_circle);
        mPurchaseShapeSharp = (TextView) findViewById(R.id.store_purchase_grid_shape_angular_small);
        mPurchaseShapeNormal = (TextView) findViewById(R.id.store_purchase_grid_shape_normal);

        mGridShapeCircle = (TextView) findViewById(R.id.store_grid_shape_circle);
        mGridShapeSharp = (TextView) findViewById(R.id.store_grid_shape_angular);
        mGridShapeNormal = (TextView) findViewById(R.id.store_grid_shape_normal);

        mAnimationNormal = (TextView) findViewById(R.id.store_animation_normal);
        mAnimationRaining = (TextView) findViewById(R.id.store_animation_raining);
        mAnimationBounce = (TextView) findViewById(R.id.store_animation_bounce);

        mPurchaseAnimationNormal = (TextView) findViewById(R.id.store_purchase_animation_normal);
        mPurchaseAnimationRaining = (TextView) findViewById(R.id.store_purchase_animation_raining);
        mPurchaseAnimationBounce = (TextView) findViewById(R.id.store_purchase_animation_bounce);

        setupPowerupValues();

        mPurchaseUndo.setText(addCommasToValue(COST_5_UNDO));
        mPurchaseDestroyer.setText(addCommasToValue(COST_5_DESTROYER));
        mPurchaseStopTime.setText(addCommasToValue(COST_5_STOP_TIME));

        mPurchaseShapeCircle.setText(addCommasToValue(COST_GRID_SHAPE_CIRCLE));
        mPurchaseShapeSharp.setText(addCommasToValue(COST_GRID_SHAPE_SHARP));
        mPurchaseShapeNormal.setText(SELECT);

        mPurchaseAnimationNormal.setText(SELECT);
        mPurchaseAnimationRaining.setText(addCommasToValue(COST_ANIMATION_RAIN));
        mPurchaseAnimationBounce.setText(addCommasToValue(COST_ANIMATION_BOUNCE));

        mPurchaseUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchasePowerup(Constants.POWERUP_UNDO, v);
                setupPowerupValues();
            }
        });

        mPurchaseDestroyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchasePowerup(Constants.POWERUP_DESTROYER, v);
                setupPowerupValues();
            }
        });

        mPurchaseStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchasePowerup(Constants.POWERUP_STOP_TIME, v);
                setupPowerupValues();
            }
        });

        mGridShapeCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeSelected(Constants.GRID_SHAPE_CIRCLE, mGridShapeCircle);
            }
        });

        mGridShapeSharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeSelected(Constants.GRID_SHAPE_SHARP, mGridShapeSharp);
            }
        });

        mGridShapeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeSelected(Constants.GRID_SHAPE_NORMAL, mGridShapeNormal);
            }
        });

        mPurchaseShapeCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeSelected(Constants.GRID_SHAPE_CIRCLE, mPurchaseShapeCircle);
            }
        });

        mPurchaseShapeSharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeSelected(Constants.GRID_SHAPE_SHARP, mPurchaseShapeSharp);
            }
        });

        mPurchaseShapeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeSelected(Constants.GRID_SHAPE_NORMAL, mPurchaseShapeNormal);
            }
        });

        mThemeNormalGrid = (GridLayout) findViewById(R.id.store_theme_vanilla_grid);
        mPurchaseThemeNormal = (TextView) findViewById(R.id.store_purchase_theme_normal);
        mThemeAutumnGrid = (GridLayout) findViewById(R.id.store_theme_autumn_grid);
        mPurchaseThemeAutumn = (TextView) findViewById(R.id.store_purchase_theme_autumn);
        mThemeRetroGrid = (GridLayout) findViewById(R.id.store_theme_retro_grid);
        mPurchaseThemeRetro = (TextView) findViewById(R.id.store_purchase_theme_retro);
        mThemeSummerGrid = (GridLayout) findViewById(R.id.store_theme_summer_grid);
        mPurchaseThemeSummer = (TextView) findViewById(R.id.store_purchase_theme_summer);
        mThemeMonochromeGrid = (GridLayout) findViewById(R.id.store_theme_winter_grid);
        mPurchaseThemeMonochrome = (TextView) findViewById(R.id.store_purchase_theme_winter);
        mThemeRoyalGrid = (GridLayout) findViewById(R.id.store_theme_royal_grid);
        mPurchaseThemeRoyal = (TextView) findViewById(R.id.store_purchase_theme_royal);

        mThemeGrids.add(mThemeNormalGrid);
        mThemeGrids.add(mThemeRetroGrid);
        mThemeGrids.add(mThemeAutumnGrid);
        mThemeGrids.add(mThemeSummerGrid);
        mThemeGrids.add(mThemeMonochromeGrid);
        mThemeGrids.add(mThemeRoyalGrid);

        mThemePurchases.add(mPurchaseThemeNormal);
        mThemePurchases.add(mPurchaseThemeRetro);
        mThemePurchases.add(mPurchaseThemeAutumn);
        mThemePurchases.add(mPurchaseThemeSummer);
        mThemePurchases.add(mPurchaseThemeMonochrome);
        mThemePurchases.add(mPurchaseThemeRoyal);

        mPurchaseThemeNormal.setText(SELECT);
        mPurchaseThemeAutumn.setText(addCommasToValue(COST_THEME_AUTUMN));
        mPurchaseThemeRetro.setText(addCommasToValue(COST_THEME_RETRO));
        mPurchaseThemeSummer.setText(addCommasToValue(COST_THEME_SUMMER));
        mPurchaseThemeMonochrome.setText(addCommasToValue(COST_THEME_WINTER));
        mPurchaseThemeRoyal.setText(addCommasToValue(COST_THEME_ROYAL));

        redrawThemeGrids();

        mPurchaseThemeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_NORMAL, v);
            }
        });

        mPurchaseThemeAutumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_AUTUMN, v);
            }
        });

        mPurchaseThemeRetro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_RETRO, v);
            }
        });

        mPurchaseThemeSummer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_SUMMER, v);
            }
        });

        mPurchaseThemeMonochrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_WINTER, v);
            }
        });

        mPurchaseThemeRoyal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_ROYAL, v);
            }
        });


        mThemeNormalGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_NORMAL, v);
            }
        });

        mThemeAutumnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_AUTUMN, v);
            }
        });

        mThemeRetroGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_RETRO, v);
            }
        });

        mThemeSummerGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_SUMMER, v);
            }
        });

        mThemeMonochromeGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_WINTER, v);
            }
        });

        mThemeRoyalGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeSelected(Constants.THEME_ROYAL, v);
            }
        });

        mAnimationNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationSelected(Constants.ANIMATION_NORMAL, v);
            }
        });

        mAnimationRaining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationSelected(Constants.ANIMATION_RAIN, v);
            }
        });

        mAnimationBounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationSelected(Constants.ANIMATION_BOUNCE, v);
            }
        });

        mPurchaseAnimationNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationSelected(Constants.ANIMATION_NORMAL, v);
            }
        });

        mPurchaseAnimationRaining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationSelected(Constants.ANIMATION_RAIN, v);
            }
        });

        mPurchaseAnimationBounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationSelected(Constants.ANIMATION_BOUNCE, v);
            }
        });

        mPurchaseRemoveAds = (TextView) findViewById(R.id.store_purchase_remove_ads);
        mPurchaseCoinDoubler = (TextView) findViewById(R.id.store_purchase_coin_doubler);

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_ADS_REMOVED, false)) {
            mPurchaseRemoveAds.setOnClickListener(null);
            mPurchaseRemoveAds.setText(PURCHASED);
        } else {
            mPurchaseRemoveAds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrateView(v);
                    v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));

                    if (mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) > COST_REMOVE_ADS) {
                        playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
                        mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                                mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0)
                                        - COST_REMOVE_ADS).apply();
                        mPrefs.edit().putBoolean(Constants.SHARED_PREFS_ADS_REMOVED, true).apply();
                        mPurchaseRemoveAds.setOnClickListener(null);
                        mPurchaseRemoveAds.setText(PURCHASED);
                        updateCoinTotal();

                    } else {
                        playSound(Constants.Sounds.INVALID_MOVE, mSoundPoolPlayer);
                        openPurchaseCoinsDialog();
                    }
                }
            });
            mPurchaseRemoveAds.setText(addCommasToValue(COST_REMOVE_ADS));
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_COIN_DOUBLER, false)) {
            mPurchaseCoinDoubler.setOnClickListener(null);
            mPurchaseCoinDoubler.setText(PURCHASED);
        } else {
            mPurchaseCoinDoubler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrateView(v);
                    v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));

                    if (mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) > COST_COIN_DOUBLER) {
                        playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
                        mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                                mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0)
                                        - COST_COIN_DOUBLER).apply();
                        mPrefs.edit().putBoolean(Constants.SHARED_PREFS_COIN_DOUBLER, true).apply();
                        mPurchaseCoinDoubler.setOnClickListener(null);
                        mPurchaseCoinDoubler.setText(PURCHASED);
                        updateCoinTotal();

                    } else {
                        playSound(Constants.Sounds.INVALID_MOVE, mSoundPoolPlayer);
                        openPurchaseCoinsDialog();
                    }
                }
            });
            mPurchaseCoinDoubler.setText(addCommasToValue(COST_COIN_DOUBLER));
        }

        ImageView back = (ImageView) findViewById(R.id.store_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateView(v);
                v.setEnabled(false);
                playSound(Constants.Sounds.GAME_OPTIONS, mSoundPoolPlayer);
                Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        onBackPressed();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(animation);
            }
        });

        setUpThemeGrids();
        setUpGridShapes();
        setUpAnimations();

        ScrollView container = (ScrollView) findViewById(R.id.store_secondary_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        mContainer = (LinearLayout) findViewById(R.id.store_container);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenImmersive();
            }
        });

        mCoinContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in_from_pin));
        mAnimatableViews = addAllViewsToList(mContainer, mAnimatableViews);
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
        if (mSoundPoolPlayer != null) {
            mSoundPoolPlayer.release();
        }

        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }

    @Override
    public void onBackPressed() {
        mAnimatableViews.clear();
        mAnimatableViews = addAllViewsToList(mContainer, mAnimatableViews);
        mCoinContainer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_pin));
        animateExit(mAnimatableViews, ANIMATION_TIME_EXIT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getBaseContext(), MainMenuActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        }, (mAnimatableViews.size() + Constants.ANIMATION_BUFFER) * ANIMATION_TIME_EXIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mHelper == null) {
            return;
        }
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCoinsPurchased(int numCoins) {

        mHelper.flagEndAsync();

        switch (numCoins) {
            case 10000:
                mHelper.launchPurchaseFlow(this, SKU_PURCHASE_10000, 10001, mPurchaseFinishedListener, null);
                break;
            case 50000:
                mHelper.launchPurchaseFlow(this, SKU_PURCHASE_50000, 10002, mPurchaseFinishedListener, null);
                break;
            case 100000:
                mHelper.launchPurchaseFlow(this, SKU_PURCHASE_100000, 10003, mPurchaseFinishedListener, null);
                break;
            case 200000:
                mHelper.launchPurchaseFlow(this, SKU_PURCHASE_200000, 10004, mPurchaseFinishedListener, null);
                break;
            case 500000:
                mHelper.launchPurchaseFlow(this, SKU_PURCHASE_500000, 10005, mPurchaseFinishedListener, null);
                break;
            case 2500000:
                mHelper.launchPurchaseFlow(this, SKU_PURCHASE_2500000, 10006, mPurchaseFinishedListener, null);
                break;
            default:
                return;
        }
    }

    //region OnQueryInventoryFinishedListener
    IabHelper.QueryInventoryFinishedListener mQueryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (result.isFailure()) {
                Log.d(LOG_TAG, "Error retrieving prices");
            } else {
                mPrice10000 = inv.getSkuDetails(SKU_PURCHASE_10000).getPrice();
                mPrice50000 = inv.getSkuDetails(SKU_PURCHASE_50000).getPrice();
                mPrice100000 = inv.getSkuDetails(SKU_PURCHASE_100000).getPrice();
                mPrice200000 = inv.getSkuDetails(SKU_PURCHASE_200000).getPrice();
                mPrice500000 = inv.getSkuDetails(SKU_PURCHASE_500000).getPrice();
                mPrice2500000 = inv.getSkuDetails(SKU_PURCHASE_2500000).getPrice();
            }
        }
    };
    //endregion

    //region OnIabPurchaseFinishedListener
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(LOG_TAG, "Error with purchasing: " + result);
                return;
            } else if (purchase.getSku().equals(SKU_PURCHASE_10000)) {
                mHelper.consumeAsync(purchase, mConsume10000FinishedListener);
            } else if (purchase.getSku().equals(SKU_PURCHASE_50000)) {
                mHelper.consumeAsync(purchase, mConsume50000FinishedListener);
            } else if (purchase.getSku().equals(SKU_PURCHASE_100000)) {
                mHelper.consumeAsync(purchase, mConsume100000FinishedListener);
            } else if (purchase.getSku().equals(SKU_PURCHASE_200000)) {
                mHelper.consumeAsync(purchase, mConsume200000FinishedListener);
            } else if (purchase.getSku().equals(SKU_PURCHASE_500000)) {
                mHelper.consumeAsync(purchase, mConsume500000FinishedListener);
            } else if (purchase.getSku().equals(SKU_PURCHASE_2500000)) {
                mHelper.consumeAsync(purchase, mConsume2500000FinishedListener);
            }
        }
    };
    //endregion

    //region 10000 coins OnConsumeFinishedListener
    IabHelper.OnConsumeFinishedListener mConsume10000FinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) + 10000).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + 10000).apply();
                updateCoinTotal();
            } else {
                Toast.makeText(StoreActivity.this, "ERROR PURCHASING COINS", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    //region 50000 coins OnConsumeFinishedListener
    IabHelper.OnConsumeFinishedListener mConsume50000FinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) + 50000).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + 50000).apply();
                updateCoinTotal();
            } else {
                Toast.makeText(StoreActivity.this, "ERROR PURCHASING COINS", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    //region 100000 coins OnConsumeFinishedListener
    IabHelper.OnConsumeFinishedListener mConsume100000FinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) + 100000).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + 100000).apply();
                updateCoinTotal();
            } else {
                Toast.makeText(StoreActivity.this, "ERROR PURCHASING COINS", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    //region 200000 coins OnConsumeFinishedListener
    IabHelper.OnConsumeFinishedListener mConsume200000FinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) + 200000).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + 200000).apply();
                updateCoinTotal();
            } else {
                Toast.makeText(StoreActivity.this, "ERROR PURCHASING COINS", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    //region 500000 coins OnConsumeFinishedListener
    IabHelper.OnConsumeFinishedListener mConsume500000FinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) + 500000).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + 500000).apply();
                updateCoinTotal();
            } else {
                Toast.makeText(StoreActivity.this, "ERROR PURCHASING COINS", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    //region 2,500,000 coins OnConsumeFinishedListener
    IabHelper.OnConsumeFinishedListener mConsume2500000FinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0) + 2500000).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_EARNED,
                        mPrefs.getInt(Constants.SHARED_PREFS_COINS_EARNED, 0) + 2500000).apply();
                updateCoinTotal();
            } else {
                Toast.makeText(StoreActivity.this, "ERROR PURCHASING COINS", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    private void redrawThemeGrids() {
        final int DIMENSIONS = 4;

        int length = ((int) getResources()
                .getDimension(R.dimen.store_purchase_theme_dimensions) / DIMENSIONS);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(length, length);

        for (GridLayout grid : mThemeGrids) {
            grid.removeAllViews();
            grid.setRowCount(DIMENSIONS);
            grid.setColumnCount(DIMENSIONS);
        }

        for (int i = 0; i < DIMENSIONS; i++) {
            for (int j = 0; j < DIMENSIONS; j++) {
                mThemeNormalGrid.addView(createTileView(layoutParams, Constants.THEME_NORMAL));
                mThemeAutumnGrid.addView(createTileView(layoutParams, Constants.THEME_AUTUMN));
                mThemeRetroGrid.addView(createTileView(layoutParams, Constants.THEME_RETRO));
                mThemeSummerGrid.addView(createTileView(layoutParams, Constants.THEME_SUMMER));
                mThemeMonochromeGrid.addView(createTileView(layoutParams, Constants.THEME_WINTER));
                mThemeRoyalGrid.addView(createTileView(layoutParams, Constants.THEME_ROYAL));
            }
        }
    }

    private void setupPowerupValues() {
        updateCoinTotal();
        mNumberUndo.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_UNDO, 5)));
        mNumberDestroyer.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, 3)));
        mNumberStopTime.setText(addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_STOP_TIME, 3)));
    }

    private void setUpGridShapes() {
        String gridShapeSelected = mPrefs.getString(
                Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL);

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_GRID_SHAPE_CIRCLE_PURCHASED, false)) {
            mPurchaseShapeCircle.setText(SELECT);
        }
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_GRID_SHAPE_SHARP_PURCHASED, false)) {
            mPurchaseShapeSharp.setText(SELECT);
        }

        if (gridShapeSelected.equals(Constants.GRID_SHAPE_NORMAL)) {
            ((GradientDrawable) mGridShapeNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_yellow_500));
            ((GradientDrawable) mGridShapeCircle.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mGridShapeSharp.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            mGridShapeNormal.setAlpha(1);
            mGridShapeCircle.setAlpha(FADED_VIEW_ALPHA);
            mGridShapeSharp.setAlpha(FADED_VIEW_ALPHA);

            ((GradientDrawable) mPurchaseShapeNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_deep_orange_500));
            ((GradientDrawable) mPurchaseShapeCircle.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseShapeSharp.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));

        } else if (gridShapeSelected.equals(Constants.GRID_SHAPE_SHARP)) {
            ((GradientDrawable) mGridShapeNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mGridShapeCircle.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mGridShapeSharp.getBackground())
                    .setColor(getResources().getColor(R.color.md_blue_500));
            mGridShapeCircle.setAlpha(FADED_VIEW_ALPHA);
            mGridShapeNormal.setAlpha(FADED_VIEW_ALPHA);
            mGridShapeSharp.setAlpha(1);

            ((GradientDrawable) mPurchaseShapeNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseShapeCircle.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseShapeSharp.getBackground())
                    .setColor(getResources().getColor(R.color.md_deep_orange_500));

        } else if (gridShapeSelected.equals(Constants.GRID_SHAPE_CIRCLE)) {

            ((GradientDrawable) mGridShapeNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mGridShapeCircle.getBackground())
                    .setColor(getResources().getColor(R.color.md_green_500));
            ((GradientDrawable) mGridShapeSharp.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            mGridShapeNormal.setAlpha(FADED_VIEW_ALPHA);
            mGridShapeCircle.setAlpha(1);
            mGridShapeSharp.setAlpha(FADED_VIEW_ALPHA);

            ((GradientDrawable) mPurchaseShapeNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseShapeCircle.getBackground())
                    .setColor(getResources().getColor(R.color.md_deep_orange_500));
            ((GradientDrawable) mPurchaseShapeSharp.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));

        }
    }

    private void setUpThemeGrids() {
        String themeSelected = mPrefs.getString(
                Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_NORMAL);

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_AUTUMN_PURCHASED, false)) {
            mPurchaseThemeAutumn.setText(SELECT);
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_RETRO_PURCHASED, false)) {
            mPurchaseThemeRetro.setText(SELECT);
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_SUMMER_PURCHASED, false)) {
            mPurchaseThemeSummer.setText(SELECT);
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_WINTER_PURCHASED, false)) {
            mPurchaseThemeMonochrome.setText(SELECT);
        }

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_ROYAL_PURCHASED, false)) {
            mPurchaseThemeRoyal.setText(SELECT);
        }

        for (GridLayout grid : mThemeGrids) {
            grid.setAlpha(FADED_VIEW_ALPHA);
        }

        for (TextView textView : mThemePurchases) {
            ((GradientDrawable) textView.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
        }

        switch (themeSelected) {
            case Constants.THEME_NORMAL:
                ((GradientDrawable) mPurchaseThemeNormal.getBackground())
                        .setColor(getResources().getColor(R.color.md_deep_orange_500));
                mThemeNormalGrid.setAlpha(1f);
                break;
            case Constants.THEME_AUTUMN:
                ((GradientDrawable) mPurchaseThemeAutumn.getBackground())
                        .setColor(getResources().getColor(R.color.md_deep_orange_500));
                mThemeAutumnGrid.setAlpha(1f);
                break;
            case Constants.THEME_RETRO:
                ((GradientDrawable) mPurchaseThemeRetro.getBackground())
                        .setColor(getResources().getColor(R.color.md_deep_orange_500));
                mThemeRetroGrid.setAlpha(1f);
                break;
            case Constants.THEME_SUMMER:
                ((GradientDrawable) mPurchaseThemeSummer.getBackground())
                        .setColor(getResources().getColor(R.color.md_deep_orange_500));
                mThemeSummerGrid.setAlpha(1f);
                break;
            case Constants.THEME_WINTER:
                ((GradientDrawable) mPurchaseThemeMonochrome.getBackground())
                        .setColor(getResources().getColor(R.color.md_deep_orange_500));
                mThemeMonochromeGrid.setAlpha(1f);
                break;
            case Constants.THEME_ROYAL:
                ((GradientDrawable) mPurchaseThemeRoyal.getBackground())
                        .setColor(getResources().getColor(R.color.md_deep_orange_500));
                mThemeRoyalGrid.setAlpha(1f);
                break;
        }
    }

    private void setUpAnimations() {
        String animationSelected = mPrefs.getString(
                Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_NORMAL);

        if (mPrefs.getBoolean(Constants.SHARED_PREFS_ANIMATION_RAIN_PURCHASED, false)) {
            mPurchaseAnimationRaining.setText(SELECT);
        }
        if (mPrefs.getBoolean(Constants.SHARED_PREFS_ANIMATION_BOUNCE_PURCHASED, false)) {
            mPurchaseAnimationBounce.setText(SELECT);
        }

        if (animationSelected.equals(Constants.ANIMATION_NORMAL)) {
            ((GradientDrawable) mAnimationNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_indigo_500));
            ((GradientDrawable) mAnimationRaining.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mAnimationBounce.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            mAnimationNormal.setAlpha(1);
            mAnimationRaining.setAlpha(FADED_VIEW_ALPHA);
            mAnimationBounce.setAlpha(FADED_VIEW_ALPHA);

            ((GradientDrawable) mPurchaseAnimationNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_deep_orange_500));
            ((GradientDrawable) mPurchaseAnimationRaining.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseAnimationBounce.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));

        } else if (animationSelected.equals(Constants.ANIMATION_BOUNCE)) {
            ((GradientDrawable) mAnimationNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mAnimationRaining.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mAnimationBounce.getBackground())
                    .setColor(getResources().getColor(R.color.md_indigo_500));
            mAnimationNormal.setAlpha(FADED_VIEW_ALPHA);
            mAnimationRaining.setAlpha(FADED_VIEW_ALPHA);
            mAnimationBounce.setAlpha(1);

            ((GradientDrawable) mPurchaseAnimationNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseAnimationRaining.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseAnimationBounce.getBackground())
                    .setColor(getResources().getColor(R.color.md_deep_orange_500));

        } else if (animationSelected.equals(Constants.ANIMATION_RAIN)) {

            ((GradientDrawable) mAnimationNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            ((GradientDrawable) mAnimationRaining.getBackground())
                    .setColor(getResources().getColor(R.color.md_indigo_500));
            ((GradientDrawable) mAnimationBounce.getBackground())
                    .setColor(getResources().getColor(R.color.md_white_1000));
            mAnimationNormal.setAlpha(FADED_VIEW_ALPHA);
            mAnimationRaining.setAlpha(1);
            mAnimationBounce.setAlpha(FADED_VIEW_ALPHA);

            ((GradientDrawable) mPurchaseAnimationNormal.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));
            ((GradientDrawable) mPurchaseAnimationRaining.getBackground())
                    .setColor(getResources().getColor(R.color.md_deep_orange_500));
            ((GradientDrawable) mPurchaseAnimationBounce.getBackground())
                    .setColor(getResources().getColor(R.color.md_teal_500));

        }
    }

    private void shapeSelected(String shapeType, View v) {

        String currentShape = mPrefs.getString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL);

        switch (shapeType) {
            case Constants.GRID_SHAPE_NORMAL:
                vibrateView(v);
                playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
                mPrefs.edit().putString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL).apply();
                break;
            case Constants.GRID_SHAPE_CIRCLE:
                vibrateView(v);
                purchaseGridShape(Constants.GRID_SHAPE_CIRCLE);
                break;
            case Constants.GRID_SHAPE_SHARP:
                vibrateView(v);
                purchaseGridShape(Constants.GRID_SHAPE_SHARP);
                break;
        }

        setUpGridShapes();
        setUpThemeGrids();
        if (!mPrefs.getString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_NORMAL).equals(currentShape)) {
            redrawThemeGrids();
        }
    }

    private void themeSelected(String themeType, View v) {
        switch (themeType) {
            case Constants.THEME_NORMAL:
                vibrateView(v);
                playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
                mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_NORMAL).apply();
                setUpThemeGrids();
                break;
            default:
                vibrateView(v);
                purchaseTheme(themeType);
                setUpThemeGrids();
                break;
        }
    }

    private void animationSelected(String animationType, View v) {
        switch (animationType) {
            case Constants.ANIMATION_NORMAL:
                vibrateView(v);
                playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_grow_fade_in_from_bottom));
                mPrefs.edit().putString(Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_NORMAL).apply();
                setUpAnimations();
                break;
            case Constants.ANIMATION_RAIN:
                vibrateView(v);
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_top));
                purchaseAnimation(Constants.ANIMATION_RAIN);
                setUpAnimations();
                break;
            case Constants.ANIMATION_BOUNCE:
                vibrateView(v);
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));
                purchaseAnimation(Constants.ANIMATION_BOUNCE);
                setUpAnimations();
                break;
        }
    }

    private TextView createTileView(
            ViewGroup.LayoutParams layoutParams, String themeName) {

        ArrayList<Integer> colours = getColours(themeName);
        Random random = new Random();
        TextView textView = new TextView(this);

        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(6);
        getCorrectGridShape(textView);

        ((GradientDrawable) textView.getBackground()).setColor(colours.get(random.nextInt(colours.size())));

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

    private void purchasePowerup(String powerupType, View v) {
        int coinsInBank = mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0);
        boolean isSuccess = false;

        vibrateView(v);
        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.menu_icon_bounce));

        if (powerupType.equals(Constants.POWERUP_UNDO)) {
            int numUndo = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_UNDO, 5);
            if (coinsInBank > COST_5_UNDO) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_5_UNDO).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_NUMBERS_UNDO, numUndo + NUMBER_POWERUPS_PER_PURCHASE).apply();
                mNumberUndo.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.flash_out));
                isSuccess = true;
            }
        } else if (powerupType.equals(Constants.POWERUP_DESTROYER)) {
            int numDestroyer = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, 3);
            if (coinsInBank > COST_5_DESTROYER) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_5_DESTROYER).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_NUMBERS_DESTROYER, numDestroyer + NUMBER_POWERUPS_PER_PURCHASE).apply();
                mNumberDestroyer.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.flash_out));
                isSuccess = true;
            }
        } else if (powerupType.equals(Constants.POWERUP_STOP_TIME)) {
            int numStop = mPrefs.getInt(Constants.SHARED_PREFS_NUMBERS_STOP_TIME, 3);
            if (coinsInBank > COST_5_DESTROYER) {
                mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_5_STOP_TIME).apply();
                mPrefs.edit().putInt(Constants.SHARED_PREFS_NUMBERS_STOP_TIME, numStop + NUMBER_POWERUPS_PER_PURCHASE).apply();
                mNumberStopTime.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.flash_out));
                isSuccess = true;
            }
        }

        if (isSuccess) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
        } else {
            playSound(Constants.Sounds.INVALID_MOVE, mSoundPoolPlayer);
            openPurchaseCoinsDialog();
        }

        updateCoinTotal();
    }

    private void purchaseGridShape(String gridShapeType) {
        int coinsInBank = mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0);
        boolean isSuccess = false;
        boolean isAlreadyPurchased = false;

        if (gridShapeType.equals(Constants.GRID_SHAPE_CIRCLE)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_GRID_SHAPE_CIRCLE_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_GRID_SHAPE_CIRCLE) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_GRID_SHAPE_CIRCLE_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_GRID_SHAPE_CIRCLE).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_CIRCLE).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_CIRCLE).apply();
            }
        } else if (gridShapeType.equals(Constants.GRID_SHAPE_SHARP)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_GRID_SHAPE_SHARP_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_GRID_SHAPE_SHARP) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_GRID_SHAPE_SHARP_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_GRID_SHAPE_SHARP).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_SHARP).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_GRID_SHAPE_SELECTED, Constants.GRID_SHAPE_SHARP).apply();
            }
        }

        setUpGridShapes();
        updateCoinTotal();

        if (isAlreadyPurchased) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
            return;
        }

        if (isSuccess) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
        } else {
            playSound(Constants.Sounds.INVALID_MOVE, mSoundPoolPlayer);
            openPurchaseCoinsDialog();
        }
    }

    private void purchaseTheme(String themeType) {
        int coinsInBank = mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0);
        boolean isSuccess = false;
        boolean isAlreadyPurchased = false;

        if (themeType.equals(Constants.THEME_AUTUMN)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_AUTUMN_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_THEME_AUTUMN) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_THEME_AUTUMN_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_THEME_AUTUMN).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_AUTUMN).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_AUTUMN).apply();
            }
        } else if (themeType.equals(Constants.THEME_RETRO)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_RETRO_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_THEME_RETRO) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_THEME_RETRO_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_THEME_RETRO).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_RETRO).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_RETRO).apply();
            }
        } else if (themeType.equals(Constants.THEME_SUMMER)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_SUMMER_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_THEME_SUMMER) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_THEME_SUMMER_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_THEME_SUMMER).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_SUMMER).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_SUMMER).apply();
            }
        } else if (themeType.equals(Constants.THEME_WINTER)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_WINTER_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_THEME_WINTER) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_THEME_WINTER_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_THEME_WINTER).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_WINTER).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_WINTER).apply();
            }
        } else if (themeType.equals(Constants.THEME_ROYAL)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_THEME_ROYAL_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_THEME_ROYAL) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_THEME_ROYAL_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_THEME_ROYAL).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_ROYAL).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_THEME_SELECTED, Constants.THEME_ROYAL).apply();
            }
        }

        setUpGridShapes();
        updateCoinTotal();

        if (isAlreadyPurchased) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
            return;
        }

        if (isSuccess) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
        } else {
            playSound(Constants.Sounds.INVALID_MOVE, mSoundPoolPlayer);
            openPurchaseCoinsDialog();
        }
    }

    private void purchaseAnimation(String animationType) {
        int coinsInBank = mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0);
        boolean isSuccess = false;
        boolean isAlreadyPurchased = false;

        if (animationType.equals(Constants.ANIMATION_RAIN)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_ANIMATION_RAIN_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_ANIMATION_RAIN) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_ANIMATION_RAIN_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_ANIMATION_RAIN).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_RAIN).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_RAIN).apply();
            }
        } else if (animationType.equals(Constants.ANIMATION_BOUNCE)) {
            boolean isPurchased = mPrefs.getBoolean(Constants.SHARED_PREFS_ANIMATION_BOUNCE_PURCHASED, false);
            if (!isPurchased) {
                if (coinsInBank > COST_ANIMATION_BOUNCE) {
                    mPrefs.edit().putBoolean(Constants.SHARED_PREFS_ANIMATION_BOUNCE_PURCHASED, true).apply();
                    mPrefs.edit().putInt(Constants.SHARED_PREFS_COINS_IN_BANK, coinsInBank - COST_ANIMATION_BOUNCE).apply();
                    mPrefs.edit().putString(Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_BOUNCE).apply();
                    isSuccess = true;
                }
            } else {
                isAlreadyPurchased = true;
                mPrefs.edit().putString(Constants.SHARED_PREFS_ANIMATION_SELECTED, Constants.ANIMATION_BOUNCE).apply();
            }
        }

        updateCoinTotal();

        if (isAlreadyPurchased) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
            return;
        }

        if (isSuccess) {
            playSound(Constants.Sounds.BOMB, mSoundPoolPlayer);
        } else {
            playSound(Constants.Sounds.INVALID_MOVE, mSoundPoolPlayer);
            openPurchaseCoinsDialog();
        }
    }

    private void updateCoinTotal() {
        mCoinTotal.setText(
                addCommasToValue(mPrefs.getInt(Constants.SHARED_PREFS_COINS_IN_BANK, 0)));
    }

    private void openPurchaseCoinsDialog() {
        PurchaseCoinsFragment fragment = new PurchaseCoinsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_PRICE_10000_COINS, mPrice10000);
        args.putString(Constants.BUNDLE_PRICE_50000_COINS, mPrice50000);
        args.putString(Constants.BUNDLE_PRICE_100000_COINS, mPrice100000);
        args.putString(Constants.BUNDLE_PRICE_200000_COINS, mPrice200000);
        args.putString(Constants.BUNDLE_PRICE_500000_COINS, mPrice500000);
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), null);
    }

}
