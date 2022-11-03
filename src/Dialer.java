/********************************************************************
 * This class is used for generating Brute-Force combinations       *
 * used to dail a safe with the following restrictions:             *
 *  - The Safe dail ranges from (0, 99]                             *
 *  - The Safe uses a 3-number combo (XX, XX, XX)                   *
 *                                                                  *
 * @Author Joseph Godwin                                            *
 * @Date November 2nd, 2022                                         *
 ********************************************************************/

public class Dialer {

    /** Stores the current combination */
    int[] combination;


    /*******************************************************************
     ** CONSTRUCTORS **
     *******************************************************************/

    /** Default constructor
     ** Sets initial combo to 0-0-0 */
    public Dialer(){
        combination = new int[]{0,0,0};
    }
    /** Parameterized constructor
     ** @param startingCombo starting point for Brute-Force Algorithm */
    public Dialer(int[] startingCombo){
        combination = startingCombo;
    }


    /*******************************************************************
     **  ACCESSORS **
     *******************************************************************/

    public int[] getCurrentCombination(){
        return this.combination;
    }

    public int getTickCount() {
        return 100;
    }


    /*******************************************************************
     **  MUTATORS **
     *******************************************************************/

    public void setCurrentCombination(int[] combo){
        this.combination = combo;
    }


    /*******************************************************************
     **  PUBLIC METHODS **
     *******************************************************************/

    /**
     * Calls getNextCombination(2)
     * @return the next Brute-Force combination
     * using a default of delta = 2
     */
    public int[] getNextCombination(){
        return getNextCombination(2);
    }

    /**
     * Calls ToString("-"),
     * @return String representation of
     * combination with "XX-XX-XX"
     */
    public String ToString(){
        return ToString("-");
    }

    /**
     * @param delta how many ticks to skip for the next combination
     * @return returns the next brute-force combination using the specified delta
     */
    public int[] getNextCombination(int delta){
        int one = 0;
        int two = 1;
        int three = 2;
        if(combination[three] >= combination[two] - delta && combination[three] < combination[two]
           || combination[three] >= 100 - delta && combination[two] <= delta){
            combination[two] += delta;
            if(combination[two] >= 100){
                combination[two] -= 100;
                combination[one] += delta;
                if(combination[one] >= 100){
                    combination[one] -= 100;
                }
            }
            combination[three] = combination[two];
        }
        else{
            combination[three] += delta;
            if(combination[three] >= 100){
                combination[three] -= 100;
            }
        }
    return combination;
    }


    public String ToString(String delimiter){
        String stringCombo = "";
        for(int i = 0; i < this.combination.length; i++){
            stringCombo += String.valueOf(this.combination[i]);
            if(i < this.combination.length - 1)
                stringCombo += delimiter;
        }
        return stringCombo;
    }

}


