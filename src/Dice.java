
// Stan O' Neill

import java.util.ArrayList;

// For rolling dice
public class Dice{

	public static final int min = 1;
	public static final int max = 6;

	public ArrayList<Integer> values;

	public int highest(){
		int highest = 0;
		for(int i=0;i<values.size();i++){
			if(values.get(i) > highest){
				highest = values.get(i);
			}
		}
		return highest;
	}

	public int secondHighest(){
		int highest = highest();
		int secondHighest = 0;
		for(int i=0;i<values.size();i++){
			if(values.get(i) > secondHighest && values.get(i) <= highest){
				secondHighest = values.get(i);
			}
		}
		return secondHighest;
	}

	public void roll(int numDice){
		for(int i = 0; i < numDice; i++){
			values.add(roll());
		}
	}
	
	public int roll(){

		int Rolled;


		//Generating random number
		Rolled= min + (int)(Math.random() * max); 

		return Rolled;
	}
}