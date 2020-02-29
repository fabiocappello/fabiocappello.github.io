boolean bernoulli(float p){
     return random(1) < p ? true : false; 
} 

ArrayList<Integer>
stringToArrayOfInt(final String s){
  return new ArrayList<Integer>(){{
  for(int i = 0; i < s.length(); ++i){
    add(Integer.parseInt(String.valueOf(s.charAt(i)))); 
  }
}};
}

  ArrayList<Integer> gradient(int r1, int g1, int b1, int r2, int g2, int b2, int steps){
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

