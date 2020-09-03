package com.halcyon.tetris.ai;

import java.util.ArrayList;
import java.util.List;

public final class Tetriminos {

  private static final int EMPTY_SQUARE = 0xEF;
  public static final int NONE = -1;
  public static final int T = 0;
  public static final int J = 1;
  public static final int Z = 2;
  public static final int O = 3;
  public static final int S = 4;
  public static final int L = 5;
  public static final int I = 6;

  public static final int[][][][] PATTERNS = {
    { { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, },    // Td (spawn)
      { {  0, -1 }, { -1,  0 }, {  0,  0 }, {  0,  1 }, },    // Tl    
      { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  0, -1 }, },    // Tu
      { {  0, -1 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, }, }, // Tr   

    { { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  1,  1 }, },    // Jd (spawn)
      { {  0, -1 }, {  0,  0 }, { -1,  1 }, {  0,  1 }, },    // Jl
      { { -1, -1 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, },    // Ju
      { {  0, -1 }, {  1, -1 }, {  0,  0 }, {  0,  1 }, }, }, // Jr   

    { { { -1,  0 }, {  0,  0 }, {  0,  1 }, {  1,  1 }, },    // Zh (spawn) 
      { {  1, -1 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, }, }, // Zv   

    { { { -1,  0 }, {  0,  0 }, { -1,  1 }, {  0,  1 }, }, }, // O  (spawn)   

    { { {  0,  0 }, {  1,  0 }, { -1,  1 }, {  0,  1 }, },    // Sh (spawn)
      { {  0, -1 }, {  0,  0 }, {  1,  0 }, {  1,  1 }, }, }, // Sv   

    { { { -1,  0 }, {  0,  0 }, {  1,  0 }, { -1,  1 }, },    // Ld (spawn)
      { { -1, -1 }, {  0, -1 }, {  0,  0 }, {  0,  1 }, },    // Ll
      { {  1, -1 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, },    // Lu
      { {  0, -1 }, {  0,  0 }, {  0,  1 }, {  1,  1 }, }, }, // Lr      

    { { { -2,  0 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, },    // Ih (spawn)    
      { {  0, -2 }, {  0, -1 }, {  0,  0 }, {  0,  1 }, }, }, // Iv      
  };
  
  public static final int[] ORIENTATION_IDS 
      = { 0x02, 0x03, 0x00, 0x01, 0x07, 0x04, 0x05, 0x06, 0x08, 0x09, 
          0x0A, 0x0B, 0x0C, 0x0E, 0x0F, 0x10, 0x0D, 0x12, 0x11 };
  
  public static final Orientation[][] ORIENTATIONS;
  
  static {
    //ORIENTATIONS is an 2 dimensional array of Orientations.
    //1st dimension has 1 entry per Tetrimino type (piece type)
    ORIENTATIONS = new Orientation[PATTERNS.length][];
    for(int i = 0, idIndex = 0; i < PATTERNS.length; i++) {

      //List for each Piece type construct a list of orientations corresponding to each piece rotation
      List<Orientation> tetriminoOrientations = new ArrayList<>();
      for(int j = 0; j < PATTERNS[i].length; j++) {

        //Create Orientation corresponding to piece rotation
        Orientation activeOrientation = new Orientation();

        //Find orientation limits relative to center
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for(int k = 0; k < PATTERNS[i][j].length; k++) {
          //Consider piece block
          int[] p = PATTERNS[i][j][k];
          activeOrientation.squares[k].x = p[0];
          activeOrientation.squares[k].y = p[1];
          minX = Math.min(minX, p[0]);
          maxX = Math.max(maxX, p[0]);
          maxY = Math.max(maxY, p[1]);
          //lowest Y implied 0
        }

        //Calculate legal positions on playfield
        activeOrientation.minX = -minX;
        activeOrientation.maxX = AI.PLAYFIELD_WIDTH - maxX - 1;
        activeOrientation.maxY = AI.PLAYFIELD_HEIGHT - maxY - 1;

        //Assign ID
        activeOrientation.orientationID = ORIENTATION_IDS[idIndex++];

        //Append to list
        tetriminoOrientations.add(activeOrientation);

      }

      //Append orientations for Tetrimino to Orientation array
      ORIENTATIONS[i] = new Orientation[tetriminoOrientations.size()];
      tetriminoOrientations.toArray(ORIENTATIONS[i]);
    }
  }
  
  private Tetriminos() {    
  }
}
