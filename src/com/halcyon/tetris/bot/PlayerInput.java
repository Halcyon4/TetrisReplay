package com.halcyon.tetris.bot;

import nintaco.api.GamepadButtons;
import org.omg.PortableInterceptor.NON_EXISTENT;

public class PlayerInput {

    public static final int
            NO_INPUT = -1,
            ROTATE_CLOCKWISE = GamepadButtons.B,
            ROTATE_COUNTER_CLOCKWISE = GamepadButtons.A,
            DIRECTION_LEFT = GamepadButtons.Left,
            DIRECTION_RIGHT = GamepadButtons.Right,
            DIRECTION_DOWN = GamepadButtons.Down;


    private int rotation;
    private int direction;

    private final boolean[] rawInput = new boolean[8];

    public PlayerInput(){

    }
    public PlayerInput(int rotation, int direction){
        this.rotation = rotation;
        this.direction = direction;
    }
    public PlayerInput(boolean[] rawInput){
        setRawInput(rawInput);
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int newRotation){
        rotation = newRotation;
    }
    public void setDirection(int newDirection){
        direction = newDirection;
    }

    public void setRawInput(boolean[] input){
        setRawInput(input, 0);
    }
    public void setRawInput(boolean[] input, int startIndex){
        for(int i = startIndex; i < rawInput.length && i < input.length; i++){
            rawInput[i] = input[i];
        }

        if( rawInput[GamepadButtons.A] && !rawInput[GamepadButtons.B] ){
            setRotation(ROTATE_COUNTER_CLOCKWISE);
        } else if (rawInput[GamepadButtons.B] && !rawInput[GamepadButtons.A]){
            setRotation(ROTATE_CLOCKWISE);
        } else {
            setRotation(NO_INPUT);
        }

        if( rawInput[GamepadButtons.Left] && !rawInput[GamepadButtons.Right] ){
            setDirection(DIRECTION_LEFT);
        }else if( rawInput[GamepadButtons.Right] && !rawInput[GamepadButtons.Left] ){
            setDirection(DIRECTION_RIGHT);
        }else if(rawInput[GamepadButtons.Down] && !rawInput[GamepadButtons.Left] && !rawInput[GamepadButtons.Right]){
            setDirection(DIRECTION_DOWN);
        }else{
            setDirection(NO_INPUT);
        }

    }

    public boolean compare(PlayerInput other){
        for(int i = 0; i < rawInput.length; i++){
          if(rawInput[i] != other.rawInput[i]) return false;
        }
        if ( rotation != other.rotation ) return false;
        if ( direction != other.direction ) return false;
        return true;
    }

    /*    public int getRotationButton(){
        switch(rotation){
            case ROTATE_CLOCKWISE:
                return GamepadButtons.A;
            case ROTATE_COUNTER_CLOCKWISE:
                return GamepadButtons.B;
            default:
                return -1;
        }
    }

    public int getDirectionButton(){
        switch(direction){
            case DIRECTION_LEFT:
                return GamepadButtons.Left;
            case DIRECTION_RIGHT:
                return GamepadButtons.Right;
            case DIRECTION_DOWN:
                return GamepadButtons.Down;
            default:
                return -1;
        }
    }*/


}
