package com.halcyon.tetris.ai;

import com.halcyon.tetris.bot.Addresses;
import com.sun.org.apache.xpath.internal.operations.Or;
import nintaco.api.API;

public class PlayfieldUtil {

  public static final int EMPTY_SQUARE = 0xEF;
  
  private final int[][] spareRows 
      = new int[8 * AI.TETRIMINOS_SEARCHED][AI.PLAYFIELD_WIDTH + 1];  //Extra width to count column height
  private final int[] columnDepths = new int[AI.PLAYFIELD_WIDTH];
  private int spareIndex;
  
  public PlayfieldUtil() {
    for(int y = 0; y < spareRows.length; y++) {
      for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
        spareRows[y][x] = Tetriminos.NONE;
      }
    }
  }  
  
  public int[][] createPlayfield() {
    int[][] playfield = new int[AI.PLAYFIELD_HEIGHT][AI.PLAYFIELD_WIDTH + 1];
    for(int y = 0; y < AI.PLAYFIELD_HEIGHT; y++) {
      for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
        playfield[y][x] = Tetriminos.NONE;
      }
    }
    return playfield;
  }
  
  public void lockTetrimino(
      int[][] playfield, int tetriminoType, State state) {
    
    Point[] squares = Tetriminos.ORIENTATIONS[tetriminoType][state.rotation]
        .squares;
    for(int i = 0; i < 4; i++) {
      Point square = squares[i];
      int y = state.y + square.y;
      if (y >= 0) {
        playfield[y][state.x + square.x] = tetriminoType;
        playfield[y][AI.PLAYFIELD_WIDTH]++;
      }
    }
    
    int startRow = state.y - 2;
    int endRow = state.y + 1;
    
    if (startRow < 1) {
      startRow = 1;
    }
    if (endRow >= AI.PLAYFIELD_HEIGHT) {
      endRow = AI.PLAYFIELD_HEIGHT - 1;
    }
    
    for(int y = startRow; y <= endRow; y++) {
      if (playfield[y][AI.PLAYFIELD_WIDTH] == AI.PLAYFIELD_WIDTH) { 
        int[] clearedRow = playfield[y];
        for(int i = y; i > 0; i--) {
          playfield[i] = playfield[i - 1];
        }        
        for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
          clearedRow[x] = Tetriminos.NONE;          
        }
        clearedRow[AI.PLAYFIELD_WIDTH] = 0;
        playfield[0] = clearedRow;
      }
    }
  }  
  
  public void evaluatePlayfield(
      int[][] playfield, PlayfieldEvaluation e) {
    
    for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
      columnDepths[x] = AI.PLAYFIELD_HEIGHT - 1;
      for(int y = 0; y < AI.PLAYFIELD_HEIGHT; y++) {
        if (playfield[y][x] != Tetriminos.NONE) {
          columnDepths[x] = y;
          break;
        }
      }      
    }
    
    e.wells = 0;
    for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
      int minY = 0;
      if (x == 0) {
        minY = columnDepths[1];
      } else if (x == AI.PLAYFIELD_WIDTH - 1) {
        minY = columnDepths[AI.PLAYFIELD_WIDTH - 2];
      } else {
        minY = Math.max(columnDepths[x - 1], columnDepths[x + 1]);
      }
      for(int y = columnDepths[x]; y >= minY; y--) {
        if ((x == 0 || playfield[y][x - 1] != Tetriminos.NONE)
            && (x == AI.PLAYFIELD_WIDTH - 1 
                || playfield[y][x + 1] != Tetriminos.NONE)) {
          e.wells++;
        } 
      }
    }
    
    e.holes = 0;
    e.columnTransitions = 0;
    for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
      boolean solid = true;
      for(int y = columnDepths[x] + 1; y < AI.PLAYFIELD_HEIGHT; y++) {      
        if (playfield[y][x] == Tetriminos.NONE) {
          if (playfield[y - 1][x] != Tetriminos.NONE) {
            e.holes++;
          }
          if (solid) {
            solid = false;
            e.columnTransitions++;
          }
        } else if (!solid) {
          solid = true;
          e.columnTransitions++;
        }       
      }
    }
    
    e.rowTransitions = 0;
    for(int y = 0; y < AI.PLAYFIELD_HEIGHT; y++) {
      boolean solidFound = false;
      boolean solid = true;
      int transitions = 0;
      for(int x = 0; x <= AI.PLAYFIELD_WIDTH; x++) {
        if (x == AI.PLAYFIELD_WIDTH) {
          if (!solid) {
            transitions++;
          }
        } else {          
          if (playfield[y][x] == Tetriminos.NONE) {            
            if (solid) {
              solid = false;
              transitions++;
            }
          } else {          
            solidFound = true;
            if (!solid) {
              solid = true;
              transitions++;                           
            }
          }
        }
      }
      if (solidFound) {        
        e.rowTransitions += transitions;
      }
    }    
  }
  
  public int clearRows(int[][] playfield, int tetriminoY) {
    
    int rows = 0;
    int startRow = tetriminoY - 2;
    int endRow = tetriminoY + 1;
    
    if (startRow < 1) {
      startRow = 1;
    }    
    if (endRow >= AI.PLAYFIELD_HEIGHT) {
      endRow = AI.PLAYFIELD_HEIGHT - 1;
    }
    
    for(int y = startRow; y <= endRow; y++) {
      if (playfield[y][AI.PLAYFIELD_WIDTH] == AI.PLAYFIELD_WIDTH) {
        rows++;
        clearRow(playfield, y);
      }
    }
    
    return rows;
  }
  
  private void clearRow(int[][] playfield, int y) {
            
    int[] clearedRow = playfield[y];
    clearedRow[AI.PLAYFIELD_WIDTH] = y;
    for(int i = y; i > 0; i--) {
      playfield[i] = playfield[i - 1];
    }
    playfield[0] = spareRows[spareIndex];
    playfield[0][AI.PLAYFIELD_WIDTH] = 0;
    
    spareRows[spareIndex++] = clearedRow;    
  }
  
  private void restoreRow(int[][] playfield) {
        
    int[] restoredRow = spareRows[--spareIndex];
    int y = restoredRow[AI.PLAYFIELD_WIDTH];
    
    spareRows[spareIndex] = playfield[0];
    
    for(int i = 0; i < y; i++) {
      playfield[i] = playfield[i + 1];
    }
    restoredRow[AI.PLAYFIELD_WIDTH] = AI.PLAYFIELD_WIDTH;
    playfield[y] = restoredRow;
  }
  
  public void restoreRows(int[][] playfield, int rows) {    
    for(int i = 0; i < rows; i++) {
      restoreRow(playfield);
    }    
  }

  public static void copyToTarget(int[][] source, int[][] target){
    for(int y = 0; y < AI.PLAYFIELD_HEIGHT; y++) {
      for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {
        target[y][x] = source[y][x];
      }
    }
  }

}
