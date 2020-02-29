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
