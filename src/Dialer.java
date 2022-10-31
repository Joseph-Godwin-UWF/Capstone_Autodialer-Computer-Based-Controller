public class Dialer {

    /**  {@code @tickCount}   the number of ticks on the dial */
    int tickCount;
    /** {@code @combination} an array storing the current combination */
    int[] combination;
    boolean doubleLastDigitComboSent = false;
    boolean doubleLastDigitDifferByDeltaSent = false;

    public Dialer(int comboSize, int tickCount){
        this.tickCount = tickCount;
        combination = new int[comboSize];
    }

    public int[] getNextCombination(){
        return getNextCombination(2);
    }

    /**
     * @param delta how many ticks to skip for the next combination
     * @return returns the next brute-force combination to try
     */
    public int[] getNextCombination(int delta){
        int lastIndex = combination.length - 1;
        if(combination[lastIndex] == combination[lastIndex - 1] && !doubleLastDigitComboSent){
            doubleLastDigitComboSent = true;
            return combination;
        }
        if(combination[lastIndex] == combination[lastIndex - 1] - delta && !doubleLastDigitDifferByDeltaSent){
            doubleLastDigitDifferByDeltaSent = true;
            return combination;
        }
        combination[lastIndex] += delta;
        if(combination[lastIndex] >= 100){
            combination[lastIndex] -= 100;
        }

        if(doubleLastDigitComboSent && doubleLastDigitDifferByDeltaSent){
            doubleLastDigitComboSent = false;
            doubleLastDigitDifferByDeltaSent = false;
            combination[lastIndex - 1] += delta;
            if(combination[lastIndex - 1] >= 100){
                combination[lastIndex - 1] -= 100;
                combination[lastIndex - 2] += delta;
            }
            combination[lastIndex] = combination[lastIndex - 1];
        }
        return combination;
    }

    public int[] getCurrentCombination(){ return this.combination; }
    public int getTickCount() { return this.tickCount; }

    public void setCurrentCombination(int[] combo){
        this.combination = combo;
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
    public String ToString(){
        return ToString("-");
    }
}


