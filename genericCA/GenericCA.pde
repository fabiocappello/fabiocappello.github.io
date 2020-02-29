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
  float bornProbabilityB =1.0;
  float surviveProbabilityB = 1.0; 
  float dieProbabilityB = 1.0;
  float bornProbabilityC = 1.0;
  float surviveProbabilityC = 1.0; 
  float dieProbabilityC = 1.0;
  
  float randomBirthProbabilityB = 0.00001; 
  float randomBirthProbabilityC = 0.00001; 
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
  float initialProportionB = 0.1;
  float  initialProportionC =0.15;
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
  void
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
  void randomlyInitializeBoard(float probB, float probC){
    randomlyInitializeBoard(board, probB, probC);
  }
  
  /**
  * Randomly initializes a board with the specified probabilities
  * of cells in state B and C. The rest of the cells are in state 0.
  * @param board the board to initialise 
  * @param probB the probability of a cell being in state B
  * @param probC the probability of a cell being in state C
  */
  void randomlyInitializeBoard(Board board, float probB, float probC){
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
        board.getCell(x,y).setState( int(state) ); // Save state of each cell
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
  void clear(){ 
    board.reset();
    boardNextState.reset();
  }

}
