package com.swiftythesloth.splash;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents the grid on which tiles are placed
 */
public class GameGrid {

    public static final int DEFAULT_COLOUR = 0xFFFFFFFF;

    private int mNumMovesChangeCountdown;

    private int mNumMovesPlayed = 0;

    private boolean mIsPaused = false;

    //Checked in Activity so it can be redrawn
    private boolean mRandomMoveMade;

    //Number of seconds between random moves
    private long mCountdownTime = 5000;

    //Current number of seconds remaining in tick
    private long mCurrentTime = mCountdownTime;

    //Number of moves made while mCountdownTime has been at its current value
    private int mNumberMovesMadeThisTick;

    //True if a move has been made, prevents tile being randomly placed
    private boolean mMoveMadeDuringCountdown;

    //Number of squares high and wide of the playing grid
    private int mDimensions;

    //Width of same-coloured tiles needed in a row to burst them
    private int mBurstWidth;

    //Radius of squares around point of impact removed
    private int mBombRadius;

    //Number of moves remaining
    private int mMovesLeft;

    //Player's score
    private int mScore;

    //Greatest number of tiles burst at once
    private int mBestBurst = 0;

    //Influences chance of initial tiles laid down being a wall
    private int mWallDensity;

    //Number of tiles destroyed by bursting normally
    private int mNumberTilesBurst;

    //Number of tiles destroyed by bombing
    private int mNumberTilesBombed;

    //Number of tiles destroyed by using destroyer powerups
    private int mNumberTilesDestroyed;

    //List of tiles currently on the board
    private ArrayList<GameTile> mPlayingTiles = new ArrayList<>();

    //List of tiles in the waiting queue
    private ArrayList<GameTile> mQueuedTiles = new ArrayList<>();

    //List of tiles affected by last move
    private ArrayList<GameTile> mDirtyTiles = new ArrayList<>();

    //List of possible colours
    private ArrayList<Integer> mColours = new ArrayList<>();

    //Sound to be played after this round is completed
    private Constants.Sounds mSoundNeeded = Constants.Sounds.NONE;

    //Gametype of this grid
    private GameType mGameType;

    public enum GameType {
        ENDLESS,
        TIME_ATTACK,
        MOVES,
        CREATE
    }

    /**
     * Constructor for GameGrid, assigns all initial values given
     *
     * @param gameType    {@link GameGrid.GameType} of this grid
     * @param dimensions  Number of squares in grid, height and width
     * @param burstWidth  Width of {@link GameTile} needed in a row to burst them
     * @param numInactive Number of {@link GameTile} to be placed in the queue
     * @param bombRadius  Number of squares detonated around epicenter
     * @param wallDensity Percentage chance of {@link GameTile} being a wall
     */
    public GameGrid(GameType gameType, int dimensions, int burstWidth, int numInactive, int bombRadius, int wallDensity, ArrayList<Integer> colours) {
        mGameType = gameType;
        mDimensions = dimensions;
        mBurstWidth = burstWidth;
        mBombRadius = bombRadius;
        mWallDensity = wallDensity;
        mMovesLeft = (dimensions * dimensions) * 2;
        mColours = colours;
        mNumMovesChangeCountdown = dimensions * 5;

        addQueuedTiles(numInactive);
        addDefaultActiveTiles();
    }

    /**
     * Creates a copy of the given {@link GameGrid}
     * @param gameGrid
     */
    public GameGrid(GameGrid gameGrid) {
        mNumMovesChangeCountdown = gameGrid.getNumMovesChangeCountdown();
        mIsPaused = gameGrid.isPaused();
        mRandomMoveMade = gameGrid.isRandomMoveMade();
        mCountdownTime = gameGrid.getCountdownTime();
        mCurrentTime = gameGrid.getCountdownTime();
        mNumMovesPlayed = gameGrid.getNumMovesPlayed();
        mNumberMovesMadeThisTick = gameGrid.getNumberMovesMadeThisTick();
        mMoveMadeDuringCountdown = gameGrid.isMoveMadeDuringCountdown();
        mDimensions = gameGrid.getDimensions();
        mBurstWidth = gameGrid.getBurstWidth();
        mBombRadius = gameGrid.getBombRadius();
        mMovesLeft = gameGrid.getMovesLeft();
        mScore = gameGrid.getScore();
        mBestBurst = gameGrid.getBestBurst();
        mWallDensity = gameGrid.getWallDensity();
        mNumberTilesBombed = gameGrid.getNumberTilesBombed();
        mNumberTilesDestroyed = gameGrid.getNumberTilesDestroyed();
        mNumberTilesBurst = gameGrid.getNumberTilesBurst();
        for (GameTile tile : gameGrid.getPlayingTiles()) {
            mPlayingTiles.add(new GameTile(tile));
        }
        for (GameTile tile : gameGrid.getQueuedTiles()) {
            mQueuedTiles.add(new GameTile(tile));
        }
        for (GameTile tile : gameGrid.getDirtyTiles()) {
            mDirtyTiles.add(new GameTile(tile));
        }
        mColours = gameGrid.getColours();
        mSoundNeeded = gameGrid.getSoundNeeded();
        mGameType = gameGrid.getGameType();
    }

    /**
     * Add new inactive {@link GameTile} to the waiting queue
     *
     * @param numToAdd Number of tiles to add
     */
    private void addQueuedTiles(int numToAdd) {
        for (int i = 0; i < numToAdd; i++) {
            int colour = getRandomColour();
            mQueuedTiles.add(new GameTile(colour, mColours.indexOf(colour) + 1, GameTile.getRandomTileType()));
        }
    }

    /**
     * Fill grid with default {@link GameTile} to start the game
     */
    private void addDefaultActiveTiles() {
        for (int i = 0; i < mDimensions; i++) {
            for (int j = 0; j < mDimensions; j++) {
                GameTile tile = new GameTile(DEFAULT_COLOUR, i, j, 0, mWallDensity, false);
                mPlayingTiles.add(tile);
            }
        }
    }

    /**
     * Analyses type of {@link GameTile} clicked, and determines what to do. For example,
     * attempt to burst if tile isFilled.
     *
     * @param tile {@link GameTile} to check
     */
    public void tileClicked(GameTile tile) {

        if (tile.getTileType().equals(GameTile.TileType.WALL)) {
            mSoundNeeded = Constants.Sounds.NONE;
            return;
        }

        GameTile nextInLine = mQueuedTiles.remove(0);

        if (nextInLine.getTileType().equals(GameTile.TileType.BOMB)) {
            mSoundNeeded = Constants.Sounds.BOMB;
            detonateBomb(tile);
            addQueuedTiles(1);
            mMoveMadeDuringCountdown = true;
            mNumMovesPlayed++;
            return;
        }

        if (!tile.isFilled()) {
            if (mGameType.equals(GameType.MOVES) && mMovesLeft == 0) {
                mSoundNeeded = Constants.Sounds.INVALID_MOVE;
                mQueuedTiles.add(0, nextInLine);
                return;
            }
            mSoundNeeded = Constants.Sounds.TILE_CLICK;
            tile.setColour(nextInLine.getColour());
            tile.setNumber(nextInLine.getNumber());
            tile.setIsFilled(true);
            tile.setIsJustLaid(true);
            mDirtyTiles.add(tile);
            mMovesLeft--;
            addQueuedTiles(1);
            mMoveMadeDuringCountdown = true;
            mNumMovesPlayed++;
        } else {
            mSoundNeeded = Constants.Sounds.NONE;
            updateBestBurst(burstTiles(tile));
            mQueuedTiles.add(0, nextInLine);
            mMoveMadeDuringCountdown = true;
        }
    }

    /**
     * Recursively analyse touching {@link GameTile}. If the number of touching tiles of
     * the same colour is greater than or equal to {@link #mBurstWidth}, pop them.
     *
     * @param sourceTile {@link GameTile} of origin
     *
     * @return           Number of tiles burst
     */
    public int burstTiles(GameTile sourceTile) {
        ArrayList<GameTile> tilesToBurst = new ArrayList<>();
        final float BURST_SCORE_MODIFIER = 1.25f;
        final float BURST_SCORE_MULTIPLIER = 100f;

        sourceTile.setMarked(true);
        tilesToBurst.add(sourceTile);

        for (GameTile tile : getTouchingTiles(sourceTile, null)) {
            tilesToBurst.add(tile);
        }

        resetMarkers();

        if (tilesToBurst.size() >= mBurstWidth) {
            mSoundNeeded = Constants.Sounds.BURST;
            for (GameTile tile : tilesToBurst) {
                tile.setIsFilled(false);
                tile.setColour(DEFAULT_COLOUR);
                mDirtyTiles.add(tile);
                tile.setJustBurst(true);
                mNumberTilesBurst++;
            }
            mNumMovesPlayed++;
            mScore += (int) ((tilesToBurst.size() * tilesToBurst.size()
                    * BURST_SCORE_MODIFIER) * BURST_SCORE_MULTIPLIER);
        } else {
            mSoundNeeded = Constants.Sounds.NONE;
            for (GameTile tile : tilesToBurst) {
                tile.setMarked(false);
            }
        }

        return tilesToBurst.size();

    }

    /**
     * Reset markers placed during the process of checking for burstability,
     * in which each touched {@link GameTile} is marked,
     * so that the recursion does not cause a StackOverflow.
     */
    private void resetMarkers() {
        for (GameTile tile : mPlayingTiles) {
            tile.setMarked(false);
        }
    }

    /**
     * Recursive algorithm which checks that the {@link GameTile} touching the
     * north, east, south and west faces of the epicenter are active and of the same colour.
     *
     * @param tile     Original {@link GameTile}
     * @param prevTile {@link GameTile} checked previously
     * @return List of touching {@link GameTile}
     */
    private ArrayList<GameTile> getTouchingTiles(GameTile tile, GameTile prevTile) {
        ArrayList<GameTile> tiles = new ArrayList<>();

        GameTile up = getTileAtPosition(tile.getRowPosition() - 1, tile.getColPosition());
        GameTile left = getTileAtPosition(tile.getRowPosition(), tile.getColPosition() - 1);
        GameTile down = getTileAtPosition(tile.getRowPosition() + 1, tile.getColPosition());
        GameTile right = getTileAtPosition(tile.getRowPosition(), tile.getColPosition() + 1);

        if (up != null && up.getColour() == tile.getColour() && up.isFilled() && up != prevTile && !up.isMarked() && !up.getTileType().equals(GameTile.TileType.WALL)) {
            tiles.add(up);
            up.setMarked(true);
            for (GameTile t : getTouchingTiles(up, tile)) {
                tiles.add(t);
            }
        }

        if (left != null && left.getColour() == tile.getColour() && left.isFilled() && left != prevTile && !left.isMarked() && !left.getTileType().equals(GameTile.TileType.WALL)) {
            tiles.add(left);
            left.setMarked(true);
            for (GameTile t : getTouchingTiles(left, tile)) {
                tiles.add(t);
            }
        }

        if (down != null && down.getColour() == tile.getColour() && down.isFilled() && down != prevTile && !down.isMarked() && !down.getTileType().equals(GameTile.TileType.WALL)) {
            tiles.add(down);
            down.setMarked(true);
            for (GameTile t : getTouchingTiles(down, tile)) {
                tiles.add(t);
            }
        }

        if (right != null && right.getColour() == tile.getColour() && right.isFilled() && right != prevTile && !right.isMarked() && !right.getTileType().equals(GameTile.TileType.WALL)) {
            tiles.add(right);
            right.setMarked(true);
            for (GameTile t : getTouchingTiles(right, tile)) {
                tiles.add(t);
            }
        }

        return tiles;
    }

    /**
     * Deactives each {@link GameTile} within {@link #mBombRadius} squares of the epicenter
     *
     * @param sourceTile Original {@link GameTile}
     */
    public void detonateBomb(GameTile sourceTile) {
        int blastGridDimensions = mBombRadius + mBombRadius + 1;
        int blastCount = 0;

        for (int i = -mBombRadius; i < blastGridDimensions - mBombRadius; i++) {
            for (int j = -mBombRadius; j < blastGridDimensions - mBombRadius; j++) {
                GameTile tile = getTileAtPosition(sourceTile.getRowPosition() + i, sourceTile.getColPosition() + j);
                if (tile == null || tile.getTileType().equals(GameTile.TileType.WALL)) {
                    continue;
                }
                if (tile.isFilled()) {
                    tile.setIsFilled(false);
                    tile.setColour(DEFAULT_COLOUR);
                    blastCount++;
                    mNumberTilesBombed++;
                }
                tile.setJustBurst(true);
                mDirtyTiles.add(tile);
            }
        }
        mScore += (2 * blastCount) * 100;
    }

    /**
     * Performs a destroyer move. Eliminates all {@link GameTile} of the same
     * colour as the sourceTile
     * @param sourceTile        {@link GameTile} original tile
     * @return                  True if any tiles were affected
     */
    public boolean activateDestroyer(GameTile sourceTile) {
        int colour = sourceTile.getColour();
        boolean isEffective = false;

        if (sourceTile.isFilled() && !sourceTile.getTileType().equals(GameTile.TileType.WALL)) {
            for (GameTile tile : mPlayingTiles) {
                if (tile.getColour() == colour) {
                    tile.setIsFilled(false);
                    tile.setColour(DEFAULT_COLOUR);
                    mDirtyTiles.add(tile);
                    tile.setJustBurst(true);
                    mNumberTilesDestroyed++;
                    isEffective = true;
                }
            }
            mNumMovesPlayed++;
            mSoundNeeded = Constants.Sounds.BURST;
            mMoveMadeDuringCountdown = true;
        } else {
            mSoundNeeded = Constants.Sounds.NONE;
        }

        mScore += (2 * mDirtyTiles.size()) * 100;
        return isEffective;
    }

    /**
     * Check if game is finished. Only finished if each {@link GameTile#isFilled()},
     * no {@link GameTile} can be burst, and the next {@link GameTile} in the upcoming
     * queue is not a bomb.
     *
     * @return True if game is finished
     */
    public boolean isGameFinished() {
        boolean isBurstableTile = false;
        boolean isUnfilledTile = false;

        if (mQueuedTiles.get(0).getTileType().equals(GameTile.TileType.BOMB)) {
            return false;
        }

        for (GameTile tile : mPlayingTiles) {
            if (!tile.isFilled()) {
                isUnfilledTile = true;
            }
            if (isBurstable(tile)) {
                isBurstableTile = true;
            }
        }

        //Game is finished if we are playing a moves game and we have no moves left
        if (mGameType.equals(GameType.MOVES) && mMovesLeft == 0 && !isBurstableTile) {
            return true;
        }

        if (!isUnfilledTile && !isBurstableTile) {
            return true;
        }

        return false;
    }

    /**
     * Check if {@link GameTile} forms a burstable set
     *
     * @param tile Original {@link GameTile}
     * @return True if {@link GameTile} is burstable
     */
    public boolean isBurstable(GameTile tile) {
        ArrayList<GameTile> tiles = new ArrayList<>();
        tiles.add(tile);
        tile.setMarked(true);

        for (GameTile tileToCheck : getTouchingTiles(tile, null)) {
            tiles.add(tileToCheck);
        }

        resetMarkers();

        if (tiles.size() >= mBurstWidth) {
            return true;
        }

        return false;
    }

    /**
     * Find a GameTile by its current row and column position
     *
     * @param row row index of GameTile to find
     * @param col column index of GameTile to find
     * @return the GameTile at the given position
     */
    public GameTile getTileAtPosition(int row, int col) {
        if (row < 0 || row > mDimensions - 1) {
            return null;
        }
        if (col < 0 || col > mDimensions - 1) {
            return null;
        }

        for (int i = 0; i < mPlayingTiles.size(); i++) {
            GameTile tile = mPlayingTiles.get(i);
            if ((tile.getRowPosition() == row) && (tile.getColPosition() == col)) {
                return tile;
            }
        }
        return null;
    }

    private void makeRandomMove() {
        Random random = new Random();
        boolean isValidTileFound = false;

        do {
            int row = random.nextInt(mDimensions);
            int col = random.nextInt(mDimensions);
            GameTile tile = getTileAtPosition(row, col);
            if (!tile.getTileType().equals(GameTile.TileType.WALL)) {
                GameTile nextInLine = mQueuedTiles.remove(0);

                if (nextInLine.getTileType().equals(GameTile.TileType.BOMB)) {
                    addQueuedTiles(1);
                    mSoundNeeded = Constants.Sounds.BOMB;
                    detonateBomb(tile);
                    isValidTileFound = true;
                    continue;
                }

                if (!tile.isFilled()) {
                    addQueuedTiles(1);
                    mSoundNeeded = Constants.Sounds.TILE_CLICK;
                    tile.setColour(nextInLine.getColour());
                    tile.setNumber(nextInLine.getNumber());
                    tile.setIsFilled(true);
                    tile.setIsJustLaid(true);
                    mDirtyTiles.add(tile);
                    isValidTileFound = true;
                } else {
                    if (isBurstable(tile)) {
                        mQueuedTiles.add(0, nextInLine);
                        burstTiles(tile);
                        isValidTileFound = true;
                    } else {
                        mQueuedTiles.add(0, nextInLine);
                    }
                }
            }
        } while (!isValidTileFound);

        mRandomMoveMade = true;
        mMoveMadeDuringCountdown = true;
        mNumMovesPlayed++;
    }

    /**
     * Select a random colour from list available, used to assign to {@link GameTile}
     *
     * @return
     */
    private int getRandomColour() {
        Random random = new Random();
        int randIndex = random.nextInt(mColours.size());
        return mColours.get(randIndex);
    }

    public void timeElapsed() {
        if (mMoveMadeDuringCountdown) {
            mMoveMadeDuringCountdown = false;
            mNumberMovesMadeThisTick++;
            mCurrentTime = mCountdownTime;
        } else {
            mCurrentTime -= 1000;
            if (mCurrentTime < 1000) {
                makeRandomMove();
            }
        }
        if (mNumMovesPlayed >= mNumMovesChangeCountdown) {
            mNumMovesPlayed = 0;
            mNumberMovesMadeThisTick = 0;
            if (mCountdownTime > 1000) {
                mCountdownTime -= 1000;
                mCurrentTime = mCountdownTime;
            }
        }
    }

    private void updateBestBurst(int size) {
        if (size > mBestBurst) {
            mBestBurst = size;
        }
    }


    public GameType getGameType() {
        return mGameType;
    }

    public int getNumMovesChangeCountdown() {
        return mNumMovesChangeCountdown;
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    public long getCountdownTime() {
        return mCountdownTime;
    }

    public int getNumMovesPlayed() {
        return mNumMovesPlayed;
    }

    public int getNumberMovesMadeThisTick() {
        return mNumberMovesMadeThisTick;
    }

    public boolean isMoveMadeDuringCountdown() {
        return mMoveMadeDuringCountdown;
    }

    public int getDimensions() {
        return mDimensions;
    }

    public int getBurstWidth() {
        return mBurstWidth;
    }

    public int getBombRadius() {
        return mBombRadius;
    }

    public int getWallDensity() {
        return mWallDensity;
    }

    public ArrayList<Integer> getColours() {
        return mColours;
    }

    public int getMovesLeft() {
        return mMovesLeft;
    }

    public ArrayList<GameTile> getPlayingTiles() {
        return mPlayingTiles;
    }

    public ArrayList<GameTile> getQueuedTiles() {
        return mQueuedTiles;
    }

    public ArrayList<GameTile> getDirtyTiles() {
        return mDirtyTiles;
    }

    public Constants.Sounds getSoundNeeded() {
        return mSoundNeeded;
    }

    public int getBestBurst() {
        return mBestBurst;
    }

    public int getScore() {
        return mScore;
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public boolean isRandomMoveMade() {
        return mRandomMoveMade;
    }

    public int getNumberTilesBurst() {
        return mNumberTilesBurst;
    }

    public int getNumberTilesBombed() {
        return mNumberTilesBombed;
    }

    public int getNumberTilesDestroyed() {
        return mNumberTilesDestroyed;
    }

    public void setRandomMoveMade(boolean randomMoveMade) {
        mRandomMoveMade = randomMoveMade;
    }

    public void setSoundNeeded(Constants.Sounds soundNeeded) {
        mSoundNeeded = soundNeeded;
    }
}
