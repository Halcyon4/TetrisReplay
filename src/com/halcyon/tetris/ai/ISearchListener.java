package com.halcyon.tetris.ai;

public interface ISearchListener {
  
  public void handleResult(int[][] playfield, int tetriminoType, 
      int id, State state);
}
