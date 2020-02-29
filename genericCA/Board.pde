import java.lang.Math;

/**
*Represents a board for the cellular automaton
*/
public class Board{
  private Cell[][] cells; 
  private boolean closedBoundaries; //Toroidal or not
  int xCells, yCells;
  
  Board(int xCells, int yCells, boolean closedBoundaries){
    cells = new Cell[xCells][yCells];
    for(int x = 0; x < xCells; ++x){
      for(int y = 0; y<yCells; ++y){
         cells[x][y] = new Cell(0); 
      }
    }
    this.closedBoundaries = closedBoundaries;
    this.xCells = xCells;
    this.yCells = yCells;
  }
  
  /**
  *Copies other Board into current object
  * @param other the object to copy
  */
  public void copyCells(Board other){
     for(int x = 0; x < xCells; ++x){
        for(int y=0; y < yCells; ++y){
           this.getCell(x,y).copy(other.getCell(x,y));
        }
     }
  }
  
  /**
  * Returns proportion of cells in state given as argument
  * @param state the state of which proportion is to be calculated
  * @return the proportion of cells in that state
  */
  public float proportionOfState(int state){
    int count =0;
    for(int x = 0; x < xCells; ++x){
      for(int y = 0; y<yCells; ++y){
         if (cells[x][y].getState() == state){
           count++;
         }
      }
    }
    return (float)count/(float)(xCells*yCells);
  }
  
  
  Cell getCell(int x, int y){
    return cells[x][y];
  }
  
  /**
  *Returns the number of neighbours of the cell in x,y 
  *in a state and with a certain age
  * @param x x coordinate of the cell
  * @param y y-coordinate of the cell
  * @param the state in which neighbours have to be to be counted
  * @param the age in which neighbours have to be to be counted
  * @return the number of neighbours
  */
  public int numNeighbours(int x, int y, int state, int age){
    int neighbours = 0;
      for (int deltaX=-1; deltaX<=1;deltaX++) {
        for (int deltaY=-1; deltaY<=1 ;deltaY++) {  
          int xNeighbour = x+deltaX;
          int yNeighbour = y + deltaY;
          if(deltaX == 0 && deltaY == 0){
            continue; //don't process the cell itself.
          }
          if(!closedBoundaries){
            xNeighbour = floorMod(xNeighbour,xCells);
            yNeighbour = floorMod(yNeighbour, yCells);
          }
          if (((xNeighbour>=0)&&(xNeighbour<xCells))&&((yNeighbour>=0)&&(yNeighbour<yCells))) { // Make sure you are not out of bounds (in case boundaries are closed)
              if (cells[xNeighbour][yNeighbour].getState()==state && cells[xNeighbour][yNeighbour].getAge()==age){
                neighbours++;
              }
          }
        }//end y for
      }//end x for
      return neighbours;
  }
  
  //utility method
  private int floorMod(int dividend, int divisor){
     return  ((dividend%divisor)+divisor)%divisor;
  }
  
  /**
  *Resets the board, i.e. resets all the cells in it.
  */
  private void reset(){
     for(int x=0; x<xCells; ++x){
        for(int y = 0; y<yCells; ++y){
           cells[x][y].reset(); 
        }
     } 
  }
}
