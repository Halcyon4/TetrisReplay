package com.halcyon.tetris.replay;


import com.halcyon.tetris.ai.PlayField;
import com.halcyon.tetris.ai.PlayfieldUtil;
import com.halcyon.tetris.ai.Tetrimino;
import com.halcyon.tetris.bot.PlayerInput;

import java.util.ArrayList;

public class TetrisReplay {

    private int replayFrameCounter = 0;
    private int uniquePlayFieldCounter = 0;
    private int uniquePlayerInputCounter = 0;

    private ReplayFrame currentFrame;
    private ReplayFrame previousFrame;
    private PlayerInput currentInput;
    private PlayField currentPlayField;
    private GameState currenentGameState;

    private final ArrayList<ReplayFrame> frames = new ArrayList<ReplayFrame>();
    private final ArrayList<PlayField> playFields = new ArrayList<PlayField>();
    private final ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>();

    public TetrisReplay(){

    }

    public void beginReplayCapture(){
        startNextFrame();
    }

    public void progressFrame(){
        //Set frame playfield
        if( currentPlayField == null){
            currentPlayField = previousFrame.getPlayfield();
        }
        if( !previousFrame.getPlayfield().compare(currentPlayField) ){
            addPlayField(currentPlayField);
        }
        currentFrame.setPlayfieldIndex(uniquePlayFieldCounter - 1);
        currentPlayField = null;

        //Set frame player input
        if( currentInput== null){
            currentInput = previousFrame.getInput();
        }
        if( !previousFrame.getInput().compare(currentInput) ){
            addPlayerInput(currentInput);
        }
        currentFrame.setplayerInputIndex(uniquePlayerInputCounter - 1);
        currentInput = null;

        //Save frame
        frames.add(currentFrame);

        //Start next frame
        startNextFrame();
    }

    private void startNextFrame(){
        if(currentFrame != null){
            frames.add(currentFrame);
        }
        previousFrame = currentFrame;
        currentFrame = new ReplayFrame(replayFrameCounter, this);
        replayFrameCounter++;
    }

    public void setCurrentInput(PlayerInput input) {
        this.currentInput = input;
    }
    public void setCurrentPlayField(PlayField playField){
        this.currentPlayField = playField;
    }

    public PlayField getPlayField(int index){
        return playFields.get(index);
    }
    public PlayerInput getPlayerInput(int index){
        return playerInputs.get(index);
    }

    private void addPlayField(PlayField playField){
        playFields.add(playField);
        uniquePlayFieldCounter++;
    }
    private void addPlayerInput(PlayerInput input){
        playerInputs.add(input);
        uniquePlayerInputCounter++;
    }



}
