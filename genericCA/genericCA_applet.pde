import controlP5.*;
import javax.swing.JOptionPane;

//Size of a cell in the screen
int cellSize = 5;

// Timer
int interval = 100; // Interval between iterations
int lastTime = 0;   // Stores last iteration time

int spawned = 1; //Type of cell spawned in edit mode
int SETUP = 1;
int mode = 0; //current modality

//Colors of cells
color dead = color(0);
ArrayList<Integer> ageColorsB;
ArrayList<Integer> ageColorsC;

//PrintWriter writer;

ControlP5 cp5;

//Cellular automaton variables
GenericCA cellularAutomaton;
Board tmpBoard;

//Set the age colors variables to a gradient, with a number of steps equal to the number of ages.
void setAgesColors(int ages){
  ageColorsB = gradient(255,0,0, 90,0,80, ages);
  ageColorsC = gradient(0,255,0, 0,90,80, ages);
}

// Pause
boolean pause = false;

//Sets the applet up
void setup() {
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
void initSetupScreen(){
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
void draw() {
  if(mode == SETUP){ //don't draw
   }else{
    runCellularAutomaton();
  }
}

//Apply a configuration reading input from the textfields
void apply(){
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

void
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
    int xCell = int(map(mouseX, 0, width, 0, width/cellSize));
    xCell = constrain(xCell, 0, width/cellSize-1);
    int yCell = int(map(mouseY, 0, height, 0, height/cellSize));
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
void keyPressed() {
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


