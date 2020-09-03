package com.halcyon.tetris.ai;

import com.halcyon.tetris.bot.Addresses;
import nintaco.api.API;

public class Tetrimino {

    public int posx,
               posy;

    public int typeID;

    Orientation orientation;

    public Tetrimino(){
        super();
    }

    public Tetrimino(int type, int rotation){
        typeID = type;
        orientation = Tetriminos.ORIENTATIONS[typeID][rotation];
    }

    public Tetrimino(char name, int rotation){
        switch(name){
            case 'T': typeID = 0;
            case 'J': typeID = 1;
            case 'Z': typeID = 2;
            case 'O': typeID = 3;
            case 'S': typeID = 4;
            case 'L': typeID = 5;
            case 'I': typeID = 6;
            default: typeID = -1;
        }
        orientation = Tetriminos.ORIENTATIONS[typeID][rotation];
    }

    public void updateActive(API api){
        final int currentTetrimino = api.readCPU(Addresses.TetriminoID);
        posx = api.readCPU(Addresses.TetriminoX);
        posy = api.readCPU(Addresses.TetriminoY1);
        typeID = api.readCPU(Addresses.TetriminoID);
    }

    public void setOrientation(int typeID, int rotation){

    }

}
