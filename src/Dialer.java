public class Dialer {

    /**  {@code @tickCount}   the number of ticks on the dial */
    int tickCount;
    /** {@code @combination} an array storing the current combination */
    int[] combination;

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
        for(int i = 0; i < combination.length; i++) {
            if (combination[lastIndex - i] < tickCount - (delta - 1)) {
                combination[lastIndex - i] += delta;
                break;
            }
            combination[lastIndex - i] = 0;
        }

        return combination;
    }

    public int[] getCurrentCombination(){ return this.combination; }
    public int getTickCount() { return this.tickCount; }

    public void setCurrentCombination(int[] combo){
        this.combination = combo;
    }

    public String ToString(){
        String stringCombo = "";
        for(int i = 0; i < this.combination.length; i++){
            stringCombo += String.valueOf(this.combination[i]);
            if(i < this.combination.length - 1)
                stringCombo += "-";
        }
        return stringCombo;
    }
}


