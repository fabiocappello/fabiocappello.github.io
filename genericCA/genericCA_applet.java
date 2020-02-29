import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import javax.swing.JOptionPane; 
import java.lang.Math; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class genericCA_applet extends PApplet {




//Size of a cell in the screen
int cellSize = 5;

// Timer
int interval = 100; // Interval between iterations
int lastTime = 0;   // Stores last iteration time

int spawned = 1; //Type of cell spawned in edit mode
int SETUP = 1;
int mode = 0; //current modality

//Colors of cells
int dead = color(0);
ArrayList<Integer> ageColorsB;
ArrayList<Integer> ageColorsC;

//PrintWriter writer;

ControlP5 cp5;

//Cellular automaton variables
GenericCA cellularAutomaton;
Board tmpBoard;

//Set the age colors variables to a gradient, with a number of steps equal to the number of ages.
public void setAgesColors(int ages){
  ageColorsB = gradient(255,0,0, 90,0,80, ages);
  ageColorsC = gradient(0,255,0, 0,90,80, ages);
}

// Pause
boolean pause = false;

//Sets the applet up
public void setup() {
  try{
  //writer = new PrintWriter("simulation_results.txt", "UTF-8");
  }catch(Exception e){e.printStackTrace();}
  //draw grid  
  size (896, 504);
  stroke(48);
  background(0);

  //Number of cells per dimension
  int xCells = width/cellSize;
  int yCells = height/cellSize;

  //Create the cellular automaton and initialise other variables
  cellularAutomaton = new GenericCA(xCells, yCells);       
  setAgesColors(cellularAutomaton.getAgesNumber());
  tmpBoard= new Board(xCells, yCells, true);
  
  //initialize setup screen
  initSetupScreen();
  
}

//Initialise configuration screen buttons
public void initSetupScreen(){
  cp5 = new ControlP5(this);
  int xSpacing = 100; int xStart = 20;
  int ySpacing = 60; int yStart = 150;
  cp5.addTextfield("Born B").setPosition(xStart,yStart).setSize(70,40).setText(cellularAutomaton.bornB);
  cp5.addTextfield("Survive B").setPosition(xStart,yStart+ySpacing).setSize(70,40).setText(cellularAutomaton.surviveB);
  cp5.addTextfield("Born C").setPosition(xStart+ xSpacing,yStart).setSize(70,40).setText(cellularAutomaton.bornC);
  cp5.addTextfield("Survive C").setPosition(xStart + xSpacing,yStart+ySpacing).setSize(70,40).setText(cellularAutomaton.surviveC);
  cp5.addTextfield("Ages").setPosition(xStart + xSpacing,yStart+2*ySpacing).setSize(70,40).setText(String.valueOf(cellularAutomaton.agesNumber));
  cp5.addTextfield("B to C").setPosition(xStart + 2*xSpacing,yStart).setSize(70,40).setText(cellularAutomaton.BtoC);
  cp5.addTextfield("C to B").setPosition(xStart + 2 *xSpacing,yStart+ySpacing).setSize(70,40).setText(cellularAutomaton.CtoB);
  cp5.addTextfield("Born probability B").setPosition(xStart + 4*xSpacing, yStart + 0*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.bornProbabilityB));
  cp5.addTextfield("Survive probability B").setPosition(xStart + 4*xSpacing, yStart +ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.surviveProbabilityB));
  cp5.addTextfield("Death probability B").setPosition(xStart + 4*xSpacing, yStart +2*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.dieProbabilityB));
    cp5.addTextfield("Born probability C").setPosition(xStart + 5*xSpacing, yStart + 0*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.bornProbabilityC));
  cp5.addTextfield("Survive probability C").setPosition(xStart + 5*xSpacing, yStart +ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.surviveProbabilityC));
  cp5.addTextfield("Death probability C").setPosition(xStart + 5*xSpacing, yStart +2*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.dieProbabilityC));

  
  cp5.addTextfield("B to C probability").setPosition(xStart + 6*xSpacing, yStart +2*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.bToCProbability));
  cp5.addTextfield("C to B probability").setPosition(xStart + 7*xSpacing, yStart +2*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.cToBProbability));
  
  
  cp5.addTextfield("Random birth prob B").setPosition(xStart + 6*xSpacing,yStart + 0*ySpacing).setSize(70,40).setText(String.format("%f",cellularAutomaton.randomBirthProbabilityB));
  cp5.addTextfield("Random birth prob C").setPosition(xStart + 6*xSpacing,yStart + 1 *ySpacing).setSize(70,40).setText(String.format("%f", cellularAutomaton.randomBirthProbabilityC));
  
  
  cp5.addTextfield("Initial proportion of B cells").setPosition(xStart+7*xSpacing,yStart).setSize(70,40).setText(String.valueOf(cellularAutomaton.initialProportionB));
  cp5.addTextfield("Initial proportion of C cells").setPosition(xStart+7*xSpacing,yStart + ySpacing).setSize(70,40).setText(String.valueOf(cellularAutomaton.initialProportionC));

  cp5.addButton("apply").setPosition(xStart+6*xSpacing, yStart + 4*ySpacing).setSize(100,30);
  
  cp5.hide();
  
}


//Drawing iteration
public void draw() {
  if(mode == SETUP){ //don't draw
   }else{
    runCellularAutomaton();
  }
}

//Apply a configuration reading input from the textfields
public void apply(){
 try{
   //Read and parse the input
   String bornB = cp5.get(Textfield.class, "Born B").getText();
   String surviveB = cp5.get(Textfield.class, "Survive B").getText();
   String bornC = cp5.get(Textfield.class, "Born C").getText();
   String surviveC = cp5.get(Textfield.class, "Survive C").getText();
   int ages = Integer.parseInt(cp5.get(Textfield.class, "Ages").getText());
   String BtoC = cp5.get(Textfield.class, "B to C").getText();
   String CtoB = cp5.get(Textfield.class, "C to B").getText();
   float randomBirthProbB = Float.parseFloat(cp5.get(Textfield.class, "Random birth prob B").getText());
   float randomBirthProbC = Float.parseFloat(cp5.get(Textfield.class, "Random birth prob C").getText());
   float probB = Float.parseFloat(cp5.get(Textfield.class, "Initial proportion of B cells").getText());
   float probC = Float.parseFloat(cp5.get(Textfield.class, "Initial proportion of C cells").getText());
   float bornProbB = Float.parseFloat(cp5.get(Textfield.class, "Born probability B").getText());
   float surviveProbB = Float.parseFloat(cp5.get(Textfield.class, "Survive probability B").getText());
   float dieProbB = Float.parseFloat(cp5.get(Textfield.class, "Death probability B").getText());
    float bornProbC = Float.parseFloat(cp5.get(Textfield.class, "Born probability C").getText());
   float surviveProbC = Float.parseFloat(cp5.get(Textfield.class, "Survive probability C").getText());
   float dieProbC = Float.parseFloat(cp5.get(Textfield.class, "Death probability C").getText());
   float BToCProbability = Float.parseFloat(cp5.get(Textfield.class, "B to C probability").getText());
   float CToBProbability = Float.parseFloat(cp5.get(Textfield.class, "C to B probability").getText());
    
   //Set the rules
   cellularAutomaton.setRules(surviveB, bornB, surviveC, bornC, BtoC, CtoB, ages, bornProbB, surviveProbB, dieProbB, bornProbC, surviveProbC, dieProbC, BToCProbability, CToBProbability, randomBirthProbB, randomBirthProbC); 
   setAgesColors(cellularAutomaton.getAgesNumber());
   
   //Restart the simulation
   cellularAutomaton.randomlyInitializeBoard(probB, probC);
   mode = 0; 
   cp5.hide();
 }catch(Exception e){
   
     JOptionPane.showMessageDialog(null, "Please check your input.\n"  +e.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);  
     return;
 }


}

public void
runCellularAutomaton(){
  //Draw grid
  for (int x=0; x<width/cellSize; x++) {
    for (int y=0; y<height/cellSize; y++) {
      Board board = cellularAutomaton.getBoard();
      int state = board.getCell(x,y).getState();
      int age = board.getCell(x,y).getAge();
      if (state>=1) {
        if(state == 1){
          fill(ageColorsB.get(age));
        }else if(state ==2){
           fill(ageColorsC.get(age)); 
        }
      }
      else {
        fill(dead); // If dead
      }
      rect (x*cellSize, y*cellSize, cellSize, cellSize);
    }
  }
  
  //Draw statistics on the screen
  fill(color(140, 140, 160));
  text(String.format("B: %.3f%%", cellularAutomaton.getBoard().proportionOfState(1)*100), width-70, height-20 );
  text(String.format("C: %.3f%%", cellularAutomaton.getBoard().proportionOfState(2)*100), width-70, height-10);
  
  // Advance simulation when timer ticks
  if (millis()-lastTime>interval) {
    if (!pause) {
      //writer.println(cellularAutomaton.getBoard().proportionOfState(1) + " " + cellularAutomaton.getBoard().proportionOfState(2));
      //writer.flush();
      cellularAutomaton.advance();
      lastTime = millis();
    }
  }

  //If in edit mode, modify the grid
  if (pause && mousePressed) {
    int xCell = PApplet.parseInt(map(mouseX, 0, width, 0, width/cellSize));
    xCell = constrain(xCell, 0, width/cellSize-1);
    int yCell = PApplet.parseInt(map(mouseY, 0, height, 0, height/cellSize));
    yCell = constrain(yCell, 0, height/cellSize-1);
    if (tmpBoard.getCell(xCell,yCell).getState()>=1) { 
      cellularAutomaton.getBoard().getCell(xCell,yCell).reset();
      fill(dead); 
    }
    else { 
      cellularAutomaton.getBoard().getCell(xCell, yCell).reset();
      cellularAutomaton.getBoard().getCell(xCell,yCell).setState(spawned); 
      fill((spawned == 1 ? ageColorsB.get(0) : ageColorsC.get(0)) ); 
    }
  } 
  
  else if (pause && !mousePressed) { // Store the new state
    tmpBoard.copyCells(cellularAutomaton.getBoard());
  } 
}


//Handle key presses
public void keyPressed() {
  if (key=='r' || key == 'R') {
    // Restart
    cellularAutomaton.randomlyInitializeBoard();
  }
  if (key==' ') { // pause
    pause = !pause;
  }
  if (key=='c' || key == 'C') { // Clear 
    cellularAutomaton.clear();
  }
  if(key == '+'){ //speedup simulation
    interval = interval/2 > 0 ? interval/2 : 1;
  }
  if(key == '-'){ //Speed down simulation
    interval*=2;
  }
  if(key == 't' ||key=='T'){ //Change kind of cell spawned
    spawned = spawned%2 + 1;
    System.out.println(spawned);
  }
  if(key== '\n'){ //Setup mode
    mode = SETUP;
    clear();
    cp5.show();
  }
}




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
  
  
  public Cell getCell(int x, int y){
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
/**
* Represents a single cell in a cellular automaton
*/
public class Cell{
   private int state; 
   private int age;
   
   Cell(){
      reset();
   }
   
   Cell(int state){
      this.state = state; 
      this.age = 0;
   }
   
   /**
   * Copies an other cell into current object
   */
   public void copy(Cell other){
      this.state=other.state;
     this.age = other.age; 
   }
   
   /**
   * Resets the state of the cell, i.e. sets its state and age to zero
   */
   public void reset(){
     this.state = 0;
     this.age = 0; 
   }
   
   public int getState(){
      return state; 
   }
  public void setState(int state){
     this.state= state; 
  }
  
  public void setAge(int age){
     this.age = age; 
  }
  public int getAge(){
     return age; 
  }
}
/**
* A generic 3-state stochastic cellular automaton.
* See http://rk867672.webs.sse.reading.ac.uk for further information
*/
public class GenericCA{
  
  //Rules 
  ArrayList<Integer> nNeighboursSurviveB;
  ArrayList<Integer> nNeighboursBornB;
  ArrayList<Integer> nNeighboursSurviveC;
  ArrayList<Integer> nNeighboursBornC;
  ArrayList<Integer> nNeighboursBtoC;
  ArrayList<Integer> nNeighboursCtoB;
  
  //Probabilities
  float bornProbabilityB =1.0f;
  float surviveProbabilityB = 1.0f; 
  float dieProbabilityB = 1.0f;
  float bornProbabilityC = 1.0f;
  float surviveProbabilityC = 1.0f; 
  float dieProbabilityC = 1.0f;
  
  float randomBirthProbabilityB = 0.00001f; 
  float randomBirthProbabilityC = 0.00001f; 
  float bToCProbability = 1;
  float cToBProbability = 1;
  
  //Default rule set
  String bornB = "3";
  String surviveB = "23";
  String bornC = "3";
  String surviveC = "23";
  String BtoC = "";
  String CtoB = "3456789";

  //default parameters
  float initialProportionB = 0.1f;
  float  initialProportionC =0.15f;
  int agesNumber = 1;
  boolean closedBoundaries = false; 

  
  Board board; 
  Board boardNextState; 

  public GenericCA(int xCells, int yCells){
    board = new Board(xCells,yCells, closedBoundaries);
    boardNextState = new Board(xCells,yCells,closedBoundaries);
    setRules(surviveB, bornB, surviveC, bornC,BtoC, CtoB, agesNumber,bornProbabilityB, surviveProbabilityB,  dieProbabilityB,bornProbabilityC, surviveProbabilityC, dieProbabilityC, bToCProbability, cToBProbability, randomBirthProbabilityB, randomBirthProbabilityC);
    randomlyInitializeBoard(board, initialProportionB, initialProportionC);
  }

/**
* Sets the rules of the cellular automaton
*/
  public void
  setRules(String surviveB, String bornB, String surviveC, String bornC, String BtoC, String CtoB, int ages,float bornProbB, float surviveProbB, float dieProbB, float bornProbC, float surviveProbC, float dieProbC, float bToCProb, float cToBProb, float randomBirthProbB, float randomBirthProbC){
    nNeighboursSurviveB = stringToArrayOfInt(surviveB);
    nNeighboursBornB = stringToArrayOfInt(bornB);
    nNeighboursSurviveC = stringToArrayOfInt(surviveC);
    nNeighboursBornC = stringToArrayOfInt(bornC);
    nNeighboursBtoC = stringToArrayOfInt(BtoC);
    nNeighboursCtoB = stringToArrayOfInt(CtoB);
    agesNumber = ages;
    randomBirthProbabilityB = randomBirthProbB;
    randomBirthProbabilityC = randomBirthProbC;
    bornProbabilityB = bornProbB;
    surviveProbabilityB = surviveProbB;
    dieProbabilityB = dieProbB;
        bornProbabilityC = bornProbC;
    surviveProbabilityC = surviveProbC;
    dieProbabilityC = dieProbC;

    bToCProbability = bToCProb;
    cToBProbability = cToBProb;
  }
  
  /**
  * Randomly initializes the board with the default proportion of 
  * b and c cells.
  */
  public void randomlyInitializeBoard(){
    randomlyInitializeBoard(board, initialProportionB, initialProportionC);
  }

  /**
  * Randomly initializes the board with the specified probabilities
  * of cells in state B and C. The rest of the cells are in state 0
  * @param probB the probability of a cell being in state B
  * @param probC the probability of a cell being in state C
  */
  public void randomlyInitializeBoard(float probB, float probC){
    randomlyInitializeBoard(board, probB, probC);
  }
  
  /**
  * Randomly initializes a board with the specified probabilities
  * of cells in state B and C. The rest of the cells are in state 0.
  * @param board the board to initialise 
  * @param probB the probability of a cell being in state B
  * @param probC the probability of a cell being in state C
  */
  public void randomlyInitializeBoard(Board board, float probB, float probC){
     for (int x=0; x< board.xCells; x++) {
      for (int y=0; y< board.yCells; y++) {
        float state = random (1);
        if (state > probB + probC) { 
          state = 0;
        }
        else {
          state = random(1)*probB > random(1)*probC ? 1:2;
        }
        board.getCell(x,y).reset();
        board.getCell(x,y).setState( PApplet.parseInt(state) ); // Save state of each cell
      }
    } 
    boardNextState.copyCells(board);
  }
  
  public int getAgesNumber(){
     return agesNumber; 
  }
  public Board getBoard(){
     return board; 
  }
  
  /**
  * Advances the state of the automaton
  */
  public void advance() {
    
    boardNextState.copyCells(board);
    
    // Iterate over all the cells
    for (int x=0; x<board.xCells; x++) {
      for (int y=0; y<board.yCells; y++) {
        Cell previousCell = board.getCell(x,y);
        Cell nextCell = boardNextState.getCell(x,y);
        int state = previousCell.getState();
        int age = previousCell.getAge();
        
        int neighboursB = board.numNeighbours(x,y,1, 0); 
        int neighboursC = board.numNeighbours(x,y,2, 0);
        
        //If the cell is dying (age>0) it cannot give birth to new ones
        if(age>0){  
          if(age+1<agesNumber){ 
              nextCell.setAge(age+1);
              nextCell.setState(state);
          }else{
             nextCell.reset(); //cell dies 
          }
          continue;
        }
        
        if (state ==1) { 
          // transformation from B to C
          if(nNeighboursBtoC.contains(neighboursC)&&bernoulli(bToCProbability)){
               nextCell.setState(2);
          }else if (!nNeighboursSurviveB.contains(neighboursB) && bernoulli(dieProbabilityB)) { //aging/death
            if(age+1 < agesNumber){
                nextCell.setAge(age+1);
                nextCell.setState(state);
            }else{
              nextCell.reset();
            }
          }else if(!bernoulli(surviveProbabilityB)){ //Cell survives with probability surviveProbability. 
            nextCell.reset();
          }
        } 
        else if(state == 2){
          if(nNeighboursCtoB.contains(neighboursB)&&bernoulli(cToBProbability)){
              nextCell.setState(1);
          }
          else if(!nNeighboursSurviveC.contains(neighboursC) && bernoulli(dieProbabilityC)){
            if(age+1 < agesNumber){
              nextCell.setAge(age+1);
              nextCell.setState(state);
            }else{
              nextCell.reset();
            } 
          }else if(!bernoulli(surviveProbabilityC)){//Cell survives with probability surviveProbability. 
            nextCell.reset(); 
          }
        }else{ // The cell is dead. It could go back to life.      
          if (nNeighboursBornB.contains(neighboursB) && bernoulli(bornProbabilityB)) { //B has priority
            nextCell.setState(1); 
          }
          else if (nNeighboursBornC.contains(neighboursC) && bernoulli(bornProbabilityC) ){
            nextCell.setState(2); 
          }else if(bernoulli(randomBirthProbabilityB + randomBirthProbabilityC)){
            nextCell.setState(random(1)*randomBirthProbabilityB > random(1)*randomBirthProbabilityC ? 1 : 2); 
          }
        } // End of if
  
      } // End of y loop
    } // End of x loop
    board.copyCells(boardNextState);
  } // End of function

  /**
  * Resets the cellular automaton board.
  */
  public void clear(){ 
    board.reset();
    boardNextState.reset();
  }

}
public boolean bernoulli(float p){
     return random(1) < p ? true : false; 
} 

public ArrayList<Integer>
stringToArrayOfInt(final String s){
  return new ArrayList<Integer>(){{
  for(int i = 0; i < s.length(); ++i){
    add(Integer.parseInt(String.valueOf(s.charAt(i)))); 
  }
}};
}

  public ArrayList<Integer> gradient(int r1, int g1, int b1, int r2, int g2, int b2, int steps){
    ArrayList<Integer> gradientArray = new ArrayList<Integer>();
    if(steps == 1){
       gradientArray.add(color(r1, g1, b1));
      return gradientArray; 
    }
    for(int i = 0; i < steps; ++i){
      gradientArray.add(color(r1 + (r2-r1)*i/(steps-1), g1 + (g2-g1)*i/(steps-1), b1 + (b2-b1)*i/(steps-1)));
    }
    return gradientArray;
  }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "genericCA_applet" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
