package com.halcyon.tetris.bot;

import com.halcyon.tetris.ai.*;
import nintaco.api.*;

public class TetrisBot {
  
  private static final int EMPTY_SQUARE = 0xEF;
  
  private final API api = ApiSource.getAPI();  
  
  private final AI ai = new AI();
  private final PlayfieldUtil playfieldUtil = new PlayfieldUtil();
  private final int[] tetriminos = new int[AI.TETRIMINOS_SEARCHED];
  private final int[][] playfield = playfieldUtil.createPlayfield();
  private final int[] TetriminosTypes = new int[19];
  private final boolean playFast;  
  
  private int playingDelay;
  private int targetTetriminoY;
  private int startCounter;
  private int movesIndex;
  private boolean moving;
  private State[] states;
  
  public TetrisBot(final boolean playFast) {
    this.playFast = playFast;
  }
  
  public void launch() throws Throwable {
    api.addActivateListener(this::apiEnabled);
    api.addAccessPointListener(this::updateScore, AccessPointType.PreExecute, 
        0x9C35);
    api.addAccessPointListener(this::speedUpDrop, AccessPointType.PreExecute, 
        0x8977);
    api.addAccessPointListener(this::tetriminoYUpdated, 
        AccessPointType.PreWrite, Addresses.TetriminoY1);
    api.addAccessPointListener(this::tetriminoYUpdated, 
        AccessPointType.PreWrite, Addresses.TetriminoY2);
    api.addFrameListener(this::renderFinished);
    api.addStatusListener(this::statusChanged);
    api.run();
  }
  
  private void apiEnabled() {
    readTetriminoTypes();
  }  
  
  private int tetriminoYUpdated(final int type, final int address, 
      final int tetriminoY) {
    
    if (tetriminoY == 0) {
      targetTetriminoY = 0;
    }
    if (moving) {      
      return targetTetriminoY;
    } else {
      return tetriminoY;
    }
  }
  
  private void readTetriminoTypes() {
    for(int i = 0; i < 19; i++) {
      TetriminosTypes[i] = api.readCPU(Addresses.TetriminoTypeTable + i);
    }
  }  
  
  private void resetPlayState(final int gameState) {
    if (gameState != 4) {
      api.writeCPU(Addresses.PlayState, 0);
    }
  }
  
  private int updateScore(final int type, final int address, 
      final int value) {
    // cap the points multiplier at 30 to avoid the kill screen
    if (api.readCPU(0x00A8) > 30) {
      api.writeCPU(0x00A8, 30);
    }
    return -1;
  }

  private int speedUpDrop(final int type, final int address, 
      final int value) {
    api.setX(0x1E);
    return -1;
  }

  private void setTetriminoYAddress(final int address, final int y) {
    targetTetriminoY = y;
    api.writeCPU(address, y);
  }

  private void setTetriminoY(final int y) {
    setTetriminoYAddress(Addresses.TetriminoY1, y);
    setTetriminoYAddress(Addresses.TetriminoY2, y);
  }

  private void makeMove(final int tetriminoType, final State state, 
      final boolean finalMove) {
    
    if (finalMove) { 
      api.writeCPU(0x006E, 0x03);
    }
    api.writeCPU(Addresses.TetriminoX, state.x);
    setTetriminoY(state.y);
    api.writeCPU(Addresses.TetriminoID, 
        Tetriminos.ORIENTATIONS[tetriminoType][state.rotation].orientationID);
  }  
  
  private int readTetrimino() {
    return TetriminosTypes[api.readCPU(Addresses.TetriminoID)];
  }

  private int readNextTetrimino() {
    return TetriminosTypes[api.readCPU(Addresses.NextTetriminoID)];
  }  
  
  private void readPlayfield() {
    tetriminos[0] = readTetrimino();
    tetriminos[1] = readNextTetrimino();
    
    for(int i = 0; i < AI.PLAYFIELD_HEIGHT; i++) {
      playfield[i][10] = 0;
      for (int j = 0; j < AI.PLAYFIELD_WIDTH; j++) {
        if (api.readCPU(Addresses.Playfield + 10 * i + j) == EMPTY_SQUARE) {
          playfield[i][j] = Tetriminos.NONE;
        } else {
          playfield[i][j] = Tetriminos.I;
          playfield[i][10]++;
        }
      }
    }    
  }
  
  private boolean spawned() {
    final int currentTetrimino = api.readCPU(Addresses.TetriminoID);
    final int playState = api.readCPU(Addresses.PlayState);
    final int tetriminoX = api.readCPU(Addresses.TetriminoX);
    final int tetriminoY = api.readCPU(Addresses.TetriminoY1); 
    
    return playState == 1 && tetriminoX == 5 && tetriminoY == 0 
        && currentTetrimino < TetriminosTypes.length;
  }
  
  private boolean isPlaying(final int gameState) {
    return gameState == 4 && api.readCPU(Addresses.PlayState) < 9;
  }  
  
  private void pressStart() {
    if (startCounter > 0) {
      startCounter--;
    } else {
      startCounter = 10;
    } 
    if (startCounter >= 5) {
      api.writeGamepad(0, GamepadButtons.Start, true);
    }    
  }  
  
  private void skipCopyrightScreen(final int gameState) {
    if (gameState == 0) {
      if (api.readCPU(Addresses.Copyright1) > 1) {
        api.writeCPU(Addresses.Copyright1, 0);
      } else if (api.readCPU(Addresses.Copyright2) > 2) {
        api.writeCPU(Addresses.Copyright2, 1);
      }
    }
  }
  
  private void skipTitleAndDemoScreens(final int gameState) {
    if (gameState == 1 || gameState == 5) {
      pressStart();   
    } else {
      startCounter = 0;
    }
  }
  
  private void renderFinished() {
    final int gameState = api.readCPU(Addresses.GameState);
    skipCopyrightScreen(gameState);
    skipTitleAndDemoScreens(gameState);
    resetPlayState(gameState);

    if (isPlaying(gameState)) {
      if (playingDelay > 0) {
        playingDelay--;
      } else if (playFast) {
        // skip line clearing animation
        if (api.readCPU(Addresses.PlayState) == 4) {
          api.writeCPU(Addresses.PlayState, 5);
        }
        if (spawned()) {
          readPlayfield();
          final State state = ai.search(playfield, tetriminos);
          if (state != null) {
            moving = true;
            makeMove(tetriminos[0], state, true);
            moving = false;
          }
        }
      } else {
        if (moving && movesIndex < states.length) {
          makeMove(tetriminos[0], states[movesIndex],
              movesIndex == states.length - 1);
          movesIndex++;
        } else {          
          moving = false;
          if (spawned()) {
            readPlayfield();
            final State state = ai.search(playfield, tetriminos);
            if (state != null) {
              states = ai.buildStatesList(state);
              movesIndex = 0;
              moving = true;
            }
          }
        }
      }
    } else {
      states = null;
      moving = false;
      playingDelay = 16;
    } 
  }
  
  private void statusChanged(final String message) {
    System.out.println(message);
  }
  
  public static void main(final String... args) throws Throwable {
    ApiSource.initRemoteAPI("localhost", 9999);    
    new TetrisBot(args.length > 0 && "fast".equals(args[0].toLowerCase()))
        .launch();
  }
}
