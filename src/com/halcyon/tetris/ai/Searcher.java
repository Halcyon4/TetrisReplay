package com.halcyon.tetris.ai;

public class Searcher {
  
  private static int globalMark = 1;


  private State[][][] states;
  private Queue queue = new Queue();
  private ISearchListener searchListener;
  private IChildFilter positionValidator;
  
  public Searcher(
      ISearchListener searchListener, IChildFilter positionValidator) {
    this.searchListener = searchListener;
    this.positionValidator = positionValidator;
    createStates();
  }
  
  private void createStates() {

    //Create an array representing every block on the playfield with every possible rotation
    states = new State[AI.PLAYFIELD_HEIGHT][AI.PLAYFIELD_WIDTH][4];
    for(int y = 0; y < AI.PLAYFIELD_HEIGHT; y++) {
      for(int x = 0; x < AI.PLAYFIELD_WIDTH; x++) {        
        for(int rotation = 0; rotation < 4; rotation++) { 
          states[y][x][rotation] = new State(x, y, rotation);
        }
      }
    }
  }
  
  private void lockTetrimino(
      int[][] playfield, int tetriminoType, int id, State state) {

    //Tetrimino point representation
    Point[] squares = Tetriminos.ORIENTATIONS[tetriminoType][state.rotation]
        .squares;

    //Write each point of tetrimino into playfield
    for(int i = 0; i < 4; i++) {
      Point square = squares[i];
      int y = state.y + square.y;
      if (y >= 0) {
        playfield[y][state.x + square.x] = tetriminoType;
        playfield[y][AI.PLAYFIELD_WIDTH]++;
      }
    }

    //Raise Search Result event
    searchListener.handleResult(playfield, tetriminoType, id, state);

    //Remove each point of the terimino from the playfield again (??? okay?)
    for(int i = 0; i < 4; i++) {
      Point square = squares[i];
      int y = state.y + square.y;
      if (y >= 0) {
        playfield[y][state.x + square.x] = Tetriminos.NONE;
        playfield[y][AI.PLAYFIELD_WIDTH]--;
      }
    }
  }
  
  // Add a state defined by position/rotation as a child to input state
  private boolean addChild(int[][] playfield, int tetriminoType, int mark, 
      State state, int x, int y, int rotation) {

    //Check whether child position is within playfield bounds
    Orientation orientation = Tetriminos.ORIENTATIONS[tetriminoType][rotation];
    if (x < orientation.minX || x > orientation.maxX || y > orientation.maxY) {
      //Orientation outside playfield bounds
      return false;
    }


    //Fetch child node definition from pre-generated matrix (child = new search node)
    State childNode = states[y][x][rotation];
    //Check whether position has already been validated searched
    if (childNode.visited == mark) {
      return true;
    }

    //Check whether tetrimino squares are occupied at child position
    Point[] squares = orientation.squares;
    for(int i = 0; i < 4; i++) {
      Point square = squares[i];
      int playfieldY = y + square.y;
      if (playfieldY >= 0 //Redundant? Do you not trust your own assumption in the orientation matrix creation, that y always >= 0?
          && playfield[playfieldY][x + square.x] != Tetriminos.NONE) {
        return false;
      }
    }

    //What does it do? Supposed to check blocks of tetrimino components?
    if (positionValidator != null && !positionValidator.validate(
        playfield, tetriminoType, x, y, rotation)) {
      return true;
    }

    childNode.visited = mark; //note visitation index
    childNode.predecessor = state; //link child to parent (input)
        
    queue.enqueue(childNode); //Put child into queue
    return true; 
  }  

  //Finds possible end locations for tetrimino, and triggers Search Listener for each possibility
  public boolean search(int[][] playfield, int tetriminoType, int id) {

    //Rotations available for tetrimino
    int maxRotation = Tetriminos.ORIENTATIONS[tetriminoType].length - 1;

    int mark = globalMark++;

    //start at top middle of board, check if position is valid for input tetrimino
    if (!addChild(playfield, tetriminoType, mark, null, 5, 0, 0)) {
      return false;
    }    

    //Explore board, looping through queue
    while(queue.isNotEmpty()) {
      //Get next (unexplored node) in queue
      State state = queue.dequeue();

      //If rotations exist
      if (maxRotation != 0) {
        // Explore previous rotation
        addChild(playfield, tetriminoType, mark, state, state.x, state.y,
            state.rotation == 0 ? maxRotation : state.rotation - 1);

        //If there is not only 1 rotation
        if (maxRotation != 1) {
          //Explore next rotation
          addChild(playfield, tetriminoType, mark, state, state.x, state.y, 
              state.rotation == maxRotation ? 0 : state.rotation + 1);
        }
      }

      //Explore node to the left
      addChild(playfield, tetriminoType, mark, state, 
          state.x - 1, state.y, state.rotation);
      //Explore node to the right
      addChild(playfield, tetriminoType, mark, state, 
          state.x + 1, state.y, state.rotation);

      //Explore node below
      if (!addChild(playfield, tetriminoType, mark, state,
          state.x, state.y + 1, state.rotation)) {
        lockTetrimino(playfield, tetriminoType, id, state);
      }
    }

    return true;
  }
}
