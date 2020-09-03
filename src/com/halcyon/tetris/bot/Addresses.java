package com.halcyon.tetris.bot;

public interface Addresses {
  int OrientationTable = 0x8A9C;
  int TetriminoTypeTable = 0x993B;
  int SpawnTable = 0x9956;
  int Copyright1 = 0x00C3;
  int Copyright2 = 0x00A8;
  int GameState = 0x00C0;
  int LowCounter = 0x00B1;
  int HighCounter = 0x00B2;
  int TetriminoX = 0x0060;
  int TetriminoY1 = 0x0061;
  int TetriminoY2 = 0x0041;
  int TetriminoID = 0x0062;
  int NextTetriminoID = 0x00BF;
  int FallTimer = 0x0065;
  int Playfield = 0x0400;
  int Level = 0x0064;
  int LevelTableAccess = 0x9808;
  int LinesHigh = 0x0071;
  int LinesLow = 0x0070;
  int PlayState = 0x0068;  
}
