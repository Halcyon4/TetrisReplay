/*
 * Nintendo Tetris AI
 * Copyright (C) 2014 meatfighter.com
 *
 * This file is part of Nintendo Tetris AI. 
 *
 * Nintendo Tetris AI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Nintendo Tetris AI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.halcyon.tetris.ai;

public class AI {

  public static final int PLAYFIELD_WIDTH = 10;
  public static final int PLAYFIELD_HEIGHT = 20;
  public static final int TETRIMINOS_SEARCHED = 2;  
  
  private static final double[] WEIGHTS = {
    1.0,
    12.885008263218383,
    15.842707182438396,
    26.89449650779595,
    27.616914062397015,
    30.18511071927904,
  };  
  
  private Searcher[] searchers; 
  private int[] tetriminoIndices;
  private PlayfieldUtil playfieldUtil = new PlayfieldUtil();
  private PlayfieldEvaluation e = new PlayfieldEvaluation();
  private int totalRows;
  private int totalDropHeight;  
  private double bestFitness;
  private State bestResult;
  private State result0;
  
  private ISearchListener searchListener = new ISearchListener() {
    @Override
    public void handleResult(
        int[][] playfield, int tetriminoType, int id, State state) {
      
      if (id == 0) {
        result0 = state;
      }

      Orientation orientation 
          = Tetriminos.ORIENTATIONS[tetriminoType][state.rotation];
      int rows = playfieldUtil.clearRows(playfield, state.y);
      int originalTotalRows = totalRows;
      int originalTotalDropHeight = totalDropHeight;
      totalRows += rows;
      totalDropHeight += orientation.maxY - state.y;

      int nextID = id + 1;

      if (nextID == tetriminoIndices.length) {

        playfieldUtil.evaluatePlayfield(playfield, e);

        double fitness = computeFitness();
        if (fitness < bestFitness) {
          bestFitness = fitness;
          bestResult = result0;
        }
      } else {
        searchers[nextID].search(playfield, tetriminoIndices[nextID], nextID);
      }

      totalDropHeight = originalTotalDropHeight;
      totalRows = originalTotalRows;
      playfieldUtil.restoreRows(playfield, rows);
    }
  };
  
  public AI() {
    this(null);
  }
  
  public AI(IChildFilter positionValidator) {
    searchers = new Searcher[AI.TETRIMINOS_SEARCHED];
    for(int i = 0; i < AI.TETRIMINOS_SEARCHED; i++) {
      searchers[i] = new Searcher(searchListener, positionValidator);
    }
  }
  
  private double computeFitness() {
    return WEIGHTS[0] * totalRows
         + WEIGHTS[1] * totalDropHeight
         + WEIGHTS[2] * e.wells
         + WEIGHTS[3] * e.holes                 
         + WEIGHTS[4] * e.columnTransitions
         + WEIGHTS[5] * e.rowTransitions;         
  }
  
  public State search(int[][] playfield, int[] tetriminoIndices) {

    this.tetriminoIndices = tetriminoIndices;
    bestResult = null;
    bestFitness = Double.MAX_VALUE;

    searchers[0].search(playfield, tetriminoIndices[0], 0);

    return bestResult;
  }  
  
  public State[] buildStatesList(State state) {
    State s = state;
    int count = 0;      
    while(s != null) {
      count++;
      s = s.predecessor;
    }
    State[] states = new State[count];
    while(state != null) {
      states[--count] = state;
      state = state.predecessor;
    }
    return states;
  }
}
