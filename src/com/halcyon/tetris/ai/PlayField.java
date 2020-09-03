package com.halcyon.tetris.ai;

public class PlayField {

    public int[][] blockStates;

    public PlayField() {

    }
    public PlayField(int[][] blockStates){
        this.blockStates = blockStates;
    }

    //Checks if blockstate arrays are equal
    public boolean compare(PlayField other){
        if (this.blockStates.length != other.blockStates.length) return false;

        for(int y = 0; y < this.blockStates.length; y++){
            if (this.blockStates[y].length != other.blockStates[y].length) return false;

            for(int x = 0; x < this.blockStates[y].length; x++){
                if (this.blockStates[y][x] != other.blockStates[y][x]) return false;
            }
        }
        return true;
    }
}
