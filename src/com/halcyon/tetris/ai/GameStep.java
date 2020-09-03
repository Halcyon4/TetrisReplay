package com.halcyon.tetris.ai;
import com.halcyon.tetris.bot.Addresses;
import com.halcyon.tetris.bot.GameReader;
import com.halcyon.tetris.bot.PlayerInput;

public class GameStep {

    private int
            newTetrimino,
            nextTetrimino;

    private Orientation
            newOrientation = new Orientation();

    private PlayerInput
            playerInput = new PlayerInput();

    public GameStep(){

    }

    public void readGameStep(GameReader reader) {
        if(reader.justSpawned()){
            newTetrimino = reader.readTetrimino();
            nextTetrimino = reader.readNextTetrimino();
        }

        reader.readPlayerInput();

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


}
