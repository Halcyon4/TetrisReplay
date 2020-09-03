package com.halcyon.tetris.ai;

public interface IChildFilter {
  public boolean validate(int[][] playfield, int tetriminoType,
      int x, int y, int rotation);
}
