package com.swiftythesloth.splash;

import java.util.Random;

/**
 * Represents a tile to be placed on the board
 */
public class GameTile {
    private int mRowPosition;
    private int mColPosition;
    private int mColour;
    private int mNumber;
    private boolean mIsFilled;
    private boolean mIsMarked;
    private boolean mIsJustBurst;
    private boolean mIsJustLaid;
    private TileType mTileType;

    enum TileType {
        NORMAL,             //standard colour tile
        BOMB,               //detonates to remove all squares around it
        WALL            //can masquerade as any colour
    }

    /**
     * Constructor for {@link GameTile}
     * @param colour        Colour to give
     * @param row           Initial row position
     * @param col           Initial column position
     * @param chanceWall    Percentage chance of tile being a {@link GameTile.TileType#WALL}
     * @param isFilled      True if {@link GameTile} is active on the board
     */
    public GameTile(int colour, int row, int col, int number, int chanceWall, boolean isFilled) {
        mColour = colour;
        mRowPosition = row;
        mColPosition = col;
        mNumber = number;
        mIsFilled = false;
        mIsMarked = false;

        Random random = new Random();
        int randInt = random.nextInt(100);
        if (randInt < chanceWall) {
            mTileType = TileType.WALL;
            mIsFilled = true;
        } else {
            mTileType = TileType.NORMAL;
        }

    }

    /**
     * Creates a copy of the given {@link GameTile}
     * @param gameTile      {@link GameTile} to copy
     */
    public GameTile(GameTile gameTile) {
        mRowPosition = gameTile.getRowPosition();
        mColPosition = gameTile.getColPosition();
        mColour = gameTile.getColour();
        mNumber = gameTile.getNumber();
        mIsFilled = gameTile.isFilled();
        mIsMarked = gameTile.isMarked();
        mIsJustBurst = gameTile.isJustBurst();
        mIsJustLaid = gameTile.isJustLaid();
        mTileType = gameTile.getTileType();
    }

    /**
     * Constructor for {@link GameTile}, used for moving tile from waiting queue to board
     * @param colour        Colour to give
     * @param tileType      {@link GameTile.TileType} to give
     */
    public GameTile(int colour, int number, TileType tileType) {
        mColour = colour;
        mNumber = number;
        mTileType = tileType;
        mIsFilled = true;
        mIsMarked = false;
    }

    /**
     * Get a random {@link GameTile.TileType}
     * @return      {@link GameTile.TileType} selected
     */
    public static TileType getRandomTileType() {
        Random random = new Random();

//        //Get random index
//        int index = random.nextInt(TileType.values().length);
//        return TileType.values()[index];

        int index = random.nextInt(100);
        if (index < 99) {
            return TileType.NORMAL;
        } else {
            return TileType.BOMB;
        }
    }

    public int getRowPosition() {
        return mRowPosition;
    }

    public int getColPosition() {
        return mColPosition;
    }

    public boolean isMarked() {
        return mIsMarked;
    }

    public int getColour() {
        return mColour;
    }

    public TileType getTileType() {
        return mTileType;
    }

    public boolean isJustBurst() {
        return mIsJustBurst;
    }

    public boolean isFilled() {
        return mIsFilled;
    }

    public boolean isJustLaid() {
        return mIsJustLaid;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public void setIsJustLaid(boolean isJustLaid) {
        mIsJustLaid = isJustLaid;
    }

    public void setIsFilled(boolean isFilled) {
        mIsFilled = isFilled;
    }

    public void setJustBurst(boolean justBurst) {
        mIsJustBurst = justBurst;
    }

    public void setMarked(boolean marked) {
        mIsMarked = marked;
    }

    public void setColour(int colour) {
        mColour = colour;
    }

}
