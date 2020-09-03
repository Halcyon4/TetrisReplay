package com.halcyon.tetris.replay;

import com.halcyon.tetris.ai.PlayField;
import com.halcyon.tetris.bot.PlayerInput;

public class ReplayFrame {

    TetrisReplay replay;

    private int frameIndex;

    private int playerInputIndex,
            playFieldIndex;

    public ReplayFrame(int frameIndex, TetrisReplay replay){
        this.frameIndex = frameIndex;
        this.replay = replay;
    }

    public void setplayerInputIndex(int index){
        playerInputIndex = index;
    }
    public void setPlayfieldIndex(int index){
        playFieldIndex = index;
    }
    public PlayField getPlayfield(){
        return replay.getPlayField(playFieldIndex);
    }
    public PlayerInput getInput(){
        return replay.getPlayerInput(playerInputIndex);
    }
}
