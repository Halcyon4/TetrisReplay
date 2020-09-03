package com.halcyon.tetris.ai;

//Tracks playefield state during search pass
public class State {
  //Position
  public int x;
  public int y;

  //Rotation
  public int rotation;

  //Searched in current pass
  public int visited;

  //Linked states
  public State predecessor; 
  public State next;
  
  public State(int x, int y, int rotation) {
    this.x = x;
    this.y = y;
    this.rotation = rotation;
  }
}
