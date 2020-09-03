package com.halcyon.tetris.ai;

import com.sun.corba.se.impl.ior.ObjectReferenceFactoryImpl;

public class Orientation {
  //Represents the position of a tetrimino's blocks, relative to it's center block position

  public Point[] squares = new Point[4];
  public int minX;
  public int maxX;
  public int maxY;
  public int orientationID;
  
  public Orientation() {
    for(int i = 0; i < 4; i++) {
      squares[i] = new Point();
    }
  }

}
