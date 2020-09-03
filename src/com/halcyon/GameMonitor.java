package com.halcyon;

import com.halcyon.tetris.replay.TetrisReplay;
import nintaco.api.AccessPointType;

import nintaco.api.API;
import nintaco.api.ApiSource;
import nintaco.api.GamepadButtons;

import com.halcyon.tetris.ai.*;
import com.halcyon.tetris.bot.*;

public class GameMonitor {


    private final API api = ApiSource.getAPI();

    private final ai = new AI();
    private final PlayfieldUtil playfieldUtil = new PlayfieldUtil();
    private final boolean playFast = false;

    private int playingDelay;
    private int targetTetriminoY;
    private int startCounter;
    private int movesIndex;
    private boolean moving;
    private State[] states;

    //My vars
    private boolean recordingReplay = false;
    private final GameReader gameReader = new GameReader(api, playfieldUtil);
    private PlayerInput currentPlayerInput;
    TetrisReplay replay;

    public void launch() throws Throwable {
        //Run on program start
        api.addActivateListener(this::apiEnabled);

        //Run before instruction executed. Listen for score update
        api.addAccessPointListener(this::updateScore, AccessPointType.PreExecute,
                0x9C35);

        //Check game state after execute
        api.addAccessPointListener(this::checkGameState, AccessPointType.PostExecute);

//        api.addAccessPointListener(this::speedUpDrop, AccessPointType.PreExecute,
//                0x8977);

        //Interfere before CPU write to disable input
        //api.addAccessPointListener(this::tetriminoYUpdated,
        //AccessPointType.PreWrite, Addresses.TetriminoY1);

        //Interfere before CPU write to disable input
        //api.addAccessPointListener(this::tetriminoYUpdated,
        //        AccessPointType.PreWrite, Addresses.TetriminoY2);

        //New block
        api.addAccessPointListener(this::tetriminoPlaced, AccessPointType.PostWrite, Addresses.NextTetriminoID);

        //Record user input
        api.addControllersListener(this::updatePlayerInput);

        //Pre render
        api.addScanlineListener(this::preRender, -1);

        //Frame rendered, update replay
        api.addFrameListener(this::postRender);

        //Status changed, check if game over
        api.addStatusListener(gameReader::statusChanged);
        api.run();
    }


    // Start of API
    private void apiEnabled() {
        gameReader.readTetriminoTypes();
    }

    private void updatePlayerInput(){
        currentPlayerInput = gameReader.readPlayerInput();
    }

    private void resetPlayState(final int gameState) {
        if (gameState != 4) {
            api.writeCPU(Addresses.PlayState, 0);
        }
    }

    private int tetriminoPlaced(int accessPointType, int address, int value) {

    }

    private void preRender(int i){
        //Update frame for replay
    }
    private void postRender(){
        finishFrame();
    }

    private void finishFrame() {
        replay.progressFrame();
    }

    private void updateReplay(){
        replay.setCurrentInput(gameReader.readPlayerInput());
        replay.setCurrentPlayField(gameReader.getCurrentPlayField());
    }


    private void saveReplay(){

    }


}
