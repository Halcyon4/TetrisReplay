package com.halcyon.tetris.bot;

import nintaco.api.API;
import com.halcyon.tetris.ai.*;
import nintaco.api.GamepadButtons;
import nintaco.input.DeviceDescriptor;
import nintaco.input.gamepad.Gamepad1Descriptor;

public class GameReader {

    API api;
    private final int[] TetriminosTypes = new int[19];
    private int gameState;
    public final int[][] playfield;
    public final PlayfieldUtil playfieldUtil;
    private final int[][] previousPlayfield;


    private final int[] tetriminos = new int[AI.TETRIMINOS_SEARCHED];
    private Tetrimino activeTetrimino = new Tetrimino();
    private PlayField currentPlayField;

    public GameReader(API api, PlayfieldUtil playfieldUtil){
        this.api = api;
        this.playfieldUtil = playfieldUtil;
        playfield = playfieldUtil.createPlayfield();
        previousPlayfield = playfieldUtil.createPlayfield();
    }

    //Initialize tetrimino table from memory
    public void readTetriminoTypes() {
        for(int i = 0; i < 19; i++) {
            TetriminosTypes[i] = api.readCPU(Addresses.TetriminoTypeTable + i);
        }
    }

    public void statusChanged(final String message) {
        gameState = api.readCPU(Addresses.GameState);
    }


    //Check whether game is ongoing
    public boolean isPlaying() {
        return gameState == 4 && api.readCPU(Addresses.PlayState) < 9;
    }

    //Check whether new tetrimino was just spawned
    public boolean justSpawned(Tetrimino activeTetrimino) {
        //Update current tetrimino
        final int playState = api.readCPU(Addresses.PlayState);
        activeTetrimino.updateActive(api);

        return playState == 1 && activeTetrimino.posx == 5 && activeTetrimino.posy == 0
                && activeTetrimino.typeID < TetriminosTypes.length;
    }


    public void readPlayfield() {
        PlayfieldUtil.copyToTarget(playfield, previousPlayfield);

        tetriminos[0] = readTetrimino();
        tetriminos[1] = readNextTetrimino();

        for(int i = 0; i < AI.PLAYFIELD_HEIGHT; i++) {
            playfield[i][10] = 0;
            for (int j = 0; j < AI.PLAYFIELD_WIDTH; j++) {
                if (api.readCPU(Addresses.Playfield + 10 * i + j) == PlayfieldUtil.EMPTY_SQUARE) {
                    playfield[i][j] = Tetriminos.NONE;
                } else {
                    playfield[i][j] = Tetriminos.I;
                    playfield[i][10]++;
                }
            }
        }
    }

    //Check whether tetrimino was just spawned
    public boolean justSpawned() {
        //Update current tetrimino
        final int playState = api.readCPU(Addresses.PlayState);
        activeTetrimino.updateActive(api);

        return playState == 1 && activeTetrimino.posx == 5 && activeTetrimino.posy == 0
                && activeTetrimino.typeID < TetriminosTypes.length;
    }

    public int readTetriminoType() {
        return TetriminosTypes[api.readCPU(Addresses.TetriminoID)];
    }

    public int readNextTetrimino() {
        return TetriminosTypes[api.readCPU(Addresses.NextTetriminoID)];
    }

    public PlayerInput readPlayerInput(){

        boolean[] inputs = new boolean[8];

        for(int i = 0; i < inputs.length; i++){
            inputs[i] = api.readGamepad(0, i);
        }

        return new PlayerInput(inputs);
    }

    public PlayField getCurrentPlayField(){
        currentPlayField = new PlayField(playfield);
        return currentPlayField;
    }

    public GameState readCurrentGameState(){
        GameState state = new GameState();
        PlayField playField = new PlayField(playfield);
        state.playField = playField;
        state.score = readScore();
        state.tetrimino = readTetrimino();
        state.nextTetriminoType = readNextTetrimino();
    }

    public int readScore(){
        return 0;
    }

    public Tetrimino readTetrimino(){
        int tetriminoType = readTetriminoType();

    }

}
