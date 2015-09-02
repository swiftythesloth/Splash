package com.swiftythesloth.splash;

/**
 * Contains several constants, accessible throughout the application
 */
public class Constants {

    public static final String DIALOG_FRAGMENT_TYPE = "dialog_fragment_type";

    public static final String BUNDLE_DIMENSIONS_KEY = "dimensions";
    public static final String BUNDLE_BURST_WIDTH_KEY = "burst_width";
    public static final String BUNDLE_QUEUED_TILES_KEY = "queued_tiles";
    public static final String BUNDLE_BOMB_RADIUS_KEY = "bomb_radius";
    public static final String BUNDLE_WALL_DENSITY_KEY = "wall_density";
    public static final String BUNDLE_IS_CREATE_GAME_KEY = "is_create_game";
    public static final String BUNDLE_IS_NEW_GAME_KEY = "is_new_game";
    public static final String BUNDLE_GAMETYPE_KEY = "gametype";
    public static final String BUNDLE_INSTRUCTIONS_PAGE_NUMBER = "page_number";
    public static final String BUNDLE_PRICE_10000_COINS = "price_10000_coins";
    public static final String BUNDLE_PRICE_50000_COINS = "price_50000_coins";
    public static final String BUNDLE_PRICE_100000_COINS = "price_100000_coins";
    public static final String BUNDLE_PRICE_200000_COINS = "price_200000_coins";
    public static final String BUNDLE_PRICE_500000_COINS = "price_500000_coins";
    public static final String BUNDLE_PRICE_2500000_COINS = "price_2500000_coins";
    public static final String BUNDLE_GAME_OVER_GRID = "game_over_grid";
    public static final String BUNDLE_GAME_OVER_COINS_EARNED = "game_over_coins_earned";

    public static final String POWERUP_UNDO = "undo";
    public static final String POWERUP_DESTROYER = "destroyer";
    public static final String POWERUP_STOP_TIME = "time_stop";

    public static final String GRID_SHAPE_NORMAL = "normal";
    public static final String GRID_SHAPE_CIRCLE = "circle";
    public static final String GRID_SHAPE_SHARP = "sharp";

    public static final String THEME_NORMAL = "theme_normal";
    public static final String THEME_AUTUMN = "theme_autumn";
    public static final String THEME_RETRO = "theme_retro";
    public static final String THEME_SUMMER = "theme_summer";
    public static final String THEME_WINTER = "theme_winter";
    public static final String THEME_ROYAL = "theme_royal";

    public static final String ANIMATION_NORMAL = "animation_normal";
    public static final String ANIMATION_RAIN = "animation_rain";
    public static final String ANIMATION_BOUNCE = "animation_bounce";

    public static final String SHARED_PREFS_NAME = "colour_grid";

    public static final String SHARED_PREFS_SOUND = "sound";
    public static final String SHARED_PREFS_VIBRATION = "vibration";
    public static final String SHARED_PREFS_LIGHTS = "lights";
    public static final String SHARED_PREFS_COLOUR_BLIND = "colour_blind";

    public static final String SHARED_PREFS_ENDLESS_SAVE = "endless_save";
    public static final String SHARED_PREFS_MOVES_SAVE = "move_save";
    public static final String SHARED_PREFS_TIME_ATTACK_SAVE = "time_attack_save";
    public static final String SHARED_PREFS_CREATE_SAVE = "create_save";

    public static final String SHARED_PREFS_NUMBER_MOVES_GAMES_PLAYED = "moves_games_played";
    public static final String SHARED_PREFS_NUMBER_TIME_ATTACK_GAMES_PLAYED = "time_attack_games_played";
    public static final String SHARED_PREFS_NUMBER_ENDLESS_GAMES_PLAYED = "endless_games_played";
    public static final String SHARED_PREFS_NUMBER_CREATE_GAMES_PLAYED = "create_games_played";

    public static final String SHARED_PREFS_PREVIOUS_SCORE_1 = "previous_score_1";
    public static final String SHARED_PREFS_PREVIOUS_SCORE_2 = "previous_score_2";
    public static final String SHARED_PREFS_PREVIOUS_SCORE_3 = "previous_score_3";

    public static final String SHARED_PREFS_COINS_IN_BANK = "coins_in_bank";
    public static final String SHARED_PREFS_GRID_SHAPE_CIRCLE_PURCHASED = "circle_purchased";
    public static final String SHARED_PREFS_GRID_SHAPE_SHARP_PURCHASED = "sharp_purchased";
    public static final String SHARED_PREFS_THEME_AUTUMN_PURCHASED = "autumn_purchased";
    public static final String SHARED_PREFS_THEME_RETRO_PURCHASED = "retro_purchased";
    public static final String SHARED_PREFS_THEME_SUMMER_PURCHASED = "summer_purchased";
    public static final String SHARED_PREFS_THEME_WINTER_PURCHASED = "winter_purchased";
    public static final String SHARED_PREFS_THEME_ROYAL_PURCHASED = "royal_purchased";
    public static final String SHARED_PREFS_ANIMATION_RAIN_PURCHASED = "rain_purchased";
    public static final String SHARED_PREFS_ANIMATION_BOUNCE_PURCHASED = "bounce_purchased";

    public static final String SHARED_PREFS_ADS_REMOVED = "ads_removed";
    public static final String SHARED_PREFS_COIN_DOUBLER = "coin_doubler";

    public static final String SHARED_PREFS_NUMBERS_UNDO = "number_undo";
    public static final String SHARED_PREFS_NUMBERS_DESTROYER = "number_destroyer";
    public static final String SHARED_PREFS_NUMBERS_STOP_TIME = "number_stop_time";

    public static final String SHARED_PREFS_GRID_SHAPE_SELECTED = "grid_shape_selected";
    public static final String SHARED_PREFS_THEME_SELECTED = "theme_selected";
    public static final String SHARED_PREFS_ANIMATION_SELECTED = "animation_selected";

    public static final String SHARED_PREFS_GAMES_COMPLETED = "games_completed";
    public static final String SHARED_PREFS_BEST_SCORE = "best_score";
    public static final String SHARED_PREFS_COINS_EARNED = "coins_earned";
    public static final String SHARED_PREFS_BEST_BURST = "best_burst";
    public static final String SHARED_PREFS_POWERUPS_USED = "powerups_used";
    public static final String SHARED_PREFS_DESTROYERS_USED = "destroyers_used";
    public static final String SHARED_PREFS_STOP_TIMES_USED = "stop_times_used";
    public static final String SHARED_PREFS_UNDOS_USED = "undos_used";
    public static final String SHARED_PREFS_TILES_BURST = "tiles_burst";
    public static final String SHARED_PREFS_TILES_BOMBED = "tiles_bombed";
    public static final String SHARED_PREFS_TILES_DESTROYED = "tiles_destroyed";

    public static final String SHARED_PREFS_LAST_APP_VERSION = "last_app_version";
    public static final String SHARED_PREFS_NUMBER_RUNS = "number_runs";

    public static final int ANIMATION_BUFFER = 50;

    public enum Sounds {
        BOMB,
        NEW_GAME,
        TILE_CLICK,
        TIME_TICK,
        GAME_OPTIONS,
        BURST,
        GAME_FINISHED,
        INVALID_MOVE,
        UNDO,
        NONE
    }

    public enum PowerupType {
        UNDO,
        DESTROYER,
        STOP_TIME
    }

    public enum DialogFragmentType {
        PURCHASE_SUCCESSFUL,
        PURCHASE_UNSUCCESSFUL,
        RATING
    }

    public static String getSharedPrefsSaveString(GameGrid.GameType gameType) {
        switch (gameType) {
            case ENDLESS:
                return SHARED_PREFS_ENDLESS_SAVE;
            case MOVES:
                return SHARED_PREFS_MOVES_SAVE;
            case TIME_ATTACK:
                return SHARED_PREFS_TIME_ATTACK_SAVE;
            case CREATE:
                return SHARED_PREFS_CREATE_SAVE;
            default:
                return "";
        }
    }

}
